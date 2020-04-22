---
title: 《Umi Hooks》官网笔记-useEventTarget
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useEventTarget

常见表单控件（通过 e.target.value 获取表单值）的 onChange 跟 value 逻辑封装，支持 自定义值转换 跟 重置 功能。

```js
const [
    {
        // 表单控件的值
        value,
        // 表单控件值发生变化时的回调
        onChange
    },
    // 重置函数
    reset
] = useEventTarget<T,U>(
    // 初始值
    initialValue?: T, 
    // 自定义回调值的转化
    transform?: (value: U) => T
)
```

