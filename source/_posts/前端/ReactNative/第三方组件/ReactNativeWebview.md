# React-Native-Webview 

**一、新增依赖**

```shell
$ yarn add react-native-webview
```

**二、link 依赖**

```shell
$ npx react-native link react-native-webview
```

**三、使用**

```js
import React, { Component } from 'react';
import { WebView } from 'react-native-webview';

class MyWeb extends Component {
  render() {
    return (
      <WebView
        source={{ uri: 'https://infinite.red' }}
        style={{ marginTop: 20 }}
      />
    );
  }
}
```



- [Getting-Started](https://github.com/react-native-community/react-native-webview/blob/master/docs/Getting-Started.md)
- [API Reference](https://github.com/react-native-community/react-native-webview/blob/master/docs/Reference.md)