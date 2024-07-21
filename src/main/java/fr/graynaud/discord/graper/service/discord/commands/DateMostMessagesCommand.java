package fr.graynaud.discord.graper.service.discord.commands;

import co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.EmbedCreateFields.Field;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Color;
import fr.graynaud.discord.graper.service.es.EsMessageService;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Component
public class DateMostMessagesCommand extends FilteredCommand implements SlashCommand {

    private static final int NB_WORDS = 5;

    private final EsMessageService esMessageService;

    public DateMostMessagesCommand(EsMessageService esMessageService) {
        this.esMessageService = esMessageService;
    }

    @Override
    public String getName() {
        return "datemostmessages";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Filter filter = prepare(event);
        Optional<DateType> type = event.getOption("type")
                                       .flatMap(ApplicationCommandInteractionOption::getValue)
                                       .map(ApplicationCommandInteractionOptionValue::asString)
                                       .flatMap(DateType::fromString);

        return event.deferReply()
                    .then(this.esMessageService.dateHistogram(event.getInteraction().getGuildId().get().asString(), filter, type.get().interval, NB_WORDS)
                                               .flatMap(date -> event.createFollowup()
                                                                     .withEmbeds(List.of(EmbedCreateSpec.create()
                                                                                                        .withDescription(phrase(filter, type.get()))
                                                                                                        .withColor(Color.WHITE)
                                                                                                        .withFields(date.entrySet()
                                                                                                                        .stream()
                                                                                                                        .sorted(COMPARATOR)
                                                                                                                        .map(e -> Field.of("",
                                                                                                                                           type.get().getFieldLabel(e.getKey()) +
                                                                                                                                           " avec **" +
                                                                                                                                           e.getValue() +
                                                                                                                                           "** message",
                                                                                                                                           false))
                                                                                                                        .toList())))))
                    .then();
    }

    @Override
    public ApplicationCommandRequest getRequest() {
        return ApplicationCommandRequest.builder()
                                        .name(getName())
                                        .nameLocalizationsOrNull(Map.of("fr", "date_plus_messages"))
                                        .description("Indique le jour/semaine/mois avec le plus de messages.")
                                        .options(ListUtils.union(List.of(ApplicationCommandOptionData.builder()
                                                                                                     .name("type")
                                                                                                     .nameLocalizationsOrNull(Map.of("fr", "type"))
                                                                                                     .description("Le type J/S/M")
                                                                                                     .type(3)
                                                                                                     .required(true)
                                                                                                     .build()),
                                                                 getOptions()))
                                        .build();
    }

    private String phrase(Filter filter, DateType type) {
        StringBuilder sb = new StringBuilder("Les **" + NB_WORDS + " " + type.label + "** avec le plus de messages ");
        filter.setPerson(Optional.empty());
        super.phrase(filter, sb);
        sb.append(" sont :");

        return StringUtils.normalizeSpace(sb.toString());
    }

    public enum DateType {
        DAY("J", CalendarInterval.Day, "jours"), WEEK("S", CalendarInterval.Week, "semaines"), MONTH("M", CalendarInterval.Month, "mois");

        public final String type;

        public final CalendarInterval interval;

        public final String label;

        DateType(String type, CalendarInterval interval, String label) {
            this.type = type;
            this.interval = interval;
            this.label = label;
        }

        public String getType() {
            return type;
        }

        public CalendarInterval getInterval() {
            return interval;
        }

        public String getLabel() {
            return label;
        }

        public String getFieldLabel(String value) {
            return switch (this) {
                case DAY -> "Le " + value;
                case WEEK -> "La semaine du " + value;
                case MONTH -> {
                    LocalDate localDate = LocalDate.parse(value, DATE_TIME_FORMATTER);

                    yield StringUtils.capitalize(localDate.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.FRANCE)) + " " + localDate.getYear();
                }
            };
        }

        public static Optional<DateType> fromString(String s) {
            for (DateType type : DateType.values()) {
                if (type.type.equalsIgnoreCase(s)) {
                    return Optional.of(type);
                }
            }

            return Optional.empty();
        }
    }
}