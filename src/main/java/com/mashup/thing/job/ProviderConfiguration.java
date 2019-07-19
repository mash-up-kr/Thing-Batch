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

        queryProvider.setSortKeys(createSortKey("id"));

        return queryProvider.getObject();
    }

    @Bean
    public PagingQueryProvider createSelectYuTuberBySubscriber(DataSource dataSource) throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = createSelectQuery(dataSource);

        queryProvider.setSortKeys(createSortKey("subscriber_count", "id"));

        return queryProvider.getObject();
    }

    @Bean
    public PagingQueryProvider createSelectYuTuberBySoaring(DataSource dataSource) throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = createSelectQuery(dataSource);

        queryProvider.setSortKeys(createSortKey("soaring", "id"));

        return queryProvider.getObject();
    }

    @Bean
    public PagingQueryProvider createSelectYuTuberBySubscriberWithCategory(DataSource dataSource) throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = createSelectQuery(dataSource);
        queryProvider.setWhereClause("where category_id = " + categoryId);

        queryProvider.setSortKeys(createSortKey("subscriber_count"));

        return queryProvider.getObject();
    }

    @Bean
    public PagingQueryProvider createSelectYuTuberBySoaringWithCategory(DataSource dataSource) throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = createSelectQuery(dataSource);
        queryProvider.setWhereClause("where category_id = " + categoryId);

        queryProvider.setSortKeys(createSortKey("soaring"));

        return queryProvider.getObject();
    }

    private Map<String, Order> createSortKey(String firstKey) {
        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put(firstKey, Order.DESCENDING);

        return sortKeys;
    }

    private Map<String, Order> createSortKey(String firstKey, String secondKey) {
        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put(firstKey, Order.DESCENDING);
        sortKeys.put(secondKey, Order.ASCENDING);

        return sortKeys;
    }

    private SqlPagingQueryProviderFactoryBean createSelectQuery(DataSource dataSource) {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("*");
        queryProvider.setFromClause("from you_tuber");

        return queryProvider;
    }
}
