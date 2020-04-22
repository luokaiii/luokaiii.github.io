---
title: 《Umi Hooks》官网笔记-useFullscreen
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useFullscreen

一个用于处理 dom 全屏的 Hook。

```js
const {
    // 是否全屏
    isFullscreen,
    // 设置全屏
    setFull,
    // 退出全屏
    exitFull,
    // 切换全屏
	toggleFull,
    // 当未传入 dom 参数时，将 ref 绑定给需全屏的节点
    ref?
} = useFullScreen({
    // 如果未传入则会监听返回结果中的 ref，否则会监听传入的节点
    dom?,
    // 监听退出全屏
    onExitFull?,
    // 监听全屏
    onFull?
})
```

