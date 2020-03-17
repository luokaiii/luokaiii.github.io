---
title: 《Spring Session》一、Spring Session Redis
date: 2019-09-26 10:00:00
tags:
  - Spring Session
categories:
  - 读书笔记
  - Spring Session
visible: hide
---

# Spring Session Redis

在 SpringBoot web 应用程序中，使用 Spring Session 来支持 HttpSession。

### 1. 添加依赖

```xml
<dependencies>
	......
    <dependency>
    	<groupId>org.springframework.session</groupId>
        <artifactId>spring-session-data-redis</artifactId>
    </dependency>
</dependencies>
```

Spring Boot 为 Spring Session 提供了依赖管理，所以不需要声明依赖的版本。

### 2. 修改配置

```properties
# Session 的存储方式
spring.session.store-type=redis

# Session 会话超时时间 / 秒
server.servlet.session.timeout=600
# Session 的刷新策略
spring.session.redis.flush-mode=on-save
# 用于存储会话的键名
spring.session.redis.namespace=spring:session

# 连接 Redis
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=password
```

> `spring.session.store-type=redis` 相当于手动添加注解 `@EnableRedisHttpSession`。此操作将会创建一个名为 `SpringSessionRepositoryFilter` 的过滤器，拦截并替换 SpringSession 支持的 HttpSession 实现。

至此，启动项目并访问项目地址后，用户的 Session 信息存储在 Redis 而不是 Tomcat 的 HttpSession 实现中。

### 3. 如何工作的

Spring Session 用 Redis 支持的实现来替换 HttpSession。当 Spring Security 的 SecurityContextPersistenceFilter 将 SecurityContext 保存到 HttpSession 时，它将被持久化到 Redis 中。

当一个新的 HttpSession 被创建时，Spring Session 会在你的浏览器中创建一个名为 Session 的 cookie，该 cookie 包含会话的 ID，可以查看 cookies。

参考资料：

1. [Spring Session Redis - DOCS](https://docs.spring.io/spring-session/docs/current/reference/html5/guides/boot-redis.html)
