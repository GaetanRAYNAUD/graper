package fr.graynaud.discord.graper.repository;

import fr.graynaud.discord.graper.model.EsGuild;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;

public interface GuildRepository extends ReactiveElasticsearchRepository<EsGuild, String> {
}