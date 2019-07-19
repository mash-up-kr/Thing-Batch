package com.mashup.thing.youtuber.domain;

import com.mashup.thing.youtube.channel.ChannelItem;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
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
    private Long likeCount;
    private Long noCount;
    private Double soaring;
    private String tag;
    private String playListId;
    private Long categoryId;

    public void updateInfo(ChannelItem channelItem) {
        this.soaring = calculateSoaring(channelItem.getStatistics().getSubscriberCount());
        this.name = channelItem.getSnippet().getTitle();
        this.description = channelItem.getSnippet().getDescription();
        this.publishedAt = channelItem.getSnippet().getPublishedAt();
        this.thumbnail = channelItem.getSnippet().getThumbnails().getHigh().getUrl();
        this.country = channelItem.getSnippet().getCountry();
        this.viewCount = channelItem.getStatistics().getViewCount();
        this.commentCount = channelItem.getStatistics().getCommentCount();
        this.videoCount = channelItem.getStatistics().getViewCount();
        this.subscriberCount = channelItem.getStatistics().getSubscriberCount();
        this.videoCount = channelItem.getStatistics().getVideoCount();
        this.playListId = Optional.ofNullable(channelItem.getContentDetails().getRelatedPlaylists().getUploads())
                .orElse("");
        this.bannerImgUrl = Optional.ofNullable(channelItem.getBrandingSettings().getImage().getBannerTvHighImageUrl())
                .orElse("http://s.ytimg.com/yts/img/channels/c4/default_banner-vfl7DRgTn.png");

    }

    private Double calculateSoaring(Long toDaySubscriberCount) {
        if (isYesterdaySubscriberCount()) {
            return (double) toDaySubscriberCount;
        }

        return ((double) toDaySubscriberCount - (double) this.subscriberCount) / (double) this.subscriberCount * 100;
    }

    private boolean isYesterdaySubscriberCount() {
        return this.subscriberCount.equals(0L);
    }

}