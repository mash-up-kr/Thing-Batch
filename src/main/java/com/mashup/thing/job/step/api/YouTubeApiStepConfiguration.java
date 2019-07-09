package com.mashup.thing.job.step.api;

import com.mashup.thing.config.jdbcquery.ApiStepQuery;
import com.mashup.thing.config.youtubeopenapi.YouTubeOpenApi;
import com.mashup.thing.job.step.ProviderConfiguration;
import com.mashup.thing.youtube.channel.ResponseChannelYouTuber;
import com.mashup.thing.youtube.search.ResponseSearchYouTuber;
import com.mashup.thing.youtuber.domain.YouTuber;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.web.reactive.function.client.WebClient;

import javax.sql.DataSource;
import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
public class YouTubeApiStepConfiguration {

    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final WebClient webClient;
    private final YouTubeOpenApi youTubeOpenApi;
    private final ApiStepQuery apiJdbcQuery;
    private final ProviderConfiguration providerConfiguration;

    private static final int CHUNK_SIZE = 200;

    @Bean
    public Step ApiStep() throws Exception {
        return stepBuilderFactory.get("ApiStep")
                .<YouTuber, YouTuber>chunk(CHUNK_SIZE)
                .reader(reqYouTubeApiReader())
                .processor(reqYouTubeApiProcessor())
                .writer(reqYouTubeApiWriter())
                .build();

    }

    @Bean
    public JdbcPagingItemReader<YouTuber> reqYouTubeApiReader() throws Exception {
        return new JdbcPagingItemReaderBuilder<YouTuber>()
                .pageSize(CHUNK_SIZE)
                .fetchSize(CHUNK_SIZE)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(YouTuber.class))
                .queryProvider(providerConfiguration.createSelectYuTuber(dataSource))
                .name("reqYouTubeApiReader")
                .build();
    }


    @Bean
    public Function<? super YouTuber, ? extends YouTuber> reqYouTubeApiProcessor() {
        return this::converting;

    }

    private YouTuber converting(YouTuber youTuber) {
        String channelId = searchYouTuberChannelId(youTuber);
        ResponseChannelYouTuber responseChannel = getYouTuberChannelInfo(channelId);
        youTuber.updateInfo(responseChannel.getItems().get(0));
        return youTuber;

    }

    private String searchYouTuberChannelId(YouTuber youTuber) {
        ResponseSearchYouTuber searchResponse = webClient.get()
                .uri(youTubeOpenApi.getSearchUrl(), youTubeOpenApi.getApiKey(), youTubeOpenApi.getSearchPart(),
                        youTuber.getName())
                .retrieve()
                .bodyToMono(ResponseSearchYouTuber.class)
                .block();

        return searchResponse.getItems().get(0).getSnippet().getChannelId();
    }

    private ResponseChannelYouTuber getYouTuberChannelInfo(String channelId) {
        ResponseChannelYouTuber channelResponse = webClient.get()
                .uri(youTubeOpenApi.getChannelUrl(), youTubeOpenApi.getApiKey(), youTubeOpenApi.getChannelPart(), channelId)
                .retrieve()
                .bodyToMono(ResponseChannelYouTuber.class)
                .block();
        return channelResponse;
    }

    @Bean
    public JdbcBatchItemWriter<YouTuber> reqYouTubeApiWriter() {
        return new JdbcBatchItemWriterBuilder<YouTuber>()
                .dataSource(dataSource)
                .sql(apiJdbcQuery.getUpdateQuery())
                .beanMapped()
                .build();
    }
}
