package com.mashup.thing.job;

import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProviderConfiguration {

    @Value("${ranking.categoryId}")
    private Long categoryId;

    @Bean
    public PagingQueryProvider createSelectYuTuber(DataSource dataSource) throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = createSelectQuery(dataSource);

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        return queryProvider.getObject();
    }

    @Bean
    public PagingQueryProvider createSelectYuTuberBySubscriber(DataSource dataSource) throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = createSelectQuery(dataSource);

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("subscriber_count", Order.DESCENDING);

        queryProvider.setSortKeys(sortKeys);

        return queryProvider.getObject();
    }

    @Bean
    public PagingQueryProvider createSelectYuTuberBySoaring(DataSource dataSource) throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = createSelectQuery(dataSource);

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("soaring", Order.DESCENDING);

        queryProvider.setSortKeys(sortKeys);

        return queryProvider.getObject();
    }

    @Bean
    public PagingQueryProvider createSelectYuTuberBySubscriberWithCategory(DataSource dataSource) throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = createSelectQuery(dataSource);
        queryProvider.setWhereClause("where category_id = " + categoryId);

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("subscriber_count", Order.DESCENDING);

        queryProvider.setSortKeys(sortKeys);

        return queryProvider.getObject();
    }

    @Bean
    public PagingQueryProvider createSelectYuTuberBySoaringWithCategory(DataSource dataSource) throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = createSelectQuery(dataSource);
        queryProvider.setWhereClause("where category_id = " + categoryId);

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("soaring", Order.DESCENDING);

        queryProvider.setSortKeys(sortKeys);

        return queryProvider.getObject();
    }


    private SqlPagingQueryProviderFactoryBean createSelectQuery(DataSource dataSource) {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("*");
        queryProvider.setFromClause("from you_tuber");

        return queryProvider;
    }
}
