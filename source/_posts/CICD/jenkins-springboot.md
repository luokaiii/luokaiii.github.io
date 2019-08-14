# Docker + Nginx + Jenkins 部署 SpringBoot Jar

本文能解决的问题：

1. 如何使用Docker 部署Jenkins
2. Jenkins 会重写url(使用三级域名)
3. nginx 配置 jenkins、前端、后端
4. 部署 Maven 子项目(单 maven 项目更简单，我相信你们能举一反三)

## 一、docker-compose

编写 docker-compose，连接 nginx 与 jenkins。

> 如果不是使用 Docker 做容器管理，此步可忽略。直接启动 Nginx 与 Jenkins 即可。

```sh
version: '3'

services:
  jenkins:
    container_name: 'jenkins.ci'
    image: 'jenkins/jenkins:lts'
    #ports:  不对外暴露端口，直接使用nginx容器转到8080
      #- '8080:8080'
      #- '50000:50000'
    volumes:
      - '/home/docker-images/jenkins/var/jenkins_home:/var/jenkins_home'
      - '/home/docker-images/jenkins/html:/usr/share/jenkins/html' #挂载编译打包后的存放路径
  nginx:
    container_name: 'nginx'
    depends_on:
      - jenkins
    image: nginx:alpine
    volumes:
      - '/home/luokaiii/backstage_page/html:/usr/www/luokaiii/cn/html'
      - '/home/docker-images/nginx/conf:/etc/nginx/conf.d'
      - '/home/docker-images/nginx/cert:/etc/nginx/cert'
    ports:
      - 80:80 # 对外统一使用80端口
      - 443:443
    restart: always

```

## 二、nginx.conf

```sh
upstream apisupport {
	# 自己部署的后端服务，因为是在容器访问外部服务，所以需要使用内网IP
	# 第二种方法是在 docker-compose 中将 jar 作为一个 service 连接
    server xxx.xx.xx.xxx:9000;
}
# 这里用到了 docker-compose 中的容器，如果没用docker，直接写localhost就可以了。
upstream dk-jenkins {
    server jenkins:8080;
}
server {
    listen 80 default_server;
    listen [::]:80 default_server;
    server_name www.luokaiii.cn;
    return    301 https://$server_name$request_uri; 
}
server {
    listen 80;
    # 我用了一个 三级域名来处理Jenkins的请求，因为 jenkins 会从根目录查找静态资源和发送请求，总是会重写url。
    server_name jenkins.luokaiii.cn;
    location / {
        proxy_pass http://dk-jenkins/;
    }
}
# 如果没有 https 证书，直接将 ssl 以下的部分写在 80 里就行了
server { 
    listen 443 ssl; 
    server_name www.luokaiii.cn luokaiii.cn; 

    # ssl config
    ssl on;   #设置为on启用SSL功能。
    ssl_certificate cert/2292046_www.luokaiii.cn.pem;
    ssl_certificate_key cert/2292046_www.luokaiii.cn.key;
    ssl_session_timeout 5m;
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4;
    ssl_protocols TLSv1 TLSv1.1 TLSv1.2;
    ssl_prefer_server_ciphers on; 

    # 压缩请求，这样服务返回会快一些，毕竟国内带宽很贵的
    gzip on;
    gzip_min_length 1k;
    gzip_comp_level 9;
    gzip_types text/plain application/javascript application/x-javascript text/css application/xml text/javascript application/x-httpd-php image/jpeg image/gif image/png;
    gzip_vary on;
    gzip_disable "MSIE [1-6]\.";

	# 静态资源代理，在Jenkins部署前端项目的文章中，就是把前端静态文件部署在该目录下，直接由nginx代理即可。
    root /usr/www/luokaiii/cn/html;

    location / {
        try_files $uri $uri/ /index.html;
    }

	# 请求转发，将 /api/ 请求转发到 jar 服务上
    location /api/ {
        proxy_pass http://apisupport/;
        proxy_redirect  http://api/support/ https://www.luokaiii.cn/api/;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header Host  $host;
        proxy_set_header X-Real_IP  $remote_addr;
        proxy_set_header REMOTE-HOST $remote_addr;
        proxy_set_header X-Nginx-Proxy true;
        proxy_connect_timeout 300;
        proxy_send_timeout 300;
        proxy_read_timeout 600;
        proxy_buffer_size 256k;
        proxy_buffers 4 256k;
        proxy_busy_buffers_size 256k;
        proxy_temp_file_write_size 256k;
        proxy_next_upstream error timeout invalid_header http_500 http_502 http_503 http_504 http_404;
        proxy_max_temp_file_size 128m;
        proxy_http_version 1.1;
        #proxy_cache mycache;
        #proxy_cache_valid 200 302 1h;
        #proxy_cache_valid 301 1h;
        #proxy_cache_valid any 1m;
    }
}
```

