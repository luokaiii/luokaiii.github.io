---
title: 《Umi Hooks》官网笔记-useThrottle
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useThrottle

用来处理值节流 Hook。

```js
const ThrottledValue = useThrottle(
    // 需要节流变化的值
	value: any,
    // 防抖等待时间(默认1000毫秒)
    wait: number
)
```