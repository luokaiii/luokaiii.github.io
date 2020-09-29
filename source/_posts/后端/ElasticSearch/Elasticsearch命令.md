---
title: ElasticSearch 常见命令
date: 2020-08-10 10:10:00
tags:
  - ElasticSearch
  - hide
categories:
  - 后端
  - java
  - ElasticSearch
---

| 命令                 | 响应                                                         | 命令描述                          | 字段描述                                                     |
| -------------------- | ------------------------------------------------------------ | --------------------------------- | ------------------------------------------------------------ |
| GET /_cluster/health | {<br/>  "cluster_name" : "elasticsearch",<br/>  "status" : "yellow",<br/>  "timed_out" : false,<br/>  "number_of_nodes" : 1,<br/>  "number_of_data_nodes" : 1,<br/>  "active_primary_shards" : 7,<br/>  "active_shards" : 7,<br/>  "relocating_shards" : 0,<br/>  "initializing_shards" : 0,<br/>  "unassigned_shards" : 1,<br/>  "delayed_unassigned_shards" : 0,<br/>  "number_of_pending_tasks" : 0,<br/>  "number_of_in_flight_fetch" : 0,<br/>  "task_max_waiting_in_queue_millis" : 0,<br/>  "active_shards_percent_as_number" : 87.5<br/>} | 显示 Elasticsearch 的集群监控信息 | status: 集群健康状况 grren 正常 / yellow 主分片正常 / red 有主分片未正常运行 |
|                      |                                                              |                                   |                                                              |
|                      |                                                              |                                   |                                                              |
|                      |                                                              |                                   |                                                              |

