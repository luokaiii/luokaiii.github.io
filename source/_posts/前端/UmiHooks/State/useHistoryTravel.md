---
title: 《Umi Hooks》官网笔记-useHistoryTravel
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useHistoryTravel

优雅的管理状态变化历史，可以快速在状态变化历史中穿梭 - 前进和后退。

## 一、API

```js
const {
    // 当前值
    value,
    // 设值函数
    setValue,
    // 可回退历史长度
    backLength,
    // 可前进历史长度
    forwardLength,
    // 历史穿梭函数：(step: number) => void
    go,
    // 后退一步
    back,
    // 前进一步
    foward
} = useHistoryTravel<T>(
    // 初始值
    initialValue?: T
);
```

