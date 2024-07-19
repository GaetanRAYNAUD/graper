package fr.graynaud.discord.graper.service.es;

import fr.graynaud.discord.graper.model.EsMessage;
import fr.graynaud.discord.graper.service.discord.commands.FilteredCommand.Filter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.elasticsearch.core.query.ByQueryResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface EsMessageService {

    void save(EsMessage message);

    Mono<ByQueryResponse> deleteByChannel(long channelId);

    Mono<Map<String, Long>> searchBestWord(String guildId, Filter filter, int nbWords);

    Mono<Long> searchText(String guildId, Filter filter, String word);

    Mono<Pair<Long, Map<String, Long>>> searchTextWho(String guildId, Filter filter, String text);
}
