package fr.graynaud.discord.graper.repository;

import fr.graynaud.discord.graper.model.EsMessage;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;

public interface MessageRepository extends ReactiveElasticsearchRepository<EsMessage, Long> {
}