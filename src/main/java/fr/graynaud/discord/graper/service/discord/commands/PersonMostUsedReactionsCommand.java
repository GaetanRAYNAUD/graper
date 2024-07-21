package fr.graynaud.discord.graper.service.discord.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateFields.Field;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Color;
import fr.graynaud.discord.graper.service.es.EsMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class PersonMostUsedReactionsCommand extends FilteredCommand implements SlashCommand {

    private static final int NB_WORDS = 5;

    private final EsMessageService esMessageService;

    public PersonMostUsedReactionsCommand(EsMessageService esMessageService) {
        this.esMessageService = esMessageService;
    }

    @Override
    public String getName() {
        return "personmostusedreactions";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Filter filter = prepare(event);
        return event.deferReply()
                    .then(this.esMessageService.searchMostReactionUsedBy(event.getInteraction().getGuildId().get().asString(), filter, NB_WORDS)
                                               .flatMap(words -> event.createFollowup()
                                                                      .withEmbeds(EmbedCreateSpec.create()
                                                                                                 .withDescription(phrase(filter))
                                                                                                 .withColor(Color.WHITE)
                                                                                                 .withFields(words.entrySet()
                                                                                                                  .stream()
                                                                                                                  .sorted(COMPARATOR)
                                                                                                                  .map(e -> Field.of("",
                                                                                                                                     e.getKey() + " avec **" + e.getValue() + "** réactions",
                                                                                                                                     false))
                                                                                                                  .toList()))))
                    .then();
    }

    @Override
    public ApplicationCommandRequest getRequest() {
        return ApplicationCommandRequest.builder()
                                        .name(getName())
                                        .nameLocalizationsOrNull(Map.of("fr", "personne_reaction_plus_utilisees"))
                                        .description("Remonte le top " + NB_WORDS + " des réactions les plus utilisées par une personne.")
                                        .options(List.of(
                                                ApplicationCommandOptionData.builder()
                                                                            .name("person")
                                                                            .nameLocalizationsOrNull(Map.of("fr", "qui"))
                                                                            .description("La personne")
                                                                            .type(6)
                                                                            .required(true)
                                                                            .build(),
                                                ApplicationCommandOptionData.builder()
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
                                                                            .build()))
                                        .build();
    }

    private String phrase(Filter filter) {
        StringBuilder sb = new StringBuilder("Les " + NB_WORDS + " réactions les plus utilisées par <@" + filter.getPerson().get() + "> ");
        filter.setPerson(Optional.empty());
        super.phrase(filter, sb);
        sb.append(" sont :");

        return StringUtils.normalizeSpace(sb.toString());
    }
}