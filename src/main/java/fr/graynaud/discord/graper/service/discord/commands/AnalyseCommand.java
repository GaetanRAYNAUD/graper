package fr.graynaud.discord.graper.service.discord.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import fr.graynaud.discord.graper.service.discord.messages.MessagesScraper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
public class AnalyseCommand implements SlashCommand {

    private final MessagesScraper scraper;

    public AnalyseCommand(MessagesScraper scraper) {
        this.scraper = scraper;
    }

    @Override
    public String getName() {
        return "analyse";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Long duration = event.getOption("duration")
                             .flatMap(ApplicationCommandInteractionOption::getValue)
                             .map(ApplicationCommandInteractionOptionValue::asLong)
                             .get();

        return event.deferReply()
                    .then(event.getOption("channel")
                               .flatMap(ApplicationCommandInteractionOption::getValue)
                               .map(ApplicationCommandInteractionOptionValue::asChannel)
                               .get()
                               .filter(TextChannel.class::isInstance)
                               .map(TextChannel.class::cast)
                               .flatMap(channel -> event.createFollowup()
                                                        .withContent("Analyse lancée pour <#" + channel.getId().asLong() + "> \uD83D\uDE80")
                                                        .flatMap(message -> this.scraper.eatMessages(channel,
                                                                                                     Snowflake.of(Instant.now().minusSeconds(60 * duration)))
                                                                                        .flatMap(whitelisted -> {
                                                                                            if (whitelisted) {
                                                                                                return channel.createMessage(MessageCreateSpec.builder()
                                                                                                                                              .messageReference(
                                                                                                                                                      message.getId())
                                                                                                                                              .content(
                                                                                                                                                      "Analyse terminée \uD83D\uDD25")
                                                                                                                                              .build());
                                                                                            } else {
                                                                                                return channel.createMessage(MessageCreateSpec.builder()
                                                                                                                                              .messageReference(message.getId())
                                                                                                                                              .content("Le channel n'est pas whitelisé 💀")
                                                                                                                                              .build());
                                                                                            }
                                                                                        })
                                                                                        .then())))
                    .then();
    }

    @Override
    public ApplicationCommandRequest getRequest() {
        return ApplicationCommandRequest.builder()
                                        .name(getName())
                                        .description("Lance l'analyse d'un channel immédiatement.")
                                        .defaultMemberPermissions("0")
                                        .options(List.of(ApplicationCommandOptionData.builder()
                                                                                     .name("channel")
                                                                                     .description("Le channel")
                                                                                     .type(7)
                                                                                     .required(true)
                                                                                     .build(), ApplicationCommandOptionData.builder()
                                                                                                                           .name("duration")
                                                                                                                           .nameLocalizationsOrNull(
                                                                                                                                   Map.of("fr", "durée"))
                                                                                                                           .description(
                                                                                                                                   "Durée en minute des messages à analyser")
                                                                                                                           .type(4)
                                                                                                                           .required(true)
                                                                                                                           .build()))
                                        .build();
    }
}