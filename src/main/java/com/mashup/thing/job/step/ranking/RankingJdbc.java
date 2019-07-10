package com.mashup.thing.job.step.ranking;

import com.mashup.thing.config.jdbcquery.RankingStepQuery;
import com.mashup.thing.ranking.domain.Ranking;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RankingJdbc {

    private final JdbcTemplate jdbcTemplate;
    private final RankingStepQuery rankingStepQuery;

    private static final Long NUM_INITIAL_VALUE = 1L;

    public void checkRanking(Long categoryId, List<Ranking> rankings) {
        Long currentRakingNum = checkRakingNum(categoryId);
        currentRakingNum = decideOrder(currentRakingNum, rankings);
        updateCurrentRakingNum(currentRakingNum);
    }

    private Long checkRakingNum(Long categoryId) {
        Long currentRakingNum = NUM_INITIAL_VALUE;
        try {
            currentRakingNum = jdbcTemplate.queryForObject(rankingStepQuery.getNumSelectQuery(),
                    Long.class, categoryId, LocalDate.now());
        } catch (EmptyResultDataAccessException e) {
            jdbcTemplate.update(rankingStepQuery.getNumInsertQuery(), NUM_INITIAL_VALUE, categoryId, LocalDate.now());
        }
        return currentRakingNum;
    }

    private Long decideOrder(Long currentRakingNum, List<Ranking> rankings) {
        for (Ranking ranking : rankings) {
            ranking.setRaking(currentRakingNum++);
            jdbcTemplate.update(rankingStepQuery.getRankingInsertQuery(), ranking.getName(), ranking.getRaking(), ranking.getCreateAt(),
                    ranking.getViewCount(), ranking.getSubscriberCount(), ranking.getThumbnail(),
                    ranking.getBannerImgUrl(), ranking.getCategoryId(), ranking.getYouTuberId());
        }
        return currentRakingNum;
    }

    private void updateCurrentRakingNum(Long currentRakingNum) {
        jdbcTemplate.update(rankingStepQuery.getNumUpdateQuery(), currentRakingNum, LocalDate.now());
    }


}
