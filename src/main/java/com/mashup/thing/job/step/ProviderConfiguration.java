package com.mashup.thing.job.step;

import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProviderConfiguration {

    @Bean
    public PagingQueryProvider createSelectYuTuber(DataSource dataSource) throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = setSelectQuery(dataSource);

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        return queryProvider.getObject();
    }

    @Bean
    public PagingQueryProvider createSelectYuTuberBySubscriber(DataSource dataSource) throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = setSelectQuery(dataSource);

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("subscriber_count", Order.DESCENDING);

        queryProvider.setSortKeys(sortKeys);

        return queryProvider.getObject();
    }


    private SqlPagingQueryProviderFactoryBean setSelectQuery(DataSource dataSource) {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("*");
        queryProvider.setFromClause("from you_tuber");

        return queryProvider;
    }
}
