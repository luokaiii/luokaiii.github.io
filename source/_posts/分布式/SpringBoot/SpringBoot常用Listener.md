---
title: SpringBoot中的几种常见监听器
date: 2019-09-04 18:17:03
tags:
  - hide
categories:
  - 分布式
---

# SpringBoot 的几种常见监听器

例如：在 Web 项目启动时，需要执行一段代码，该写在哪里呢？
解决方法：注册监听器监听 ServletContext 创建的钩子函数，并重写自己的实现

## 1. 创建 Listener

```java
@WebListener
public class InitDemo implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Web Initialized!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Web Destroyed!");
    }
}
```

> 使用 @WebListener 注册声明类为监听器

## 2. 添加 servlet 扫描

```java
@SpringBootApplication
@ServletComponentScan(basePackages = "com.test.*")
public class App {
    public static void main(String[] args){
        SpringApplication.run(App.class,args);
    }
}
```

## 3. 测试

启动项目，可在堆栈信息中找到对应的输出

## 4. 常见监听器接口

1. ServletContextListener - 监听 ServletContext 对象的创建及销毁
   1. contextInitialized(ServletContextEventsce) - web 应用程序初始化进程正在启动的通知
   2. contextDestroyed(ServletContextEvent sce) - 通知 servlet 上下文即将关闭。所有 servlet 和过滤器之前已经被销毁()
2. HttpSessionListener - 监听 session 对象的创建及销毁
   1. sessionCreated(HttpSessionEvent se) - 创建会话的通知
   2. sessionDestroyed(HttpSessionEvent se) - 会话即将失效的通知
3. ServletRequestListener - 监听 request 对象的创建及销毁
   1. requestDestroyed(ServletRequestEvent src) - 请求将超出 web 应用程序的范围
   2. requestInitialized(ServletRequestEvent src) - 请求将进入 web 应用程序的范围
4. ServletContextAttributeListener - 监听 servletContext 对象中属性的变化
   1. attributeAdded(ServletContextAttributeEvent scae) - 通知 servlet 上下文中添加了一个新属性。在添加属性后调用
   2. attributeRemoved(ServletContextAttributeEvent scae) - 从 servlet 上下文中删除现有属性的通知。删除属性后调用
   3. attributeReplaced(ServletContextAttributeEvent scae) - 通知 servlet 上下文上的属性已被替换。替换属性后调用。
5. HttpSessionAttributeListener - 监听 session 对象中属性的改变
   1. 同 ServletContextAttributeListener
6. ServletRequestAttributeListener - 监听 request 对象中属性的改变
   1. 同 ServletContextAttributeListener
