package fr.graynaud.discord.graper.service.discord.commands;

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
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class HourCommand extends FilteredCommand implements SlashCommand {

    private static final int NB_WORDS = 5;

    private final EsMessageService esMessageService;

    public HourCommand(EsMessageService esMessageService) {
        this.esMessageService = esMessageService;
    }

    @Override
    public String getName() {
        return "hour";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Filter filter = prepare(event);
        Optional<String> text = event.getOption("text")
                                     .flatMap(ApplicationCommandInteractionOption::getValue)
                                     .map(ApplicationCommandInteractionOptionValue::asString);

        return event.deferReply()
                    .then(this.esMessageService.searchHour(event.getInteraction().getGuildId().get().asString(), filter, text, NB_WORDS)
                                               .flatMap(hours -> event.createFollowup()
                                                                      .withEmbeds(List.of(EmbedCreateSpec.create()
                                                                                                         .withDescription(phrase(filter, text))
                                                                                                         .withColor(Color.WHITE)
                                                                                                         .withFields(hours.entrySet()
                                                                                                                          .stream()
                                                                                                                          .sorted(FilteredCommand.LONG_COMPARATOR)
                                                                                                                          .map(e -> Field.of("",
                                                                                                                                             "**" + e.getKey() +
                                                                                                                                             "h** avec **" +
                                                                                                                                             e.getValue() +
                                                                                                                                             "** messages",
                                                                                                                                             false))
                                                                                                                          .toList())))))
                    .then();
    }

    @Override
    public ApplicationCommandRequest getRequest() {
        return ApplicationCommandRequest.builder()
                                        .name(getName())
                                        .nameLocalizationsOrNull(Map.of("fr", "heures_les_plus_actives"))
                                        .description("Indique les heures de la journée les plus actives.")
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
        StringBuilder sb = new StringBuilder("Les **" + NB_WORDS + "** heures les plus actives ");
        super.phrase(filter, sb);
        text.ifPresent(s -> sb.append("contenant **").append(s).append("** "));
        sb.append(" sont ");

        return StringUtils.normalizeSpace(sb.toString());
    }
}