# MyBatis多数据源实现方式对比分析

本文档详细对比分析了三种MyBatis多数据源实现方式的技术细节、适用场景、优缺点等。

## 概述

| 实现方式 | 核心技术 | 适用场景 | 复杂度 |
|---------|---------|---------|--------|
| **mybatis-multi-datasource** | 静态配置多SqlSessionFactory | 业务模块明确分离 | ⭐⭐ |
| **mybatis-dynamic-datasource** | AbstractRoutingDataSource + AOP | 读写分离、主从切换 | ⭐⭐⭐ |
| **mybatis-official-datasource** | Spring Boot官方推荐配置 | 企业级标准化配置 | ⭐⭐⭐⭐ |

## 一、mybatis-multi-datasource（静态多数据源）

### 🔧 实现细节

#### 核心配置方式
```java
// 主数据源配置
@Configuration
@MapperScan(basePackages = "com.example.primary.mapper", 
           sqlSessionTemplateRef = "primarySqlSessionTemplate")
public class PrimaryDataSourceConfig {
    
    @Primary
    @Bean(name = "primaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }
}

// 第二数据源配置
@Configuration
@MapperScan(basePackages = "com.example.secondary.mapper", 
           sqlSessionTemplateRef = "secondarySqlSessionTemplate")
public class SecondaryDataSourceConfig {
    
    @Bean(name = "secondaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }
}
```

#### 项目结构
```
mybatis-multi-datasource/
├── config/
│   ├── PrimaryDataSourceConfig.java      # 主数据源配置
│   └── SecondaryDataSourceConfig.java    # 第二数据源配置
├── primary/                              # 主数据源模块
│   ├── domain/User.java
│   └── mapper/UserMapper.java
├── secondary/                            # 第二数据源模块
│   ├── domain/Order.java
│   └── mapper/OrderMapper.java
└── resources/
    ├── mapper/primary/UserMapper.xml
    └── mapper/secondary/OrderMapper.xml
```

#### 技术特点
- **分包管理**：不同包对应不同数据源
- **XML映射**：分目录存放XML文件
- **静态绑定**：编译时确定数据源
- **多事务管理器**：每个数据源独立的事务管理器

### ✅ 优点
1. **简单直观**：配置清晰，易于理解
2. **性能稳定**：无运行时切换开销
3. **事务安全**：每个数据源独立事务管理
4. **维护简单**：代码结构清晰，便于维护

### ❌ 缺点
1. **灵活性差**：无法动态切换数据源
2. **配置冗余**：每个数据源需要完整配置
3. **扩展困难**：增加数据源需要修改配置
4. **包管理复杂**：需要严格按包分离

### 🎯 适用场景
- 业务模块明确分离的应用
- 数据源数量固定且较少
- 对性能要求高，不需要动态切换
- 团队技术水平一般，需要简单方案

## 二、mybatis-dynamic-datasource（动态数据源）

### 🔧 实现细节

#### 核心技术架构
```java
// 1. 动态数据源路由器
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDataSourceType().getValue();
    }
}

// 2. 线程本地上下文
public class DataSourceContextHolder {
    private static final ThreadLocal<DataSourceType> CONTEXT_HOLDER = new ThreadLocal<>();
    
    public static void setDataSourceType(DataSourceType dataSourceType) {
        CONTEXT_HOLDER.set(dataSourceType);
    }
}

// 3. AOP切面自动切换
@Aspect
@Component
@Order(1)
public class DataSourceAspect {
    @Around("dataSourcePointcut() || mapperPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        DataSourceType dataSourceType = determineDataSourceType(method, methodName);
        DataSourceContextHolder.setDataSourceType(dataSourceType);
        try {
            return point.proceed();
        } finally {
            DataSourceContextHolder.clearDataSourceType();
        }
    }
}
```

