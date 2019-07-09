package com.mashup.thing.youtube.channel;

import lombok.Getter;

import java.util.List;

@Getter
public class Channel {

    private String title;
    private String description;
    private String keywords;
    private String defaultTab;
    private Boolean showBrowseView;
    private String featuredChannelsTitle;
    private List<String> featuredChannelsUrls;
    private String profileColor;
    private String defaultLanguage;
    private String country;
}
