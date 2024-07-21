package fr.graynaud.discord.graper.service.discord.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateFields.Field;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Color;
import fr.graynaud.discord.graper.model.EsMessage;
import fr.graynaud.discord.graper.service.es.EsMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;

@Component
public class MessageMostMentionsCommand extends FilteredCommand implements SlashCommand {

    private static final int NB_WORDS = 5;

    private final EsMessageService esMessageService;

    public MessageMostMentionsCommand(EsMessageService esMessageService) {
        this.esMessageService = esMessageService;
    }

    @Override
    public String getName() {
        return "messagemostping";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Filter filter = prepare(event);
        return event.deferReply()
                    .then(this.esMessageService.searchMessageMostMentions(event.getInteraction().getGuildId().get().asString(), filter, NB_WORDS)
                                               .flatMap(messages -> event.createFollowup()
                                                                         .withEmbeds(EmbedCreateSpec.create()
                                                                                                    .withDescription(phrase(filter))
                                                                                                    .withColor(Color.WHITE)
                                                                                                    .withFields(messages.stream()
                                                                                                                        .map(this::mapField)
                                                                                                                        .toList()))))
                    .then();
    }

    @Override
    public ApplicationCommandRequest getRequest() {
        return ApplicationCommandRequest.builder()
                                        .name(getName())
                                        .nameLocalizationsOrNull(Map.of("fr", "message_plus_ping"))
                                        .description("Remonte le top " + NB_WORDS + " des messages avec le plus de mentions.")
                                        .options(getOptions())
                                        .build();
    }

    private String phrase(Filter filter) {
        StringBuilder sb = new StringBuilder("Les " + NB_WORDS + " messages avec le plus de mentions ");
        super.phrase(filter, sb);
        sb.append(" sont :");

        return StringUtils.normalizeSpace(sb.toString());
    }

    private Field mapField(EsMessage message) {
        return Field.of("", "Écrit par <@" + message.getAuthorId() + "> le " +
                            DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(message.getTimestamp()).atZone(ZoneId.of("Europe/Paris"))) + " dans <#" +
                            message.getChannelId() + "> avec " + message.getNbMentions() + " mentions :\nhttps://discord.com/channels/" +
                            message.getGuildId() + "/" + message.getChannelId() + "/" +
                            message.getId() + "\n\n", false);
    }
}