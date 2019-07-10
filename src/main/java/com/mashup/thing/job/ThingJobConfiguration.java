package com.mashup.thing.job;

import com.mashup.thing.job.step.api.PlayListItemApiStepConfiguration;
import com.mashup.thing.job.step.api.YouTuberApiStepConfiguration;
import com.mashup.thing.job.step.ranking.CategoryRankingStepConfiguration;
import com.mashup.thing.job.step.ranking.TotalRankingStepConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ThingJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final PlayListItemApiStepConfiguration playListItemApiStepConfiguration;
    private final YouTuberApiStepConfiguration youTuberApiStepConfiguration;
    private final TotalRankingStepConfiguration totalRankingStepConfiguration;
    private final CategoryRankingStepConfiguration categoryRankingStepConfiguration;


    @Bean
    public Job thingJob() throws Exception {
        return jobBuilderFactory.get("thingJob")
                .start(youTuberApiStepConfiguration.youTuberApiStep(null))
                .next(playListItemApiStepConfiguration.playListItemApiStep())
                .next(totalRankingStepConfiguration.totalRankingStep())
                .next(categoryRankingStepConfiguration.categoryRankingStep())
                .build();
    }

}
