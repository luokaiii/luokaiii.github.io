---
title: 《Umi Hooks》官网笔记-useScroll
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useScroll

获取元素的滚动状态。

```js
interface Position {
    left: number;
    top: number;
}

type Target = HTMLElement | Document;

function useScroll<T extends Target>(): [
    // 滚动容器当前的滚动位置
    Position,
    // 当未传入任何参数时，将 ref 绑定给需监听的节点
    MutableRefObject<T>
]

function useScroll<T extends Target>(arg: Target | (() => Target)): [Position]{}
```

