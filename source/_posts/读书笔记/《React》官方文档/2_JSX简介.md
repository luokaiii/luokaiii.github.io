---
title: 《React官方文档》JSX简介
date: 2019-09-12 11:27:00
tags: 
  - 读书笔记
  - React
visible: hide
---
## JSX 简介

React 认为渲染逻辑本质上与其他 UI 逻辑内在耦合，比如，在 UI 中需要绑定处理事件、在某些时刻状态发生变化时需要通知 UI，以及需要在 UI 中展示准备好的数据。

### JSX 防止注入攻击

```js
const title = response.potentiallyMaliciousInput
// 直接使用是安全的
const element = <h1>{title}</h1>
```

React DOM 在渲染所有输入内容之前，默认会进行转义。它可以确保在你的应用中，永远不会注入那些非自己明确编写的内容。所有内容在渲染之前都被转换成了字符串。这样可以有效地防止 XSS 攻击。

### JSX 表示对象

```js
const element = (
	<h1 className="greeting">
    	Hello,World!
    </h1>
)
```

Babel 会把 JSX 转译成一个名为 React.createElement() 的函数调用。React.createElement() 会预先执行一些检查，以帮助你编写无错代码：

```javascript
// 这是简化的过程，真实的要复杂的多
const element = {
    type: 'h1',
    props: {
        className: 'greeting',
        children: 'Hello,World!'
    }
}
```

这些对象被称为"React元素"，描述了屏幕前看到的内容，React通过读取这些对象，然后使用它们来构建 DOM 以及保持随时更新。

