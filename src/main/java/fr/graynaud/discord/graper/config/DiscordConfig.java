package fr.graynaud.discord.graper.config;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.rest.RestClient;
import fr.graynaud.discord.graper.config.properties.GraperProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscordConfig {

    @Bean
    public GatewayDiscordClient gatewayDiscordClient(GraperProperties properties) {
        return DiscordClientBuilder.create(properties.getDiscord().getToken()).build()
                                   .gateway()
                                   .setInitialPresence(ignore -> ClientPresence.online(ClientActivity.listening("vos conneries")))
                                   .login()
                                   .block();
    }

    @Bean
    public RestClient discordRestClient(GatewayDiscordClient client) {
        return client.getRestClient();
    }
}
