# MyBatis官方推荐多数据源示例

这是一个严格按照Spring Boot官方文档推荐方式实现的MyBatis多数据源示例项目。

## 项目特性

- ✅ **严格遵循Spring Boot官方文档**：使用`DataSourceProperties` + `@ConfigurationProperties`配置方式
- ✅ **官方推荐注解模式**：使用`@Qualifier("second")`和`defaultCandidate=false`
- ✅ **类型安全的配置绑定**：避免配置错误，提供IDE智能提示
- ✅ **HikariCP连接池完整配置**：支持所有高级配置选项
- ✅ **XML映射文件**：所有SQL写在XML文件中，便于维护
- ✅ **两个数据源示例**：主数据源（用户管理）+ 第二数据源（订单管理）

## 核心配置方式

### 主数据源配置
```java
@Primary
@Bean(name = "primaryDataSourceProperties")
@ConfigurationProperties("spring.datasource")
public DataSourceProperties primaryDataSourceProperties() {
    return new DataSourceProperties();
}

@Primary
@Bean(name = "primaryDataSource")
@ConfigurationProperties("spring.datasource.hikari")
public HikariDataSource primaryDataSource(DataSourceProperties properties) {
    return properties.initializeDataSourceBuilder()
            .type(HikariDataSource.class)
            .build();
}
```

### 第二数据源配置（官方推荐模式）
```java
@Qualifier("second")
@Bean(defaultCandidate = false)
@ConfigurationProperties("app.datasource")
public DataSourceProperties secondDataSourceProperties() {
    return new DataSourceProperties();
}

@Qualifier("second")
@Bean(defaultCandidate = false)
@ConfigurationProperties("app.datasource.configuration")
public HikariDataSource secondDataSource(
        @Qualifier("second") DataSourceProperties secondDataSourceProperties) {
    return secondDataSourceProperties.initializeDataSourceBuilder()
            .type(HikariDataSource.class)
            .build();
}
```

## 配置说明

### application.yml配置
```yaml
# 主数据源配置 - 用户管理数据库
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/primary_db
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      pool-name: PrimaryHikariPool
      maximum-pool-size: 20
      minimum-idle: 5

# 第二数据源配置 - 订单管理数据库
app:
  datasource:
    url: jdbc:mysql://localhost:3306/secondary_db
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
    configuration:
      pool-name: SecondaryHikariPool
      maximum-pool-size: 15
      minimum-idle: 3
```

## 快速开始

### 1. 创建数据库
```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE primary_db;"
mysql -u root -p -e "CREATE DATABASE secondary_db;"

# 执行初始化脚本
mysql -u root -p primary_db < src/main/resources/sql/primary_db_schema.sql
mysql -u root -p secondary_db < src/main/resources/sql/secondary_db_schema.sql
```

### 2. 启动应用
```bash
mvn spring-boot:run
```

### 3. 测试接口
```bash
# 系统概览
curl http://localhost:8086/api/demo/overview

# 查询用户（主数据源）
curl http://localhost:8086/api/demo/users

# 查询订单（第二数据源）
curl http://localhost:8086/api/demo/orders

# 跨数据源查询
curl http://localhost:8086/api/demo/user-orders/1

# 健康检查
curl http://localhost:8086/api/demo/health
```

## 核心优势

### 1. 官方推荐
完全遵循Spring Boot官方文档推荐的配置方式，使用`@Qualifier("second")`和`defaultCandidate=false`。

### 2. 类型安全
使用`DataSourceProperties`进行类型安全的配置绑定，避免配置错误。

### 3. 功能完整
支持HikariCP连接池的所有高级配置选项，满足企业级应用需求。

### 4. 简洁实用
专注于核心的多数据源配置，易于理解和维护。

## 注意事项

1. **配置管理**：确保数据库连接信息正确配置
2. **Bean管理**：使用`@Qualifier("second")`明确指定Bean依赖关系
3. **事务管理**：每个数据源有独立的事务管理器
4. **连接池配置**：根据实际负载调整HikariCP参数

这个项目展示了Spring Boot官方推荐的多数据源配置方式，适合对配置质量和类型安全有较高要求的企业级应用。
