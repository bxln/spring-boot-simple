package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * MyBatis动态数据源应用启动类
 * 
 * 特性：
 * 1. 动态数据源路由
 * 2. 注解驱动的MyBatis配置
 * 3. AOP实现数据源自动切换
 * 4. 读写分离支持
 */
@SpringBootApplication
@EnableAspectJAutoProxy
@MapperScan("com.example.mapper")
public class DynamicDatasourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicDatasourceApplication.class, args);
    }

}
