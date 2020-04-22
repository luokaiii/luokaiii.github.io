---
title: 《Umi Hooks》官网笔记-usePrevious
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# usePrevious

保存上一次渲染时状态的 Hook。

```js
// 上次的state值
const previousState: T = usePrevious<T>(
    // 需要记录变化的值
	state: T,
    // 自定义值变化规则
    compareFunction?: (prev: T | undefined, next: T) => boolean
)
```

