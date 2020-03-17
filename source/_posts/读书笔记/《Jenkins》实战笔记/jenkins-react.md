---
title: Jenkins 从零到有部署一个前端项目
date: 2019-08-05 17:47:00
tags: 
  - Jenkins部署
  - hide
categories:
  - 读书笔记
  - Jenkins部署
---

# Jenkins 从零到有部署一个React项目

## 一、下载并启动Jenkins

从 [Jenkins 官网](<https://jenkins.io/download/>)下载所需的版本，这里使用 Docker 容器部署 Jenkins war(你也可以直接使用 `java -jar jenkins.war -httpPort=8080` 来启动，这样的话直接进入 [填写密码(锚点)](#1.  填写密码) 即可。)。

> 注意：我们并没有使用 hub.docker.com 中提供的 jenkins/jenkins 这个镜像。因为我试了一下没装成功，哈哈。

在 war 包的同级目录下，编写 Dockerfile，如下：

```dockerfile
FROM openjdk:8
VOLUME /tmp
COPY jenkins.war app.war
ENTRYPOINT ["java","-jar","app.war","--httpPort=8080"]
```

构建 jenkins 镜像

```sh
#使用Dockerfile构建jenkins镜像
docker build -t luokaiii.jenkins .
```

编写 jenkins 启动脚本， start.sh：

```sh
docker run \
  --name luokaiii.jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -d luokaiii.jenkins
```

> 注意：1. 需要给 start.sh 文件授权，否则无法执行；2. 通过 -v 命令将jenkins文件挂载到本地；

## 二、环境初始化

### 1.  填写密码

运行上面写的 start.sh 启动脚本，并打开 localhost:8080，稍等片刻就会提示我们输入初始密码。

进入 jenkins 容器，`docker exec -it luokaiii.jenkins bash` （如果提示有误，则可以使用 container 的 id 来进入），找到位于 `/root/.jenkins/secrets/initialAdminPassword ` 的密码并填写进入。

### 2. 安装插件

密码输入成功后，会提示我们选择需要安装的插件，这里我选择的是社区推荐插件。对于新入门的用户来说，推荐的往往够用了。

![安装插件中...](https://i.loli.net/2019/08/05/URXsYagSkyl96pP.png)

在插件安装完成之后，我们需要加入以下额外的插件：

1. Publish Over SSH     // 向服务器发送文件
2. SSH         // 需要 SSH 来连接服务器
3. Config File Provider
4. NodeJS	 // 因为我们的测试项目是使用 react 写的一个模拟项目，因此需要NodeJS 与 npm 进行编译

> 添加插件的目录为：Manage Jenkins > Manage Plugins > 可选插件

### 3. 新建一个管理员账户

![创建账户](https://i.loli.net/2019/08/05/CYpL2sb3PlefOAi.png)

ok，安装成功，进入我们的正题。

![欢迎来到 Jenkins](https://i.loli.net/2019/08/05/foPH29MNlGvrXsk.png)

## 三、使用 SSH 登录服务器

配置 SSH Key 有两种方式，一种是在本地生成，然后将公钥发送至服务器；另一种是在服务器生成，将私钥拷贝至本地。(好像没啥区别啊)

### 1.  在本地电脑生成 SSH

1. 打开命令行，生成ssh-key：`ssh-keygen -t rsa`
2. 将ssh-key 发送至服务器：`ssh-copy-id -i ~/.ssh/id_rsa.pub root@xxx.xxx.xxx.xxx`
3. 输入服务器密码，即可上传成功
4. 验证登录：`ssh root@xxx.xxx.xxx.xxx -i id_rsa` 
5. 输入私钥的 password 即可

### 2.  服务器端生成 SSH

1. 使用密码登录远程服务器
2. 执行命令 ssh-keygen ，输入文件地址、密码等信息，如名为 ssh_rsa
3. 将 生成的私钥拷贝出来，放到需要连接的电脑上(可以新建文件，把 ssh_rsa 的内容拷贝进去)
4. 重设本地文件的权限 chmod 0600 ssh_rsa
5. 开启 SSH Key 登录
   1. 在远程服务器中找到 /etc/ssh/sshd_config
   2. 将以下两个参数设置为 on(默认为on)
      1. RSAAuthentication on
      2. PubkeyAuthentication on
6. 关闭 密码 登录(可选，前提是你要确保之前的私钥备份好了，不然可就无法登陆了)
   1. 将 PasswordAuthentication 改为 no
7. 测试使用 SSH Key 登录服务器
   1. ssh root@xxx.xxx.xxx.xxx -p port -i ~/.ssh/id_rsa
   2. xxx.xxx.xxx.xxx 为服务器 ip 地址
   3. port 为服务器登录端口
   4. ~/.ssh/id_rsa 为本地私钥地址

## 四、配置SSH

进入 Manage Jenkins >  Configure System，并作出以下两个修改：

### 1. SSH remote hosts

配置远程服务器

![UTOOLS1564997986052.png](https://i.loli.net/2019/08/05/ulX69MTxD1HFwYQ.png)

### 2.Publish over SSH

配置推送文件时的服务器配置，使用 ssh key

![1564997904177](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1564997904177.png)

## 四、配置完整的任务

新建一个 `Freestyle project`

![手动狗头](http://img.pic35.com/uploads/allimg/170701/1-1FF11312380-L.jpg)

设置源码仓库地址，并添加用户名密码、或 SSHKey 的凭证

![源码管理](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1564996570789.png)

构建前的环境配置，指定服务器地址、[#NodeJS版本]()

![构建环境](https://i.loli.net/2019/08/05/MbAGENWmIod94rP.png)

执行构建，并打印 Node 与 NPM 的版本，来确定是否安装成功；指定 npm 仓库为taobao镜像，并进行编译(react 编译后的文件放在 /build 下，其他语言参照各自特性而定)

![构建](https://i.loli.net/2019/08/05/XPuSnVHjb21pqG7.png)

构建完成后，就是把生成的 build 目录发送至服务器指定目录了。如果您使用了 nginx 作了静态代理，则只需将 build 后的文件发送至该目录即可。

![构建后操作](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1564996807629.png)

## 五、测试是否正确安装及配置

### 1. 随便修改点文件，提交至github上

### 2. 点击立即构建

![立即构建](https://i.loli.net/2019/08/05/QlRNP3zxiLmOeJ6.png)

### 3. 打开控制台输出，查看日志

不出意外的话，控制台中会依次打印出一下日志(日志太长，这里就不贴了，仔细看一下应该都很好理解，也不是什么比较难的单词)

1. git fetch --tags ....   // 从远程拉取代码
2. node -v ； npm -v     // 打印的node版本
3. npm  install --registry...    // 修改npm 镜像源
4. npm run build        // 开始构建
5. [SSH] executing post build script    // 连接并上传build目录

### 4. 重新访问项目地址

