package com.mashup.thing.youtube.playlistitem;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponsePlayList {

    private String kind;
    private String nextPageToken;
    private PageInfo pageInfo;
    private List<PlayListItem> items;
    private Long youTuberId;

}
