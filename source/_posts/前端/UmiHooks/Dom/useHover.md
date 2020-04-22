---
title: 《Umi Hooks》官网笔记-useHover
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useHover

一个用于追踪 dom 元素是否有鼠标悬停的 Hook。

```js
const [
    // 判断鼠标是否处于hover元素
    isHovering,
    // 当未传入任何参数时，将 ref 绑定给需监听的节点
    ref
] = useHover({
    // 如果未传入则会监听返回结果中的 ref，否则会监听传入的节点
    dom?,
    // 监听进入 hover
    onEnter?,
    // 监听离开 hover
    onLeave?,
})
```

