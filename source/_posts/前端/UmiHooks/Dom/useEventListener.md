---
title: 《Umi Hooks》官网笔记-useEventListener
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useEventListener

优雅使用  EventListener 的 Hook。

```js
function useEventListener<T extends Target = HTMLElement> (
	eventName: string,
    handler: Function,
    options?: {
    	capture?: boolean;
    	once?: boolean;
    	passive?: boolean;
    }
): MutableRefObject<T>;
```

