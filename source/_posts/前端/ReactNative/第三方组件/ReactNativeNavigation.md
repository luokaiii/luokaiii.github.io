---
title: React Native第三方组件react-native-navigation
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

**添加依赖**：

```shell
$ yarn add react-native-navigation
# or use npm
$ npm install --save react-native-navigation
```

**关联依赖**：

```shell
$ npx react-native link react-native-navigation
```

**更新 index.js**：

```js
import { Navigation } from "react-native-navigation";
import App from "./App";

Navigation.registerComponent("App", () => App);

Navigation.events().registerAppLaunchedListener(() => {
  Navigation.setRoot({
    root: {
      stack: {
        children: [
          {
            component: {
              name: "App",
            },
          },
        ],
      },
    },
  });
});
```

## 二、使用导航

### 2.1 基础示例

```js
import React from "react";
import { View, Text, Button, StyleSheet } from "react-native";
import { Navigation } from "react-native-navigation";

// 页面一
const Module1 = (props) => {
  return (
    <View style={styles.root}>
      <Text>这里是页面1</Text>
      <Button
        title="跳转至2"
        color="#710ce3"
        onPress={() =>
          Navigation.push(props.componentId, {
            component: {
              name: "page2",
            },
          })
        }
      />
    </View>
  );
};

// 页面一的头标题、tab标题
Module1.options = {
  topBar: {
    title: {
      text: "第一页",
    },
  },
  bottomTab: {
    text: "page1",
  },
};

// 页面二
const Module2 = (props) => {
  return (
    <View style={styles.root}>
      <Text>这里是页面2</Text>
    </View>
  );
};

// 页面二的头标题、tab标题
Module2.options = {
  topBar: {
    title: {
      text: "第2页",
    },
  },
  bottomTab: {
    text: "page2",
  },
};

// 注册页面
Navigation.registerComponent("page1", () => Module1);
Navigation.registerComponent("page2", () => Module2);

// 设置默认导航信息
Navigation.setDefaultOptions({
  statusBar: {
    backgroundColor: "#4d089a",
  },
  topBar: {
    title: {
      color: "white",
    },
    backButton: {
      color: "white",
    },
    background: {
      color: "#4d089a",
    },
  },
  bottomTab: {
    fontSize: 14,
    selectedFontSize: 14,
  },
});

// 注册事件监听器
Navigation.events().registerAppLaunchedListener(async () => {
  Navigation.setRoot({
    root: {
      bottomTabs: {
        children: [
          {
            stack: {
              children: [
                {
                  component: {
                    name: "page1",
                  },
                },
              ],
            },
          },
          {
            stack: {
              children: [
                {
                  component: {
                    name: "page1",
                  },
                },
              ],
            },
          },
        ],
      },
    },
  });
});

// 使用 StyleSheet 创建的样式
const styles = StyleSheet.create({
  root: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "whitesmoke",
  },
});
```

### 2.2 LifeCycle - 生命周期

#### 2.2.1 装载组件

依次调用 `constructor() >> render() >> componentDidMount() >> componentDidAppear()`

#### 2.2.2 卸载组件

依次调用 `componentDidDisappear() >> componentWillUnmount()`

### 2.3 passing data - 传递 Props

使用 passProps 属性来传递 props。

```js
Navigation.push(this.props.componentId, {
  component: {
    name: "page2",
    id: "USER_ID",
    passProps: {
      name: "JACK",
      status: "online",
    },
  },
});
```

**更新 Props**，使用 id 来更新该组件的 props：

```js
Navigation.updateProps("USER_ID", {
  status: "offline",
});
```

### 2.4 functional components - 函数组件的生命周期

使用函数组件来监听生命周期函数。

```js
import React, { useEffect } from "react";
import { View, StyleSheet, Text } from "react-native";
import { Navigation } from "react-native-navigation";

const Home = (props) => {
  useEffect(() => {
    const listener = {
      componentDidAppear: () => {
        console.log("RNN", "componentDidAppear");
      },
      componentDidDisappear: () => {
        console.log("RNN", "componentDidDisappear");
      },
    };
    // 为当前组件注册相关的监听器
    const unsubscribe = Navigation.events().registerComponentListener(
      listener,
      props.componentId
    );
    // 确保组件清理时移除了监听器
    return () => {
      unsubscribe.remove();
    };
  }, [props]);

  return (
    <View style={styles.root}>
      <Text>首页</Text>
    </View>
  );
};
```

也可以使用 `react-native-navigation-hooks` 来监听：

```js
import { useNavigationComponentDidAppear } from "react-native-navigation-hooks";

const ScreenComponent = ({ componentId }) => {
  // Global listener
  useNavigationComponentDidAppear((e) => {
    console.log(`${e.componentName} (${e.componentId}) appeared`);
  });

  // Listen events only for this screen (componentId)
  useNavigationComponentDidAppear((e) => {
    console.log(`${e.componentName} appeared`);
  }, componentId);

  return (
    <View>
      <Text>Screen Component</Text>
    </View>
  );
};
```

## 三、Layout - 布局

### 3.1 Stack - 堆



### 3.2 Bottom Tabs - 底部导航

### 3.3 Side Menu - 侧边菜单

### 3.4 External Component - 外元件
