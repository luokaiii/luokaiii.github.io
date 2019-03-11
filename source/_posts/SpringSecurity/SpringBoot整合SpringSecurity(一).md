---
title: SpringBoot整合SpringSecurity(一)-简单介绍Demo
date: 2018-11-01 10:18:03
categories: 
 - Java成神之路
 - Study社区
---
# SpringBoot整合SpringSecurity(一)-简单介绍Demo

本文使用的是 SpringBoot + SpringSecurity，做一个 Web端的权限验证框架。大致分为以下几个部分：

1. 初始化Security框架
2. 使用数据库验证
3. 重写`hasAnyAuthority`，改为动态验证
4. 整合 `CAS` 实现多模块权限验证
5. 测试集群状况下的身份验证
6. 解决多重登录问题

下面我们开始第一部分，初始化一个 `Spring Security` 环境:

## 一、引入依赖

新建一个 SpringBoot 项目，引入依赖的方式有很多种：

### 1.通过IDE来初始化

![1.通过IDE来初始化](https://upload-images.jianshu.io/upload_images/13603359-9e0376a4368e75e5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 2.在构建工具中引入依赖

如果是已经创建好的项目，想重新引入依赖，可以在 `POM.xml` 或者 `build.gradle` 中加入以下几个依赖，使用starter的好处是可以快速集成spring框架：

```gradle
implementation('org.springframework.boot:spring-boot-starter-security')
runtimeOnly('mysql:mysql-connector-java')
implementation('org.springframework.boot:spring-boot-starter-data-mongodb')
implementation('org.springframework.boot:spring-boot-starter-web')
testImplementation('org.springframework.boot:spring-boot-starter-test')
testImplementation('org.springframework.security:spring-security-test')
```

注：因为我们后面需要使用 `MySQL` 或者 `MongoDB` 作为数据源，所以这里先引入这两个依赖

这时我们直接请求 `localhost:8080`，会被 security 以未登录身份强制跳转至默认的登录页面:

![Please login](http://koral-home.oss-cn-beijing.aliyuncs.com/18-11-20/72708746.jpg)

## 二、添加登录账号

### 1.HelloController

随便写一个控制器，用于测试登录之后的请求是否通过了。

```java
@RestController
@RequestMapping("/hello")
public class HelloController {
    @GetMapping
    public String hello(){
        return "Hello SpringSecurity !";
    }
}
```

### 2.SecurityConfiguration

创建 `SecurityConfiguration` 安全配置类，在其中添加默认的登录用户。

```java
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 在内存中认证，后面也是通过这里指定数据库验证的
        auth.inMemoryAuthentication()
                // 加密方式
                .passwordEncoder(passwordEncoder())
                // 用户名密码
                .withUser("username").password("password")
                // 权限
                .roles("USER");
    }

    /**
     * 不加密的 密码加密方式
     * 如果不指定密码加密方式，会抛出异常如下：
     *  There is no PasswordEncoder mapped for the id “null”
     */
    @Bean
    public NoOpPasswordEncoder passwordEncoder(){
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }
}
```

重新访问 `localhost:8080/hello`，输入我们设置的账号密码，即可看到内容 `hello world`。