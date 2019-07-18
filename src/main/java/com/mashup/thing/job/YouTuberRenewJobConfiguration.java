package com.mashup.thing.job;

import com.mashup.thing.config.jdbcquery.YouTuberRenewQuery;
import com.mashup.thing.config.youtubeopenapi.YouTubeOpenApi;
import com.mashup.thing.youtube.channel.ResponseChannelYouTuber;
import com.mashup.thing.youtuber.domain.YouTuber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
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

@Slf4j
@RequiredArgsConstructor
@Configuration
public class YouTuberRenewJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final WebClient webClient;
    private final YouTubeOpenApi youTubeOpenApi;
    private final YouTuberRenewQuery youTuberRenewQuery;
    private final ProviderConfiguration providerConfiguration;
    private final QuotaCalculator quotaCalculator;

    private static final int CHUNK_SIZE = 300;

    @Bean
    public Job YouTuberRenewJob() throws Exception {
        return jobBuilderFactory.get("youTuberRenewJob")
                .start(youTuberRenewStep())
                .build();
    }

    @Bean
    public Step youTuberRenewStep() throws Exception {
        return stepBuilderFactory.get("youTuberRenewStep")
                .<YouTuber, YouTuber>chunk(CHUNK_SIZE)
                .reader(youTuberRenewReader())
                .processor(youTuberRenewProcessor())
                .writer(youTuberRenewWriter())
                .build();

    }

    @Bean
    public JdbcPagingItemReader<YouTuber> youTuberRenewReader() throws Exception {
        return new JdbcPagingItemReaderBuilder<YouTuber>()
                .pageSize(CHUNK_SIZE)
                .fetchSize(CHUNK_SIZE)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(YouTuber.class))
                .queryProvider(providerConfiguration.createSelectYuTuber(dataSource))
                .name("youTuberRenewReader")
                .build();
    }

    @Bean
    public Function<? super YouTuber, ? extends YouTuber> youTuberRenewProcessor() {
        return this::updateYouTuber;
    }

    private YouTuber updateYouTuber(YouTuber youTuber) {
        ResponseChannelYouTuber responseChannel = getYouTuberChannelInfo(youTuber.getChannelId());
        if (responseChannel.getItems().isEmpty()) {
            return youTuber;
        }

        youTuber.updateInfo(responseChannel.getItems().get(0));
        return youTuber;

    }

    private ResponseChannelYouTuber getYouTuberChannelInfo(String channelId) {
        quotaCalculator.increaseQuota();
        if (quotaCalculator.isOverQuota()) {
            youTubeOpenApi.setApiKey(quotaCalculator.nextApiKey());
        }

        ResponseChannelYouTuber channelResponse = webClient.get()
                .uri(youTubeOpenApi.getChannelUrl(), youTubeOpenApi.getApiKey(),
                        youTubeOpenApi.getChannelPart(), channelId)
                .retrieve()
                .bodyToMono(ResponseChannelYouTuber.class)
                .block();
        return channelResponse;
    }


    @Bean
    public JdbcBatchItemWriter<YouTuber> youTuberRenewWriter() {
        return new JdbcBatchItemWriterBuilder<YouTuber>()
                .dataSource(dataSource)
                .sql(youTuberRenewQuery.getYouTuberUpdateQuery())
                .beanMapped()
                .build();
    }

}
