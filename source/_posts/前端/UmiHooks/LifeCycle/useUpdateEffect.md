---
title: 《Umi Hooks》官网笔记-useUpdateEffect
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useUpdateEffect

一个只在依赖更新时执行的 useEffect hook。

## 一、API

```js
useUpdateEffect(
    // 可执行函数
	effect: () => (void | (() => void | undefined)),
    // 传入依赖变化的对象
    deps?: deps;
)
```

