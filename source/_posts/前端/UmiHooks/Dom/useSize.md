---
title: 《Umi Hooks》官网笔记-useSize
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useSize

一个用于监听 dom 节点尺寸变化的 Hook。

```js
const [
    // dom节点的尺寸和位置：{width: number, height: number}
    state,
    // 当未传入任何参数时，将ref绑定给需要监听的节点
    ref?
] = useSize(
    // 如果未传入则会监听返回结果中的 ref，否则会监听传入的节点
    dom?
);
```

