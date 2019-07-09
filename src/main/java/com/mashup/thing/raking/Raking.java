package com.mashup.thing.raking;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Long youTuberId;
    private Long categoryId;

}
