---
title: SpringBoot(一)-SpringBootCLI
date: 2018-10-29 18:06:03
categories: 
 - Java成神之路
 - Study社区
---
# SpringBoot(一)-SpringBootCLI

Spring Boot CLI 可以让我们通过 Groovy 语言快速构建一个 Spring Boot 项目。

## 安装CLI

在 [官方](https://repo.spring.io/release/org/springframework/boot/spring-boot-cli/) 下载自己所需的压缩包，解压后的 bin 目录下，包含了可执行的二进制文件。

为了方便使用，我们将 解压路径的bin，添加至系统环境变量中。

执行下面的脚本：

```shell
$ spring --version
```

![安装成功](https://images.gitee.com/uploads/images/2018/1030/110231_e9f51974_1872936.png "屏幕截图.png")

## 创建hello.groovy

然后我们新建一个 `hello.groovy` 文件，并写入一个控制器。

```java
@RestController
class ThisWillActuallyRun{
  @RequestMapping("/")
  String home(){
    "hello world!"
  }
}
```

## 运行

运行该文件，生成 SpringBoot 的环境：

```shell
$ spring run hello.groovy
```

![启动程序](https://upload-images.jianshu.io/upload_images/13603359-4516fb8c6e2e60d3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## 测试

请求测试,或者直接通过浏览器访问，结果都是相同的：

```shell
$ curl localhost:8080
Hello World!
```