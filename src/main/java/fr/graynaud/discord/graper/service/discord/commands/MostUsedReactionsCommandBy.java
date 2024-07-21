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

@Component
public class MostUsedReactionsCommandBy extends FilteredCommand implements SlashCommand {

    private static final int NB_WORDS = 5;

    private final EsMessageService esMessageService;

    public MostUsedReactionsCommandBy(EsMessageService esMessageService) {
        this.esMessageService = esMessageService;
    }

    @Override
    public String getName() {
        return "mostusedreactionby";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Filter filter = prepare(event);
        String text = event.getOption("reaction")
                           .flatMap(ApplicationCommandInteractionOption::getValue)
                           .map(ApplicationCommandInteractionOptionValue::asString)
                           .get();

        return event.deferReply()
                    .then(this.esMessageService.searchReactionMostUsedBy(event.getInteraction().getGuildId().get().asString(), filter, text, NB_WORDS)
                                               .flatMap(users -> event.createFollowup()
                                                                         .withEmbeds(EmbedCreateSpec.create()
                                                                                                    .withDescription(phrase(filter, text))
                                                                                                    .withColor(Color.WHITE)
                                                                                                    .withFields(users.entrySet()
                                                                                                                     .stream()
                                                                                                                     .sorted(COMPARATOR)
                                                                                                                     .map(e -> Field.of("",
                                                                                                                                        "<@" + e.getKey() + "> avec **" + e.getValue() + "** utilisations",
                                                                                                                                        false))
                                                                                                                     .toList()))))
                    .then();
    }

    @Override
    public ApplicationCommandRequest getRequest() {
        return ApplicationCommandRequest.builder()
                                        .name(getName())
                                        .nameLocalizationsOrNull(Map.of("fr", "reaction_plus_utilisee_par"))
                                        .description("Remonte le top " + NB_WORDS + " des personnes qui utilise une réaction.")
                                        .options(ListUtils.union(List.of(ApplicationCommandOptionData.builder()
                                                                                                     .name("reaction")
                                                                                                     .nameLocalizationsOrNull(Map.of("fr", "reaction"))
                                                                                                     .description("La réaction'")
                                                                                                     .type(3)
                                                                                                     .required(true)
                                                                                                     .build()),
                                                                 getOptions()))
                                        .build();
    }

    private String phrase(Filter filter, String text) {
        StringBuilder sb = new StringBuilder("Les " + NB_WORDS + " personnes qui utilisent le plus " + text + " ");
        super.phrase(filter, sb);
        sb.append(" sont :");

        return StringUtils.normalizeSpace(sb.toString());
    }
}