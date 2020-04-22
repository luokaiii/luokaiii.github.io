---
title: 《Umi Hooks》官网笔记-useInViewport
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useInViewport

一个用于判断 dom 元素是否在可视范围之内的 Hook。

```js
const [
    // 判断dom元素是否在可视范围之内的标志
    inViewPort,
    // 当未传入任何参数时，将 ref 绑定给需监听的节点
    ref?
] = useInViewport(
    // 如果未传入则会监听返回结果中的 ref，否则会监听传入的节点
    dom?
);
```

