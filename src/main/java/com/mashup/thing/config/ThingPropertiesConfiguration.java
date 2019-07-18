package com.mashup.thing.config;

import com.mashup.thing.config.jdbcquery.YouTuberRenewQuery;
import com.mashup.thing.config.jdbcquery.RankingStepQuery;
import com.mashup.thing.config.jdbcquery.VideoRenewQuery;
import com.mashup.thing.config.youtubeopenapi.YouTubeOpenApi;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThingPropertiesConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "youtube")
    public YouTubeOpenApi youTubeOpenApi() {
        return new YouTubeOpenApi();
    }

    @Bean
    @ConfigurationProperties(prefix = "youtuberrenew")
    public YouTuberRenewQuery youTuberRenewQuery() {
        return new YouTuberRenewQuery();
    }

    @Bean
    @ConfigurationProperties(prefix = "videorenew")
    public VideoRenewQuery videoRenewQuery() {
        return new VideoRenewQuery();
    }

    @Bean
    @ConfigurationProperties(prefix = "ranking.step")
    public RankingStepQuery rankingStepQuery() {
        return new RankingStepQuery();
    }

}
