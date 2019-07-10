package com.mashup.thing.ranking;

import com.mashup.thing.ranking.domain.Ranking;
import com.mashup.thing.youtuber.domain.YouTuber;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RankingMapper {

    public Ranking toRanking(YouTuber youTuber, Long categoryId) {
        return new Ranking(youTuber.getBannerImgUrl(),
                categoryId, youTuber.getName(),
                youTuber.getSubscriberCount(), youTuber.getThumbnail(),
                youTuber.getViewCount(), youTuber.getId(),
                LocalDateTime.now());


    }

}
