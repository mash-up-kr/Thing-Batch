package com.mashup.thing.youtube.playlistitem;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Snippet {

    private LocalDateTime publishedAt;
    private String title;
    private Thumbnails thumbnails;
    private ResourceId resourceId;

}
