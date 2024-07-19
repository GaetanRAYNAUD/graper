package fr.graynaud.discord.graper.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "graper")
public class GraperProperties {

    @NestedConfigurationProperty
    private DiscordProperties discord;

    @NestedConfigurationProperty
    private EsProperties es;

    public DiscordProperties getDiscord() {
        return discord;
    }

    public void setDiscord(DiscordProperties discord) {
        this.discord = discord;
    }

    public EsProperties getEs() {
        return es;
    }

    public void setEs(EsProperties es) {
        this.es = es;
    }
}