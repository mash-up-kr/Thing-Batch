package com.mashup.thing.job.step.api;

import com.mashup.thing.config.jdbcquery.ApiStepQuery;
import com.mashup.thing.config.youtubeopenapi.YouTubeOpenApi;
import com.mashup.thing.job.step.ProviderConfiguration;
import com.mashup.thing.youtube.channel.ResponseChannelYouTuber;
import com.mashup.thing.youtuber.domain.YouTuber;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.web.reactive.function.client.WebClient;

import javax.sql.DataSource;
import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
public class YouTuberApiStepConfiguration {

    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final WebClient webClient;
    private final YouTubeOpenApi youTubeOpenApi;
    private final ApiStepQuery apiJdbcQuery;
    private final ProviderConfiguration providerConfiguration;

    private static final int CHUNK_SIZE = 200;

    @Bean
    @JobScope
    public Step youTuberApiStep(@Value("#{jobParameters[requestDate]}") String requestDate) throws Exception {
        return stepBuilderFactory.get("youTuberApiStep")
                .<YouTuber, YouTuber>chunk(CHUNK_SIZE)
                .reader(reqYouTuberApiReader())
                .processor(reqYouTuberApiProcessor())
                .writer(reqYouTuberApiWriter())
                .build();

    }

    @Bean
    public JdbcPagingItemReader<YouTuber> reqYouTuberApiReader() throws Exception {
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
    public Function<? super YouTuber, ? extends YouTuber> reqYouTuberApiProcessor() {
        return this::updateYouTuber;

    }

    private YouTuber updateYouTuber(YouTuber youTuber) {
        ResponseChannelYouTuber responseChannel = getYouTuberChannelInfo(youTuber.getChannelId());
        youTuber.updateInfo(responseChannel.getItems().get(0));

        return youTuber;

    }

    private ResponseChannelYouTuber getYouTuberChannelInfo(String channelId) {
        ResponseChannelYouTuber channelResponse = webClient.get()
                .uri(youTubeOpenApi.getChannelUrl(), youTubeOpenApi.getApiKey(),
                        youTubeOpenApi.getChannelPart(), channelId)
                .retrieve()
                .bodyToMono(ResponseChannelYouTuber.class)
                .block();
        return channelResponse;
    }

    @Bean
    public JdbcBatchItemWriter<YouTuber> reqYouTuberApiWriter() {
        return new JdbcBatchItemWriterBuilder<YouTuber>()
                .dataSource(dataSource)
                .sql(apiJdbcQuery.getYouTuberUpdateQuery())
                .beanMapped()
                .build();
    }


}


