---
title: SpringBoot整合SpringSecurity(三)-动态验证权限
date: 2018-11-01 10:18:03
categories: 
 - Java成神之路
 - Study社区
---
# SpringBoot整合SpringSecurity(三)-动态更新用户的权限

用户权限的加载是在 调用了 UserDetailsService.loadUserByUsername(String username) 后构建出的 UserDetails，并将UserDetails 构建出 UsernamePasswordAuthenticationToken，通过 Token 生成了Authentication。

解题思路：

1. 监听 session 的创建和销毁事件，将其保存在map中
2. 若有用户发生权限变更，则通过sessionID 拿到session，从而获取到 SecurityContext上下文，重新加载用户的信息
3. 重新加载的代码类似于 UsernamePasswordAuthenticationFilter 的 attemptAuthentication(request,response) 方法。

遗留问题：

1. 不支持小程序等无状态应用，即使用 Token，而不是 session 记录状态。