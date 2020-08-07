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