#### 智能路由规则
```java
// 自动判断数据源类型
public static DataSourceType getByMethodName(String methodName) {
    String lowerMethodName = methodName.toLowerCase();
    
    // 读操作使用从库
    if (lowerMethodName.startsWith("select") ||
        lowerMethodName.startsWith("find") ||
        lowerMethodName.startsWith("get") ||
        lowerMethodName.startsWith("query") ||
        lowerMethodName.startsWith("count")) {
        return SLAVE;
    }
    
    // 写操作使用主库
    return MASTER;
}
```

#### 注解驱动配置
```java
// 自定义注解
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSource {
    DataSourceType value() default DataSourceType.AUTO;
}

// 使用示例
@Service
public class CustomerService {
    
    @DataSource(DataSourceType.SLAVE)  // 强制使用从库
    public Customer getCustomerById(Long id) {
        return customerMapper.selectById(id);
    }
    
    // 自动判断：方法名以create开头，使用主库
    public int createCustomer(Customer customer) {
        return customerMapper.insert(customer);
    }
}
```

### ✅ 优点
1. **动态切换**：运行时动态选择数据源
2. **读写分离**：自动实现主从数据库读写分离
3. **注解驱动**：使用MyBatis注解，无需XML文件
4. **智能路由**：根据方法名自动判断数据源
5. **透明切换**：对业务代码侵入性小

### ❌ 缺点
1. **实现复杂**：需要AOP、ThreadLocal等技术
2. **调试困难**：动态切换增加调试复杂度
3. **事务限制**：跨数据源事务处理复杂
4. **性能开销**：运行时判断和切换有性能损耗

### 🎯 适用场景
- 需要读写分离的应用
- 主从数据库架构
- 查询密集型应用
- 需要根据业务逻辑动态切换数据源

## 三、mybatis-official-datasource（官方推荐）

### 🔧 实现细节

#### Spring Boot官方推荐配置
```java
// 主数据源配置
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

// 第二数据源配置（官方推荐模式）
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

#### 配置文件示例
```yaml
# 主数据源配置
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
      connection-timeout: 30000

# 第二数据源配置
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

#### 核心特性
- **DataSourceProperties**：类型安全的配置绑定
- **@Qualifier("second")**：官方推荐的Bean标识方式
- **defaultCandidate=false**：防止自动配置冲突
- **HikariCP完整配置**：支持所有高级配置选项

### ✅ 优点
1. **官方认可**：完全遵循Spring Boot官方文档
2. **类型安全**：使用DataSourceProperties确保配置正确
3. **功能完整**：支持HikariCP的所有高级特性
4. **企业级**：适合大型企业应用的架构设计
5. **可维护**：清晰的配置结构和最佳实践
6. **IDE支持**：完整的IDE智能提示和验证

### ❌ 缺点
1. **配置复杂**：相比简单配置方式，配置稍显复杂
2. **学习成本**：需要理解Spring Boot官方配置原理
3. **Bean管理**：需要明确管理多个数据源相关的Bean

### 🎯 适用场景
- 企业级应用的多数据源配置
- 需要精细化连接池配置的场景
- 对配置类型安全有要求的项目
- 需要遵循Spring Boot最佳实践的团队

## 四、综合对比分析

### 📊 技术对比表

| 对比维度 | mybatis-multi-datasource | mybatis-dynamic-datasource | mybatis-official-datasource |
|---------|-------------------------|---------------------------|----------------------------|
| **实现方式** | 静态多SqlSessionFactory | AbstractRoutingDataSource | DataSourceProperties配置 |
| **配置复杂度** | ⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ |
| **运行时性能** | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ |
| **动态切换** | ❌ | ✅ | ❌ |
| **类型安全** | ⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐ |
| **官方支持** | ⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐ |
| **学习成本** | ⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| **维护成本** | ⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐ |
| **扩展性** | ⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |

### 🔄 数据源切换方式对比

#### 1. 静态切换（multi-datasource）
```java
// 编译时确定，通过包路径区分
com.example.primary.mapper.UserMapper    → 主数据源
com.example.secondary.mapper.OrderMapper → 第二数据源
```

