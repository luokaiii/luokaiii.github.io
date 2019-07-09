# spring-messaging模块解析

spring-messaging 模块为集成 messaging api 和消息协议提供支持。

![springframework:spring-messaging](https://i.loli.net/2019/07/09/5d2435f4c338214322.png)

从其目录结构可以看出，spring-messaging 共包含六个基本模块：

### 1. base模块

base模块中定义了 消息Message(MessageHeader和body)、消息处理 MessageHandler、发送消息 MessageChannel。

### 2. converter 模块

converter模块主要对消息转换提供支持，如 string、json、byte[] 之间的相互转换。

### 3. core 模块

提供消息的模板方法

### 4. handler 模块

- HandlerMethod 封装了一个 Bean 的方法相关信息，提供了访问方法参数的便捷工具。可以将HandlerMathod 认为是将 conditions 映射到 message 的约定
- HandlerMethodArgumentResolver 解析方法参数到 Context 中指定 Message 的参数值的策略接口
- HandlerMethodReturnValueHandler 处理从触发一个 Message 的method handling 返回值的策略接口

### 5. simp 模块

包含诸如 STOMP 协议的简单消息协议的通用支持。

### 6. support 模块

提供了 Message 的实现，及创建消息的 MessageBuilder 和获取消息头的 MessageHeaderAccessor，还有各种不同的 MessageChannel 实现和 channel interceptor 支持。

### 7. tcp 模块

通过 TcpOperations 建立 TCP connection

通过 TcpConnectionHandler 处理消息和通过 TcpConnectionf 发送消息的抽象及实现

包含了基于 Reactor 的 tcp 消息支持。