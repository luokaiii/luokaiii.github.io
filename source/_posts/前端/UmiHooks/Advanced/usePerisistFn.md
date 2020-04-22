---
title: 《Umi Hooks》官网笔记-usePersistFn
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# usePersistFn

持久化 function 的 Hook。

> 在某些场景中，你可能会需要用 useCallback 记住一个回调，但由于内部函数必须经常重新创建，记忆效果不是很好，导致子组件重复 render。对于超级复杂的子组件，重新渲染会对性能造成影响。通过 usePersistFn，可以保证函数地址永远不会变化。

```js
type noop = (...args: any[]) => any;

// fn: 引用地址永远不会变化的fn
const fn = usePersistFn<T extends noop> (
    // 需要持久化的函数
	fn: T
)
```

