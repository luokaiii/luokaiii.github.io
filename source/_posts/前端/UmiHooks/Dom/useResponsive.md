---
title: 《Umi Hooks》官网笔记-useResponsive
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useResponsive

获取响应式信息。

```js
interface ResponsiveConfig {
    [key: string]: number;
}

interface ResponsiveInfo {
    [key: string]: boolean;
}

function configResponsive(config: ResponsiveConfig): void

function useResponsive(): ResponsiveInfo
```

