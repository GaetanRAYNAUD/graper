package fr.graynaud.discord.graper.service.discord.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandInteractionOptionData;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import fr.graynaud.discord.graper.service.discord.messages.MessagesScraper;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class RecapCommand extends FilteredCommand implements SlashCommand {

    private static final int NB_WORDS = 5;

    private final MessagesScraper scraper;

    public RecapCommand(MessagesScraper scraper) {
        this.scraper = scraper;
    }

    @Override
    public String getName() {
        return "recap";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        AtomicReference<LocalDate> startPreviousWeek = new AtomicReference<>(LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusDays(-7));
        AtomicReference<LocalDate> endPreviousWeek = new AtomicReference<>(LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)));

        event.getOption("day")
             .flatMap(ApplicationCommandInteractionOption::getValue)
             .map(ApplicationCommandInteractionOptionValue::asString)
             .filter(StringUtils::isNotBlank)
             .flatMap(s -> {
                 try {
                     return Optional.of(LocalDate.parse(s, DATE_TIME_FORMATTER));
                 } catch (DateTimeParseException e) {
                     return Optional.empty();
                 }
             })
             .ifPresent(localDate -> {
                 startPreviousWeek.set(localDate);
                 endPreviousWeek.set(localDate);
             });

        Filter filter = prepare(ListUtils.union(event.getOptions(),
                                                List.of(new ApplicationCommandInteractionOption(event.getClient(),
                                                                                                ApplicationCommandInteractionOptionData.builder()
                                                                                                                                       .name("start")
                                                                                                                                       .value(DATE_TIME_FORMATTER.format(startPreviousWeek.get()))
                                                                                                                                       .type(3)
                                                                                                                                       .build(),
                                                                                                event.getInteraction().getGuildId().get().asLong(),
                                                                                                null),
                                                        new ApplicationCommandInteractionOption(event.getClient(),
                                                                                                ApplicationCommandInteractionOptionData.builder()
                                                                                                                                       .name("end")
                                                                                                                                       .value(DATE_TIME_FORMATTER.format(endPreviousWeek.get()))
                                                                                                                                       .type(3)
                                                                                                                                       .build(),
                                                                                                event.getInteraction().getGuildId().get().asLong(),
                                                                                                null))));

        return event.deferReply()
                    .then(event.createFollowup()
                               .withEmbeds(EmbedCreateSpec.builder().title("⌛").build())
                               .flatMap(message -> message.getChannel().map(channel -> {
                                   this.scraper.doRecap(message, (TextChannel) channel, filter, NB_WORDS);

                                   return 0;
                               })))
                    .then();
    }

    @Override
    public ApplicationCommandRequest getRequest() {
        return ApplicationCommandRequest.builder()
                                        .name(getName())
                                        .description("Récapitulatif de la semaine dernière.")
                                        .options(List.of(ApplicationCommandOptionData.builder()
                                                                                     .name("channel")
                                                                                     .nameLocalizationsOrNull(Map.of("fr", "channel"))
                                                                                     .description("Dans le channel")
                                                                                     .type(7)
                                                                                     .required(false)
                                                                                     .build(),
                                                         ApplicationCommandOptionData.builder()
                                                                                     .name("person")
                                                                                     .nameLocalizationsOrNull(Map.of("fr", "qui"))
                                                                                     .description("De")
                                                                                     .type(6)
                                                                                     .required(false)
                                                                                     .build(),
                                                         ApplicationCommandOptionData.builder()
                                                                                     .name("day")
                                                                                     .nameLocalizationsOrNull(Map.of("fr", "jour"))
                                                                                     .description("Recap d'un jour précis dd/MM/yyyy")
                                                                                     .type(3)
                                                                                     .required(false)
                                                                                     .build()))
                                        .build();
    }
}