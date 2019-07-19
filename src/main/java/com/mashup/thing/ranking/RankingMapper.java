package com.mashup.thing.ranking;

import com.mashup.thing.ranking.domain.Ranking;
import com.mashup.thing.ranking.domain.RankingType;
import com.mashup.thing.youtuber.domain.YouTuber;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class RankingMapper {

    public Ranking toRanking(YouTuber youTuber, Long categoryId, RankingType rankingType) {
        return new Ranking(youTuber.getBannerImgUrl(),
                categoryId, youTuber.getName(),
                youTuber.getSubscriberCount(), youTuber.getThumbnail(),
                youTuber.getViewCount(), youTuber.getId(),
                LocalDate.now(), youTuber.getSoaring(), rankingType.getType());
    }

}
