package fr.graynaud.discord.graper.config.properties;

public class DiscordProperties {

    private String token;

    private Long guildTestId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getGuildTestId() {
        return guildTestId;
    }

    public void setGuildTestId(Long guildTestId) {
        this.guildTestId = guildTestId;
    }
}