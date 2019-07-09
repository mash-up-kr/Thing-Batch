package com.mashup.thing.raking.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Raking {

    private Long id;
    private Long raking;
    private Long subscriberCount;
    private Long viewCount;
    private String name;
    private String thumbnail;
    private String bannerImgUrl;
    private LocalDateTime createAt;
    private Long youTuberId;
    private Long categoryId;

    public Raking(String bannerImgUrl, Long categoryId, String name,
                  Long subscriberCount, String thumbnail, Long viewCount,
                  Long youTuberId, LocalDateTime createAt) {
        this.createAt = createAt;
        this.bannerImgUrl = bannerImgUrl;
        this.categoryId = categoryId;
        this.name = name;
        this.subscriberCount = subscriberCount;
        this.thumbnail = thumbnail;
        this.viewCount = viewCount;
        this.youTuberId = youTuberId;
    }
}
