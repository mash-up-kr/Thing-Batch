package com.mashup.thing.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuotaCalculator {
    private final JdbcTemplate jdbcTemplate;

    private static final int MAX_QUOTA = 10000;


    private int youTubeApiQuota = 10000;

    @Value("${ranking.currentApiKey}")
    private int currentApiKey;

    @Value("${ranking.quota}")
    private int quota;

    public void increaseQuota() {
        youTubeApiQuota += quota;
    }

    public boolean isOverQuota() {
        return youTubeApiQuota >= MAX_QUOTA;
    }

    public String nextApiKey() {
        currentApiKey++;
        youTubeApiQuota = quota;
        String key = jdbcTemplate
                .queryForObject("SELECT key_value FROM api_key WHERE num = ?", String.class, currentApiKey);
        log.info(key);

        return key;
    }

}
