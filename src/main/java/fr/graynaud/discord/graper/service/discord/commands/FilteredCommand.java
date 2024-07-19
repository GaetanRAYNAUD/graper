package fr.graynaud.discord.graper.service.discord.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public abstract class FilteredCommand implements SlashCommand {

    protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    protected static final Comparator<Entry<String, Long>> COMPARATOR = Entry.<String, Long>comparingByValue().reversed().thenComparing(Entry::getValue);

    protected Filter prepare(ChatInputInteractionEvent event) {
        return new Filter(event);
    }

    protected List<ApplicationCommandOptionData> getOptions() {
        return List.of(ApplicationCommandOptionData.builder()
                                                   .name("channel")
                                                   .nameLocalizationsOrNull(Map.of("fr", "channel"))
                                                   .description("Dans le channel")
                                                   .type(7)
                                                   .required(false)
                                                   .build(),
                       ApplicationCommandOptionData.builder()
                                                   .name("start")
                                                   .nameLocalizationsOrNull(Map.of("fr", "depuis"))
                                                   .description("Date de début dd/MM/yyyy")
                                                   .type(3)
                                                   .required(false)
                                                   .build(),
                       ApplicationCommandOptionData.builder()
                                                   .name("end")
                                                   .nameLocalizationsOrNull(Map.of("fr", "jusqua"))
                                                   .description("Date de fin dd/MM/yyyy")
                                                   .type(3)
                                                   .required(false)
                                                   .build(),
                       ApplicationCommandOptionData.builder()
                                                   .name("person")
                                                   .nameLocalizationsOrNull(Map.of("fr", "qui"))
                                                   .description("Écrit par")
                                                   .type(6)
                                                   .required(false)
                                                   .build());
    }

    protected void phrase(Filter filter, StringBuilder sb) {
        filter.getChannel().ifPresent(s -> sb.append("dans <#").append(s).append("> "));
        filter.getPerson().ifPresent(s -> sb.append("écris par <@").append(s).append("> "));
        filter.getStart().ifPresent(s -> sb.append("depuis le ").append(DATE_TIME_FORMATTER.format(s)).append(" "));
        filter.getEnd().ifPresent(s -> sb.append("jusqu'au ").append(DATE_TIME_FORMATTER.format(s)).append(" "));

        sb.deleteCharAt(sb.length() - 1);
    }

    public static class Filter {

        private final Optional<String> channel;

        private final Optional<String> person;

        private final Optional<LocalDate> start;

        private final Optional<LocalDate> end;

        public Filter(ChatInputInteractionEvent event) {
            this.channel = event.getOption("channel")
                                .flatMap(ApplicationCommandInteractionOption::getValue)
                                .map(ApplicationCommandInteractionOptionValue::asSnowflake)
                                .map(Snowflake::asString)
                                .filter(StringUtils::isNotBlank);
            this.person = event.getOption("person")
                               .flatMap(ApplicationCommandInteractionOption::getValue)
                               .map(ApplicationCommandInteractionOptionValue::asSnowflake)
                               .map(Snowflake::asString)
                               .filter(StringUtils::isNotBlank);
            this.start = event.getOption("start")
                              .flatMap(ApplicationCommandInteractionOption::getValue)
                              .map(ApplicationCommandInteractionOptionValue::asString)
                              .filter(StringUtils::isNotBlank)
                              .flatMap(s -> {
                                  try {
                                      return Optional.of(LocalDate.parse(s, DATE_TIME_FORMATTER));
                                  } catch (DateTimeParseException e) {
                                      return Optional.empty();
                                  }
                              });
            this.end = event.getOption("end")
                            .flatMap(ApplicationCommandInteractionOption::getValue)
                            .map(ApplicationCommandInteractionOptionValue::asString)
                            .filter(StringUtils::isNotBlank)
                            .flatMap(s -> {
                                try {
                                    return Optional.of(LocalDate.parse(s, DATE_TIME_FORMATTER));
                                } catch (DateTimeParseException e) {
                                    return Optional.empty();
                                }
                            });
        }

        public Optional<String> getChannel() {
            return channel;
        }

        public Optional<String> getPerson() {
            return person;
        }

        public Optional<LocalDate> getStart() {
            return start;
        }

        public Optional<LocalDate> getEnd() {
            return end;
        }
    }
}