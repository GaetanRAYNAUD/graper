package fr.graynaud.discord.graper.config;

import discord4j.rest.RestClient;
import fr.graynaud.discord.graper.config.properties.GraperProperties;
import fr.graynaud.discord.graper.service.discord.commands.SlashCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GlobalCommandRegistrar implements ApplicationRunner {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final RestClient client;

    private final List<SlashCommand> commands;

    private final GraperProperties properties;

    public GlobalCommandRegistrar(RestClient client, List<SlashCommand> commands, GraperProperties properties) {
        this.client = client;
        this.commands = commands;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (this.properties.getDiscord().getGuildTestId() != null) {
            this.client.getApplicationId()
                       .flatMapMany(id -> this.client.getApplicationService()
                                                     .bulkOverwriteGuildApplicationCommand(id, this.properties.getDiscord().getGuildTestId(),
                                                                                           this.commands.stream().map(SlashCommand::getRequest).toList()))
                       .collectList()
                       .doOnNext(ignore -> LOGGER.debug("Successfully registered Guild Test Commands"))
                       .doOnError(e -> LOGGER.error("Failed to register Guild Test commands", e))
                       .subscribe();
        } else {
            this.client.getApplicationId()
                       .flatMapMany(id -> this.client.getApplicationService()
                                                     .bulkOverwriteGlobalApplicationCommand(id, this.commands.stream().map(SlashCommand::getRequest).toList()))
                       .collectList()
                       .doOnNext(ignore -> LOGGER.debug("Successfully registered Global Commands"))
                       .doOnError(e -> LOGGER.error("Failed to register Global commands", e))
                       .subscribe();
        }
    }
}