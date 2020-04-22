---
title: 《Umi Hooks》官网笔记-useDebounceFn
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useDebounceFn

用来处理防抖函数的 Hook。

## 一、API

```js
const {
    // 触发执行fn，函数参数将会传递给fn：(...args: any[]) => any
    run,
    // 取消当前防抖：() => void
    cancel
} = useDebounceFn(
    // 需要防抖执行的函数
	fn: (...args: any[]) => any,
    // 依赖数组，如果数组变化，则会在等待时间后触发fn
    deps: any[],
    // 防抖等待时间(1000毫秒)
    wait: number
)
```

