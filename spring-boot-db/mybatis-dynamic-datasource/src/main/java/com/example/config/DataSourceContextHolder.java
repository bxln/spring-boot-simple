package com.example.config;

import com.example.annotation.DataSourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据源上下文持有者
 * 使用ThreadLocal保证线程安全
 */
public class DataSourceContextHolder {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSourceContextHolder.class);
    
    /**
     * 使用ThreadLocal保存当前线程的数据源类型
     */
    private static final ThreadLocal<DataSourceType> CONTEXT_HOLDER = new ThreadLocal<>();
    
    /**
     * 设置当前线程的数据源类型
     */
    public static void setDataSourceType(DataSourceType dataSourceType) {
        if (dataSourceType == null) {
            dataSourceType = DataSourceType.MASTER;
        }
        logger.debug("切换数据源到: {}", dataSourceType.getValue());
        CONTEXT_HOLDER.set(dataSourceType);
    }
    
    /**
     * 获取当前线程的数据源类型
     */
    public static DataSourceType getDataSourceType() {
        DataSourceType dataSourceType = CONTEXT_HOLDER.get();
        if (dataSourceType == null) {
            dataSourceType = DataSourceType.MASTER;
            logger.debug("当前线程未设置数据源，使用默认数据源: {}", dataSourceType.getValue());
        }
        return dataSourceType;
    }
    
    /**
     * 清除当前线程的数据源类型
     */
    public static void clearDataSourceType() {
        DataSourceType dataSourceType = CONTEXT_HOLDER.get();
        if (dataSourceType != null) {
            logger.debug("清除数据源: {}", dataSourceType.getValue());
            CONTEXT_HOLDER.remove();
        }
    }
    
    /**
     * 获取当前数据源的字符串标识
     */
    public static String getCurrentDataSourceKey() {
        return getDataSourceType().getValue();
    }
}
