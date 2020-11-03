---
title: React Native第三方组件react-native-elements
date: 2020-06-19
tags:
  - React Native
  - hide
categories:
  - 前端
  - React Native
---

## 一、基础部分

### 1.1 安装

```shell
$ yarn add react-native-elements
# or with npm
$ npm install --save react-native-elements
```

### 1.2 使用

```js
import { Button, ThemeProvider } from 'react-native-elements';

const MyApp = () => {
    return (
    	<ThemeProvider>
        	<Button title="Hello World!" />
        </ThemeProvider>
    )
}
```

