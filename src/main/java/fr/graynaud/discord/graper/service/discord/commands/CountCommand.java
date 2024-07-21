package fr.graynaud.discord.graper.service.discord.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
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
public class CountCommand extends FilteredCommand implements SlashCommand {

    private final EsMessageService esMessageService;

    public CountCommand(EsMessageService esMessageService) {
        this.esMessageService = esMessageService;
    }

    @Override
    public String getName() {
        return "count";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Filter filter = prepare(event);

        return event.deferReply()
                    .then(this.esMessageService.count(event.getInteraction().getGuildId().get().asString(), filter)
                                               .flatMap(nb -> event.createFollowup()
                                                                   .withEmbeds(List.of(EmbedCreateSpec.create()
                                                                                                      .withDescription(phrase(filter, nb))
                                                                                                      .withColor(Color.WHITE)))))
                    .then();
    }

    @Override
    public ApplicationCommandRequest getRequest() {
        return ApplicationCommandRequest.builder()
                                        .name(getName())
                                        .nameLocalizationsOrNull(Map.of("fr", "compter"))
                                        .description("Indique combien de messages ont été envoyés.")
                                        .options(getOptions())
                                        .build();
    }

    private String phrase(Filter filter, long nb) {
        StringBuilder sb = new StringBuilder("**" + nb + "** messages ont été envoyés ");
        super.phrase(filter, sb);

        return StringUtils.normalizeSpace(sb.toString());
    }
}