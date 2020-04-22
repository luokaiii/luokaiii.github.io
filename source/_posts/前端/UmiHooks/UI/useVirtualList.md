---
title: 《Umi Hooks》官网笔记-useVirtualList
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useVirtualList

提供虚拟化列表能力的 Hook，用于解决展示海量数据渲染时首屏渲染缓慢和滚动卡顿问题。

## 一、API

```js
const {
    // 当前需要展示的列表内容
    list,
    // 滚动容器的 props
    containerProps,
    // children 外层包裹器 props
    wrapperProps,
    // 快速滚动到指定 index
    scrollTo
} = useVirtualList(
    // 包含大量数据的列表
	originalList: any[],
    options: {
        // 行高度，静态高度可以直接写入像素值，动态高度可传入函数
        itemHeight,
        // 视区上、下额外展示的 dom 节点数量
        overscan
    }
)
```

