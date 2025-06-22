# MyBatis动态数据源项目总结

## 项目概述

`mybatis-dynamic-datasource` 是一个基于Spring Boot和MyBatis的动态数据源示例项目，采用**注解驱动**和**AOP切面**的方式实现读写分离和动态数据源切换。

## 核心特性

### 1. 动态数据源路由
- 基于 `AbstractRoutingDataSource` 实现运行时数据源切换
- 使用 `ThreadLocal` 保证线程安全
- 支持主从数据库的读写分离

### 2. 注解驱动配置
- 完全使用MyBatis注解，无XML映射文件
- 自定义 `@DataSource` 注解控制数据源切换
- 支持方法级别和类级别的数据源指定

### 3. AOP自动切换
- 通过AOP切面自动拦截方法调用
- 根据方法名自动判断使用主库还是从库
- 支持强制指定数据源覆盖自动判断

### 4. 智能路由规则
- **读操作**: select、get、find、query、count等方法自动使用从库
- **写操作**: insert、update、delete、save、create等方法自动使用主库
- **注解优先**: @DataSource注解优先级最高

## 技术架构

```
┌─────────────────┐    ┌─────────────────┐
│   Controller    │    │   Controller    │
└─────────────────┘    └─────────────────┘
         │                       │
┌─────────────────┐    ┌─────────────────┐
│   Service       │    │   Service       │
│   @DataSource   │    │   (Auto Route)  │
└─────────────────┘    └─────────────────┘
         │                       │
         └───────┬───────────────┘
                 │
    ┌─────────────────┐
    │  AOP Aspect     │
    │ DataSourceAspect│
    └─────────────────┘
                 │
    ┌─────────────────┐
    │ ContextHolder   │
    │  ThreadLocal    │
    └─────────────────┘
                 │
    ┌─────────────────┐
    │ DynamicDataSource│
    │ AbstractRouting │
    └─────────────────┘
         │         │
┌─────────────┐ ┌─────────────┐
│ Master DB   │ │ Slave DB    │
│ (Write)     │ │ (Read)      │
└─────────────┘ └─────────────┘
```

## 项目结构

```
mybatis-dynamic-datasource/
├── src/main/java/com/example/
│   ├── annotation/          # 自定义注解
│   ├── aspect/             # AOP切面
│   ├── config/             # 配置类
│   ├── domain/             # 实体类
│   ├── mapper/             # Mapper接口(注解驱动)
│   ├── service/            # 服务层
│   └── controller/         # 控制器
├── src/main/resources/
│   ├── application.yml     # 配置文件
│   └── sql/               # 数据库脚本
└── src/test/              # 测试代码
```

## 关键组件

### 1. 数据源配置 (DataSourceConfig)
- 配置主从两个数据源
- 创建动态数据源路由器
- 配置SqlSessionFactory和事务管理器

### 2. 动态数据源 (DynamicDataSource)
- 继承AbstractRoutingDataSource
- 实现determineCurrentLookupKey方法
- 根据ThreadLocal中的数据源类型返回对应的数据源key

### 3. 上下文持有者 (DataSourceContextHolder)
- 使用ThreadLocal存储当前线程的数据源类型
- 提供设置、获取、清除数据源的方法
- 保证线程安全

### 4. AOP切面 (DataSourceAspect)
- 拦截带有@DataSource注解的方法
- 拦截所有Mapper接口的方法
- 根据注解或方法名自动判断数据源类型
- 在方法执行前设置数据源，执行后清理

### 5. 注解驱动Mapper
- 使用@Select、@Insert、@Update、@Delete注解
- 使用@Results和@Result定义结果映射
- 支持动态SQL（@SelectProvider等）

## 使用示例

### 1. 自动判断数据源
```java
@Service
public class CustomerService {
    // 自动使用从库（方法名以get开头）
    public Customer getCustomerById(Long id) {
        return customerMapper.selectById(id);
    }
    
    // 自动使用主库（方法名以create开头）
    public int createCustomer(Customer customer) {
        return customerMapper.insert(customer);
    }
}
```

### 2. 强制指定数据源
```java
@Service
public class CustomerService {
    // 强制使用从库
    @DataSource(DataSourceType.SLAVE)
    public Customer getCustomerById(Long id) {
        return customerMapper.selectById(id);
    }
    
    // 强制使用主库
    @DataSource(DataSourceType.MASTER)
    public int createCustomer(Customer customer) {
        return customerMapper.insert(customer);
    }
}
```

### 3. Mapper层指定
```java
@Repository
public interface CustomerMapper {
    @DataSource(DataSourceType.SLAVE)
    @Select("SELECT * FROM customers WHERE id = #{id}")
    Customer selectById(Long id);
    
    @Insert("INSERT INTO customers (...) VALUES (...)")
    int insert(Customer customer);
}
```

## API接口

### 客户管理
- `GET /api/customers` - 查询客户（从库）
- `POST /api/customers` - 创建客户（主库）
- `PUT /api/customers/{id}` - 更新客户（主库）

### 订单管理  
- `GET /api/orders` - 查询订单（从库）
- `POST /api/orders` - 创建订单（主库）
- `PUT /api/orders/{id}/pay` - 支付订单（主库）

### 动态数据源演示
- `GET /api/dynamic-datasource/health` - 健康检查
- `GET /api/dynamic-datasource/master-query` - 强制主库查询
- `GET /api/dynamic-datasource/slave-query` - 强制从库查询

## 配置要点

```yaml
spring:
  datasource:
    master:
      jdbc-url: jdbc:mysql://localhost:3306/master_db
      username: root
      password: 123456
    slave:
      jdbc-url: jdbc:mysql://localhost:3306/slave_db
      username: root
      password: 123456

mybatis:
  configuration:
    map-underscore-to-camel-case: true
    use-generated-keys: true
```

## 测试覆盖

- 数据源上下文测试
- 自动路由规则测试
- 读写操作分离测试
- 强制指定数据源测试
- 多数据源并发测试
- 健康检查测试

## 优势特点

1. **灵活性**: 支持自动判断和强制指定两种模式
2. **简洁性**: 注解驱动，无需XML配置文件
3. **透明性**: 对业务代码侵入性小
4. **性能**: 读写分离提高查询性能
5. **可扩展**: 易于扩展支持多个从库

## 适用场景

- 需要读写分离的应用
- 主从数据库架构
- 需要动态切换数据源的场景
- 对性能有较高要求的查询密集型应用

## 注意事项

1. **事务一致性**: 跨数据源操作需要考虑分布式事务
2. **数据同步**: 主从数据库需要配置同步机制
3. **AOP顺序**: 数据源切换切面必须在事务切面之前执行
4. **线程安全**: 使用ThreadLocal保证线程安全

## 快速开始

```bash
# 1. 初始化数据库
mysql -u root -p < src/main/resources/sql/master_db_schema.sql
mysql -u root -p < src/main/resources/sql/slave_db_schema.sql

# 2. 启动应用
mvn spring-boot:run

# 3. 测试API
curl -X GET http://localhost:8084/api/customers
curl -X GET http://localhost:8084/api/dynamic-datasource/health

# 4. 运行测试
mvn test
```

这个项目展示了企业级应用中常见的读写分离架构，可以作为学习和实际项目的参考模板。
