package fr.graynaud.discord.graper.service.discord.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateFields.Field;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Color;
import fr.graynaud.discord.graper.service.es.EsMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class WordsCommand extends FilteredCommand implements SlashCommand {

    private static final int NB_WORDS = 5;

    private final EsMessageService esMessageService;

    public WordsCommand(EsMessageService esMessageService) {
        this.esMessageService = esMessageService;
    }

    @Override
    public String getName() {
        return "words";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Filter filter = prepare(event);
        return event.deferReply()
                    .then(this.esMessageService.searchBestWord(event.getInteraction().getGuildId().get().asString(), filter, NB_WORDS)
                                               .flatMap(words -> event.createFollowup()
                                                                      .withEmbeds(List.of(EmbedCreateSpec.create()
                                                                                                         .withDescription(phrase(filter))
                                                                                                         .withColor(Color.WHITE)
                                                                                                         .withFields(words.entrySet()
                                                                                                                          .stream()
                                                                                                                          .sorted(COMPARATOR)
                                                                                                                          .map(e -> Field.of(e.getKey(),
                                                                                                                                             String.valueOf(
                                                                                                                                                     e.getValue()),
                                                                                                                                             false))
                                                                                                                          .toList())))))
                    .then();
    }

    @Override
    public ApplicationCommandRequest getRequest() {
        return ApplicationCommandRequest.builder()
                                        .name(getName())
                                        .nameLocalizationsOrNull(Map.of("fr", "mots"))
                                        .description("Remonte le top " + NB_WORDS + " des mots les plus utilisés.")
                                        .options(getOptions())
                                        .build();
    }

    private String phrase(Filter filter) {
        StringBuilder sb = new StringBuilder("Les " + NB_WORDS + " mots les plus utilisés ");
        super.phrase(filter, sb);
        sb.append(" sont :");

        return StringUtils.normalizeSpace(sb.toString());
    }
}