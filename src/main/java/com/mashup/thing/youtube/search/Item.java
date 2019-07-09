package com.mashup.thing.youtube.search;

import lombok.Getter;

@Getter
public class Item {

    private String kind;
    private String etag;
    private Id id;
    private Snippet snippet;

}
