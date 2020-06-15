---
title: immer.js官方文档笔记
date: 2020-06-14
tags:
  - immer.js
categories:
  - 前端
  - immer.js
---
## 简介

Immer，用于处理不可变状态，基于“写时复制”机制。

基本思想为：将所有更改都应用于临时的 draftState，它是 currentState 的代理。一旦完成所有突变，Immer 将基于到草稿

<!-- More -->

**优势：**

- 简单易学，Immutability 为常见的 JavaScript 对象、数组、集合和Map。
- 强类型，没有基于字符串的路径选择器。
- 开箱即用的结构共享
- 开箱即用的对象冻结
- 深度更新轻而易举
- 减少样板代码，更加简洁
- 3kb的大小

### 一、安装

```sh
// YARN
$ yarn add immer
// NPM
$ npm install immer
// CDN
<script src="https://unpkg.com/immer/dist/immer.umd.js"></script>
<script src="https://cdn.jsdelivr.net/npm/immer/dist/immer.umd.js"></script>
```

