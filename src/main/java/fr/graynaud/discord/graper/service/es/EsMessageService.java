package fr.graynaud.discord.graper.service.es;

import fr.graynaud.discord.graper.model.EsMessage;
import fr.graynaud.discord.graper.service.discord.commands.FilteredCommand;
import fr.graynaud.discord.graper.service.discord.commands.FilteredCommand.Filter;
import fr.graynaud.discord.graper.service.es.object.RecapResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.elasticsearch.core.query.ByQueryResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EsMessageService {

    void save(EsMessage message);

    Mono<ByQueryResponse> deleteByChannel(long channelId);

    Mono<Map<String, Long>> searchBestWord(String guildId, Filter filter, int nbWords);

    Mono<Long> searchText(String guildId, Filter filter, String word);

    Mono<Pair<Long, Map<String, Long>>> searchTextWho(String guildId, Filter filter, String text);

    Mono<Pair<Long, Map<String, Long>>> searchTextWhere(String guildId, Filter filter, String text);

    Mono<List<EsMessage>> searchLongestMessages(String guildId, Filter filter, int nbWords);

    Mono<EsMessage> searchRandomMessage(String guildId, Filter filter, Optional<String> text);

    Mono<RecapResult> recap(String guildId, Filter filter, int nbWords);
}
