---
title: 《Umi Hooks》官网笔记-useDebounce
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useDebounce

用来处理防抖值的 Hook。

## 一、API

```js
const debouncedValue = useDebounce(
    // 需要防抖的值
	value: any,
    // 防抖等待时间(1000毫秒)
    wait: number
);
```

