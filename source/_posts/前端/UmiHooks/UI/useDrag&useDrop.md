---
title: 《Umi Hooks》官网笔记-useDrop & useDrag
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useDrop & useDrag

一对能帮助处理拖拽中 进行数据转移 的 hooks。

>useDrop 可能单独使用来接收文件、文字和网址的拖拽。
>
>useDrag 允许一个 dom 节点被拖拽，需要配合 useDrop 使用。
>
>向节点内触发粘贴时也会被视为拖拽的内容。

## 一、API

```js
// getDragProps：一个接收拖拽的值，并返回需要透传给被拖拽节点 props 的方法
const getDragProps = useDrag();

<div {...getDragProps(id)}>draggable</div>

/**
 * props: 需要透传给接受拖拽区域 dom 节点的 props
 * isHovering：是否拖拽中，且光标处于释放区域内
 */
const [ props, { isHovering } ] = useDrop({
    // 拖拽文字的回调
    onText: (text: string, e: Event) => void,
    // 拖拽文件的回调
    onFiles: (files: File[], e: Event) => void,
    // 拖拽链接的回调
    onUri: (uri: string, e: Event) => void,
    // 拖拽自定义 dom 节点的回调
    onDom: (content: any, e: Event) => void
})
```

## 二、示例

拖拽区域可以接受文件、链接、文字和下方的box节点。

```tsx
import React, { useState } from 'react';
import { message } from 'antd';
import { useDrop, useDrag } from '@umijs/hooks';

export default () => {
  const getDragProps = useDrag();
  const [props, { isHovering }] = useDrop({
    onText: (text) => {
      message.success(`text:${text}`);
    },
    onFiles: (files) => {
      message.success(`files: ${files.length}`);
    },
    onUri: (uri) => {
      message.success(`uri: ${uri}`);
    },
    onDom: (content) => {
      message.success(`content: ${content}`);
    },
  });
  return (
    <div>
      <div style={{ border: '1px dashed #e8e8e8', padding: 16, textAlign: 'center' }} {...props}>
        {isHovering ? '放这儿' : '拖到这儿'}
      </div>
      <div style={{ display: 'flex', marginTop: 8 }}>
        {Array.from(Array(5)).map((e, i) => (
          <div
            {...getDragProps(`box${i}`)}
            style={{
              border: '1px solid #e8e8e8',
              padding: 16,
              width: 80,
              textAlign: 'center',
              marginRight: 16,
            }}
          >
            box[i]
          </div>
        ))}
       <a href="www.baidu.com">baidu</a>
      </div>
    </div>
  );
};
```

