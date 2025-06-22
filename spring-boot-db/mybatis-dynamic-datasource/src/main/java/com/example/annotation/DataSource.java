package com.example.annotation;

import java.lang.annotation.*;

/**
 * 数据源切换注解
 * 
 * 使用方式：
 * 1. @DataSource(DataSourceType.MASTER) - 强制使用主库
 * 2. @DataSource(DataSourceType.SLAVE) - 强制使用从库
 * 3. @DataSource - 默认根据方法名自动判断（select开头使用从库，其他使用主库）
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
    
    /**
     * 数据源类型
     * 默认为AUTO，根据方法名自动判断
     */
    DataSourceType value() default DataSourceType.AUTO;
    
}
