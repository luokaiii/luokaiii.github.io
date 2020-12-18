---
title: Nginx 之 Kong网关
date: 2020-12-11 09:00:00
categories:
  - Kong
hide: true
tags: 
  - Kong
---
# 运行 SSP 项目

在 Ubuntu18 上安装 PostgreSQL、Kong。

<!-- More -->

## 一、安装 PostgreSQL

```sh
# 使用 apt-get 安装 PostgreSQL
$ sudo apt-get update
$ sudo apt-get install postgresql postgresql-client

# 修改 pg_hba.conf 允许本地用户访问
$ sudo vim /etc/postgresql/12/main/pg_hba.conf
# local all postgres trust
# local all all      trust

# 进入 postgre 用户
$ sudo -i -u postgres

# 进入 postgres，出现版本号及 help 表示安装成功
$ psql
psql (9.5.23)
Type "help" for help.

# 创建用户 kong 及数据库 kkong
postgres=# create user kong;
postgres=# create database kong owner kong;

# 退出
postgres=# \q
```

## 二、安装 Kong

```sh
# 下载 kong 的 deb 包
$ wget https://bintray.com/kong/kong-deb/download_file?file_path=kong-2.2.0.xenial.amd64.deb

# 安装
$ sudo dpkg -i kong-2.2.0.xenial.amd64.deb

# 复制默认配置文件，并对之进行修改
$ cp /etc/kong/kong.conf.default /etc/kong/kong.conf

# 修改配置
$ vim /etc/kong/kong.conf

admin_listen = 127.0.0.1:8001 reuseport backlog=16384, 127.0.0.1:8444 http2 ssl reuseport backlog=16384
pg_host = 127.0.0.1             # Host of the Postgres server.
pg_port = 5432                  # Port of the Postgres server.
pg_user = kong                  # Postgres user.
pg_password =                   # Postgres user's password.
pg_database = kong              # The database name to connect to.


```