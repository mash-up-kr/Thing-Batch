package com.mashup.thing.youtube.channel;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChannelSnippet {

    private String title;
    private String description;
    private String customUrl;
    private LocalDateTime publishedAt;
    private Thumbnail thumbnails;
    private String defaultLanguage;
    private String country;
    private Localized localized;
}
