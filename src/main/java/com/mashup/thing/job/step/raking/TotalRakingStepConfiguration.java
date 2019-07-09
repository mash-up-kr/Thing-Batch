package com.mashup.thing.job.step.raking;

import com.mashup.thing.config.jdbcquery.RakingStepQuery;
import com.mashup.thing.job.step.ProviderConfiguration;
import com.mashup.thing.raking.RakingMapper;
import com.mashup.thing.raking.domain.Raking;
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class TotalRakingStepConfiguration {

    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final RakingMapper rakingMapper;
    private final ProviderConfiguration providerConfiguration;
    private final RakingStepQuery rakingStepQuery;

    private static final long CATEGORY_TOTAL_NUM = 12L;
    private static final long NUM_INITIAL_VALUE = 1L;
    private static final int CHUNK_SIZE = 200;

    @Bean
    public Step totalRakingStep() throws Exception {
        return stepBuilderFactory.get("totalRakingStep")
                .<YouTuber, Raking>chunk(CHUNK_SIZE)
                .reader(youTuberRakingReader())
                .processor(youTuberRakingProcessor())
                .writer(customItemWriter())
                .build();
    }

    @Bean
    public JdbcPagingItemReader<YouTuber> youTuberRakingReader() throws Exception {
        return new JdbcPagingItemReaderBuilder<YouTuber>()
                .pageSize(CHUNK_SIZE)
                .fetchSize(CHUNK_SIZE)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(YouTuber.class))
                .queryProvider(providerConfiguration.createSelectYuTuberBySubscriber(dataSource))
                .name("youTuberRakingReader")
                .build();
    }

    @Bean
    public Function<? super YouTuber, ? extends Raking> youTuberRakingProcessor() {
        return this::toRaking;
    }

    private Raking toRaking(YouTuber youTuber) {
        return rakingMapper.toRaking(youTuber, LocalDateTime.now());
    }

    @Bean
    public ItemWriter<Raking> customItemWriter() {
        return rankings -> {
            List<Raking> rakingList = rankings.stream().sorted(bySubscriberCount()).collect(Collectors.toList());

            Long currentRakingNum = checkRakingNum();
            currentRakingNum = decideOrder(currentRakingNum, rakingList);
            updateCurrentRakingNum(currentRakingNum);
        };
    }

    private Long checkRakingNum() {
        Long currentRakingNum = NUM_INITIAL_VALUE;
        try {
            currentRakingNum = jdbcTemplate.queryForObject(rakingStepQuery.getNumSelectQuery(), Long.class, CATEGORY_TOTAL_NUM);
        } catch (EmptyResultDataAccessException e) {
            jdbcTemplate.update(rakingStepQuery.getNumInsertQuery(), NUM_INITIAL_VALUE, CATEGORY_TOTAL_NUM, LocalDate.now());
        }
        return currentRakingNum;
    }

    private Comparator<Raking> bySubscriberCount() {
        return Comparator.comparing(Raking::getSubscriberCount).reversed();
    }

    private Long decideOrder(Long currentRakingNum, List<Raking> rankings) {
        for (Raking raking : rankings) {
            raking.setRaking(currentRakingNum++);
            jdbcTemplate.update(rakingStepQuery.getRakingInsertQuery(), raking.getName(), raking.getRaking(), raking.getCreateAt(),
                    raking.getViewCount(), raking.getSubscriberCount(), raking.getThumbnail(),
                    raking.getBannerImgUrl(), raking.getCategoryId(), raking.getYouTuberId());
        }
        return currentRakingNum;
    }

    private void updateCurrentRakingNum(Long currentRakingNum) {
        jdbcTemplate.update(rakingStepQuery.getNumUpdateQuery(), currentRakingNum, LocalDate.now());
    }

}
