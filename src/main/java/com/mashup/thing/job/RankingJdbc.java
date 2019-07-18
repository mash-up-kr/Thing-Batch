package com.mashup.thing.job;

import com.mashup.thing.config.jdbcquery.RankingStepQuery;
import com.mashup.thing.ranking.domain.Ranking;
import com.mashup.thing.ranking.domain.RankingType;
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

    public void checkRanking(Long categoryId, List<Ranking> rankings, RankingType rankingType) {
        Long currentRakingNum = checkRakingNum(categoryId, rankingType);
        currentRakingNum = decideOrder(currentRakingNum, rankings);
        updateCurrentRakingNum(currentRakingNum, categoryId, rankingType);
    }

    private Long checkRakingNum(Long categoryId, RankingType rankingType) {
        Long currentRakingNum = NUM_INITIAL_VALUE;
        try {
            currentRakingNum = jdbcTemplate.queryForObject(rankingStepQuery.getNumSelectQuery(),
                    Long.class, LocalDate.now(), categoryId, rankingType.getType());
        } catch (EmptyResultDataAccessException e) {
            jdbcTemplate.update(rankingStepQuery.getNumInsertQuery(),
                    NUM_INITIAL_VALUE, LocalDate.now(), categoryId, rankingType.getType());
        }
        return currentRakingNum;
    }

    private Long decideOrder(Long currentRakingNum, List<Ranking> rankings) {
        for (Ranking ranking : rankings) {
            ranking.setRaking(currentRakingNum++);
            jdbcTemplate.update(rankingStepQuery.getRankingInsertQuery(), ranking.getName(),
                    ranking.getRaking(), ranking.getRankingType(), ranking.getCreateAt(),
                    ranking.getViewCount(), ranking.getSubscriberCount(), ranking.getThumbnail(),
                    ranking.getBannerImgUrl(), ranking.getCategoryId(), ranking.getYouTuberId());
        }
        return currentRakingNum;
    }

    private void updateCurrentRakingNum(Long currentRakingNum, Long categoryId, RankingType rankingType) {
        jdbcTemplate.update(rankingStepQuery.getNumUpdateQuery(), currentRakingNum,
                LocalDate.now(), categoryId, rankingType.getType());
    }


}
