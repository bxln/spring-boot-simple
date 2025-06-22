# MyBatis多数据源示例

这是一个基于Spring Boot和MyBatis的多数据源配置示例项目，演示了如何在同一个应用中使用多个MySQL数据源。

## 项目特性

- ✅ 支持多个MySQL数据源配置
- ✅ 独立的事务管理器
- ✅ 分包管理不同数据源的Mapper
- ✅ HikariCP连接池配置
- ✅ 完整的CRUD操作示例
- ✅ RESTful API接口
- ✅ 单元测试覆盖
- ✅ H2内存数据库测试支持

## 项目结构

```
mybatis-multi-datasource/
├── src/main/java/com/example/
│   ├── MultipleDatasourceApplication.java          # 启动类
│   ├── config/                                     # 配置类
│   │   ├── PrimaryDataSourceConfig.java           # 主数据源配置
│   │   └── SecondaryDataSourceConfig.java         # 从数据源配置
│   ├── primary/                                    # 主数据源相关
│   │   ├── domain/User.java                       # 用户实体
│   │   ├── mapper/UserMapper.java                 # 用户Mapper接口
│   │   └── service/UserService.java               # 用户服务
│   ├── secondary/                                  # 从数据源相关
│   │   ├── domain/Product.java                    # 产品实体
│   │   ├── mapper/ProductMapper.java              # 产品Mapper接口
│   │   └── service/ProductService.java            # 产品服务
│   └── controller/                                 # 控制器
│       ├── UserController.java                    # 用户API
│       ├── ProductController.java                 # 产品API
│       └── MultiDataSourceController.java         # 多数据源演示API
├── src/main/resources/
│   ├── application.yml                             # 主配置文件
│   ├── mapper/                                     # MyBatis映射文件
│   │   ├── primary/UserMapper.xml                 # 用户映射
│   │   └── secondary/ProductMapper.xml            # 产品映射
│   └── sql/                                        # 数据库脚本
│       ├── primary_db_schema.sql                  # 主数据库脚本
│       └── secondary_db_schema.sql                # 从数据库脚本
└── src/test/                                       # 测试相关
    ├── java/com/example/MultipleDatasourceApplicationTests.java
    └── resources/
        ├── application-test.yml                    # 测试配置
        └── sql/                                    # 测试数据库脚本
```

## 数据源配置

### 主数据源 (Primary)
- **数据库**: `primary_db`
- **用途**: 存储用户相关数据
- **实体**: User
- **Mapper包**: `com.example.primary.mapper`
- **XML位置**: `classpath:mapper/primary/*.xml`

### 从数据源 (Secondary)
- **数据库**: `secondary_db`
- **用途**: 存储产品相关数据
- **实体**: Product
- **Mapper包**: `com.example.secondary.mapper`
- **XML位置**: `classpath:mapper/secondary/*.xml`

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
mysql -u root -p < src/main/resources/sql/primary_db_schema.sql

# 创建并初始化从数据库
mysql -u root -p < src/main/resources/sql/secondary_db_schema.sql
```

### 3. 配置数据库连接

修改 `src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    primary:
      jdbc-url: jdbc:mysql://localhost:3306/primary_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC
      username: your_username
      password: your_password
    secondary:
      jdbc-url: jdbc:mysql://localhost:3306/secondary_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC
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

应用启动后，访问 http://localhost:8082

### 5. 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=MultipleDatasourceApplicationTests
```

## API接口

### 用户相关接口 (主数据源)

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/users` | 查询所有用户 |
| GET | `/api/users/{id}` | 根据ID查询用户 |
| GET | `/api/users/username/{username}` | 根据用户名查询用户 |
| GET | `/api/users/status/{status}` | 根据状态查询用户 |
| GET | `/api/users/age-range?minAge=25&maxAge=35` | 根据年龄范围查询用户 |
| GET | `/api/users/page?pageNum=1&pageSize=10` | 分页查询用户 |
| GET | `/api/users/count` | 统计用户总数 |
| POST | `/api/users` | 创建用户 |
| PUT | `/api/users/{id}` | 更新用户 |
| DELETE | `/api/users/{id}` | 删除用户 |
| POST | `/api/users/batch` | 批量创建用户 |
| DELETE | `/api/users/batch` | 批量删除用户 |

