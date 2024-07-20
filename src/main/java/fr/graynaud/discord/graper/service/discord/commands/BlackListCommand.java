package fr.graynaud.discord.graper.service.discord.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import fr.graynaud.discord.graper.service.es.EsGuildService;
import fr.graynaud.discord.graper.service.es.EsMessageService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class BlackListCommand implements SlashCommand {

    private final EsGuildService esGuildService;

    private final EsMessageService esMessageService;

    public BlackListCommand(EsGuildService esGuildService, EsMessageService esMessageService) {
        this.esGuildService = esGuildService;
        this.esMessageService = esMessageService;
    }

    @Override
    public String getName() {
        return "blacklist";
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
                               .flatMap(c -> this.esGuildService.blacklistChannel(c)
                                                                .flatMap(b -> event.createFollowup()
                                                                                                   .withEphemeral(true)
                                                                                   .withContent("Channel <#" + c.getId().asBigInteger() +
                                                                                                (b ?
                                                                                                 "> exclu, ses messages vont être supprimés 💀." :
                                                                                                                 "> déjà exclu."))
                                                                                   .flatMap(message -> {
                                                                                       if (b) {
                                                                                           return this.esMessageService.deleteByChannel(c.getId().asLong())
                                                                                                                       .flatMap(r -> c.createMessage(
                                                                                                                                       MessageCreateSpec.builder()
                                                                                                                                                        .messageReference(message.getId())
                                                                                                                                                        .content("Suppression terminée 💀")
                                                                                                                                                        .build()));
                                                                                       } else {
                                                                                           return Mono.empty();
                                                                                       }
                                                                                   })
                                                                                   .then()))
                               .then());
    }

    @Override
    public ApplicationCommandRequest getRequest() {
        return ApplicationCommandRequest.builder()
                                        .name(getName())
                                        .description("Exclure un channel de l'analyse. (Supprime tous les messages analysés)")
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