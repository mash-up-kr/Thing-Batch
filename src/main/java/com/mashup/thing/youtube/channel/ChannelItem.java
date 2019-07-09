package com.mashup.thing.youtube.channel;

import lombok.Getter;

@Getter
public class ChannelItem {

    private String id;
    private ChannelSnippet snippet;
    private ChannelStatistics statistics;
    private BrandingSettings brandingSettings;
}
