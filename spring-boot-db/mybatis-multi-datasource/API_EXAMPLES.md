# MyBatis多数据源 API使用示例

本文档提供了详细的API使用示例，展示如何与多数据源应用进行交互。

## 基础信息

- **服务地址**: http://localhost:8082
- **内容类型**: application/json
- **字符编码**: UTF-8

## 用户管理API (主数据源)

### 1. 查询所有用户

```bash
curl -X GET http://localhost:8082/api/users
```

**响应示例**:
```json
[
  {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "phone": "13800000001",
    "age": 30,
    "status": "ACTIVE",
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-01T10:00:00"
  }
]
```

### 2. 根据ID查询用户

```bash
curl -X GET http://localhost:8082/api/users/1
```

### 3. 根据用户名查询用户

```bash
curl -X GET http://localhost:8082/api/users/username/admin
```

### 4. 分页查询用户

```bash
curl -X GET "http://localhost:8082/api/users/page?pageNum=1&pageSize=5"
```

**响应示例**:
```json
{
  "users": [...],
  "totalCount": 10,
  "pageNum": 1,
  "pageSize": 5,
  "totalPages": 2
}
```

### 5. 根据年龄范围查询用户

```bash
curl -X GET "http://localhost:8082/api/users/age-range?minAge=25&maxAge=35"
```

### 6. 创建用户

```bash
curl -X POST http://localhost:8082/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "newuser@example.com",
    "phone": "13900000000",
    "age": 25,
    "status": "ACTIVE"
  }'
```

**响应示例**:
```json
{
  "success": true,
  "message": "用户创建成功",
  "user": {
    "id": 11,
    "username": "newuser",
    "email": "newuser@example.com",
    "phone": "13900000000",
    "age": 25,
    "status": "ACTIVE",
    "createTime": "2024-01-01T15:30:00",
    "updateTime": "2024-01-01T15:30:00"
  }
}
```

### 7. 更新用户

```bash
curl -X PUT http://localhost:8082/api/users/11 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "updateduser",
    "email": "updated@example.com",
    "age": 26
  }'
```

### 8. 删除用户

```bash
curl -X DELETE http://localhost:8082/api/users/11
```

### 9. 批量创建用户

```bash
curl -X POST http://localhost:8082/api/users/batch \
  -H "Content-Type: application/json" \
  -d '[
    {
      "username": "batchuser1",
      "email": "batch1@example.com",
      "phone": "13900000001",
      "age": 25
    },
    {
      "username": "batchuser2",
      "email": "batch2@example.com",
      "phone": "13900000002",
      "age": 28
    }
  ]'
```

### 10. 批量删除用户

```bash
curl -X DELETE http://localhost:8082/api/users/batch \
  -H "Content-Type: application/json" \
  -d '[12, 13, 14]'
```

## 产品管理API (从数据源)

### 1. 查询所有产品

```bash
curl -X GET http://localhost:8082/api/products
```

**响应示例**:
```json
[
  {
    "id": 1,
    "productCode": "P001",
    "productName": "iPhone 15 Pro",
    "description": "苹果最新旗舰手机，搭载A17 Pro芯片",
    "price": 8999.00,
    "stock": 50,
    "category": "手机",
    "status": "ACTIVE",
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-01T10:00:00"
  }
]
```

### 2. 根据产品编码查询

```bash
curl -X GET http://localhost:8082/api/products/code/P001
```

### 3. 根据分类查询产品

```bash
curl -X GET http://localhost:8082/api/products/category/手机
```

### 4. 根据价格范围查询产品

```bash
curl -X GET "http://localhost:8082/api/products/price-range?minPrice=1000&maxPrice=5000"
```

### 5. 产品名称模糊查询

```bash
curl -X GET "http://localhost:8082/api/products/search?productName=iPhone"
```

### 6. 创建产品

```bash
curl -X POST http://localhost:8082/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "productCode": "P999",
    "productName": "新产品",
    "description": "这是一个新产品的描述",
    "price": 999.99,
    "stock": 100,
    "category": "电子产品",
    "status": "ACTIVE"
  }'
```

### 7. 更新产品

```bash
curl -X PUT http://localhost:8082/api/products/13 \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "更新后的产品名称",
    "price": 1199.99,
    "stock": 80
  }'
```

### 8. 更新库存

```bash
curl -X PUT "http://localhost:8082/api/products/13/stock?stock=150"
```

