---
title: 《Umi Hooks》官网笔记-useSet
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useSet

一个可以管理 Set 类型状态的 Hook。

## 一、API

```js
const [
    // Set 对象
    set,
    {
        // 添加元素
        add,
        // 判断是否存在元素
        has,
        // 移除元素
        remove,
        // 重置为默认值
        reset
    }
] = useSet(
    // 默认值
    initialValue?: Iterable<K>
);
```

