---
title: SVN简介
date: 2020-12-08 09:00:00
categories:
  - svn
top: true
tags: 
  - svn
---
# SVN 学习

## 一、SVN 简介

SVN(Subversion) 是一个开源的版本控制系统。

<!-- More -->

**SVN中的一些概念**：

- repository（源代码库）：源代码存放的地方
- Checkout（提取）：当手上没有源代码时，需要从 repository checkout 一份
- Commit（提交）：修改后的代码，commit 到 repository
- Update（更新）：用于与 repository 上的源代码同步

**SVN的主要功能**：

- 目录版本控制
- 真实的版本历史
- 自动提交
- 纳入版本控管的元数据
- 选择不同的网络层
- 一致的数据处理方式
- 有效的分支与标签
- Hackability

**对比 CVS 的优势**：

1. 原子提交：单次提交的所有文件，作为一个整体提交，不会因意外中断导致数据损坏或不完整。
2. 版本历史记录中，保存有：重命名、复制、删除文件等动作。
3. 对于二进制文件，空间更节省（只保存和上一版的差异之处）
4. 目录具有版本历史
5. 分支的开销非常小
6. 优化数据库访问：部分操作不需要访问数据库就能做到，减少了与数据库之间的网络流量

## 二、SVN 安装

1. 下载安装文件：https://sourceforge.net/projects/win32svn/
2. 双击运行
3. 测试，运行：`svnserve --version`

## 三、SVN 生命周期

1. **创建版本库**：Create，创建一个新的版本库
2. **检出**：Checkout，从版本库创建一个工作副本，即个人开发空间
3. **更新**：Update，从版本库更新本地的工作副本
4. **执行变更**：对工作副本的修改，在 commit 之前都不会影响版本库
5. **复查变化**：Status，检查工作副本中的改动，通过 diff 查看变动的详细信息
6. **修复错误**：Revert，重置单个/多个文件/目录
7. **解决冲突**：Merge，合并可合并的分支，不可合并的会被标记出来，通过 Resolve 找出冲突，并修改
8. **提交更改**：Commit，提交工作副本至版本库

## 四、启动

```sh
# 新建版本库目录
$ mkdir /opt/svn

# 创建版本库
$ svnadmin create /opt/svn/runoob

# 启动服务
$ svnserve -d -r /opt/svn/runoob --listen-port 3690

# 访问
$ svn co svn://127.0.0.1/
```