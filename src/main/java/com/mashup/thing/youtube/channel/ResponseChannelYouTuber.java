package com.mashup.thing.youtube.channel;

import lombok.Getter;

import java.util.List;

@Getter
public class ResponseChannelYouTuber {

    private String kind;
    private List<ChannelItem> items;
}
