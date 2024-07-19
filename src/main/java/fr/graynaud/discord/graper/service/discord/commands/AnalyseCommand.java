package fr.graynaud.discord.graper.service.discord.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.channel.TextChannel;
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
                               .flatMap(channel -> {
                                   this.scraper.eatMessages(channel, Snowflake.of(Instant.now().minusSeconds(60 * duration)));

                                   return event.createFollowup().withEphemeral(true).withContent("Analyse lancée \uD83D\uDE80");
                               }))
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