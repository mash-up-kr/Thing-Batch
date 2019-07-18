package com.mashup.thing.job.step.ranking;

import com.mashup.thing.job.ProviderConfiguration;
import com.mashup.thing.ranking.RankingMapper;
import com.mashup.thing.ranking.domain.Ranking;
import com.mashup.thing.youtuber.domain.YouTuber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CategoryRankingStepConfiguration {

    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final RankingMapper rankingMapper;
    private final ProviderConfiguration providerConfiguration;
    private final RankingJdbc rankingJdbc;

    private static final int CHUNK_SIZE = 200;

    @Bean
    public Step categoryRankingStep() throws Exception {
        return stepBuilderFactory.get("categoryRankingStep")
                .<YouTuber, Ranking>chunk(CHUNK_SIZE)
                .reader(categoryRankingReader())
                .processor(categoryRankingProcessor())
                .writer(categoryRankingWriter())
                .build();
    }

    @Bean
    public JdbcPagingItemReader<YouTuber> categoryRankingReader() throws Exception {
        return new JdbcPagingItemReaderBuilder<YouTuber>()
                .pageSize(CHUNK_SIZE)
                .fetchSize(CHUNK_SIZE)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(YouTuber.class))
                .queryProvider(providerConfiguration.createSelectYuTuberBySubscriber(dataSource))
                .name("categoryRankingReader")
                .build();
    }

    @Bean
    public Function<? super YouTuber, ? extends Ranking> categoryRankingProcessor() {
        return this::toRanking;
    }

    private Ranking toRanking(YouTuber youTuber) {
        return rankingMapper.toRanking(youTuber, youTuber.getCategoryId());
    }

    @Bean
    public ItemWriter<Ranking> categoryRankingWriter() {
        return rankings -> {
            Long categoryId = rankings.get(0).getCategoryId();
            List<Ranking> rankingList = rankings.stream().sorted(bySubscriberCount()).collect(Collectors.toList());
            rankingJdbc.checkRanking(categoryId, rankingList);
        };
    }


    private Comparator<Ranking> bySubscriberCount() {
        return Comparator.comparing(Ranking::getSubscriberCount).reversed();
    }
}
