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

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static final Comparator<Entry<String, Long>> COMPARATOR = Entry.<String, Long>comparingByValue().reversed().thenComparing(Entry::getValue);

    public static final Comparator<Entry<Long, Long>> LONG_COMPARATOR = Entry.<Long, Long>comparingByValue().reversed().thenComparing(Entry::getValue);

    protected Filter prepare(ChatInputInteractionEvent event) {
        return new Filter(event.getOptions());
    }

    protected Filter prepare(List<ApplicationCommandInteractionOption> options) {
        return new Filter(options);
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

        private Optional<String> channel;

        private Optional<String> person;

        private Optional<LocalDate> start;

        private Optional<LocalDate> end;

        public Filter(Optional<String> channel, Optional<String> person, Optional<LocalDate> start, Optional<LocalDate> end) {
            this.channel = channel;
            this.person = person;
            this.start = start;
            this.end = end;
        }

        public Filter(List<ApplicationCommandInteractionOption> options) {
            this.channel = options.stream()
                                .filter(option -> option.getName().equals("channel"))
                                .findFirst()
                                .flatMap(ApplicationCommandInteractionOption::getValue)
                                .map(ApplicationCommandInteractionOptionValue::asSnowflake)
                                .map(Snowflake::asString)
                                .filter(StringUtils::isNotBlank);
            this.person = options.stream()
                               .filter(option -> option.getName().equals("person"))
                               .findFirst()
                               .flatMap(ApplicationCommandInteractionOption::getValue)
                               .map(ApplicationCommandInteractionOptionValue::asSnowflake)
                               .map(Snowflake::asString)
                               .filter(StringUtils::isNotBlank);
            this.start = options.stream()
                              .filter(option -> option.getName().equals("start"))
                              .findFirst()
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
            this.end = options.stream()
                            .filter(option -> option.getName().equals("end"))
                            .findFirst()
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

        public void setChannel(Optional<String> channel) {
            this.channel = channel;
        }

        public Optional<String> getPerson() {
            return person;
        }

        public void setPerson(Optional<String> person) {
            this.person = person;
        }

        public Optional<LocalDate> getStart() {
            return start;
        }

        public void setStart(Optional<LocalDate> start) {
            this.start = start;
        }

        public Optional<LocalDate> getEnd() {
            return end;
        }

        public void setEnd(Optional<LocalDate> end) {
            this.end = end;
        }
    }
}