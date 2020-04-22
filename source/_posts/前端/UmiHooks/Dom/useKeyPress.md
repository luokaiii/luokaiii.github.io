---
title: 《Umi Hooks》官网笔记-useKeyPress
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useKeyPress

一个优雅的管理 keyup 和 keydown 键盘事件的 Hook，支持键盘组合键，定义键盘事件的 key 和 keyCode 别名输入。

```js
// ref：	当未传入任何 target 时，将 ref 绑定给需监听的节点
const ref = useKeyPress(
    // 支持键盘事件中的 key 和 keyCode，支持回调方式返回 boolean 判断，支持别名使用
	keyFilter: KeyFilter,
    eventHandler: EventHandler = noop,
    options?: {
	    // 触发事件
    	events,
    	target
    }
)
```

**备注：按键别名**

```tex
enter
tab
delete
esc
space
up
down
left
right
ctrl
alt
shift
meta
```

