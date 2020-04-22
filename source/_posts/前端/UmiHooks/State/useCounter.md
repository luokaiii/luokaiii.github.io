---
title: 《Umi Hooks》官网笔记-useCounter
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useCounter

一个可以管理 count 的 Hook。

```js
const [
    // 当前值
    current,
    {
        // 加
	    inc,
        // 减
	    dec,
        // 设置
    	set,
        // 重置为默认值
	    reset        
    }
] = useCounter(
    // 默认值
    initialValue,
    {min, max}
);
```