### 产品相关接口 (从数据源)

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/products` | 查询所有产品 |
| GET | `/api/products/{id}` | 根据ID查询产品 |
| GET | `/api/products/code/{productCode}` | 根据产品编码查询产品 |
| GET | `/api/products/category/{category}` | 根据分类查询产品 |
| GET | `/api/products/status/{status}` | 根据状态查询产品 |
| GET | `/api/products/price-range?minPrice=100&maxPrice=1000` | 根据价格范围查询产品 |
| GET | `/api/products/search?productName=iPhone` | 根据产品名称模糊查询 |
| GET | `/api/products/page?pageNum=1&pageSize=10` | 分页查询产品 |
| GET | `/api/products/count` | 统计产品总数 |
| POST | `/api/products` | 创建产品 |
| PUT | `/api/products/{id}` | 更新产品 |
| PUT | `/api/products/{id}/stock?stock=100` | 更新库存 |
| DELETE | `/api/products/{id}` | 删除产品 |
| POST | `/api/products/batch` | 批量创建产品 |
| DELETE | `/api/products/batch` | 批量删除产品 |

### 多数据源演示接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/multi-datasource/overview` | 获取系统概览信息 |
| GET | `/api/multi-datasource/latest` | 获取最新数据 |
| GET | `/api/multi-datasource/health` | 数据源健康检查 |
| GET | `/api/multi-datasource/config` | 获取数据源配置信息 |

## 示例请求

### 创建用户

```bash
curl -X POST http://localhost:8082/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "newuser@example.com",
    "phone": "13900000000",
    "age": 25
  }'
```

### 创建产品

```bash
curl -X POST http://localhost:8082/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "productCode": "P999",
    "productName": "新产品",
    "description": "这是一个新产品",
    "price": 999.99,
    "stock": 100,
    "category": "电子产品"
  }'
```

### 获取系统概览

```bash
curl http://localhost:8082/api/multi-datasource/overview
```

## 核心配置说明

### 数据源配置类

- `PrimaryDataSourceConfig`: 配置主数据源，使用 `@Primary` 注解标记为默认数据源
- `SecondaryDataSourceConfig`: 配置从数据源

### 事务管理

- 主数据源使用 `primaryTransactionManager`
- 从数据源使用 `secondaryTransactionManager`
- 在服务类中通过 `@Transactional(transactionManager = "xxx")` 指定事务管理器

### Mapper扫描

- 主数据源Mapper: `@MapperScan(basePackages = "com.example.primary.mapper")`
- 从数据源Mapper: `@MapperScan(basePackages = "com.example.secondary.mapper")`

## 注意事项

1. **包路径分离**: 不同数据源的Mapper接口必须放在不同的包路径下
2. **事务管理**: 跨数据源的操作需要考虑分布式事务问题
3. **连接池配置**: 根据实际负载调整HikariCP连接池参数
4. **XML映射文件**: 确保XML文件路径与配置中的 `mapper-locations` 一致

## 扩展功能

- 可以添加更多数据源
- 支持读写分离配置
- 集成分布式事务管理
- 添加数据源监控和健康检查

## 故障排除

### 常见问题

1. **数据源连接失败**: 检查数据库连接信息和网络连通性
2. **Mapper找不到**: 确认包扫描路径和XML文件位置
3. **事务不生效**: 检查事务管理器配置和注解使用
4. **SQL执行错误**: 查看MyBatis日志输出

### 日志配置

在 `application.yml` 中启用详细日志：

```yaml
logging:
  level:
    com.example: debug
    org.apache.ibatis: debug
    org.springframework.jdbc: debug
```
