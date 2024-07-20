package fr.graynaud.discord.graper.service.discord.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Color;
import fr.graynaud.discord.graper.model.EsMessage;
import fr.graynaud.discord.graper.service.es.EsMessageService;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Component
public class RandomMessageCommand extends FilteredCommand implements SlashCommand {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locale.FRANCE);

    private final EsMessageService esMessageService;

    public RandomMessageCommand(EsMessageService esMessageService) {
        this.esMessageService = esMessageService;
    }

    @Override
    public String getName() {
        return "randommessage";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Filter filter = prepare(event);
        Optional<String> text = event.getOption("text")
                                     .flatMap(ApplicationCommandInteractionOption::getValue)
                                     .map(ApplicationCommandInteractionOptionValue::asString);

        return event.deferReply()
                    .then(this.esMessageService.searchRandomMessage(event.getInteraction().getGuildId().get().asString(), filter, text)
                                               .flatMap(message -> event.createFollowup()
                                                                        .withEmbeds(List.of(EmbedCreateSpec.create()
                                                                                                           .withTitle("Voici un message aléatoire")
                                                                                                           .withDescription(mapMessage(message))
                                                                                                           .withColor(Color.WHITE)))))
                    .then();
    }

    @Override
    public ApplicationCommandRequest getRequest() {
        return ApplicationCommandRequest.builder()
                                        .name(getName())
                                        .nameLocalizationsOrNull(Map.of("fr", "message_aleatoire"))
                                        .description("Remonte un message aléatoire.")
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

    private String mapMessage(EsMessage message) {
        return "Écrit par <@" + message.getAuthorId() + "> le " +
               DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(message.getTimestamp()).atZone(ZoneId.of("Europe/Paris"))) +
               " dans <#" + message.getChannelId() + "> avec " + message.getContentLength() + " caractères :\nhttps://discord.com/channels/" +
               message.getGuildId() + "/" + message.getChannelId() + "/" + message.getId() + "\n\n";
    }
}