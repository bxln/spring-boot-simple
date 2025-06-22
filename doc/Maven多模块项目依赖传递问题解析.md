# Maven多模块项目依赖传递问题解析

## 问题描述

在构建多模块Maven项目时，遇到了依赖传递问题。具体表现为：

1. 项目结构为：
   - spring-boot-simple (根模块)
     - spring-boot-dependencies (依赖管理模块)
     - spring-boot-poitl (功能模块)
     - spring-boot-db (中间聚合模块)
       - simple-mybatis (功能模块)

2. 错误信息：
```
[ERROR] Non-resolvable import POM: The following artifacts could not be resolved: com.example:spring-boot-dependencies:pom:0.0.1-SNAPSHOT (absent)
[ERROR] 'dependencies.dependency.version' for org.springframework.boot:spring-boot-starter-web:jar is missing.
[ERROR] 'dependencies.dependency.version' for org.mybatis.spring.boot:mybatis-spring-boot-starter:jar is missing.
[ERROR] 'dependencies.dependency.version' for com.mysql:mysql-connector-j:jar is missing.
[ERROR] 'dependencies.dependency.version' for org.springframework.boot:spring-boot-starter-test:jar is missing.
```

3. 奇怪的现象：
   - 不安装spring-boot-dependencies到本地仓库时，编译simple-mybatis会报错
   - 但编译spring-boot-poitl却不会报错

## 原因分析

这个问题涉及Maven的依赖管理传递机制：

1. **依赖管理传递规则**：
   - 父模块的`<dependencyManagement>`会传递给直接子模块
   - 但中间模块（如spring-boot-db）需要显式重新导入依赖管理才能正确传递给它的子模块

2. **模块打包方式与依赖传递**：
   - 聚合模块必须使用`<packaging>pom</packaging>`
   - 虽然spring-boot-db使用了`<packaging>pom</packaging>`（这是正确的），但这并不意味着它会自动传递父模块的依赖管理
   - 聚合模块不会自动传递导入父模块的依赖管理，需要在中间模块中显式重新导入
   - 这是Maven依赖管理的一个重要特性，而非缺陷

3. **为什么spring-boot-poitl不报错**：
   - spring-boot-poitl直接继承自根模块spring-boot-simple
   - 根模块已经导入了spring-boot-dependencies作为依赖管理
   - 因此spring-boot-poitl能直接获取到所有依赖的版本信息

4. **为什么simple-mybatis报错**：
   - simple-mybatis继承自中间模块spring-boot-db
   - spring-boot-db没有显式导入依赖管理
   - 因此依赖版本信息没有正确传递给simple-mybatis

## 解决方案

有两种解决方案：

### 方案1：先安装spring-boot-dependencies到本地仓库

```bash
mvn clean install -f spring-boot-dependencies/pom.xml
```

这样Maven能够解析到这个模块，从而解决依赖问题。但这只是临时解决方案，不是最佳实践。

### 方案2：在中间模块中添加依赖管理（推荐）

在`spring-boot-db/pom.xml`中添加依赖管理部分：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>${project.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

这样修改后，即使没有预先安装spring-boot-dependencies，simple-mybatis模块也能正确解析依赖版本。

## 最佳实践

1. 在多层嵌套的Maven项目中，每个中间聚合模块都应该显式导入依赖管理
2. 保持聚合模块的打包方式为`pom`
3. 使用`<scope>import</scope>`和`<type>pom</type>`来导入依赖管理
4. 使用属性（如`${project.version}`）来保持版本一致性

## 总结

Maven的依赖管理传递机制在多层模块结构中需要特别注意。通过在中间聚合模块中显式导入依赖管理，可以确保依赖版本信息正确传递给所有子模块，避免构建错误。