# MyBatis动态数据源示例

这是一个基于Spring Boot和MyBatis的动态数据源配置示例项目，采用**注解驱动**和**AOP切面**的方式实现读写分离和动态数据源切换。

## 项目特性

- ✅ 动态数据源路由（基于AbstractRoutingDataSource）
- ✅ 注解驱动的MyBatis配置（无XML映射文件）
- ✅ AOP切面实现数据源自动切换
- ✅ 读写分离支持（主从数据库）
- ✅ 自定义注解强制指定数据源
- ✅ 根据方法名自动判断数据源
- ✅ 完整的CRUD操作示例
- ✅ RESTful API接口
- ✅ 单元测试覆盖
- ✅ H2内存数据库测试支持

## 与mybatis-multi-datasource的区别

| 特性 | mybatis-multi-datasource | mybatis-dynamic-datasource |
|------|-------------------------|---------------------------|
| **配置方式** | 静态配置多个SqlSessionFactory | 动态数据源路由 |
| **Mapper管理** | 分包管理，不同包对应不同数据源 | 统一Mapper，通过注解切换数据源 |
| **XML映射** | 分目录存放XML文件 | 纯注解驱动，无XML文件 |
| **数据源切换** | 编译时确定 | 运行时动态切换 |
| **事务管理** | 多个事务管理器 | 单一事务管理器 + 动态路由 |
| **实现复杂度** | 相对简单，配置较多 | 较复杂，代码较少 |
| **适用场景** | 明确的业务模块分离 | 读写分离、主从切换 |

## 项目结构

```
mybatis-dynamic-datasource/
├── src/main/java/com/example/
│   ├── DynamicDatasourceApplication.java          # 启动类
│   ├── annotation/                                # 自定义注解
│   │   ├── DataSource.java                       # 数据源切换注解
│   │   └── DataSourceType.java                   # 数据源类型枚举
│   ├── aspect/                                    # AOP切面
│   │   └── DataSourceAspect.java                 # 数据源切换切面
│   ├── config/                                    # 配置类
│   │   ├── DataSourceConfig.java                 # 数据源配置
│   │   ├── DataSourceContextHolder.java          # 数据源上下文持有者
│   │   └── DynamicDataSource.java                # 动态数据源路由器
│   ├── domain/                                    # 实体类
│   │   ├── Customer.java                         # 客户实体
│   │   └── Order.java                            # 订单实体
│   ├── mapper/                                    # Mapper接口（注解驱动）
│   │   ├── CustomerMapper.java                   # 客户Mapper
│   │   └── OrderMapper.java                      # 订单Mapper
│   ├── service/                                   # 服务层
│   │   ├── CustomerService.java                  # 客户服务
│   │   └── OrderService.java                     # 订单服务
│   └── controller/                                # 控制器
│       ├── CustomerController.java               # 客户API
│       ├── OrderController.java                  # 订单API
│       └── DynamicDataSourceController.java      # 动态数据源演示API
├── src/main/resources/
│   ├── application.yml                            # 主配置文件
│   └── sql/                                       # 数据库脚本
│       ├── master_db_schema.sql                  # 主数据库脚本
│       └── slave_db_schema.sql                   # 从数据库脚本
└── src/test/                                      # 测试相关
    ├── java/com/example/DynamicDatasourceApplicationTests.java
    └── resources/
        ├── application-test.yml                   # 测试配置
        └── sql/                                   # 测试数据库脚本
```

## 核心实现原理

### 1. 动态数据源路由

<augment_code_snippet path="spring-boot-db/mybatis-dynamic-datasource/src/main/java/com/example/config/DynamicDataSource.java" mode="EXCERPT">
```java
public class DynamicDataSource extends AbstractRoutingDataSource {
    
    @Override
    protected Object determineCurrentLookupKey() {
        DataSourceType dataSourceType = DataSourceContextHolder.getDataSourceType();
        String dataSourceKey = dataSourceType.getValue();
        logger.debug("当前使用数据源: {}", dataSourceKey);
        return dataSourceKey;
    }
}
```
</augment_code_snippet>

### 2. 自定义注解

<augment_code_snippet path="spring-boot-db/mybatis-dynamic-datasource/src/main/java/com/example/annotation/DataSource.java" mode="EXCERPT">
```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
    DataSourceType value() default DataSourceType.AUTO;
}
```
</augment_code_snippet>

### 3. AOP切面自动切换

<augment_code_snippet path="spring-boot-db/mybatis-dynamic-datasource/src/main/java/com/example/aspect/DataSourceAspect.java" mode="EXCERPT">
```java
@Aspect
@Component
@Order(1) // 确保在事务切面之前执行
public class DataSourceAspect {
    
    @Around("dataSourcePointcut() || mapperPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 根据注解或方法名自动判断数据源
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
</augment_code_snippet>

### 4. 注解驱动的Mapper

<augment_code_snippet path="spring-boot-db/mybatis-dynamic-datasource/src/main/java/com/example/mapper/CustomerMapper.java" mode="EXCERPT">
```java
@Repository
public interface CustomerMapper {

