---
title: 《Umi Hooks》官网笔记-useUpdateLayoutEffect
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useUpdateLayoutEffect

一个只在依赖更新时执行的 useLayoutEffect Hook。

## 一、API

```js
useUpdateLayoutEffect(
	effect: () => (void | (() => void | undefined)),
    deps?: deps
)
```

