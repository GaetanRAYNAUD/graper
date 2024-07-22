package fr.graynaud.discord.graper.service.discord.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.EmbedCreateFields.Field;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Color;
import fr.graynaud.discord.graper.service.chart.ChartUtils;
import fr.graynaud.discord.graper.service.es.EsMessageService;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DayCommand extends FilteredCommand implements SlashCommand {

    private static final int NB_WORDS = 7;

    private final EsMessageService esMessageService;

    public DayCommand(EsMessageService esMessageService) {
        this.esMessageService = esMessageService;
    }

    @Override
    public String getName() {
        return "day";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Filter filter = prepare(event);
        Optional<String> text = event.getOption("text")
                                     .flatMap(ApplicationCommandInteractionOption::getValue)
                                     .map(ApplicationCommandInteractionOptionValue::asString);

        return event.deferReply()
                    .then(this.esMessageService.searchDay(event.getInteraction().getGuildId().get().asString(), filter, text, NB_WORDS)
                                               .map(days -> days.entrySet()
                                                                .stream()
                                                                .sorted(Map.Entry.comparingByKey())
                                                                .map(e -> Pair.of(StringUtils.capitalize(DayOfWeek.of(e.getKey().intValue())
                                                                                                                  .getDisplayName(TextStyle.FULL_STANDALONE,
                                                                                                                                  Locale.FRANCE)),
                                                                                  e.getValue()))
                                                                .collect(Collectors.toMap(Pair::getKey, Pair::getValue, (a ,b) -> a, LinkedHashMap::new)))
                                               .flatMap(days -> event.createFollowup()
                                                                      .withEmbeds(List.of(EmbedCreateSpec.create()
                                                                                                         .withDescription(phrase(filter, text))
                                                                                                         .withColor(Color.WHITE)
                                                                                                         .withFields(days.entrySet()
                                                                                                                         .stream()
                                                                                                                         .sorted(FilteredCommand.COMPARATOR)
                                                                                                                         .map(e -> Field.of("",
                                                                                                                                            "**" +
                                                                                                                                            e.getKey() +
                                                                                                                                            "** avec **" +
                                                                                                                                            e.getValue() +
                                                                                                                                            "** messages",
                                                                                                                                            false))
                                                                                                                         .toList())
                                                                                                         .withImage(ChartUtils.getBHS(days,
                                                                                                                                      days.values()
                                                                                                                                                .stream()
                                                                                                                                                .mapToLong(Long::longValue)
                                                                                                                                                .sum()))))))
                    .then();
    }

    @Override
    public ApplicationCommandRequest getRequest() {
        return ApplicationCommandRequest.builder()
                                        .name(getName())
                                        .nameLocalizationsOrNull(Map.of("fr", "jours_les_plus_actifs"))
                                        .description("Indique les jours de la semaines les plus actives.")
                                        .options(ListUtils.union(List.of(ApplicationCommandOptionData.builder()
                                                                                                     .name("text")
                                                                                                     .nameLocalizationsOrNull(Map.of("fr", "texte"))
                                                                                                     .description("Filtrer sur un texte")
                                                                                                     .type(3)
                                                                                                     .required(false)
                                                                                                     .build()),
                                                                 getOptions()))
                                        .build();
    }

    private String phrase(Filter filter, Optional<String> text) {
        StringBuilder sb = new StringBuilder("Les **" + NB_WORDS + "** jours les plus actives ");
        super.phrase(filter, sb);
        text.ifPresent(s -> sb.append(" contenant `").append(s).append("` "));
        sb.append(" sont ");

        return StringUtils.normalizeSpace(sb.toString());
    }
}