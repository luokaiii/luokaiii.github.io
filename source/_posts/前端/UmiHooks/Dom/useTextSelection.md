---
title: 《Umi Hooks》官网笔记-useTextSelection
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useTextSelection

实时获取用户当前选取的文本内容及位置。

```js
const [
    // dom节点内选取文本的内容
    state: {
        // 用户选取的文本值
        text,
        // 文本的左坐标
        left,
        // 文本的右坐标
        right,
        // 文本的顶坐标
        top,
        // 文本的底坐标
        bottom,
        // 文本的高度
        height,
        // 文本的宽度
        width
    },
    // 当未传入任何参数时，将 ref 绑定给需监听的节点
    ref?
] = useTextSelection(
	dom?
)
```