#### 2. 动态切换（dynamic-datasource）
```java
// 运行时切换，支持多种方式
@DataSource(DataSourceType.SLAVE)        // 注解指定
public Customer getById(Long id) { ... }

// 方法名自动判断
public Customer findById(Long id) { ... } // 自动使用从库
public void saveCustomer(Customer c) { ... } // 自动使用主库
```

#### 3. 官方推荐（official-datasource）
```java
// 通过不同的Mapper接口使用不同数据源
@Autowired
private UserMapper userMapper;     // 使用主数据源

@Autowired
private OrderMapper orderMapper;   // 使用第二数据源
```

### 📈 性能对比

| 性能指标 | multi-datasource | dynamic-datasource | official-datasource |
|---------|------------------|-------------------|---------------------|
| **启动时间** | 快 | 中等 | 中等 |
| **运行时开销** | 最低 | 中等（AOP+ThreadLocal） | 低 |
| **内存占用** | 中等 | 高（多个代理对象） | 中等 |
| **并发性能** | 最佳 | 良好 | 最佳 |

### 🛠️ 依赖对比

#### mybatis-multi-datasource
```xml
<dependencies>
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>com.zaxxer</groupId>
        <artifactId>HikariCP</artifactId>
    </dependency>
</dependencies>
```

#### mybatis-dynamic-datasource
```xml
<dependencies>
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>
    <dependency>
        <groupId>com.zaxxer</groupId>
        <artifactId>HikariCP</artifactId>
    </dependency>
</dependencies>
```

#### mybatis-official-datasource
```xml
<dependencies>
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>com.zaxxer</groupId>
        <artifactId>HikariCP</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-configuration-processor</artifactId>
    </dependency>
</dependencies>
```

## 五、选择建议

### 🎯 场景选择指南

#### 选择 mybatis-multi-datasource 当：
- ✅ 业务模块明确分离，数据源用途固定
- ✅ 团队技术水平一般，需要简单可靠的方案
- ✅ 对性能要求高，不需要动态切换
- ✅ 数据源数量少且固定（2-3个）

#### 选择 mybatis-dynamic-datasource 当：
- ✅ 需要读写分离，有主从数据库架构
- ✅ 需要根据业务逻辑动态切换数据源
- ✅ 查询密集型应用，需要负载均衡
- ✅ 团队有较强的技术能力

#### 选择 mybatis-official-datasource 当：
- ✅ 企业级应用，需要标准化配置
- ✅ 对配置类型安全有严格要求
- ✅ 需要精细化的连接池配置
- ✅ 希望遵循Spring Boot官方最佳实践

### 🔄 迁移路径建议

1. **从简单到复杂**：multi-datasource → official-datasource → dynamic-datasource
2. **从静态到动态**：multi-datasource → dynamic-datasource
3. **标准化升级**：multi-datasource → official-datasource

## 六、最佳实践总结

### 🏆 通用最佳实践

1. **配置管理**
   - 使用环境变量管理敏感信息
   - 为不同环境提供不同的配置文件
   - 启用配置加密和安全存储

2. **连接池优化**
   - 根据实际负载调整连接池大小
   - 启用连接泄漏检测
   - 配置合适的超时时间

3. **监控和运维**
   - 启用JMX监控
   - 配置健康检查端点
   - 设置连接池指标监控

4. **事务管理**
   - 明确事务边界
   - 避免跨数据源事务
   - 考虑分布式事务方案

### 📝 注意事项

1. **数据一致性**：跨数据源操作需要考虑数据一致性
2. **事务传播**：注意事务传播行为对数据源切换的影响
3. **连接池配置**：合理配置连接池参数，避免连接泄漏
4. **监控告警**：建立完善的监控和告警机制

---

## 总结

三种MyBatis多数据源实现方式各有特点：

- **mybatis-multi-datasource**：简单可靠，适合固定场景
- **mybatis-dynamic-datasource**：灵活强大，适合动态场景
- **mybatis-official-datasource**：标准规范，适合企业级应用

选择时应根据具体的业务需求、团队技术水平和项目规模来决定。建议从简单方案开始，随着业务复杂度增加再考虑升级到更复杂的方案。
