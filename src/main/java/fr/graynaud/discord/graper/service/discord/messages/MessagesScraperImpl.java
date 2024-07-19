package fr.graynaud.discord.graper.service.discord.messages;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import fr.graynaud.discord.graper.model.EsGuild;
import fr.graynaud.discord.graper.model.EsMessage;
import fr.graynaud.discord.graper.model.EsMessageReaction;
import fr.graynaud.discord.graper.service.es.EsGuildService;
import fr.graynaud.discord.graper.service.es.EsMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessagesScraperImpl implements MessagesScraper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessagesScraperImpl.class);

    private static final Map<String, List<String>> WHITELISTED_CHANNELS = new HashMap<>();

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
                   .subscribe(c -> eatMessages(c, Snowflake.of(Instant.now().minusSeconds(60 * 30))));
    }

    @Scheduled(cron = "@midnight")
    public void scrapeLot() { //Do get reactions to old messages
        this.client.getGuilds()
                   .flatMap(Guild::getChannels)
                   .filter(TextChannel.class::isInstance)
                   .map(TextChannel.class::cast)
                   .subscribe(c -> eatMessages(c, Snowflake.of(Instant.now().minusSeconds(60 * 60 * 24 * 2))));
    }

    @Override
    public void eatMessages(TextChannel channel, Snowflake from) {
        if (WHITELISTED_CHANNELS.containsKey(channel.getGuildId().asString()) &&
            WHITELISTED_CHANNELS.get(channel.getGuildId().asString()).contains(channel.getId().asString())) {
            channel.getMessagesAfter(from).map(Message::getData).subscribe(message -> {
                EsMessage esMessage = new EsMessage(message, channel.getGuildId().asLong());

                if (!message.reactions().isAbsent()) {
                    Flux.concat(message.reactions()
                                       .get()
                                       .stream()
                                       .map(reaction -> channel.getMessageById(Snowflake.of(message.id()))
                                                               .flatMapMany(m -> m.getReactors(ReactionEmoji.of(reaction.emoji())))
                                                               .collectList()
                                                               .map(users -> new EsMessageReaction(reaction.emoji().name().orElse(null),
                                                                                                   users.stream().map(u -> u.getId().asLong()).toList()))
                                                               .map(esMessage::addReaction)
                                                               .then())
                                       .toList()).collectList().subscribe(unused -> this.esMessageService.save(esMessage));
                } else {
                    this.esMessageService.save(esMessage);
                }
            });
        }
    }

    @Override
    public void upsertGuildChannels(EsGuild guild) {
        WHITELISTED_CHANNELS.put(guild.getId(), guild.getWhitelistedChannelsIds());

        LOGGER.info("Whitelisted channels for {}: {}", guild.getName(), guild.getWhitelistedChannelsIds());
    }
}
