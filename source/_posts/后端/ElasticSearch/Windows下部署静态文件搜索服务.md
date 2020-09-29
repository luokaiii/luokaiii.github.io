---
title: ElasticSearch 实战 - Windows下部署静态文件搜索服务
date: 2020-08-10 10:16:20
tags:
  - ElasticSearch
  - hide
categories:
  - 后端
  - java
  - ElasticSearch
---

目录

1. 安装部署 Elasticsearch
2. 下载FSCrawler
3. 下载并运行 Search-UI
4. 踩坑路障

> Elasticsearch 版本为 7.8.0，fscrawler 版本为 2.7

![示例](https://luokaiii.oss-cn-shanghai.aliyuncs.com/blog/elasticsearch/es-result.png)

## 一、下载并启动 Elasticsearch

推荐从国内镜像站**下载**：[https://repo.huaweicloud.com/elasticsearch/](https://repo.huaweicloud.com/elasticsearch/)，选择指定平台的指定版本。

**解压**：`tar-zxvf elasticsearch-7.8.0-linux-x86_64.tar.gz`

**启动**：`./bin/elasticsearch`

**测试**：`curl http://localhost:9200/?pretty`

![ES启动信息](https://luokaiii.oss-cn-shanghai.aliyuncs.com/blog/elasticsearch/es-pretty.png)

> 启动时可能出现内存不够、Xpack这两个问题，详见踩坑路障

## 二、下载FSCrawler

FSCrawler 用于扫描本地文件，并发送给 Elasticsearch。可做到自动扫描、分词等功能。

**下载**，选择es7对应的版本：[https://fscrawler.readthedocs.io/en/latest/installation.html](https://fscrawler.readthedocs.io/en/latest/installation.html)

**解压**：`unzip fscrawler-es7-2.7-20200717.105855-117.zip`

**创建Job**：`./bin/fscrawler file_job --config_dir E:\1software\fscrawler-es7-2.7-SNAPSHOT\data `

```sh
PS E:\1software\fscrawler-es7-2.7-SNAPSHOT> .\bin\fscrawler file_job                                                    13:16:08,201 INFO  [f.p.e.c.f.c.BootstrapChecks] Memory [Free/Total=Percent]: HEAP [10.6mb/247.5mb=4.32%], RAM [8.2gb/15.9gb=51.64%], Swap [9.1gb/19.9gb=45.71%].
13:16:08,230 WARN  [f.p.e.c.f.c.FsCrawlerCli] job [file_job] does not exist
## 创建 file_job 的配置文件
13:16:08,231 INFO  [f.p.e.c.f.c.FsCrawlerCli] Do you want to create it (Y/N)?
## 这个地址，是我们要去修改的
13:16:10,976 INFO  [f.p.e.c.f.c.FsCrawlerCli] Settings have been created in [C:\Users\user\.fscrawler\file_job\_settings.yaml]. Please review and edit before relaunch
```

**编辑配置**，位于上面日志输出的 `C:\Users\user\.fscrawler\file_job\_settings.yaml`

> 配置说明详见 [FSCrawler官网](https://fscrawler.readthedocs.io/en/latest)：

```yaml
---
name: "file_job"
fs:
## 文件夹扫描地址
  url: "F:\\fstp\\prd"
  update_rate: "15m"
## 包含的文件
  includes:
  - "*/*.html"
  - "*/*.txt"
## 不包含的文件
  excludes:
  - "*/~*"
  json_support: false
  filename_as_id: true
  add_filesize: true
  remove_deleted: true
  add_as_inner_object: false
  store_source: false
  index_content: true
  attributes_support: false
  raw_metadata: false
  xml_support: false
  index_folders: true
  lang_detect: false
  continue_on_error: false
  ocr:
    language: "eng"
    enabled: false
    pdf_strategy: "ocr_and_text"
 ## 输出的类型
    output_type: "doc"
  follow_symlinks: false
elasticsearch:
  nodes:
  - url: "http://127.0.0.1:9200"
  bulk_size: 100
  flush_interval: "5s"
  byte_size: "10mb"
```

**再次启动fscrawler**：`./bin/fscrawler file_job --config_dir E:\1software\fscrawler-es7-2.7-SNAPSHOT\data`

**测试**：`curl http://localhost:9200/file_job/_count`

**结果**：

```json
{
    "count": 191,
    "_shards": {
        "total":1,
        "successful":1,
        "skipped":0,
        "failed":0
    }
}
```

## 三、下载Search-UI

下载 Elasticsearch 的搜索 UI 示例，[地址 Github：https://github.com/elastic/search-ui/tree/master/examples/elasticsearch](https://github.com/elastic/search-ui/tree/master/examples/elasticsearch)。

主要是编辑三个文件：`buildRequest.js`、`runRequest.js`、`buildState.js`

**buildRequest.js**：查询请求体的构建

**runRequest.js**：ajax 请求的地址，要根据自己的配置换成代理或者9200

**buildState.js**：处理响应数据，决定展示数据的字段。如 `nps_link` 为链接地址、`title` 为标题、`score` 为匹配度等

![结果](https://luokaiii.oss-cn-shanghai.aliyuncs.com/blog/elasticsearch/es-result.png)

## 四、踩坑

1. ES 启动失败1

   1. 异常信息：`Error occurred during initialization of VM ，Could not reserve enough space for 2097152KB object heap`
   2. 原因：不能为堆对象保留2G的空间，内存空间不足
   3. 解决方案1：修改jvm配置，编辑`config/jvm.options`，修改`-Xms512m -Xmx512m` 即可。
   4. 解决方案2：增加启动参数，`ES_JAVA_OPTS="-Xms512m -Xmx512m ./bin/elasticsearch"`
   5. 解决方案3：如果都没有用，请检查Windows的环境变量，是否是以前装过ES并做了相关服务，如果有，则删掉之前的配置

2. ES 启动警告

   1. 警告信息：`future versions of Elasticsearch will require Java 11;`
   2. 解决方案：警告最好使用java11，但是会向下兼容。不需要处理

3. ES 启动失败2

   1. 错误信息：`org.elasticsearch.bootstrap.StartupException: ElasticsearchException[X-Pack is not supported and Machine Learning is not available for [windows-x86]; you can use the other X-Pack features (unsupported) by setting xpack.ml.enabled: false in elasticsearch.yml]`
   2. 原因：X-Pack 不支持Windows
   3. 解决方案：编辑`config/elasticsearch.yml`，添加一行`xpack.ml.enabled: false`