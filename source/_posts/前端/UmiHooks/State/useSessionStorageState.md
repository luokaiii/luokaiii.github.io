---
title: 《Umi Hooks》官网笔记-useSessionStorageState
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useSessionStorageState

一个可以将状态持久化存储在 sessionStorage 中的 Hook。

## 一、API

与 `useLocalStorageState` 一样。只是存储地址换成了 sessionStorage。

```js
const [
    state,
    setState
] = useSessionStorageState<T>(
	key: string,
    defaultValue?: T | (() => T)
) : [T?, (value?: T | ((previousState: T) => T)) => void];
```

