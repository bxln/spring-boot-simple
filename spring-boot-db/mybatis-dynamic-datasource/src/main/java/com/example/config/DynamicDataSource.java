package com.example.config;

import com.example.annotation.DataSourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源路由器
 * 继承AbstractRoutingDataSource，实现数据源的动态切换
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSource.class);
    
    /**
     * 决定使用哪个数据源
     * 这个方法在每次数据库操作时都会被调用
     */
    @Override
    protected Object determineCurrentLookupKey() {
        DataSourceType dataSourceType = DataSourceContextHolder.getDataSourceType();
        String dataSourceKey = dataSourceType.getValue();
        
        logger.debug("当前使用数据源: {}", dataSourceKey);
        return dataSourceKey;
    }
    
    /**
     * 数据源切换后的回调方法
     */
    @Override
    protected javax.sql.DataSource determineTargetDataSource() {
        javax.sql.DataSource dataSource = super.determineTargetDataSource();
        String currentKey = DataSourceContextHolder.getCurrentDataSourceKey();
        logger.debug("成功切换到数据源: {}", currentKey);
        return dataSource;
    }
}
