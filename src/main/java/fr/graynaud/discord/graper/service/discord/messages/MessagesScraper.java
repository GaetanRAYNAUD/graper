package fr.graynaud.discord.graper.service.discord.messages;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import fr.graynaud.discord.graper.model.EsGuild;
import fr.graynaud.discord.graper.service.discord.commands.FilteredCommand.Filter;
import reactor.core.publisher.Mono;

public interface MessagesScraper {

    Mono<Boolean> eatMessages(TextChannel channel, Snowflake from);

    void upsertGuildChannels(EsGuild guild);

    void doRecap(Message id, TextChannel channel, Filter filter, int nbWords);
}
