package fr.graynaud.discord.graper.config;

import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import fr.graynaud.discord.graper.config.properties.GraperProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchConfiguration;

@Configuration
public class EsConfig extends ReactiveElasticsearchConfiguration {

    private final GraperProperties properties;

    public EsConfig(GraperProperties properties) {
        this.properties = properties;
    }

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder().connectedTo(this.properties.getEs().getUrl()).build();
    }

    @Override
    public JsonpMapper jsonpMapper() {
        return new JacksonJsonpMapper(JsonMapper.builder()
                                                .findAndAddModules()
                                                .configure(SerializationFeature.INDENT_OUTPUT, false)
                                                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                                                .serializationInclusion(Include.NON_EMPTY)
                                                .build());
    }
}