### 9. 删除产品

```bash
curl -X DELETE http://localhost:8082/api/products/13
```

## 多数据源演示API

### 1. 获取系统概览

```bash
curl -X GET http://localhost:8082/api/multi-datasource/overview
```

**响应示例**:
```json
{
  "userStatistics": {
    "totalUsers": 10,
    "activeUsers": 8,
    "inactiveUsers": 2
  },
  "productStatistics": {
    "totalProducts": 12,
    "activeProducts": 11,
    "inactiveProducts": 1
  },
  "message": "多数据源统计信息获取成功"
}
```

### 2. 获取最新数据

```bash
curl -X GET http://localhost:8082/api/multi-datasource/latest
```

**响应示例**:
```json
{
  "latestUsers": [
    {
      "id": 10,
      "username": "wushiyi",
      "email": "wushiyi@example.com",
      "age": 33,
      "status": "ACTIVE"
    }
  ],
  "latestProducts": [
    {
      "id": 12,
      "productCode": "P012",
      "productName": "PlayStation 5",
      "price": 3999.00,
      "category": "游戏机"
    }
  ],
  "message": "最新数据获取成功"
}
```

### 3. 数据源健康检查

```bash
curl -X GET http://localhost:8082/api/multi-datasource/health
```

**响应示例**:
```json
{
  "primaryDataSource": {
    "status": "UP",
    "totalRecords": 10,
    "message": "主数据源连接正常"
  },
  "secondaryDataSource": {
    "status": "UP",
    "totalRecords": 12,
    "message": "从数据源连接正常"
  }
}
```

### 4. 获取数据源配置信息

```bash
curl -X GET http://localhost:8082/api/multi-datasource/config
```

## 错误处理示例

### 1. 用户名已存在

```bash
curl -X POST http://localhost:8082/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "test@example.com"
  }'
```

**错误响应**:
```json
{
  "success": false,
  "message": "用户名已存在: admin"
}
```

### 2. 产品编码已存在

```bash
curl -X POST http://localhost:8082/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "productCode": "P001",
    "productName": "重复产品"
  }'
```

**错误响应**:
```json
{
  "success": false,
  "message": "产品编码已存在: P001"
}
```

### 3. 资源不存在

```bash
curl -X GET http://localhost:8082/api/users/999
```

**响应**: HTTP 404 Not Found

## 使用Postman测试

### 1. 导入环境变量

创建Postman环境，设置以下变量：
- `baseUrl`: http://localhost:8082
- `contentType`: application/json

### 2. 常用请求集合

可以创建以下请求集合：

**用户管理**:
- GET {{baseUrl}}/api/users
- POST {{baseUrl}}/api/users
- PUT {{baseUrl}}/api/users/{{userId}}
- DELETE {{baseUrl}}/api/users/{{userId}}

**产品管理**:
- GET {{baseUrl}}/api/products
- POST {{baseUrl}}/api/products
- PUT {{baseUrl}}/api/products/{{productId}}
- DELETE {{baseUrl}}/api/products/{{productId}}

**多数据源**:
- GET {{baseUrl}}/api/multi-datasource/overview
- GET {{baseUrl}}/api/multi-datasource/health

## 性能测试示例

### 使用Apache Bench (ab)

```bash
# 测试用户查询接口
ab -n 1000 -c 10 http://localhost:8082/api/users

# 测试产品查询接口
ab -n 1000 -c 10 http://localhost:8082/api/products

# 测试多数据源概览接口
ab -n 500 -c 5 http://localhost:8082/api/multi-datasource/overview
```

### 使用curl进行压力测试

```bash
# 并发创建用户测试
for i in {1..10}; do
  curl -X POST http://localhost:8082/api/users \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"testuser$i\",\"email\":\"test$i@example.com\",\"age\":25}" &
done
wait
```

## 监控和调试

### 1. 查看应用日志

```bash
# 查看实时日志
tail -f logs/application.log

# 查看MyBatis SQL日志
grep "==> Preparing" logs/application.log
```

### 2. 数据库连接监控

可以通过以下SQL查看数据库连接状态：

```sql
-- 查看MySQL连接数
SHOW PROCESSLIST;

-- 查看连接状态统计
SHOW STATUS LIKE 'Threads%';
```

### 3. JVM监控

```bash
# 查看JVM内存使用
jstat -gc <pid>

# 查看线程状态
jstack <pid>
```
