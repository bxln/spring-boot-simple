package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MyBatis官方推荐多数据源应用启动类
 * 
 * 基于Spring Boot官方文档推荐的多数据源配置方式：
 * - 使用DataSourceProperties进行类型安全的配置绑定
 * - 使用@ConfigurationProperties配置HikariCP连接池
 * - 使用@Qualifier和defaultCandidate=false管理多个数据源
 * 
 * @author example
 */
@SpringBootApplication
public class OfficialDatasourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OfficialDatasourceApplication.class, args);
    }

}