    @DataSource(DataSourceType.SLAVE)
    @Select("SELECT * FROM customers WHERE id = #{id}")
    @Results({...})
    Customer selectById(Long id);

    @Insert("INSERT INTO customers (...) VALUES (...)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Customer customer);
}
```
</augment_code_snippet>

## 数据源配置

### 主数据源 (Master)
- **数据库**: `master_db`
- **用途**: 写操作（增删改）
- **特点**: 数据一致性、事务支持

### 从数据源 (Slave)
- **数据库**: `slave_db`
- **用途**: 读操作（查询统计）
- **特点**: 读性能优化、负载分担

## 快速开始

### 1. 环境准备

确保已安装以下软件：
- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库准备

执行数据库初始化脚本：

```bash
# 创建并初始化主数据库
mysql -u root -p < src/main/resources/sql/master_db_schema.sql

# 创建并初始化从数据库
mysql -u root -p < src/main/resources/sql/slave_db_schema.sql
```

### 3. 配置数据库连接

修改 `src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    master:
      jdbc-url: jdbc:mysql://localhost:3306/master_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC
      username: your_username
      password: your_password
    slave:
      jdbc-url: jdbc:mysql://localhost:3306/slave_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC
      username: your_username
      password: your_password
```

### 4. 运行应用

```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run
```

应用启动后，访问 http://localhost:8084

### 5. 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=DynamicDatasourceApplicationTests
```

## API接口

### 客户相关接口

| 方法 | 路径 | 描述 | 数据源 |
|------|------|------|--------|
| GET | `/api/customers` | 查询所有客户 | 从库 |
| GET | `/api/customers/{id}` | 根据ID查询客户 | 从库 |
| GET | `/api/customers/code/{customerCode}` | 根据编码查询客户 | 从库 |
| POST | `/api/customers` | 创建客户 | 主库 |
| PUT | `/api/customers/{id}` | 更新客户 | 主库 |
| DELETE | `/api/customers/{id}` | 删除客户 | 主库 |

### 订单相关接口

| 方法 | 路径 | 描述 | 数据源 |
|------|------|------|--------|
| GET | `/api/orders` | 查询所有订单 | 从库 |
| GET | `/api/orders/{id}` | 根据ID查询订单 | 从库 |
| GET | `/api/orders/orderNo/{orderNo}` | 根据订单号查询订单 | 从库 |
| POST | `/api/orders` | 创建订单 | 主库 |
| PUT | `/api/orders/{id}` | 更新订单 | 主库 |
| PUT | `/api/orders/{id}/pay` | 支付订单 | 主库 |

### 动态数据源演示接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/dynamic-datasource/overview` | 获取系统概览信息 |
| GET | `/api/dynamic-datasource/master-query` | 强制使用主库查询 |
| GET | `/api/dynamic-datasource/slave-query` | 强制使用从库查询 |
| GET | `/api/dynamic-datasource/auto-datasource` | 自动判断数据源 |
| GET | `/api/dynamic-datasource/health` | 数据源健康检查 |
| GET | `/api/dynamic-datasource/config` | 获取数据源配置信息 |
| GET | `/api/dynamic-datasource/performance` | 数据源性能测试 |

## 使用方式

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

### 3. Mapper层指定数据源

```java
@Repository
public interface CustomerMapper {
    
    // 强制使用从库
    @DataSource(DataSourceType.SLAVE)
    @Select("SELECT * FROM customers WHERE id = #{id}")
    Customer selectById(Long id);
    
    // 强制使用主库
    @DataSource(DataSourceType.MASTER)
    @Insert("INSERT INTO customers (...) VALUES (...)")
    int insert(Customer customer);
}
```

## 数据源路由规则

### 自动判断规则

- **读操作使用从库**: select、find、get、query、count、list、search、exists等开头的方法
- **写操作使用主库**: insert、update、delete、save、remove、create等开头的方法

### 注解优先级

1. 方法级别的 `@DataSource` 注解（最高优先级）
2. 类级别的 `@DataSource` 注解
3. 根据方法名自动判断（默认规则）

## 注意事项

1. **事务一致性**: 动态数据源只能使用一个事务管理器，跨数据源操作需要考虑分布式事务
2. **数据同步**: 主从数据库之间的数据同步需要通过MySQL主从复制或其他同步机制实现
3. **AOP顺序**: 数据源切换切面必须在事务切面之前执行（@Order(1)）
4. **线程安全**: 使用ThreadLocal保证数据源上下文的线程安全

## 扩展功能

- 支持多个从库的负载均衡
- 集成数据源监控和健康检查
- 支持数据源故障自动切换
- 集成分布式事务管理

## 故障排除

### 常见问题

1. **数据源切换不生效**: 检查AOP配置和切面顺序
2. **事务回滚异常**: 确认事务管理器配置正确
3. **数据不一致**: 检查主从数据库同步状态
4. **性能问题**: 优化连接池配置和SQL查询

### 调试技巧

启用详细日志查看数据源切换过程：

```yaml
logging:
  level:
    com.example: debug
    org.springframework.aop: debug
    org.springframework.transaction: debug
```
