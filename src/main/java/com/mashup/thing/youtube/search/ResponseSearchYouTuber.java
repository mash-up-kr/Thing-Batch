package com.mashup.thing.youtube.search;

import lombok.Getter;

import java.util.List;

@Getter
public class ResponseSearchYouTuber {

    private String kind;
    private String nextPageToken;
    private String regionCode;
    private PageInfo pageInfo;
    private List<Item> items;

}
