package com.mashup.thing.job;

import com.mashup.thing.job.step.api.YouTubeApiStepConfiguration;
import com.mashup.thing.job.step.raking.TotalRakingStepConfiguration;
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
    private final YouTubeApiStepConfiguration youTubeApiStepConfiguration;
    private final TotalRakingStepConfiguration totalRakingStepConfiguration;


    @Bean
    public Job thingJob() throws Exception {
        return jobBuilderFactory.get("thingJob")
                .start(youTubeApiStepConfiguration.ApiStep())
                .next(totalRakingStepConfiguration.totalRakingStep())
                .build();
    }

}
