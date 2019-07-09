package com.mashup.thing.youtuber.domain;

import com.mashup.thing.youtube.channel.ChannelItem;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class YouTuber {

    private Long id;
    private String name;
    private String channelId;
    private String description;
    private LocalDateTime publishedAt;
    private String thumbnail;
    private String country;
    private Long viewCount;
    private Long commentCount;
    private Long subscriberCount;
    private Long videoCount;
    private String bannerImgUrl;
    private Long categoryId;

    public void updateInfo(ChannelItem channelItem) {
        this.name = channelItem.getSnippet().getTitle();
        this.channelId = channelItem.getId();
        this.description = channelItem.getSnippet().getDescription();
        this.publishedAt = channelItem.getSnippet().getPublishedAt();
        this.thumbnail = channelItem.getSnippet().getThumbnails().getHigh().getUrl();
        this.country = channelItem.getSnippet().getCountry();
        this.viewCount = channelItem.getStatistics().getViewCount();
        this.commentCount = channelItem.getStatistics().getCommentCount();
        this.videoCount = channelItem.getStatistics().getViewCount();
        this.subscriberCount = channelItem.getStatistics().getSubscriberCount();
        this.videoCount = channelItem.getStatistics().getVideoCount();
        this.bannerImgUrl = channelItem.getBrandingSettings().getImage().getBannerTvHighImageUrl();
    }
}