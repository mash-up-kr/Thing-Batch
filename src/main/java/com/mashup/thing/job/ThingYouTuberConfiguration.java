package com.mashup.thing.job;

import com.mashup.thing.config.jdbcquery.JdbcQuery;
import com.mashup.thing.config.youtubeopenapi.YouTubeOpenApi;
import com.mashup.thing.raking.Raking;
import com.mashup.thing.youtube.channel.ResponseChannelYouTuber;
import com.mashup.thing.youtube.search.ResponseSearchYouTuber;
import com.mashup.thing.youtuber.domain.YouTuber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.*;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.web.reactive.function.client.WebClient;

import javax.sql.DataSource;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ThingYouTuberConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final WebClient webClient;
    private final YouTubeOpenApi youTubeOpenApi;
    private final JdbcQuery jdbcQuery;
    private static final int chunkSize = 200;

    @Bean
    public Job thingYouTubeApiJob() throws Exception {
        return jobBuilderFactory.get("thingYouTuberJob")
                .start(reqYouTubeApiStep())
                .next(youTuberRakingStep())
                .build();
    }

    @Bean
    public Step youTuberRakingStep() throws Exception {
        return stepBuilderFactory.get("youTuberRakingStep")
                .<YouTuber, Raking>chunk(chunkSize)
                .reader(youTuberRakingReader())
                .processor(youTuberRakingProcessor())
                .writer(customItemWriter())
                .build();
    }

    @Bean
    public JdbcPagingItemReader<YouTuber> youTuberRakingReader() throws Exception {
        return new JdbcPagingItemReaderBuilder<YouTuber>()
                .pageSize(chunkSize)
                .fetchSize(chunkSize)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(YouTuber.class))
                .queryProvider(createSelectYuTuberProvider())
                .name("youTuberRakingReader")
                .build();
    }

    @Bean
    public Function<? super YouTuber, ? extends Raking> youTuberRakingProcessor() {
        return this::decideOrder;
    }

    private Raking decideOrder(YouTuber youTuber) {
        Raking raking = new Raking();
        raking.setBannerImgUrl(youTuber.getBannerImgUrl());
        raking.setCategoryId(youTuber.getCategoryId());
        raking.setName(youTuber.getName());
        raking.setSubscriberCount(youTuber.getSubscriberCount());
        raking.setThumbnail(youTuber.getThumbnail());
        raking.setViewCount(youTuber.getViewCount());
        raking.setYouTuberId(youTuber.getId());

        return raking;
    }

    @Bean
    public ItemWriter<Raking> customItemWriter() {
        return rakings -> {
            List<Raking> rakingList = rakings.stream().sorted(bySubscriberCount()).collect(Collectors.toList());
            Long num = 1L;
            for (int i = 0; i < rakingList.size(); i++) {
                rakingList.get(i).setRaking(num++);
                log.info("raking -{} ,youtuber - {}, subscriber - {}", rakingList.get(i).getRaking()
                        , rakingList.get(i).getName(), rakingList.get(i).getSubscriberCount());
            }

        };
    }


    private Comparator<Raking> bySubscriberCount() {
        return Comparator.comparing(Raking::getSubscriberCount).reversed();
    }


    @Bean
    public Step reqYouTubeApiStep() throws Exception {
        return stepBuilderFactory.get("reqYouTuberApiStep")
                .<YouTuber, YouTuber>chunk(chunkSize)
                .reader(reqYouTubeApiReader())
                .processor(reqYouTubeApiProcessor())
                .writer(reqYouTubeApiWriter())
                .build();

    }

    @Bean
    public JdbcPagingItemReader<YouTuber> reqYouTubeApiReader() throws Exception {
        return new JdbcPagingItemReaderBuilder<YouTuber>()
                .pageSize(chunkSize)
                .fetchSize(chunkSize)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(YouTuber.class))
                .queryProvider(createSelectYuTuberProvider())
                .name("reqYouTubeApiReader")
                .build();
    }

    @Bean
    public PagingQueryProvider createSelectYuTuberProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("*");
        queryProvider.setFromClause("from you_tuber");

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        return queryProvider.getObject();
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
                .sql(jdbcQuery.getUpdateQuery())
                .beanMapped()
                .build();
    }


}
