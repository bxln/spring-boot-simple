package com.example.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * 数据源配置类
 * 
 * 严格按照Spring Boot官方文档推荐方式配置多数据源：
 * 1. 使用DataSourceProperties进行类型安全的配置绑定
 * 2. 使用@ConfigurationProperties配置HikariCP连接池参数
 * 3. 使用@Qualifier明确Bean依赖关系
 * 4. 使用defaultCandidate=false防止自动配置冲突
 * 
 * @author example
 */
@Configuration(proxyBeanMethods = false)
public class DataSourceConfiguration {

    /**
     * 主数据源配置
     * 用于用户管理相关数据
     */
    @Configuration(proxyBeanMethods = false)
    @MapperScan(basePackages = "com.example.primary.mapper",
                sqlSessionTemplateRef = "primarySqlSessionTemplate")
    static class PrimaryDataSourceConfiguration {

        /**
         * 主数据源属性配置
         * 绑定spring.datasource.*配置
         */
        @Primary
        @Bean(name = "primaryDataSourceProperties")
        @ConfigurationProperties("spring.datasource")
        public DataSourceProperties primaryDataSourceProperties() {
            return new DataSourceProperties();
        }

        /**
         * 主数据源
         * 使用DataSourceProperties初始化，支持HikariCP高级配置
         */
        @Primary
        @Bean(name = "primaryDataSource")
        @ConfigurationProperties("spring.datasource.hikari")
        public HikariDataSource primaryDataSource(
                @Qualifier("primaryDataSourceProperties") DataSourceProperties properties) {
            return properties.initializeDataSourceBuilder()
                    .type(HikariDataSource.class)
                    .build();
        }

        /**
         * 主数据源SqlSessionFactory
         */
        @Primary
        @Bean(name = "primarySqlSessionFactory")
        public SqlSessionFactory primarySqlSessionFactory(
                @Qualifier("primaryDataSource") DataSource dataSource) throws Exception {
            SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
            bean.setDataSource(dataSource);
            bean.setMapperLocations(
                    new PathMatchingResourcePatternResolver()
                            .getResources("classpath:mapper/primary/*.xml"));
            return bean.getObject();
        }

        /**
         * 主数据源事务管理器
         */
        @Primary
        @Bean(name = "primaryTransactionManager")
        public DataSourceTransactionManager primaryTransactionManager(
                @Qualifier("primaryDataSource") DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        /**
         * 主数据源SqlSessionTemplate
         */
        @Primary
        @Bean(name = "primarySqlSessionTemplate")
        public SqlSessionTemplate primarySqlSessionTemplate(
                @Qualifier("primarySqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
            return new SqlSessionTemplate(sqlSessionFactory);
        }
    }

    /**
     * 第二数据源配置
     * 用于订单管理相关数据
     */
    @Configuration(proxyBeanMethods = false)
    @MapperScan(basePackages = "com.example.secondary.mapper",
                sqlSessionTemplateRef = "secondarySqlSessionTemplate")
    static class SecondaryDataSourceConfiguration {

        /**
         * 第二数据源属性配置
         * 绑定app.datasource.*配置
         */
        @Qualifier("second")
        @Bean(defaultCandidate = false)
        @ConfigurationProperties("app.datasource")
        public DataSourceProperties secondDataSourceProperties() {
            return new DataSourceProperties();
        }

        /**
         * 第二数据源
         * 使用DataSourceProperties初始化，支持HikariCP高级配置
         */
        @Qualifier("second")
        @Bean(defaultCandidate = false)
        @ConfigurationProperties("app.datasource.configuration")
        public HikariDataSource secondDataSource(
                @Qualifier("second") DataSourceProperties secondDataSourceProperties) {
            return secondDataSourceProperties.initializeDataSourceBuilder()
                    .type(HikariDataSource.class)
                    .build();
        }

        /**
         * 第二数据源SqlSessionFactory
         */
        @Qualifier("second")
        @Bean(name = "secondarySqlSessionFactory", defaultCandidate = false)
        public SqlSessionFactory secondarySqlSessionFactory(
                @Qualifier("second") DataSource dataSource) throws Exception {
            SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
            bean.setDataSource(dataSource);
            bean.setMapperLocations(
                    new PathMatchingResourcePatternResolver()
                            .getResources("classpath:mapper/secondary/*.xml"));
            return bean.getObject();
        }

        /**
         * 第二数据源事务管理器
         */
        @Qualifier("second")
        @Bean(name = "secondaryTransactionManager", defaultCandidate = false)
        public DataSourceTransactionManager secondaryTransactionManager(
                @Qualifier("second") DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        /**
         * 第二数据源SqlSessionTemplate
         */
        @Qualifier("second")
        @Bean(name = "secondarySqlSessionTemplate", defaultCandidate = false)
        public SqlSessionTemplate secondarySqlSessionTemplate(
                @Qualifier("second") SqlSessionFactory sqlSessionFactory) {
            return new SqlSessionTemplate(sqlSessionFactory);
        }
    }
}
