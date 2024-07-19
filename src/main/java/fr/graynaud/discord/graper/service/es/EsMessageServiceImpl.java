package fr.graynaud.discord.graper.service.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._helpers.bulk.BulkIngester;
import co.elastic.clients.elasticsearch._helpers.bulk.BulkListener;
import co.elastic.clients.elasticsearch._types.ScriptLanguage;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.SignificantStringTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.SignificantTextAggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch._types.aggregations.ValueCountAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.json.JsonData;
import fr.graynaud.discord.graper.model.EsMessage;
import fr.graynaud.discord.graper.service.discord.commands.FilteredCommand.Filter;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.ByQueryResponse;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class EsMessageServiceImpl implements EsMessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EsMessageServiceImpl.class);

    private static final String INDEX_NAME = "messages";

    private static final DateTimeFormatter INDEX_NAME_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private final ReactiveElasticsearchOperations operations;

    private final BulkIngester<Void> bulkIngester;

    protected final ElasticsearchConverter converter;

    public EsMessageServiceImpl(ReactiveElasticsearchOperations operations, ElasticsearchClient elasticsearchClient, ElasticsearchConverter converter) {
        this.operations = operations;
        this.converter = converter;

        BulkListener<Void> listener = new BulkListener<>() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request, List<Void> voids) {

            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, List<Void> voids, BulkResponse response) {
                if (response.errors()) {
                    for (BulkResponseItem item : response.items()) {
                        if (item.error() != null) {
                            LOGGER.error("An error occurred while indexing {}({}): {}", item.id(), item.get(), item.error());
                        }
                    }
                }

            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, List<Void> voids, Throwable failure) {
                LOGGER.error("An error occurred while bulking: {}", failure.getMessage(), failure);
            }
        };

        this.bulkIngester = BulkIngester.of(b -> b.client(elasticsearchClient).flushInterval(5, TimeUnit.SECONDS).maxOperations(500).listener(listener));
    }

    @Override
    public void save(EsMessage message) {
        this.bulkIngester.add(BulkOperation.of(
                b -> b.index(bb -> bb.id(String.valueOf(message.getId())).document(this.converter.mapObject(message)).index(getIndexName(message)))));
    }

    @Override
    public Mono<ByQueryResponse> deleteByChannel(long channelId) {
        return this.operations.delete(DeleteQuery.builder(CriteriaQuery.builder(Criteria.where("channel_id").in(channelId)).build()).build(), EsMessage.class,
                                      IndexCoordinates.of(INDEX_NAME + "-*"));
    }

    @Override
    public Mono<Map<String, Long>> searchBestWord(String guildId, Filter filter, int nbWords) {
        Query.Builder builder = prepare(guildId, filter);
        Aggregation aggregation = SignificantTextAggregation.of(b -> b.field("content")
                                                                      .size(nbWords)
                                                                      .scriptHeuristic(bb -> bb.script(bbb -> bbb.inline(
                                                                              bbbb -> bbbb.lang(ScriptLanguage.Painless).source("params._subset_freq + 0.0")))))
                                                            ._toAggregation();

        NativeQuery nativeQuery = NativeQuery.builder()
                                             .withQuery(builder.build())
                                             .withMaxResults(0)
                                             .withAggregation("words", aggregation)
                                             .build();

        return this.operations.searchForHits(nativeQuery, EsMessage.class)
                              .map(hits -> ((ElasticsearchAggregations) hits.getAggregations())
                                      .get("words")
                                      .aggregation()
                                      .getAggregate()
                                      .sigsterms()
                                      .buckets()
                                      .array()
                                      .stream()
                                      .collect(Collectors.toMap(SignificantStringTermsBucket::key, SignificantStringTermsBucket::docCount)));
    }

    @Override
    public Mono<Long> searchText(String guildId, Filter filter, String text) {
        Query.Builder builder = prepareText(guildId, filter, text);

        Aggregation aggregation = ValueCountAggregation.of(b -> b.field("id"))._toAggregation();

        NativeQuery nativeQuery = NativeQuery.builder()
                                             .withQuery(builder.build())
                                             .withMaxResults(0)
                                             .withAggregation("count", aggregation)
                                             .build();

        return this.operations.searchForHits(nativeQuery, EsMessage.class)
                              .map(hits -> ((ElasticsearchAggregations) hits.getAggregations())
                                      .get("count")
                                      .aggregation()
                                      .getAggregate()
                                      .valueCount()
                                      .value())
                              .map(Double::longValue);
    }

    @Override
    public Mono<Pair<Long, Map<String, Long>>> searchTextWho(String guildId, Filter filter, String text) {
        Query.Builder builder = prepareText(guildId, filter, text);
        builder.matchPhrase(b -> b.query(text).field("content"));

        NativeQuery nativeQuery = NativeQuery.builder()
                                             .withQuery(builder.build())
                                             .withMaxResults(0)
                                             .withAggregation("count", ValueCountAggregation.of(b -> b.field("id"))._toAggregation())
                                             .withAggregation("author", TermsAggregation.of(b -> b.field("author_id"))._toAggregation())
                                             .build();

        return this.operations.searchForHits(nativeQuery, EsMessage.class)
                              .map(hits -> ((ElasticsearchAggregations) hits.getAggregations()))
                              .map(aggregations -> {
                                  long total = (long) aggregations.get("count").aggregation().getAggregate().valueCount().value();
                                  Map<String, Long> authors = aggregations.get("author").aggregation().getAggregate().sterms().buckets().array().stream()
                                                                          .collect(Collectors.toMap(b -> b.key().stringValue(), StringTermsBucket::docCount));

                                  return Pair.of(total, authors);
                              });
    }

    private String getIndexName(EsMessage message) {
        return INDEX_NAME + "-" + LocalDateTime.ofInstant(Instant.ofEpochMilli(message.getTimestamp()), ZoneOffset.UTC).format(INDEX_NAME_DATETIME_FORMATTER);
    }

    private Query.Builder prepare(String guildId, Filter filter) {
        Query.Builder builder = new Query.Builder();
        builder.term(bb -> bb.field("guild_id").value(guildId));

        filter.getChannel().ifPresent(s -> builder.term(bb -> bb.field("channel_id").value(s)));
        filter.getPerson().ifPresent(s -> builder.term(bb -> bb.field("author_id").value(s)));

        if (filter.getStart().isPresent() || filter.getEnd().isPresent()) {
            builder.range(bb -> {
                bb.field("@timestamp");
                filter.getStart().ifPresent(localDate -> bb.gte(JsonData.of(localDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli())));
                filter.getEnd().ifPresent(localDate -> bb.lt(JsonData.of(localDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli())));

                return bb;
            });
        }

        return builder;
    }

    private Query.Builder prepareText(String guildId, Filter filter, String text) {
        Query.Builder builder = prepare(guildId, filter);
        builder.matchPhrase(b -> b.query(text).field("content"));

        return builder;
    }
}
