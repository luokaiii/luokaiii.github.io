---
title: Linux后台部署Jar
date: 2018-09-08 14:46:03
tags:
  - Linux
  - hide
categories:
  - Linux
---

# 部署

1. 安装 git

2. 拉取代码

   1. 如果不是通过 ssh 的方式拉取的话，每次都需要输入密码

   2. 解决方法：进入项目目录，输入：

   3. ```shell
      git config --global credential.helper store
      ```

   4. 再重新 pull 一次，输入密码之后，以后就不用输入密码了

3. 创建可执行文件，用于拉取代码、构建项目

   1. 需要给可执行文件授权：chmod 777 build.sh
   2. 注意权限的大小

4. 创建可执行文件，用于执行 jar 包

   1. 执行文件可以直接通过 java -jar 来执行

   2. 但是我们需要后台执行 ，用到 nohup

   3. ```shell
      nohup /usr/local/node/bin/node /www/im/chat.js >> /usr/local/node/output.log 2>&1 &
      ```

   4. 第二种，是将 jar 包注册为服务，通过 service stop 等方法启动停止

5. 部署前端项目

   1. 安装 nodejs

   2. 安装 yarn，注意，17.0 以上的 ubuntu 需要先卸载 cmdtest

      1. ```java
         sudo apt remove cmdtest
         ```

      2. ```java
         curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | sudo apt-key add -
         echo "deb https://dl.yarnpkg.com/debian/ stable main" | sudo tee /etc/apt/sources.list.d/yarn.list
         sudo apt-get update && sudo apt-get install yarn
         ```

重启：

```java
#!/bin/bash
ps -ef | grep "/opt/hifox/PGC/teacherApp/teacherapp-0.0.1-SNAPSHOT.jar" | grep -v grep | awk '{print $2}' | xargs kill -15

pid=`ps -ef | grep "/opt/hifox/PGC/teacherApp/teacherapp-0.0.1-SNAPSHOT.jar" | grep -v grep | awk '{print $2}'`
while( ! test -z $pid)
do
 sleep 1
 pid=`ps -ef | grep "/opt/hifox/PGC/teacherApp/teacherapp-0.0.1-SNAPSHOT.jar" | grep -v grep | awk '{print $2}'`
echo -n "*"

done

nohup java -jar teacherapp-0.0.1-SNAPSHOT.jar >/dev/null 2>&1 &
```

以外部配置文件启动 jar
加载 properties 优先级：

            1. jar包同级目录下，config目录下的application.properties
                  jar包同级目录下的application.properties
                       3. 最后才是jar内的application.properties
                       依旧是 java -jar app.jar，默认按照上面的加载优先级加载。
                       也可以手动指定 java -jar app.jar -Dspring.config.location=application.properties

注意：cas 和 common 需要是同一个数据库

通过 maven 的默认仓库位置在 /root/.m2 ，这个文件夹可能被隐藏了

查找方式为：find / .m2 ，表示查找"/"目录下的 .m2 文件或文件夹

注意：当在 shell 终端中使用 ctrl+s 表示暂停当前命令行窗口，取消暂停为：ctrl+q
