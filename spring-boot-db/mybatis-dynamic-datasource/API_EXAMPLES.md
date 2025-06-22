# MyBatis动态数据源 API使用示例

## 基础信息
- **服务地址**: http://localhost:8084
- **特色**: 动态数据源切换，读写分离

## 核心API示例

### 1. 客户管理
```bash
# 查询客户 (从库)
curl -X GET http://localhost:8084/api/customers

# 创建客户 (主库)
curl -X POST http://localhost:8084/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "customerCode": "C999",
    "customerName": "新客户",
    "email": "newcustomer@example.com",
    "age": 25
  }'

# 更新客户 (主库)
curl -X PUT http://localhost:8084/api/customers/1 \
  -H "Content-Type: application/json" \
  -d '{"age": 26}'
```

### 2. 订单管理
```bash
# 查询订单 (从库)
curl -X GET http://localhost:8084/api/orders

# 创建订单 (主库)
curl -X POST http://localhost:8084/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orderNo": "ORD999",
    "customerId": 1,
    "customerName": "张三",
    "totalAmount": 1999.99
  }'

# 支付订单 (主库)
curl -X PUT "http://localhost:8084/api/orders/1/pay?paymentMethod=支付宝"
```

### 3. 动态数据源演示
```bash
# 强制使用主库查询
curl -X GET http://localhost:8084/api/dynamic-datasource/master-query

# 强制使用从库查询  
curl -X GET http://localhost:8084/api/dynamic-datasource/slave-query

# 自动判断数据源
curl -X GET http://localhost:8084/api/dynamic-datasource/auto-datasource

# 数据源健康检查
curl -X GET http://localhost:8084/api/dynamic-datasource/health

# 获取配置信息
curl -X GET http://localhost:8084/api/dynamic-datasource/config

# 性能测试
curl -X GET http://localhost:8084/api/dynamic-datasource/performance
```

## 响应示例

### 客户创建响应
```json
{
  "success": true,
  "message": "客户创建成功",
  "customer": {
    "id": 11,
    "customerCode": "C999",
    "customerName": "新客户",
    "status": "ACTIVE"
  },
  "dataSource": "master"
}
```

### 数据源健康检查响应
```json
{
  "masterDataSource": {
    "status": "UP",
    "message": "主库连接正常",
    "dataSource": "master"
  },
  "slaveDataSource": {
    "status": "UP", 
    "message": "从库连接正常",
    "dataSource": "slave"
  }
}
```

## 验证读写分离

### 自动路由规则
- **读操作** (GET请求): 自动使用从库
- **写操作** (POST/PUT/DELETE): 自动使用主库
- **强制指定**: 通过@DataSource注解覆盖自动规则

### 验证方法
1. 查看响应中的`dataSource`字段
2. 观察应用日志中的数据源切换信息
3. 使用健康检查接口验证连接状态

## 测试命令

```bash
# 运行单元测试
mvn test

# 启动应用
mvn spring-boot:run

# 性能测试
ab -n 1000 -c 10 http://localhost:8084/api/customers

# 并发测试
for i in {1..5}; do
  curl -X GET http://localhost:8084/api/customers &
done
wait
```

## 数据库准备

```bash
# 初始化主数据库
mysql -u root -p < src/main/resources/sql/master_db_schema.sql

# 初始化从数据库
mysql -u root -p < src/main/resources/sql/slave_db_schema.sql
```

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
```

## 监控调试

启用详细日志查看数据源切换：
```yaml
logging:
  level:
    com.example: debug
    org.springframework.aop: debug
```
