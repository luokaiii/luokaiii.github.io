---
title: 《Umi Hooks》官网笔记-useDynamicList
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useDynamicList

一个帮助你管理列表状态，并能生成唯一 key 的 Hook。

## 一、API

```js
const {
    // 当前的列表：T[]
    list,
    // 重新设置 list 的值：(list: T[]) => void
    resetList,
    // 在指定位置插入元素：(index: number, obj: T) => void
    insert,
    // 在指定位置插入多个元素：(index: number, obj: T) => void
    merge,
    // 替换指定元素：(index: number, obj: T) => void
    replace,
    // 删除指定元素：(index: number) => void
    remove,
    // 移动元素：(oldIndex: number, newIndex: number) => void
    move,
    // 获得某个元素的 UUID：(index: number) => number
    getKey,
    // 获得某个 key 的 index：(key: number) => number
    getIndex,
    // 根据表单结果自动排序：(list: unKnown[]) => unKnown[]
    sortForm,
    // 在列表末尾添加元素：(obj: T) => void
    push,
    // 移除末尾元素：() => void
    pop,
    // 在列表起始位置添加元素：(obj: T) => void
    unshift,
    // 移除起始位置元素：() => void
    shift
} = useDynamicList(
    // 列表的初始值：T[]
    initialValue: T[]
);
```

