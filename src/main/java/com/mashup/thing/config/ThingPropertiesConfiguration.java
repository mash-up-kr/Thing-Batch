package com.mashup.thing.config;

import com.mashup.thing.config.jdbcquery.ApiStepQuery;
import com.mashup.thing.config.jdbcquery.RakingStepQuery;
import com.mashup.thing.config.youtubeopenapi.YouTubeOpenApi;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThingPropertiesConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "youtube")
    public YouTubeOpenApi YouTubeOpenApi() {
        return new YouTubeOpenApi();
    }

    @Bean
    @ConfigurationProperties(prefix = "api.step")
    public ApiStepQuery stepQuery() {
        return new ApiStepQuery();
    }

    @Bean
    @ConfigurationProperties(prefix = "ranking.step")
    public RakingStepQuery rakingStepQuery() {
        return new RakingStepQuery();
    }

}
