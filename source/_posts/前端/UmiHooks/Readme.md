---
title: 《Umi Hooks》官网笔记
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
top: true
---
# 目录

- Async
  - [useRequest](/2020/04/21/前端/UmiHooks/Async/useRequest/index.html)：管理数据异步请求
- Ant Design
  - [useFormTable](/2020/04/21/前端/UmiHooks/Async/useFormTable/index.html)：封装了AntD Form 与 AntD Table 的联动逻辑
- UI
  - [useDrag & useDrop](/2020/04/21/前端/UmiHooks/Async/useDrag&useDrop/index.html)：一对处理拖拽时数据转移的hook
  - [useSelections](/2020/04/21/前端/UmiHooks/Async/useSelections/index.html)：联动 checkbox 逻辑封装
  - [useDynamicList](/2020/04/21/前端/UmiHooks/Async/useDynamicList/index.html)：管理列表状态
  - [useVirtualList](/2020/04/21/前端/UmiHooks/Async/useVirtualList/index.html)：虚拟化列表，解决大数据量时的渲染缓慢、滚动卡顿问题
- SideEffect
  - [useDebounce](/2020/04/21/前端/UmiHooks/Async/useDebounce/index.html)：防抖
  - [useThrottle](/2020/04/21/前端/UmiHooks/Async/useThrottle/index.html)：节流
  - [useDebounceFn](/2020/04/21/前端/UmiHooks/Async/useDebounceFn/index.html)：函数防抖
  - [useThrottleFn](/2020/04/21/前端/UmiHooks/Async/useThrottleFn/index.html)：函数节流
- LifeCycle
  - [useMount](/2020/04/21/前端/UmiHooks/Async/useMount/index.html)：组件 mount 时执行
  - [useUnmount](/2020/04/21/前端/UmiHooks/Async/useUnmount/index.html)：组件 unmount 时执行
  - [useUpdate](/2020/04/21/前端/UmiHooks/Async/useUpdate/index.html)：强制组件重新渲染
  - [useUpdateEffect](/2020/04/21/前端/UmiHooks/Async/useUpdateEffect/index.html)：只在依赖更新时执行的 useEffect
  - [useUpdateLayoutEffect](/2020/04/21/前端/UmiHooks/Async/useUpdateLayoutEffect/index.html)：只在依赖更新时执行的 useLayoutEffect
- State
  - [useMap](/2020/04/21/前端/UmiHooks/Async/useMap/index.html)：管理 Map 类型状态
  - [useSet](/2020/04/21/前端/UmiHooks/Async/useSet/index.html)：管理 Set 类型状态
  - [useToggle](/2020/04/21/前端/UmiHooks/Async/useToggle/index.html)：管理两个状态值切换的状态
  - [useBoolean](/2020/04/21/前端/UmiHooks/Async/useBoolean/index.html)：管理 Boolean 类型状态
  - [useCounter](/2020/04/21/前端/UmiHooks/Async/useCounter/index.html)：管理 count 的 hook
  - [usePrevious](/2020/04/21/前端/UmiHooks/Async/usePrevious/index.html)：保存上一次状态
  - [useHistoryTravel](/2020/04/21/前端/UmiHooks/Async/useHistoryTravel/index.html)：保存历史状态
  - [useControllableValue](/2020/04/21/前端/UmiHooks/Async/useControllableValue/index.html)：即可被自己管理，也可被外部控制的state
  - [useLocalStorageState](/2020/04/21/前端/UmiHooks/Async/useLocalStorageState/index.html)：使用 LocalStorage 管理 state
  - [useSessionStorageState](/2020/04/21/前端/UmiHooks/Async/useSessionStorageState/index.html)：使用 SessionStorage 管理 state
- Dom
  - [useSize](/2020/04/21/前端/UmiHooks/Async/useSize/index.html)：监听 dom 节点尺寸变化
  - [useHover](/2020/04/21/前端/UmiHooks/Async/useHover/index.html)：追踪 dom 元素是否有鼠标悬停
  - [useMouse](/2020/04/21/前端/UmiHooks/Async/useMouse/index.html)：跟踪鼠标位置
  - [useScroll](/2020/04/21/前端/UmiHooks/Async/useScroll/index.html)：获取元素滚动状态
  - [useKeyPress](/2020/04/21/前端/UmiHooks/Async/useKeyPress/index.html)：管理 keyup 和 keydown 的键盘事件，支持组合键
  - [useClickAway](/2020/04/21/前端/UmiHooks/Async/useClickAway/index.html)：管理目标元素外的点击事件
  - [useFullscreen](/2020/04/21/前端/UmiHooks/Async/useFullscreen/index.html)：处理 dom 全屏
  - [useResponsive](/2020/04/21/前端/UmiHooks/Async/useResponsive/index.html)：获取响应式信息
  - [useInViewport](/2020/04/21/前端/UmiHooks/Async/useInViewport/index.html)：判断 dom 元素是否在可视范围之内
  - [useEventTarget](/2020/04/21/前端/UmiHooks/Async/useEventTarget/index.html)：常见表单控件的 onChange 跟 value 逻辑封装
  - [useEventListener](/2020/04/21/前端/UmiHooks/Async/useEventListener/index.html)：EventListener
  - [useTextSelection](/2020/04/21/前端/UmiHooks/Async/useTextSelection/index.html)：获取选取文本内容及位置
  - [useDocumentVisibility](/2020/04/21/前端/UmiHooks/Async/useDocumentVisibility/index.html)：页面可见状态
- Advanced
  - [useCreation](/2020/04/21/前端/UmiHooks/Async/useCreation/index.html)：创建常量，且不会重复执行
  - [usePersistFn](/2020/04/21/前端/UmiHooks/Async/usePersistFn/index.html)：持久化 Function
  - [useEventEmitter](/2020/04/21/前端/UmiHooks/Async/useEventEmitter/index.html)：组件间通信，类似发布订阅