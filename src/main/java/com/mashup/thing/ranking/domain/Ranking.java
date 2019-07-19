package com.mashup.thing.ranking.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class Ranking {

    private Long id;
    private Long raking;
    private Long subscriberCount;
    private Double soaring;
    private Long viewCount;
    private String name;
    private String thumbnail;
    private String bannerImgUrl;
    private LocalDate createAt;
    private String rankingType;
    private Long youTuberId;
    private Long categoryId;

    public Ranking(String bannerImgUrl, Long categoryId, String name,
                   Long subscriberCount, String thumbnail, Long viewCount,
                   Long youTuberId, LocalDate createAt, Double soaring,
                   String rankingType) {
        this.createAt = createAt;
        this.bannerImgUrl = bannerImgUrl;
        this.categoryId = categoryId;
        this.name = name;
        this.subscriberCount = subscriberCount;
        this.thumbnail = thumbnail;
        this.viewCount = viewCount;
        this.youTuberId = youTuberId;
        this.soaring = soaring;
        this.rankingType = rankingType;
    }
}
