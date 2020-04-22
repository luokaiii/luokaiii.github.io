---
title: 《Umi Hooks》官网笔记-useMap
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useMap

一个可以管理 Map 类型状态的 Hook。

```js
const [
    // Map对象
    map,
    {
        // 添加对象
        set,
        // 添加并生成一个新的Map对象
        setAll,
        // 移除元素
        remove,
        // 重置为默认值
        reset,
        // 获取元素
        get
    }
] = useMap(
    // 传入的默认Map参数
    initialValue?: Iterable<[any, any]>
);
```

