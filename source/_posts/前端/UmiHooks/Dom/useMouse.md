---
title: 《Umi Hooks》官网笔记-useMouse
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useMouse

一个跟踪鼠标位置的 Hook。

```js
const state : {
    // 距离显示器左侧的距离
	screenX: number, 
    // 距离显示器顶部的距离
	screenY: number, 
    // 距离当前视窗左侧的距离
	clientX: number, 
    // 距离当前视窗顶部的距离
	clientY: number,
    // 距离完整页面顶部的距离
	pageX: number,
    // 距离完整页面顶部的距离
	pageY: number,
} = useMouse();
```

