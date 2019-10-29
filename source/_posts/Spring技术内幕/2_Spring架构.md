# 第一章 Spring 的设计理念和整体架构

## 一、设计目标

Spring 为开发者提供一个一站式的轻量级应用开发框架(平台)。

在 Java EE 的应用开发中，支持 POJO 和使用 JavaBean 的开发方式，使应用面向接口开发，充分支持 OO(面向对象) 的设计方法。

## 一、Spring中的各个子项目

打开 [Spring.io](https://spring.io/projects ) 能看到 Spring 支持的所有模块：

1. Spring Boot：更快地配置、启动和运行 Spring 应用程序
2. Spring Framework(Core)：提供依赖注入、事务管理、web支持、DB访问、消息传递等核心支持
3. Spring Cloud Data Flow：
4. Spring Cloud：为常见的分布式系统提供用于建立和部署微型服务的工具
5. Spring Data：为数据访问提供一致的方法，包含关系型、非关系型、map-reduce等
6. Spring Integration：企业集成模块
7. Spring Batch：简化大批量处理操作
8. Spring Security：全面可扩展的身份验证和授权支持
9. Spring Rest Docs：对 RESTful服务进行文档化
10. Spring AMQP：AMQP 消息传递的解决方案
11. Spring Web Flow
12. Spring Web Services：SOAP web 服务的开发
13. Spring LDAP：轻量级目录访问协议的开发
14. Spring Session：管理用户会话信息的API 和实现
15. Spring Shell：为使用基于 spring 的编程模型构建命令行应用程序提供了基础
16. Spring Kafka：Kafka 的抽象
17. Spring Scala：Spring 与 Scala 的结合

其中有几个没用过的就没加上

## 二、Spring 的整体架构

![UTOOLS1572233510528.png](https://i.loli.net/2019/10/28/Pjx6kau89h4GeyV.png)

Spring架构分为以下七个模块：

1. Spring IOC：包含了基本的IOC容器 BeanFactory的接口与实现
2. Spring AOP：集成了 AspectJ 作为 AOP 的特定时限，围绕着 AOP 的增强功能，作为 Spring 集成其他模块的工具，如 TransactionProxyFactoryBean 声明式事务处理便是通过 AOP 集成到 Spring中的。Spring AOP 实现了一个完整的 AOP 代理对象，实现 AOP 拦截器、直至实现各种 Advice 通知的过程
3. Spring MVC：包含对 Web 应用的支持。该模块以 DispatcherServlet 为核心，实现了 MVC 模式，包含与 Web 容器环境的集成，web 请求的拦截、分发、处理和 ModelAndView 数据的返回，以及如何继承各种 UI 视图的展现，如 PDF、Excel等
4. Spring JDBC/ORM：对于关系型数据库的数据处理的支持。除了 JdbcTemplate 外，还提供了对其他 ORM 工具的封装，如 Hibernate、iBatis等
5. Spring 事务：是一个通过 Spring AOP 实现自身功能增强的典型模块。通过 AOP 的切面增强实现了声明式事务处理的功能，包含怎样配置事务处理的拦截器，怎样读入事务配置属性，并结合事务配置属性对事务对象进行处理（如事务的创建、挂起、提交、回滚等基本过程），还可以看到具体的事务处理器（DataSourceTransactionManager、HibernateTransactionManager、JtaTransactionManager等）是怎样封装不同的事务处理机制的。
6. Spring 远程调用：远端调用为应用提供解耦，解耦后将应用模块分布式地部署，从而提高系统整体的性能
7. Spring 应用：对于 Spring 子项目的应用支持。如Spring 处理安全问题的 Spring Security、J2EE 实现规范的接口、对 JMS、JNID、JMX等的支持等等

## 三、Spring 的应用场景

在 Spring 这个一站式的应用平台或框架中，各个模块除了依赖 IoC 容器和 AOP 之外，相互之间并没有很强的耦合性。

Spring 设计时的轻量级特性，以及推崇 POJO 开发，所以使用起来非常灵活。

Spring 的特点：

- Spring 是一个非侵入性框架，其目标是使应用程序代码对框架的依赖最小化，应用代码可以在没有 Spring 或其他容器的情况下运行
- Spring 提供了一个一致的编程模型，使应用直接使用 POJO 开发，从而可以与运行环境隔离开
- Spring 改进了体系结构的选择，Spring 可以帮助我们选择不同的技术实现，如 Hibernate 到 Mybatis、Struts 到 SpringMVC 等，降低了平台锁定的风险(虽然一般不会这样做)

## 四、总结

简要回顾了 Spring 的设计理念、架构设计和应用场景，对各个模块进行了简要的介绍。

后面会详细针对 **Spring 的核心实现(IoC、AOP)**、__Spring 的常用组件(Web、数据库、事务、远端调用)__ 等几个模块进行深入阐述。