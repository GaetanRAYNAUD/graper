package fr.graynaud.discord.graper.service.discord.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
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
public class TextCommand extends FilteredCommand implements SlashCommand {

    private final EsMessageService esMessageService;

    public TextCommand(EsMessageService esMessageService) {
        this.esMessageService = esMessageService;
    }

    @Override
    public String getName() {
        return "text";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Filter filter = prepare(event);
        String text = event.getOption("text")
                           .flatMap(ApplicationCommandInteractionOption::getValue)
                           .map(ApplicationCommandInteractionOptionValue::asString)
                           .get();

        return event.deferReply()
                    .then(this.esMessageService.searchText(event.getInteraction().getGuildId().get().asString(), filter, text)
                                               .flatMap(nb -> event.createFollowup()
                                                                   .withEmbeds(List.of(EmbedCreateSpec.create()
                                                                                                      .withDescription(phrase(filter, text, nb))
                                                                                                      .withColor(Color.WHITE)))))
                    .then();
    }

    @Override
    public ApplicationCommandRequest getRequest() {
        return ApplicationCommandRequest.builder()
                                        .name(getName())
                                        .nameLocalizationsOrNull(Map.of("fr", "texte"))
                                        .description("Indique dans combien de messages un texte a été utilisé.")
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

    private String phrase(Filter filter, String text, long nb) {
        StringBuilder sb = new StringBuilder("Le texte `" + text + "` a été utilisé dans **" + nb + "** messages ");
        super.phrase(filter, sb);

        return StringUtils.normalizeSpace(sb.toString());
    }
}