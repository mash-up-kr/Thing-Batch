package com.mashup.thing.youtube.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Thumbnail {

    @JsonProperty(value = "default")
    private Img low;
    private Img medium;
    private Img high;
}
