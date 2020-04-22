---
title: 《Umi Hooks》官网笔记-useControllableValue
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useControllableValue

在某些组件开发时，我们需要组件的状态既可以自己管理，也可以被外部控制，useControllableValue 就是帮你管理这种状态的 Hook。

```js
const [
    // 状态值
    state,
    // 修改 state 函数
    setState
] = useControllableValue(
    // 组件的 props
    props: object,
    // 可选配置项
    options?: {
	    // 默认值
    	defaultValue,
    	// 默认值的属性名
    	defaultValuePropName,
	    // 值的属性名
    	valuePropName,
	    // 修改值时，触发的函数
    	trigger
    }
);
```

