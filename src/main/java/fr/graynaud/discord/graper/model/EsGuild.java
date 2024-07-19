package fr.graynaud.discord.graper.model;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.TextChannel;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
import java.util.List;

@Document(indexName = "guild")
public class EsGuild {

    @Id
    @Field(value = "id", type = FieldType.Keyword)
    private String id;

    @Field(name = "name")
    private String name;

    @Field(name = "whitelisted_channels_ids", type = FieldType.Keyword)
    private List<String> whitelistedChannelsIds;

    public EsGuild() {
    }

    public EsGuild(Guild guild) {
        this.id = guild.getId().asString();
        this.name = guild.getName();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getWhitelistedChannelsIds() {
        return whitelistedChannelsIds;
    }

    public boolean whitelistChannel(TextChannel textChannel) {
        if (this.whitelistedChannelsIds == null) {
            this.whitelistedChannelsIds = new ArrayList<>();
        }

        return this.whitelistedChannelsIds.add(textChannel.getId().asString());
    }

    public boolean blacklistChannel(TextChannel textChannel) {
        if (this.whitelistedChannelsIds == null) {
            return false;
        }

        return this.whitelistedChannelsIds.remove(textChannel.getId().asString());
    }

    public void setWhitelistedChannelsIds(List<String> whitelistedChannelsIds) {
        this.whitelistedChannelsIds = whitelistedChannelsIds;
    }
}
