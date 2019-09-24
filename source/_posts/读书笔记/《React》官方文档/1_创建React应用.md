---
title: 《React》官方文档 - 目录
date: 2019-09-12 11:27:00
tags: 
  - 读书笔记
  - React
top: true
---

React 推荐的工具链：

1. 学习 React 或创建一个新的单页应用，可使用 Create React App
2. 用 Node.js 构建服务端渲染的网站，可使用 Next.js
3. 构建面向内容的静态网站，可使用 Gatsby
4. 打造组件库或将 React 集成到现有代码仓库，可尝试更灵活的工具链

## Create React App

`Create React App` 是创建 单页应用 的最佳方式。

> 需要 Node >= 8.10 和 npm >= 5.6

创建项目，可以执行：

```sh
npx create-react-app my-app
cd my-app
npm start
```

Create React App 只是创建一个前端构建流水线，它在内部使用 `Babel` 和 `Webpack`。

## Next.js

Next.js 是一个流行的、轻量级的框架，用于配合 React 打造静态化和服务端渲染应用。包括开箱即用的样式和路由方案。

## Gatsby

Gatsby 是用 React 创建静态网站的最佳方式。它让你能使用 React 组件，但输出预渲染的 HTML 和 CSS 以保证最快的加载速度。

## 更灵活的工具链

- Neutrino 把 webpack 的强大功能和简单预设结合在一起。
- nwb 适合将 React 组件发布到 npm
- Parcel 是一个快速的、零配置的网页应用打包器
- Razzle 是一个无需配置的服务端渲染框架

## 从头开始打造工具链

- package 管理器，如 Yarn、npm
- 打包器：如 webpack、Parcel
- 编译器：如 Babel