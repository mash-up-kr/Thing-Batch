package com.mashup.thing.video.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Video {

    private Long id;
    private Long youTuberId;
    private LocalDateTime publishedAt;
    private String thumbnail;
    private String youtubeVideoId;
    private String title;

    public Video(String title, LocalDateTime publishedAt, String videoId, String thumbnail, Long youTuberId) {
        this.title = title;
        this.publishedAt = publishedAt;
        this.youtubeVideoId = videoId;
        this.thumbnail = thumbnail;
        this.youTuberId = youTuberId;
    }
}
