package com.mashup.thing.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuotaCalculator {
    private final JdbcTemplate jdbcTemplate;

    private static final int MAX_QUOTA = 10000;
    private static final int QUOTA_INIT = 0;
    private static final int PLAYLIST_QUOTA = 3;

    private int youTubeApiQuota = 10000;
    private int currentApiKey = 6;

    public void increaseQuota() {
        youTubeApiQuota += PLAYLIST_QUOTA;
    }

    public boolean isOverQuota() {
        return youTubeApiQuota >= MAX_QUOTA;
    }

    public String nextApiKey() {
        currentApiKey++;
        youTubeApiQuota = QUOTA_INIT;
        String key = jdbcTemplate
                .queryForObject("SELECT key_value FROM api_key WHERE num = ?", String.class, currentApiKey);
        log.info(key);

        return key;
    }

}
