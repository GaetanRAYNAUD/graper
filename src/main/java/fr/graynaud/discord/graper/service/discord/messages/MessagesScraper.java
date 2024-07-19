package fr.graynaud.discord.graper.service.discord.messages;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.TextChannel;
import fr.graynaud.discord.graper.model.EsGuild;

public interface MessagesScraper {

    void eatMessages(TextChannel channel, Snowflake from);

    void upsertGuildChannels(EsGuild guild);
}
