package fr.graynaud.discord.graper.service.discord.commands;

import discord4j.common.util.Snowflake;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TextWhoCommand extends FilteredCommand implements SlashCommand {

    private static final int NB_WORDS = 5;

    private final EsMessageService esMessageService;

    public TextWhoCommand(EsMessageService esMessageService) {
        this.esMessageService = esMessageService;
    }

    @Override
    public String getName() {
        return "text_who";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Filter filter = prepare(event);
        String text = event.getOption("text")
                           .flatMap(ApplicationCommandInteractionOption::getValue)
                           .map(ApplicationCommandInteractionOptionValue::asString)
                           .get();

        return event.deferReply()
                    .then(this.esMessageService.searchTextWho(event.getInteraction().getGuildId().get().asString(), filter, text, NB_WORDS)
                                               .zipWhen(nb -> event.getInteraction()
                                                                   .getGuild()
                                                                   .flatMapMany(g -> g.requestMembers(nb.getValue()
                                                                                                        .keySet()
                                                                                                        .stream()
                                                                                                        .map(Long::parseLong)
                                                                                                        .map(Snowflake::of)
                                                                                                        .collect(Collectors.toSet())))
                                                                   .collectList())
                                               .flatMap(nb -> event.createFollowup()
                                                                   .withEmbeds(List.of(EmbedCreateSpec.create()
                                                                                                      .withDescription(phrase(filter, text, nb.getT1()))
                                                                                                      .withColor(Color.WHITE)
                                                                                                      .withFields(nb.getT1()
                                                                                                                    .getValue()
                                                                                                                    .entrySet()
                                                                                                                    .stream()
                                                                                                                    .sorted(COMPARATOR)
                                                                                                                    .map(e -> Field.of("",
                                                                                                                            "Dont " + e.getValue() + " fois par <@" + e.getKey() + ">",
                                                                                                                                       false))
                                                                                                                    .toList())
                                                                                                      .withImage(ChartUtils.getPieRecap(nb.getT1().getValue(),
                                                                                                                                        nb.getT1().getKey(),
                                                                                                                                        nb.getT2()))))))
                    .then();
    }

    @Override
    public ApplicationCommandRequest getRequest() {
        return ApplicationCommandRequest.builder()
                                        .name(getName())
                                        .nameLocalizationsOrNull(Map.of("fr", "texte_qui"))
                                        .description("Indique dans combien de messages un texte a été utilisé et qui l'a dit le plus.")
                                        .options(ListUtils.union(List.of(ApplicationCommandOptionData.builder()
                                                                                                     .name("text")
                                                                                                     .nameLocalizationsOrNull(Map.of("fr", "texte"))
                                                                                                     .description("Le texte")
                                                                                                     .type(3)
                                                                                                     .required(true)
                                                                                                     .build()),
                                                                 getOptions()))
                                        .build();
    }

    private String phrase(Filter filter, String text, Pair<Long, Map<String, Long>> nb) {
        StringBuilder sb = new StringBuilder(
                "Top **" + NB_WORDS + "** des personnes qui utilisent le texte `" + text + "`, au total il a été utilisé dans **" + nb.getKey() +
                "** messages ");
        super.phrase(filter, sb);

        return StringUtils.normalizeSpace(sb.toString());
    }
}