> 这里使用了一个三级域名来代理 Jenkins，因为 Jenkins 总是会从根目录查找静态资源，以及发送请求。如果是 www.luokaiii.cn/jenkins/ 的话总是会被重写为 www.luokaiii.cn，所以直接用三级域名，让它从根目录找好了。
>
> 如果有知道如何代理的大佬也可以告诉我，虚心请教，谢谢。

## 三、SpringBoot 项目

这里项目以我自己写的 Demo [<https://github.com/luokaiii/luokaiii.api>](<https://github.com/luokaiii/luokaiii.api>) 为例，一个 Maven 多级项目，我们只部署其中的 admin-server 模块。

具体的项目是怎么写的就不说了，大致是：打开idea  > 新建 Spring Initializr > 选择maven > 在 Application 中写一个 GetMapping > OK

## 四、进入正题

打开Jenkins，并新建一个 Freestyle（即自由风格的软件项目）。

除了上一节 [Jenkins部署前端React项目](<https://www.jianshu.com/p/e34674f34242>) 中所需的依赖之外，这里还需要 `Maven Integration` 依赖。

### 1. 项目描述（随便写）

![UTOOLS1565082361195.png](https://i.loli.net/2019/08/06/3AErybK1UaCoXgv.png)

### 2. 源码管理

刚创建的用户，Credentials 可能为空，创建一个 username+password(一般是你的git账号密码)就行，(如果觉得不安全，可以使用SSH的方式，自己百度)。

![1565082401841](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1565082401841.png)

> 这里分支使用的是 `develop`，原因：在 Git Flow 中，develop 为开发分支(这里用来做测试服务器的分支)，master 为主分支(即正式服务器的分支)。
>
> 因人而异，你想用 master 也行，反正不是我的项目，哈哈。

### 3. 构建（jenkins与测试服务器在同一台服务器上）

如果你的 jenkins 部署在测试服务器上，那么直接将打包后的 jar 文件移动至指定目录即可。

1. 调用顶层 Maven 目标，使用maven 打包项目
   1. 因为打包的是子项目，所以使用 -pl 参数指定需要打包的子项目，通过 -am 参数开启依赖项目的构建
   2. -Dmaven.test.skip=true 跳过测试(一般对单元测试有严格要求的都不应该跳过此步骤)
2. Send files or execute commands over SSH
   1. 通过 SSH发送文件或执行命令 
   2. 因为jenkins与测试在一起，所以我们只需要执行mv命令即可
   3. 下面的jenkins与服务器分开(比如正式服部署，总不能还浪费带宽吧)，就需要发送将jar发送到服务器然后运行

![UTOOLS1565082655139.png](https://i.loli.net/2019/08/06/nSx9yFpINqrEAlo.png)

>

### 4. 构建（jenkins与测试服务器分离）

跟步骤3没什么太大的区别，只是将 jar 发送到服务器。

![UTOOLS1565083626352.png](https://i.loli.net/2019/08/06/he49txiGX5PQgzT.png)

> 最后一句 `pm2 restart api-support` 类似于 `nohup java -jar xxx.jar ` 我是使用 pm2 管理的jar包，有兴趣的可以自己查一下(或者推荐一下其他的进程管理工具也行)。

## 5. 测试

随便提交一次代码，点击立即构建。经过漫长的等待(maven首次更新、向服务器发送jar包)，打印出以下日志。

![构建成功](https://i.loli.net/2019/08/06/GaonXC9TAKLF1wr.png)

完成，收工！

> 最后提一下我为什么会写两种构建方式，因为我先用步骤 四4 中的构建时，1M (上行才100多k，T_T)小水管发一个 jar 包快二十分钟了。所以想到同服务器还是直接拷贝的比较好。