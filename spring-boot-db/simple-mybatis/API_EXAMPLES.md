# Simple MyBatis API 使用示例

本文档提供了 Simple MyBatis 模块的 API 使用示例，特别是关于 Gender 枚举的使用。

## 基础信息

- 基础URL: `http://localhost:8080/api/staff`
- 数据格式: JSON

## Gender 枚举说明

Gender 枚举定义了三种性别类型：

| 枚举值 | 数据库代码 | 显示描述 | JSON输出 |
|--------|------------|----------|----------|
| MALE   | 0          | 男       | "0"      |
| WOMAN  | 1          | 女       | "1"      |
| OTHER  | 2          | 其他     | "2"      |

在数据库中存储的是数字代码（0、1、2），通过 GenderTypeHandler 自动转换为枚举类型。
在JSON序列化时，会输出数字代码字符串形式。

## API 接口列表

### 1. 根据ID查询员工

**请求方式:** GET
**请求路径:** `/api/staff/{id}`

**示例:**
```bash
curl -X GET "http://localhost:8080/api/staff/1"
```

**响应示例:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "code": "EMP001",
    "name": "张三",
    "age": 25,
    "gender": "0",
    "phone": "13800138001",
    "email": "zhangsan@example.com",
    "createTime": "2024-01-01T10:00:00",
    "modifyTime": "2024-01-01T10:00:00"
  },
  "message": "查询成功"
}
```

### 2. 根据员工编码查询

**请求方式:** GET
**请求路径:** `/api/staff/code/{code}`

**示例:**
```bash
curl -X GET "http://localhost:8080/api/staff/code/EMP001"
```

### 3. 查询所有员工

**请求方式:** GET
**请求路径:** `/api/staff/all`

**示例:**
```bash
curl -X GET "http://localhost:8080/api/staff/all"
```

### 4. 根据性别查询员工 (新增)

**请求方式:** GET
**请求路径:** `/api/staff/gender/{gender}`

**参数说明:**
- gender: 性别代码，支持 "0"（男）、"1"（女）、"2"（其他）

**示例:**
```bash
# 查询所有男性员工
curl -X GET "http://localhost:8080/api/staff/gender/0"

# 查询所有女性员工
curl -X GET "http://localhost:8080/api/staff/gender/1"

# 查询其他性别员工
curl -X GET "http://localhost:8080/api/staff/gender/2"
```

**响应示例:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "code": "EMP001",
      "name": "张三",
      "age": 25,
      "gender": "0",
      "phone": "13800138001",
      "email": "zhangsan@example.com"
    }
  ],
  "total": 1,
  "message": "查询成功"
}
```

### 5. 获取所有性别枚举值 (新增)

**请求方式:** GET
**请求路径:** `/api/staff/genders`

**示例:**
```bash
curl -X GET "http://localhost:8080/api/staff/genders"
```

**响应示例:**
```json
{
  "success": true,
  "data": {
    "MALE": {
      "code": "0",
      "description": "男"
    },
    "WOMAN": {
      "code": "1",
      "description": "女"
    },
    "OTHER": {
      "code": "2",
      "description": "其他"
    }
  },
  "message": "获取成功"
}
```

### 6. 根据姓名模糊查询

**请求方式:** GET
**请求路径:** `/api/staff/search?name={name}`

**示例:**
```bash
curl -X GET "http://localhost:8080/api/staff/search?name=张"
```

### 7. 根据年龄范围查询

**请求方式:** GET
**请求路径:** `/api/staff/age-range?minAge={minAge}&maxAge={maxAge}`

**示例:**
```bash
curl -X GET "http://localhost:8080/api/staff/age-range?minAge=25&maxAge=30"
```

### 8. 分页查询

**请求方式:** GET
**请求路径:** `/api/staff/page?pageNum={pageNum}&pageSize={pageSize}`

**示例:**
```bash
curl -X GET "http://localhost:8080/api/staff/page?pageNum=1&pageSize=10"
```

### 9. 新增员工

**请求方式:** POST
**请求路径:** `/api/staff`

**请求体示例:**
```json
{
  "code": "EMP006",
  "name": "新员工",
  "age": 28,
  "gender": "0",
  "phone": "13900139006",
  "email": "new@example.com"
}
```

**示例:**
```bash
curl -X POST "http://localhost:8080/api/staff" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "EMP006",
    "name": "新员工",
    "age": 28,
    "gender": "0",
    "phone": "13900139006",
    "email": "new@example.com"
  }'
```

### 10. 批量新增员工

**请求方式:** POST
**请求路径:** `/api/staff/batch`

**请求体示例:**
```json
[
  {
    "code": "EMP007",
    "name": "员工7",
    "age": 25,
    "gender": "1",
    "phone": "13900139007",
    "email": "emp7@example.com"
  },
  {
    "code": "EMP008",
    "name": "员工8",
    "age": 30,
    "gender": "0",
    "phone": "13900139008",
    "email": "emp8@example.com"
  }
]
```

