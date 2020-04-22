---
title: 《Umi Hooks》官网笔记-useToggle
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useToggle

用于在两个状态值间切换的 Hook。

## 一、API

```js
const {
    // 状态值
    state,
    // 触发状态更改的函数
    toggle,
    setLeft,
    setRight
} = useToggle(
    // 默认状态值
	defaultValue?: boolean,
    // 取反的状态值
    reverseValue?: any
);
```

