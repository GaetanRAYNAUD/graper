package fr.graynaud.discord.graper.service.es;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.TextChannel;
import fr.graynaud.discord.graper.model.EsGuild;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EsGuildService {

    Flux<EsGuild> findAll();

    Mono<EsGuild> findById(String id);

    Mono<EsGuild> find(Guild guild);

    Mono<EsGuild> save(EsGuild guild);

    Mono<Boolean> whitelistChannelAndAnalyse(TextChannel channel);

    Mono<EsGuild> create(Guild guild);

    Mono<Boolean> whitelistChannel(Guild guild, TextChannel channel);

    Mono<Boolean> whitelistChannelAndAnalyse(Guild guild, TextChannel channel);

    Mono<Boolean> blacklistChannel(Guild guild, TextChannel channel);

    Mono<Boolean> blacklistChannelAndDelete(Guild guild, TextChannel channel);

    Mono<Boolean> blacklistChannelAndDelete(TextChannel channel);
}
