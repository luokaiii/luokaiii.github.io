---
title: 《Umi Hooks》官网笔记-useThrottleFn
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useThrottleFn

用来处理函数节流的 Hook。

## 一、API

```js
const {
    // 触发执行fn，函数参数将会传递给fn
    run,
    // 取消当前节流
    cancel
} = useThrottleFn(
    // 需要节流的函数
	fn: (...args: any[]) => any,
    // 依赖数组，如果数组变化，则会在节流后，触发fn
    deps: any[],
    // 节流间隔时间(默认1000毫秒)
    wait: number
)
```

