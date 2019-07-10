package com.mashup.thing.video;

import com.mashup.thing.video.domain.Video;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class VideoMapper {

    public Video toVideo(String title, LocalDateTime publishedAt, String videoId,
                         String thumbnail, Long youTuberId) {
        return new Video(title, publishedAt, videoId, thumbnail, youTuberId);
    }
}
