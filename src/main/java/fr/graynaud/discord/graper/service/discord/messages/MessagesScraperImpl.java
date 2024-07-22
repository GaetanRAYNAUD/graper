package fr.graynaud.discord.graper.service.discord.messages;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateFields.Field;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.possible.PossibleFilter;
import discord4j.discordjson.possible.PossibleModule;
import discord4j.rest.util.Color;
import fr.graynaud.discord.graper.model.EsGuild;
import fr.graynaud.discord.graper.model.EsMessage;
import fr.graynaud.discord.graper.model.EsMessageReaction;
import fr.graynaud.discord.graper.service.chart.ChartUtils;
import fr.graynaud.discord.graper.service.discord.commands.FilteredCommand;
import fr.graynaud.discord.graper.service.discord.commands.FilteredCommand.Filter;
import fr.graynaud.discord.graper.service.discord.messages.ImmutableMessageData.Json;
import fr.graynaud.discord.graper.service.es.EsGuildService;
import fr.graynaud.discord.graper.service.es.EsMessageService;
import fr.graynaud.discord.graper.service.es.object.RecapResult;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class MessagesScraperImpl implements MessagesScraper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessagesScraperImpl.class);

    private static final Map<String, Set<String>> WHITELISTED_CHANNELS = new HashMap<>();

    private final GatewayDiscordClient client;

    private final EsMessageService esMessageService;

    public MessagesScraperImpl(GatewayDiscordClient client, EsMessageService esMessageService, EsGuildService esGuildService) {
        this.client = client;
        this.esMessageService = esMessageService;

        esGuildService.findAll().subscribe(this::upsertGuildChannels);
    }

    @Scheduled(fixedDelayString = "PT15M")
    public void scrape() {
        this.client.getGuilds()
                   .flatMap(Guild::getChannels)
                   .filter(TextChannel.class::isInstance)
                   .map(TextChannel.class::cast)
                   .flatMap(c -> eatMessages(c, Snowflake.of(Instant.now().minusSeconds(60 * 30))).doOnSuccess(whitelisted -> {
                       if (whitelisted) {
                           LOGGER.info("Analysed messages for {} 🔥", c.getName());
                       }
                   })).subscribe();
    }

    @Scheduled(cron = "@midnight")
    public void scrapeLot() { //Do get reactions to old messages
        this.client.getGuilds()
                   .flatMap(Guild::getChannels)
                   .filter(TextChannel.class::isInstance)
                   .map(TextChannel.class::cast)
                   .flatMap(c -> eatMessages(c, Snowflake.of(Instant.now().minusSeconds(60 * 60 * 24 * 2))).doOnSuccess(whitelisted -> {
                       if (whitelisted) {
                           LOGGER.info("Night analysed messages for {} 🔥", c.getName());
                       }
                   }))
                   .subscribe();
    }

    @Override
    public Mono<Boolean> eatMessages(TextChannel channel, Snowflake from) {
        if (WHITELISTED_CHANNELS.containsKey(channel.getGuildId().asString()) &&
            WHITELISTED_CHANNELS.get(channel.getGuildId().asString()).contains(channel.getId().asString())) {
            return channel.getMessagesAfter(from).map(Message::getData).flatMap(message -> {
                EsMessage esMessage = new EsMessage(message, channel.getGuildId().asLong());

                if (!message.reactions().isAbsent()) {
                    return Flux.concat(message.reactions()
                                              .get()
                                              .stream()
                                              .map(reaction -> channel.getMessageById(Snowflake.of(message.id()))
                                                                      .flatMapMany(m -> m.getReactors(ReactionEmoji.of(reaction.emoji())))
                                                                      .collectList()
                                                                      .map(users -> new EsMessageReaction(reaction.emoji().name().orElse(null),
                                                                                                          users.stream().map(u -> u.getId().asLong()).toList()))
                                                                      .map(esMessage::addReaction)
                                                                      .then())
                                              .toList()).collectList().flatMap(voidList -> {
                        this.esMessageService.save(esMessage);

                        return Mono.just(0);
                    });
                } else {
                    this.esMessageService.save(esMessage);

                    return Mono.just(0);
                }
            }).collectList().flatMap(o -> Mono.just(true));
        }

        return Mono.just(false);
    }

    @Override
    public void upsertGuildChannels(EsGuild guild) {
        WHITELISTED_CHANNELS.put(guild.getId(), SetUtils.emptyIfNull(guild.getWhitelistedChannelsIds()));

        LOGGER.info("Whitelisted channels for {}: {}", guild.getName(), guild.getWhitelistedChannelsIds());
    }

    @Override
    public void doRecap(Message edit, TextChannel channel, Filter filter, int nbWords) {
        this.esMessageService.recap(String.valueOf(channel.getGuildId().asLong()), filter, nbWords)
                             .flatMap(recap -> {
                                 Mono<Message> message;
                                 if (edit != null) {
                                     message = edit.edit().withEmbeds(EmbedCreateSpec.create()
                                                                                     .withDescription(recapDescription(filter, recap))
                                                                                     .withColor(Color.WHITE));
                                 } else {
                                     message = channel.createMessage(EmbedCreateSpec.create()
                                                                                    .withDescription(recapDescription(filter, recap))
                                                                                    .withColor(Color.WHITE));
                                 }

                                 return message.zipWhen(m -> channel.getGuild()
                                                                    .flatMapMany(g -> g.requestMembers(recap.getAuthors()
                                                                                                            .keySet()
                                                                                                            .stream()
                                                                                                            .map(Long::parseLong)
                                                                                                            .map(Snowflake::of)
                                                                                                            .collect(Collectors.toSet())))
                                                                    .collectList())
                                               .flatMap(m -> channel.createMessage(EmbedCreateSpec.create()
                                                                                                  .withDescription("Le top **" + Math.min(nbWords,
                                                                                                                                          recap.getAuthors()
                                                                                                                                               .size()) +
                                                                                                                   "** des plus gros parleurs sont :")
                                                                                                  .withFields(recap.getAuthors()
                                                                                                                   .entrySet()
                                                                                                                   .stream()
                                                                                                                   .sorted(FilteredCommand.COMPARATOR)
                                                                                                                   .map(e -> Field.of("",
                                                                                                                                      "<@" + e.getKey() +
                                                                                                                                      "> avec **" +
                                                                                                                                      e.getValue() +
                                                                                                                                      "** messages",
                                                                                                                                      false))
                                                                                                                   .toList())
                                                                                                  .withColor(Color.WHITE)
                                                                                                  .withImage(ChartUtils.getPieRecap(recap.getAuthors(),
                                                                                                                                    recap.getNbMessage(),
                                                                                                                                    m.getT2())))
                                                                    .withMessageReference(m.getT1().getId()))
                                               .zipWhen(m1 -> channel.getGuild()
                                                                     .flatMapMany(g -> Flux.concat(recap.getChannels()
                                                                                                        .keySet()
                                                                                                        .stream()
                                                                                                        .map(Long::parseLong)
                                                                                                        .map(Snowflake::of)
                                                                                                        .map(g::getChannelById)
                                                                                                        .collect(Collectors.toSet())))
                                                                     .collectList())
                                               .flatMap(m1 ->
                                                                channel.createMessage(EmbedCreateSpec.create()
                                                                                                     .withDescription("Le top **" + Math.min(nbWords,
                                                                                                                                             recap.getChannels()
                                                                                                                                                  .size()) +
                                                                                                                      "** des plus gros channels sont :")
                                                                                                     .withFields(recap.getChannels()
                                                                                                                      .entrySet()
                                                                                                                      .stream()
                                                                                                                      .sorted(FilteredCommand.COMPARATOR)
                                                                                                                      .map(e -> Field.of("",
                                                                                                                                         "<#" + e.getKey() +
                                                                                                                                         "> avec **" +
                                                                                                                                         e.getValue() +
                                                                                                                                         "** messages",
                                                                                                                                         false))
                                                                                                                      .toList())
                                                                                                     .withColor(Color.WHITE)
                                                                                                     .withImage(ChartUtils.getPieRecap(recap.getChannels(),
                                                                                                                                       recap.getNbMessage(),
                                                                                                                                       m1.getT2())))
                                                                       .withMessageReference(m1.getT1().getId()))
                                               .flatMap(m1 ->
                                                                channel.createMessage(EmbedCreateSpec.create()
                                                                                                     .withDescription("Le top **" +
                                                                                                                      Math.min(nbWords,
                                                                                                                               recap.getWords().size()) +
                                                                                                                      "** des mots les plus utilisés sont :")
                                                                                                     .withFields(recap.getWords()
                                                                                                                      .entrySet()
                                                                                                                      .stream()
                                                                                                                      .sorted(FilteredCommand.COMPARATOR)
                                                                                                                      .map(e -> Field.of("",
                                                                                                                                         "**" + e.getKey() +
                                                                                                                                         "** avec **" +
                                                                                                                                         e.getValue() +
                                                                                                                                         "** utilisations",
                                                                                                                                         false))
                                                                                                                      .toList())
                                                                                                     .withColor(Color.WHITE))
                                                                       .withMessageReference(m1.getId()))
                                               .flatMap(m1 ->
                                                                channel.createMessage(EmbedCreateSpec.create()
                                                                                                     .withDescription("Le top **" + recap.getHours().size() +
                                                                                                                      "** des heures les plus actives sont :")
                                                                                                     .withFields(recap.getHours()
                                                                                                                      .entrySet()
                                                                                                                      .stream()
                                                                                                                      .sorted(FilteredCommand.LONG_COMPARATOR)
                                                                                                                      .map(e -> Field.of("",
                                                                                                                                         "**" + e.getKey() +
                                                                                                                                         "h** avec **" +
                                                                                                                                         e.getValue() +
                                                                                                                                         "** messages",
                                                                                                                                         false))
                                                                                                                      .toList())
                                                                                                     .withColor(Color.WHITE)
                                                                                                     .withImage(ChartUtils.getBHS(recap.getHours()
                                                                                                                                       .entrySet()
                                                                                                                                       .stream()
                                                                                                                                       .sorted(Map.Entry.comparingByKey())
                                                                                                                                       .collect(
                                                                                                                                               Collectors.toMap(e -> e.getKey() + "h",
                                                                                                                                                                Map.Entry::getValue,
                                                                                                                                                                (a, b) -> a,
                                                                                                                                                                LinkedHashMap::new)),
                                                                                                                                  recap.getNbMessage())))
                                                                       .withMessageReference(m1.getId()))
                                               .flatMap(m1 ->
                                                                channel.createMessage(EmbedCreateSpec.create()
                                                                                                     .withDescription("Le top **" +
                                                                                                                      Math.min(7, recap.getDays().size()) +
                                                                                                                      "** des jours les plus actifs sont :")
                                                                                                     .withFields(recap.getDays()
                                                                                                                      .entrySet()
                                                                                                                      .stream()
                                                                                                                      .sorted(FilteredCommand.LONG_COMPARATOR)
                                                                                                                      .map(e -> Field.of("",
                                                                                                                                         "**" +
                                                                                                                                         StringUtils.capitalize(DayOfWeek.of(e.getKey().intValue()).getDisplayName(TextStyle.FULL_STANDALONE, Locale.FRANCE)) +
                                                                                                                                         "** avec **" +
                                                                                                                                         e.getValue() +
                                                                                                                                         "** messages",
                                                                                                                                         false))
                                                                                                                      .toList())
                                                                                                     .withColor(Color.WHITE)
                                                                                                     .withImage(ChartUtils.getBHS(recap.getDays()
                                                                                                                                       .entrySet()
                                                                                                                                       .stream()
                                                                                                                                       .sorted(Map.Entry.comparingByKey())
                                                                                                                                       .map(e -> Pair.of(
                                                                                                                                               StringUtils.capitalize(
                                                                                                                                                       DayOfWeek.of(e.getKey().intValue())
                                                                                                                                                                .getDisplayName(
                                                                                                                                                                        TextStyle.FULL_STANDALONE,
                                                                                                                                                                        Locale.FRANCE)),
                                                                                                                                               e.getValue()))
                                                                                                                                       .collect(
                                                                                                                                               Collectors.toMap(
                                                                                                                                                       Pair::getKey,
                                                                                                                                                       Pair::getValue,
                                                                                                                                                       (a, b) -> a,
                                                                                                                                                       LinkedHashMap::new)),
                                                                                                                                  recap.getDays().values()
                                                                                                                                       .stream()
                                                                                                                                       .mapToLong(
                                                                                                                                               Long::longValue)
                                                                                                                                       .sum())))
                                                                       .withMessageReference(m1.getId()));
                             }).subscribe();
    }

    private String recapDescription(Filter filter, RecapResult recap) {
        StringBuilder sb = new StringBuilder("**Voici un récapitulatif du " + FilteredCommand.DATE_TIME_FORMATTER.format(filter.getStart().get()) + " au " +
                                             FilteredCommand.DATE_TIME_FORMATTER.format(filter.getEnd().get()) + " ");

        filter.getPerson().ifPresent(s -> sb.append("de <@").append(s).append("> "));
        filter.getChannel().ifPresent(s -> sb.append("dans <#").append(s).append("> "));

        sb.append("**");
        sb.append("\n\n");

        sb.append("Il y a eu **");
        sb.append(recap.getNbMessage());
        sb.append("** messages d'envoyés.");
        sb.append("\n\n");
        sb.append("D'ailleurs en voici un aléatoire :\n");
        sb.append("https://discord.com/channels/");
        sb.append(recap.getRandomMessage().getGuildId());
        sb.append("/");
        sb.append(recap.getRandomMessage().getChannelId());
        sb.append("/");
        sb.append(recap.getRandomMessage().getId());

        return StringUtils.trimToEmpty(sb.toString());
    }

    @Scheduled(cron = "0 0 9 * * MON", zone = "Europe/Paris")
    public void sendRecap() {
        LocalDate startPreviousWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusDays(-7);
        LocalDate endPreviousWeek = LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
        Filter filter = new Filter(Optional.empty(), Optional.empty(), Optional.of(startPreviousWeek), Optional.of(endPreviousWeek));

        this.client.getGuilds()
                   .flatMap(Guild::getSystemChannel)
                   .subscribe(channel -> doRecap(null, channel, filter, 5));
    }
}
