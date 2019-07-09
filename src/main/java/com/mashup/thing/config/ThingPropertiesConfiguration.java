package com.mashup.thing.config;

import com.mashup.thing.config.jdbcquery.JdbcQuery;
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
    @ConfigurationProperties(prefix = "step")
    public JdbcQuery stepQuery() {
        return new JdbcQuery();
    }

}
