package com.mashup.thing.job;

import com.mashup.thing.ranking.RankingMapper;
import com.mashup.thing.ranking.domain.Ranking;
import com.mashup.thing.ranking.domain.RankingType;
import com.mashup.thing.youtuber.domain.YouTuber;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
@Configuration
public class CategorySoaringJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final RankingMapper rankingMapper;
    private final ProviderConfiguration providerConfiguration;
    private final RankingJdbc rankingJdbc;

    private static final int CHUNK_SIZE = 200;

    @Value("${ranking.categoryId}")
    private Long categoryId;

    @Bean
    public Job categorySoaringJob() throws Exception {
        return jobBuilderFactory.get("categorySoaringJob")
                .start(categorySoaringStep())
                .build();
    }

    @Bean
    public Step categorySoaringStep()
            throws Exception {
        return stepBuilderFactory.get("categorySoaringStep")
                .<YouTuber, Ranking>chunk(CHUNK_SIZE)
                .reader(categorySoaringReader())
                .processor(categorySoaringProcessor())
                .writer(categorySoaringWriter())
                .build();
    }

    @Bean
    public JdbcPagingItemReader<YouTuber> categorySoaringReader() throws Exception {
        return new JdbcPagingItemReaderBuilder<YouTuber>()
                .pageSize(CHUNK_SIZE)
                .fetchSize(CHUNK_SIZE)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(YouTuber.class))
                .queryProvider(providerConfiguration.createSelectYuTuberBySoaringWithCategory(dataSource))
                .name("categorySoaringReader")
                .build();
    }

    @Bean
    public Function<? super YouTuber, ? extends Ranking> categorySoaringProcessor() {
        return this::toRanking;
    }

    private Ranking toRanking(YouTuber youTuber) {
        return rankingMapper.toRanking(youTuber, categoryId, RankingType.SOARING);
    }

    @Bean
    public ItemWriter<Ranking> categorySoaringWriter() {
        return rankings -> {
            List<Ranking> rankingList = new ArrayList<>(rankings);

            rankingJdbc.checkRanking(categoryId, rankingList, RankingType.SOARING);
        };
    }

}
