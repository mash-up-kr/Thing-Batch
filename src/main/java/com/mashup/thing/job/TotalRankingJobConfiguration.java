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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
@Configuration
public class TotalRankingJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final RankingMapper rankingMapper;
    private final ProviderConfiguration providerConfiguration;
    private final RankingJdbc rankingJdbc;
    private static final Long CATEGORY_TOTAL_NUM = 12L;
    private static final int CHUNK_SIZE = 200;

    @Bean
    public Job totalRankingJob() throws Exception {
        return jobBuilderFactory.get("totalRankingJob")
                .start(totalRankingStep())
                .build();
    }

    @Bean
    public Step totalRankingStep() throws Exception {
        return stepBuilderFactory.get("totalRankingStep")
                .<YouTuber, Ranking>chunk(CHUNK_SIZE)
                .reader(totalRankingReader())
                .processor(totalRankingProcessor())
                .writer(totalRankingWriter())
                .build();
    }

    @Bean
    public JdbcPagingItemReader<YouTuber> totalRankingReader() throws Exception {
        return new JdbcPagingItemReaderBuilder<YouTuber>()
                .pageSize(CHUNK_SIZE)
                .fetchSize(CHUNK_SIZE)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(YouTuber.class))
                .queryProvider(providerConfiguration.createSelectYuTuberBySubscriber(dataSource))
                .name("totalRankingReader")
                .build();
    }

    @Bean
    public Function<? super YouTuber, ? extends Ranking> totalRankingProcessor() {
        return this::toRanking;
    }

    private Ranking toRanking(YouTuber youTuber) {
        return rankingMapper.toRanking(youTuber, CATEGORY_TOTAL_NUM, RankingType.TOTAL);
    }

    @Bean
    public ItemWriter<Ranking> totalRankingWriter() {
        return rankings -> {
            List<Ranking> rankingList = new ArrayList<>(rankings);

            rankingJdbc.checkRanking(CATEGORY_TOTAL_NUM, rankingList, RankingType.TOTAL);
        };
    }

}
