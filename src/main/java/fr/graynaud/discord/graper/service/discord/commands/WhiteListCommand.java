package fr.graynaud.discord.graper.service.discord.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import fr.graynaud.discord.graper.service.es.EsGuildService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class WhiteListCommand implements SlashCommand {

    private final EsGuildService esGuildService;

    public WhiteListCommand(EsGuildService esGuildService) {
        this.esGuildService = esGuildService;
    }

    @Override
    public String getName() {
        return "whitelist";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        return event.deferReply()
                    .then(event.getOption("channel")
                               .flatMap(ApplicationCommandInteractionOption::getValue)
                               .map(ApplicationCommandInteractionOptionValue::asChannel)
                               .get()
                               .filter(TextChannel.class::isInstance)
                               .map(TextChannel.class::cast)
                               .flatMap(channel -> this.esGuildService.whitelistChannelAndAnalyse(channel)
                                                                      .flatMap(whitelisted -> event.createFollowup()
                                                                                                   .withEphemeral(true)
                                                                                                   .withContent("Channel <#" + channel.getId().asBigInteger() +
                                                                                                                (whitelisted ?
                                                                                                                 "> ajouté, ces messages vont être analysés." :
                                                                                                                 "> déjà inclu."))
                                                                                                   .then(Mono.just(channel))))
                               .then());
    }

    @Override
    public ApplicationCommandRequest getRequest() {
        return ApplicationCommandRequest.builder()
                                        .name(getName())
                                        .description("Inclu un channel dans l'analyse. (Analyse directement tous les anciens messages)")
                                        .defaultMemberPermissions("0")
                                        .options(List.of(ApplicationCommandOptionData.builder()
                                                                                     .name("channel")
                                                                                     .description("Le channel")
                                                                                     .type(7)
                                                                                     .required(true)
                                                                                     .build()))
                                        .build();
    }
}