### 11. 更新员工信息

**请求方式:** PUT
**请求路径:** `/api/staff/{id}`

**请求体示例:**
```json
{
  "name": "更新后的姓名",
  "age": 30,
  "gender": "1",
  "phone": "13900139999"
}
```

### 12. 删除员工

**请求方式:** DELETE
**请求路径:** `/api/staff/{id}`

**示例:**
```bash
curl -X DELETE "http://localhost:8080/api/staff/1"
```

### 13. 批量删除员工

**请求方式:** DELETE
**请求路径:** `/api/staff/batch`

**请求体示例:**
```json
[1, 2, 3]
```

### 14. 统计员工总数

**请求方式:** GET
**请求路径:** `/api/staff/count`

**示例:**
```bash
curl -X GET "http://localhost:8080/api/staff/count"
```

**响应示例:**
```json
{
  "success": true,
  "data": {
    "total": 5
  },
  "message": "统计成功"
}
```

### 15. 检查员工编码是否存在

**请求方式:** GET
**请求路径:** `/api/staff/exists/{code}`

**示例:**
```bash
curl -X GET "http://localhost:8080/api/staff/exists/EMP001"
```

**响应示例:**
```json
{
  "success": true,
  "data": {
    "exists": true
  },
  "message": "检查完成"
}
```

## Gender 枚举使用示例

### Java 代码中使用 Gender 枚举

```java
// 创建员工对象
Staff staff = new Staff();
staff.setCode("EMP001");
staff.setName("张三");
staff.setAge(25);
staff.setGender(Gender.MALE);  // 使用枚举

// 根据性别查询
List<Staff> maleStaff = staffService.getByGender(Gender.MALE);

// 根据代码获取枚举
Gender gender = Gender.fromCode("0");  // 返回 Gender.MALE

// 获取枚举的数字代码
String code = Gender.MALE.getCode();  // 返回 "0"

// 获取枚举的描述
String description = Gender.MALE.getDescription();  // 返回 "男"
```

### 数据库中的存储

在数据库中，gender 字段存储的是数字字符串：
- "0" 对应 Gender.MALE（男）
- "1" 对应 Gender.WOMAN（女）
- "2" 对应 Gender.OTHER（其他）

GenderTypeHandler 会自动处理枚举与数据库数字代码之间的转换。

## 错误响应格式

当请求失败时，API 会返回以下格式的错误响应：

```json
{
  "success": false,
  "message": "错误信息描述"
}
```

## 注意事项

1. 所有时间字段使用 ISO 8601 格式
2. 员工编码必须唯一
3. **性别字段使用数字代码：0（男）、1（女）、2（其他）**
4. 分页查询的页码从1开始
5. 批量操作建议一次不超过1000条记录
6. **Gender 枚举提供了类型安全的性别处理**
7. **数据库存储数字代码，Java 代码使用枚举，通过 TypeHandler 自动转换**

## 完整的测试流程示例

### 1. 创建一个完整的员工记录

```bash
# 创建男性员工
curl -X POST "http://localhost:8080/api/staff" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "TEST001",
    "name": "测试男员工",
    "age": 28,
    "gender": "0",
    "phone": "13900139001",
    "email": "male@test.com"
  }'

# 创建女性员工
curl -X POST "http://localhost:8080/api/staff" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "TEST002",
    "name": "测试女员工",
    "age": 25,
    "gender": "1",
    "phone": "13900139002",
    "email": "female@test.com"
  }'

# 创建其他性别员工
curl -X POST "http://localhost:8080/api/staff" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "TEST003",
    "name": "测试其他员工",
    "age": 30,
    "gender": "2",
    "phone": "13900139003",
    "email": "other@test.com"
  }'
```

### 2. 验证Gender枚举功能

```bash
# 查询所有男性员工
curl -X GET "http://localhost:8080/api/staff/gender/0"

# 查询所有女性员工
curl -X GET "http://localhost:8080/api/staff/gender/1"

# 查询所有其他性别员工
curl -X GET "http://localhost:8080/api/staff/gender/2"

# 获取所有性别枚举值
curl -X GET "http://localhost:8080/api/staff/genders"
```

### 3. 数据验证示例

```bash
# 验证员工编码唯一性
curl -X GET "http://localhost:8080/api/staff/exists/TEST001"

# 尝试创建重复编码的员工（应该失败）
curl -X POST "http://localhost:8080/api/staff" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "TEST001",
    "name": "重复编码员工",
    "age": 25,
    "gender": "0"
  }'
```

## 常见错误处理

### 1. 无效的性别代码

如果传入无效的性别代码（如"3"），系统会返回错误：

```json
{
  "success": false,
  "message": "无效的性别代码: 3"
}
```

### 2. 重复的员工编码

```json
{
  "success": false,
  "message": "员工编码已存在: TEST001"
}
```

### 3. 必填字段缺失

```json
{
  "success": false,
  "message": "员工编码不能为空"
}
```
