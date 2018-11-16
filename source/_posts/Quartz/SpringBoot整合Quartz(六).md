---
title: SpringBoot整合Quartz(六)-使用Elastic-Job
date: 2018-11-05 10:18:03
categories: 
 - Java成神之路
 - Study社区
---

Elastic-Job 的底层还是使用的Quartz，只是节点通过 Zookeeper 来进行分配。

Quartz只能解决HA(高可用)

1. But 这里有一个问题，ElasticJob 的开发代码中使用了 lombok，但是并没有引入该框架，所以需要我们在项目中引入 lombok。
2. 因为项目使用 Zookeeper 作为注册中心，所以需要自行安装和配置 Zookeeper。