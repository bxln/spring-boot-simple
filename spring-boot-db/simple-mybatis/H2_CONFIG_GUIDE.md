# H2数据库配置指南

## 配置优化说明

### 1. 数据库URL参数详解

```properties
spring.datasource.url=jdbc:h2:mem:spring-boot-simple;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE
```

**参数说明：**
- `mem:spring-boot-simple` - 使用内存数据库，名称为spring-boot-simple
- `DB_CLOSE_DELAY=-1` - 防止数据库在最后一个连接关闭时立即关闭
- `DB_CLOSE_ON_EXIT=FALSE` - JVM退出时不自动关闭数据库
- `MODE=MySQL` - 兼容MySQL语法模式
- `DATABASE_TO_LOWER=TRUE` - 数据库名转为小写
- `CASE_INSENSITIVE_IDENTIFIERS=TRUE` - 标识符不区分大小写

### 2. 连接池配置

```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
```

### 3. H2控制台配置

```properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.h2.console.settings.web-allow-others=true
```

**访问方式：**
- URL: http://localhost:8081/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- 用户名: sa
- 密码: (空)

### 4. MyBatis优化配置

```properties
mybatis.configuration.cache-enabled=false
mybatis.configuration.use-generated-keys=true
```

## 常见问题解决

### 问题1: 中文乱码
**解决方案：**
1. 确保文件编码为UTF-8
2. 添加编码配置
3. 使用logback配置UTF-8输出

### 问题2: 数据库连接失败
**检查项：**
1. H2依赖是否正确添加
2. 数据库URL格式是否正确
3. 初始化脚本是否存在

### 问题3: 测试数据不生效
**解决方案：**
1. 检查schema.sql和data.sql路径
2. 确认spring.sql.init.mode=always
3. 验证SQL语法是否正确

## 测试运行命令

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=SimpleMybatisApplicationTests

# 使用UTF-8编码运行测试
mvn test -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8
```
