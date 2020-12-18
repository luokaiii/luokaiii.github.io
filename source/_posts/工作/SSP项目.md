---
title: Kong 之 UIP项目
date: 2020-12-11 09:00:00
categories:
  - Kong
hide: true
tags: 
  - Kong
---
一、UIP 与 Kong 介绍

UIP 以 Kong 为底层实现，是在客户端和服务之间转发 API 通信的 API 网关。还能通过插件扩展其功能。

<!-- More -->

Kong 有两个主要组件：

- Kong Server：基于 Nginx 的服务器，用来接收 API 请求
- PostgreSQL：存储操作数据

二、Kong 的几个主要对象

1. Service 服务

服务实体，即每个上有服务的抽象。

2. Route 路由

路由实体，是进入 Kong 的入口点，定义了要匹配的请求规则，并路由到指定的服务。

指定 hosts、paths、service.id、methods、protocols 等信息。

3. Consumer 消费者

服务的消费者，或者用户。

4. Plugin 插件

Kong 能够通过插件，扩展已有的功能，这些插件在 API 请求、响应的生命周期中被执行。通过 Admin API 将插件配置在 全局或特定路由和服务上。插件大致包括：

- Authentication 身份验证
  - **Basic Auth** 用户名密码认证（Headers）
    - 为消费者创建凭证
    - 使用时，在 Header 中增加 Authentication: Basic base64(username:password)
- Security 权限安全
  - **IP Restriction**（黑白名单）
    - 为服务/路由/消费者/全局，指定 允许/拒绝 访问IP
- Traffic Control 流量控制
  - **ACL **访问控制列表
    - 为服务/路由/消费者/全局，创建 允许/拒绝 访问组
  - **Rate Limiting** 流量控制
    - 控制某一时间段内的访问次数
    - 为服务/路由/消费者/全局，指定限制配置
    - 并返回 Headers 给客户端（包含时间限制、剩余请求数等信息）
- Logging 日志 
  - **Http Log **
    - 将请求与响应发送至 Http 服务器

三、几个注意点

1. 应用系统与服务如何建立关系？

**通过 ACL 控制**：创建 Service 时，检查 Consumer 是否已绑定了 ACL，如果没有的话，创建一个 ConsumerNameGroup 的组，将 ConsumerNameGroup 加入到 Service 的允许访问组中。

2. 应用是如何鉴权的？

**通过 Basic Auth 鉴权**：先为 Consumer 创建一个鉴权用户，在请求时附带请求头 `Authentication: Basic base64(username:password)`

3. 服务与路由的关系？

**服务下可包含多个路由**，路由是 kong 的入口点，并定义规则以匹配客户端请求。路由匹配到后，kong 将请求代理到与路由相关联的服务

4. 各插件的粒度

日志、IP 黑白名单、流量限制，均可以细化到 路由层。现在使用的都是全局配置。

5. 视图

视图为我们自己管理的，非 Kong 的功能。目前只看到表关系。