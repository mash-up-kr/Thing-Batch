package com.mashup.thing.youtube.search;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Snippet {

    private LocalDateTime publishedAt;
    private String channelId;
    private String title;

}
