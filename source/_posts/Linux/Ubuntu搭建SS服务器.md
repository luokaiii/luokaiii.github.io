---
title: Linux学习之路(一)
date: 2018-09-08 14:46:03
tags:
 - Linux
categories: 
 - Linux学习之路
---
## 一、准备工作

    1.更新软件源：
        sudo apt-get update
    2. 安装 PIP 环境
        sudo apt-get install python-pip
    3. 使用 pip 命令安装 shadowsocks
        sudo pip install shadowsocks
    4. 如果出现异常，可以尝试升级pip
        pip install -U pip

## 二、运行

    (1). 命令启动
        sudo ssserver -p 8388 -k mypassword -m -rc4md5 -d start
    (2). 配置文件
        vim /etc/shadowsocks.json
        添加如下内容：
            {
            "server":"my_server_ip",
            "server_port":8388,
            "local_address":"127.0.0.1",
            "local_port":1080,
            "password":"mypassword",
            "timeout":300,
            "method":"rc4-md5"
            }
            //多个用户的配置
            {
            "server":"my_server_ip",
            "port_password":{
            "9001":"pwd001",
            "9002":"pwd002",
            "9003":"pwd003"
            },
            "local_address":"127.0.0.1",
            "local_port":1080,
            "timeout":300,
            "method":"rc4-md5"
            }
        3. 赋予 shadowsocks.json 文件权限：
            sudo apt-get install python - m2crypto
        4. 使用配置文件在后台运行
            sudo ssserver -c /etc/shadowsocks.json -d start
        5. 配置开机启动
            编辑 /etc/rc.local 文件
                sudo vim /etc/rc.local
            在 exit 0 上加入：
                /usr/local/bin/ssserver - c /etc/shadowsocks.json
                或
                /usr/local/bin/ssserver -p 8388 -k password -m rc4md5 -d start