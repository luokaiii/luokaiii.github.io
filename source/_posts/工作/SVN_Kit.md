---
title: SVNKit - JAVA API操作svn
date: 2020-12-08 09:00:00
categories:
  - svn
top: true
tags: 
  - svn
---
## 一、SVN 快速开始

介绍 SVN 的安装运行、存储库、用户、以及基础的 SVN WC 操作。

<!-- More -->

#### 1.1 下载安装软件

下载 Subversion、TortoiseSVN，并安装。安装后检查环境变量中是否存在 svn/bin 的变量。

打开 cmd，运行 `svn --version` 查看 svn 是否安装正确。

#### 1.2 创建版本库 Repository

方法一：命令行运行 **svnadmin create repository**

方法二：右键 > TortoiseSVN > create repository here

#### 1.3 配置用户和权限

1. 修改 conf/svnserve.conf，password-db = password
2. 修改 conf/passwd，去掉最后两行注释

#### 1.4 运行存储库服务

命令行中执行：`svnserve -d -r repository`，服务就已经启动了

#### 1.5 初始化导入

1. 在另一个目录中，创建文件 test.txt
2. 右键 > TortoiseSVN > Import
3. url 中填入：svn://localhost/（localhost为主机名，可以换位ip）
4. 如果没有报错，此时数据就已经导入到了版本库中

#### 1.6 基本操作

1. 获取工作副本：在任意空目录下，右键 > Checkout > URL: svn://localhost/
2. 在工作副本中修改并提交
   - 编辑 test.txt
   - 右键 > Commit
   - 此时修改就提交到了版本库
3. 查看修改
   - 在 test.txt 上右键 > TortoiseSVN > Show Log 即可查询到对文件的所有提交
   - 在提交版本上右键 > CompareWith working copy，可以比较版本区别

## 二、SVNKit

SVNkit 是一个纯 JAVA 的 Subversion 客户端库，因此无需安装任何 Subversion 客户端。

SVNKit 的 API 主要分为两类：High Level API 和 Low Level API。

### 2.1 High Level API

通过 SVNClientManager，访问多个接口，这些接口几乎实现了 Subversion 所能执行的所有操作，如：checking out、更新、提交、获取历史版本、比较版本差异、浏览存储库等。

#### 1. SVNLogClient

SVNLogClient 可以获得版本修订历史记录、浏览存储库条目、文件内容注释等。

| SVNLogClient 方法 | Subversion 命令 | 方法说明           |
| ----------------- | --------------- | ------------------ |
| doLog()           | svn log         | 获取版本的修订历史 |
| doList()          | svn list        | 获取存储库条目树   |
| doAnnotate()      | svn blame       | 获取文件内容注释   |

#### 2. SVNUpdateClient

SVNUpdateClient 可以 checkout、更新、切换工作副本，也可以从存储库中导出目录或文件。

| SVNUpdateClient 方法 | Subversion 命令                     | 方法说明                                       |
| -------------------- | ----------------------------------- | ---------------------------------------------- |
| doCheckout()         | svn checkout                        | 从存储库中检出工作副本                         |
| doUpdate()           | svn update                          | 把工作副本更新为最新版本或某个指定版本         |
| doSwitch()           | svn switch                          | 把工作副本更新为同一个存储库的不同分支上的版本 |
| doRelocate()         | svn switch --relocate oldURL newURL | 把工作副本更新为不同的存储库中的版本           |
| doExport()           | svn export                          | 从存储库中导出目录或文件                       |

#### 3. SVNWCClient

SVNWCClient 提交了许多与本地工作副本相关的操作，也能访问存储库。

| SVNWCClient方法         | Subversion命令                                               | 方法说明                                                     |
| ----------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| doAdd()                 | svn add                                                      | 添加文件、目录到工作副本，并且预添加到存储库。在下次提交上传并添加到存储库中 |
| doDelete()              | svn delete                                                   | 从工作副本中删除一个文件或目录，在下次提交时上传并添加到存储库中 |
| doCleanup()             | svn cleanup                                                  | 递归清理工作副本，删除未完成的工作副本锁定，并恢复未完成的操作 |
| doInfo()                | svn info                                                     | 获取一个工作副本条目的信息                                   |
| doLock()                | svn lock                                                     | 锁定工作副本或存储库中的条目，使其他用户不能对条目进行修改   |
| doUnlock()              | svn unlock                                                   | 解锁工作副本或存储库中的条目                                 |
| doSetProperty()         | svn propset PROPNAME PATH / svn propdel PROPNAME PATH / svn propedit PROPNAME PATH | 对工作副本或存储库中的条目设置属性名和属性值                 |
| doSetRevisionProperty() |                                                              | 对修订版本的条目设置属性名和属性值                           |
| doGetProperty()         | svn propget PROPNAME PATH / svn proplist PATH                | 获得工作副本或存储库中条目的属性值                           |
| doGetRevisionProperty() |                                                              | 获得修订版本中的条目的属性值                                 |
| doResolve()             | svn resolved                                                 | 手动编辑修复，通知SVN服务器冲突已解决                        |
| doRevert()              | svn revert                                                   | 取消所有本地编辑                                             |

#### 4. SVNStatusClient

SVNStatusClient 用来获取工作副本条目（文件或目录）的状态信息。

| SVNStatusClient 方法 | Subversion 命令 | 方法说明                   |
| -------------------- | --------------- | -------------------------- |
| doStatus()           | svn status      | 获取一个工作副本条目的状态 |

#### 5. SVNCommitClient

SVNCommitClient 提供了把改变提交到存储库上的一些操作。

| SVNCommitClient 方法 | Subversion 命令 | 方法说明                             |
| -------------------- | --------------- | ------------------------------------ |
| doCommit()           | svn commit      | 将修改从工作副本提交到存储库         |
| doImport()           | svn import      | 递归提交一个路径（本地目录）到存储库 |
| doDelete()           | svn delete URL  | 从储存库中删除一个条目               |
| doMkDir()            | svn mkdir URL   | 在存储库中创建一个目录               |

#### 6. SVNMoveClient

SVNMoveClient 提供文件在工作副本内移动、取消移动等操作。

| SVNMoveClient 方法 | Subversion 命令 | 方法说明                                     |
| ------------------ | --------------- | -------------------------------------------- |
| doMove()           |                 | 移动条目                                     |
| undoMove()         |                 | 取消上次的移动操作                           |
| doVirtualCopy()    |                 | 复制或移动源文件的**版本控制信息**到目标文件 |

#### 7. SVNCopyClient

SVNCopyClient 提供 SVN 支持的任何复制和移动操作。

| SVNCopyClient 方法 | Subversion 命令 | 方法说明                               |
| ------------------ | --------------- | -------------------------------------- |
| doCopy()           |                 | 如果复制多个source，则dist必须是目录。 |

#### 8. SVNDiffClient

SVNDiffClient 提供比较不同版本间的差异和合并差异的方法。

| SVNDiffClient 方法 | Subversion 命令 | 方法说明             |
| ------------------ | --------------- | -------------------- |
| doDiff()           | svn diff        | 获取两个版本间的差异 |
| doMerge()          | svn merge       | 合并两组文件间的插件 |

### 2.2 Low Level API

Low Level API 为 Subversion 存储库访问层的抽象，只通过不同的协议访问 Subversion 的存储库。