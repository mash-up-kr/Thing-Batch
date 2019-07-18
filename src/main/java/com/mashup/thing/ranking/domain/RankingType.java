package com.mashup.thing.ranking.domain;

public enum RankingType {
    TOTAL("ranking"), SOARING("soaring");

    private String type;

    RankingType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
