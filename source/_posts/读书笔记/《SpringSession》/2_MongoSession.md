---
title: 《Spring Session》二、Spring Session MongoDB
date: 2019-09-26 10:08:00
tags:
  - Spring Session
  - hide
categories:
  - 读书笔记
  - Spring Session
---

# Spring Session MongoDB

`Spring Session MongoDB` 提供了一个在 `MongoDB` 上存储 Session 的 API 和实现。

最低版本要求：

1. JAVA 8
2. Servlet 2.5+
3. Spring 5.0

### 1. 添加依赖

```xml
<dependencies>
	......
    <dependency>
    	<groupId>org.springframework.session</groupId>
        <artifactId>spring-session-data-mongodb</artifactId>
        <version>${spring-session-data-mongodb.version}</version>
    </dependency>
    <dependency>
    	<groupId>org.springframework.boo</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
    </dependency>
</dependencies>
```

### 2. Spring Configuration

增加一个配置类，用于声明 Spring Session 的实现替换 HttpSession 的实现。

```java
/**
 * 创建一个名为 SpringSessionRepositoryFilter 的过滤器
 *  负责替换 SpringSession 支持的 HttpSession 实现。
 */
@EnableMongoHttpSession
public class HttpSessionConfig {
    @Bean
    public JdkMongoSessionConverter jdkMongoSessionConverter() {
        return new JdkMongoSessionConverter(Duration.ofMinutes(30));
    }
}
```

为了能在 MongoDB 中持久化 Session 对象，需要提供 序列化与反序列化 机制。默认情况下，使用 `JdkMongoSessionConverter`。

您也可以替换使用 JacksonMongoSessionConverter，或者自定义其实现。

### 3. 连接 MongoDB

```properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=test
```

也可以使用 url 的方式直连:

```properties
spring.data.mongodb.url=mongodb://localhost:27017/test
```

### 4. 如何工作的

同 `Spring Session Redis` 一样，将原本在 Tomcat 中的 `HttpSession` 持久化到 SpringSession 的实现中。

在 `Spring Security` 中，`SecurityContextPersistenceFilter` 将 `SecurityContext` 保存到 HttpSession 时，他就会被持久化到 Mongo 中。

### 5. 可能遇到的问题

#### 1. 部分属性序列化失败

```java
@Configuration
@EnableMongoHttpSession
public class SessionConfig {
    /**
     * 使用 Jackson 进行序列化转换，只需要将转换失败的对象，创建一个对应的 mixin 对象即可
     */
    @Bean
    public JacksonMongoSessionConverter jdkMongoSessionConverter() {
        List<Module> modules = new ArrayList<>();

        SimpleModule module = new SimpleModule();
        module.setMixInAnnotation(GrantedAuthority.class, GrantedAuthorityMixin.class);

        SimpleModule module1 = new SimpleModule();
        module1.setMixInAnnotation(LoginUserDetails.class, GrantedAuthorityMixin.class);

        modules.add(module);

        return new SessionConverter(modules);
    }

    private static class GrantedAuthorityMixin {}    
}
```

参考资料：

1. [Spring Data Session MongoDB - DOCS](https://docs.spring.io/spring-session-data-mongodb/docs/2.2.0.BUILD-SNAPSHOT/reference/htmlsingle/boot-mongo.html)
2. [Spring Session MongoDB - DOCS](https://docs.spring.io/spring-session-data-mongodb/docs/2.2.0.BUILD-SNAPSHOT/reference/htmlsingle/index.html)
