---
title: 《Umi Hooks》官网笔记-useClickAway
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useClickAway

优雅的管理目标元素外点击事件的 Hook。

```js
// ref：当未传入任何参数时，将 ref 绑定给需监听的节点
const ref = useClickAway(
    // 触发事件的函数
	onClickAway: (event: KeyboardEvent) => void,
    // 如果未传入则会监听返回结果中的 ref，否则会监听传入的节点
    dom?: RefType
)
```

