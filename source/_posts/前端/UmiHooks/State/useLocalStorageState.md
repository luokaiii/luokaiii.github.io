---
title: 《Umi Hooks》官网笔记-useLocalStorageState
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useLocalStorageState

一个可以将状态持久化存储在 localStorage 中的 Hook。

## 一、API

与 `useState` 类似，但是多了一个 key 作为存储时的 key。

```js
const [
    state,
    setState
] = useLocalStorageState<T> (
	key: string,
    defaultValue?: T | (() => T)
) : [T?, (value?: T | ((previousState: T) => T)) => void];
```

