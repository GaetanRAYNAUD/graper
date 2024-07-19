package fr.graynaud.discord.graper.service.es;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.TextChannel;
import fr.graynaud.discord.graper.model.EsGuild;
import fr.graynaud.discord.graper.repository.GuildRepository;
import fr.graynaud.discord.graper.service.discord.messages.MessagesScraper;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
public class EsGuildServiceImpl implements EsGuildService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EsGuildServiceImpl.class);

    private final GuildRepository repository;

    private final EsMessageService esMessageService;

    private final MessagesScraper scraper;

    public EsGuildServiceImpl(GuildRepository repository, EsMessageService esMessageService, @Lazy MessagesScraper scraper,
                              GatewayDiscordClient discordClient) {
        this.repository = repository;
        this.esMessageService = esMessageService;
        this.scraper = scraper;

        discordClient.on(GuildCreateEvent.class, this::upsertGuilds).subscribe();
    }

    @Override
    public Flux<EsGuild> findAll() {
        return this.repository.findAll();
    }

    @Override
    public Mono<EsGuild> findById(String id) {
        return this.repository.findById(id);
    }

    @Override
    public Mono<EsGuild> find(Guild guild) {
        return findById(guild.getId().asString());
    }

    @Override
    public Mono<EsGuild> save(EsGuild guild) {
        return this.repository.save(guild);
    }

    @Override
    public Mono<EsGuild> create(Guild guild) {
        return this.repository.save(new EsGuild(guild));
    }

    @Override
    public Mono<Boolean> whitelistChannel(Guild guild, TextChannel channel) {
        return this.repository.findById(guild.getId().asString()).flatMap(g -> {
            if (g.whitelistChannel(channel)) {
                return this.repository.save(g).then(Mono.just(true));
            } else {
                return Mono.just(false);
            }
        });
    }

    @Override
    public Mono<Boolean> whitelistChannelAndAnalyse(TextChannel channel) {
        return channel.getGuild().flatMap(guild -> whitelistChannelAndAnalyse(guild, channel));
    }

    @Override
    public Mono<Boolean> whitelistChannelAndAnalyse(Guild guild, TextChannel channel) {
        return whitelistChannel(guild, channel).flatMap(blacklisted -> {
            if (BooleanUtils.toBoolean(blacklisted)) {
                this.scraper.eatMessages(channel, Snowflake.of(Instant.EPOCH));

                return Mono.just(true);
            } else {
                return Mono.just(false);
            }
        });
    }

    @Override
    public Mono<Boolean> blacklistChannel(Guild guild, TextChannel channel) {
        return this.repository.findById(guild.getId().asString()).flatMap(g -> {
            if (g.blacklistChannel(channel)) {
                return this.repository.save(g).then(Mono.just(true));
            } else {
                return Mono.just(false);
            }
        });
    }

    @Override
    public Mono<Boolean> blacklistChannelAndDelete(Guild guild, TextChannel channel) {
        return blacklistChannel(guild, channel).flatMap(blacklisted -> {
            if (BooleanUtils.toBoolean(blacklisted)) {
                return this.esMessageService.deleteByChannel(channel.getId().asLong()).then(Mono.just(true));
            } else {
                return Mono.just(false);
            }
        });
    }

    @Override
    public Mono<Boolean> blacklistChannelAndDelete(TextChannel channel) {
        return channel.getGuild().flatMap(guild -> blacklistChannelAndDelete(guild, channel));
    }

    private Mono<Void> upsertGuilds(GuildCreateEvent event) {
        return find(event.getGuild()).switchIfEmpty(create(event.getGuild())).then();
    }
}
