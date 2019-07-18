package com.mashup.thing.config.jdbcquery;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RankingStepQuery {
    private String numSelectQuery;
    private String numInsertQuery;
    private String numUpdateQuery;
    private String rankingInsertQuery;
}
