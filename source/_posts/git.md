---
title: Git多账户配置
date: 2018-09-28 16:51:15
tags: 
  - Testing
  - Another Tag
---
# 同时使用多个Git源（Github,Gitee,Gitlab等）

在很多情况下，我们在同一台PC上可能需要关联多个 `Git 源`。比如公司的项目放在 `gitee` 上，但是个人有很多学习的小项目都放在 `github` 上，此时就需要有能连接多个 `ssh` 的需求。

## 第一步：为每个 git 在线服务生成一对公私钥

```node
$ ssh-keygen -t rsa -C "yourEmail@email.com"

Enter file in which to save the key (/c/Users/user/.ssh/id_rsa): /c/User/user/.ssh/id_rsa_github
```

我们以 id_rsa_github 为生成的公私钥文件命名，并指定文件的存储路径为 "/c/User/user/.ssh/" 下

![生成的公私钥](https://upload-images.jianshu.io/upload_images/13603359-accb6aef01836f8e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

如果你需要多个服务，则依次 `重复第一步` 即可。
`注意每一对公私钥的命名不能重复。`

## 第二步：在 /c/User/user/.ssh/ 下创建 config 文件，并填入刚刚的公私钥名

```groovy
Host gitee.com
  Hostname gitee.com
  PreferredAuthentications publickey
  IdentityFile ~/.ssh/id_rsa_gitee
  User koral

Host github.com
  Hostname github.com
  PreferredAuthentications publickey
  IdentityFile ~/.ssh/id_rsa_github
  User koral
```

这一步表示，在git发起 ssh 链接时，对应着哪一个 ssh 私钥。这里我只以 码云( `gitee@OSC` )和 `github` 为例。

不要问我 `~/.ssh/` 前面的 `~` 表示什么意思。我也不会告诉你，它表示当前PC用户的根目录的。至于你想设置为全局的，我也不知道。

## 第三步：将生成的公钥添加至各自的 SSH 中

这里以 Github 为例，其他几个网站类似：

### 在 SSH keys 中"New SSH key"

![找到github的ssh](https://upload-images.jianshu.io/upload_images/13603359-c8f3bdda9deef6da.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 写入生成的SSH

找到第一步中生成的SSH公钥(以".pub"结尾的)，并填入，Title随便写
![image.png](https://upload-images.jianshu.io/upload_images/13603359-1381194685beccaa.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## 第四步：测试

```shell
ssh -T git@github.com
ssh -T git@gitee.com
```

如果提示了什么不得了的东西，请先删除 "/c/User/user/.ssh/" 目录下的 "known_hosts"，并重新执行测试。它会重新生成一个。
当出现 `Hi koral! You've successfully authenticated, but GitHub does not provide shell access.` 时，恭喜你，连接成功。