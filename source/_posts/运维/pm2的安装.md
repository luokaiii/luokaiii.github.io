---
title: PM2 - 进程管理工具
date: 2020-03-11
---

# 使用 pm2 部署 SpringBoot 项目

<!-- more -->

## 一、准备pm

1. 安装nodejs
   1. `yum install nodejs`
2. 查看npm版本
   1. `npm -v`
3. 安装pm2
   1. `npm install -g pm2`

## 二、编写 pm2.json

在 jar 包所在的同级目录，编写 `pm2.json`：

```json
{
    "name": "admin-server",
    "script": "/usr/bin/java",
    "args": [
        "-jar",
        "admin-server-0.0.1-SNAPSHOT.jar"
    ],
    "exec_interpreter":"",
    "exec_mode": "fork"
}
```

## 三、启动进程

使用 `pm2.json` 启动 jar 包

```sh
$ pm2 start pm2.json
```

## 四、查看进程列表

```sh
$ pm2 list
```

![查看进程列表](https://luokaiii.oss-cn-shanghai.aliyuncs.com/blog/operate/pm2list.png)

## 五、其它命令

- `pm2 restart id/Appname` 重启进程
- `pm2 stop id/Appname` 停止进程
- `pm2 logs id/Appname` 查看进程输出