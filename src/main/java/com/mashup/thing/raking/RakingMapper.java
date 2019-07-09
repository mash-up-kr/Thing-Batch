package com.mashup.thing.raking;

import com.mashup.thing.raking.domain.Raking;
import com.mashup.thing.youtuber.domain.YouTuber;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RakingMapper {

    public Raking toRaking(YouTuber youTuber, LocalDateTime createAt) {
        return new Raking(youTuber.getBannerImgUrl(),
                youTuber.getCategoryId(), youTuber.getName(),
                youTuber.getSubscriberCount(), youTuber.getThumbnail(),
                youTuber.getViewCount(), youTuber.getId(),
                createAt);
    }

}
