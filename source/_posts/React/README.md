---
title: 完整React配置
date: 2018-10-01 17:38:03
tags:
 - java
categories: 
 - React学习
---
# React 入门

## 一、初始化项目结构

### 1. 创建ReactApp

> create-react-app leetcode

### 2. 引入其他可能用到的依赖

> yarn add antd                 // Ant Design 设计库  
> yarn add redux                // Redux 数据层框架  
> yarn add react-redux          // React-Redux React更加方便集成Redux  
> yarn add redux-thunk          // Redux-Thunk 异步Action  
> yarn add axios                // Axios Http请求工具  
> yarn add react-router-dom     // ReactRouterDom 路由工具  
> yarn add immutable            // Immutable 封装工具，将store封装起来  
> yarn add redux-immutable      // ReactImmutable 子组件封装工具，将store的子reducer封装起来  
> yarn add styled-components    // StyledComponents 使用JS编写CSS样式  
> yarn add react-loadable       // 异步加载组件  

### 3. 创建Redux中的Store，并使用Redux-DevelopTools

#### 3.1 在src目录下，创建store/reducer.js

defaultState 为默认的state，此文件导出一个函数，state为store中存储的内容，action为视图层发出的事件。

> 需要先引入 redux。

```js
const defaultState = {}

export default (state = defaultState, action) => {
    return state;
}
```

官方定义：Action 是把数据从应用（译者注：这里之所以不叫 view 是因为这些数据有可能是服务器响应，用户输入或其它非 view 的数据 ）传到 store 的有效载荷。
其格式大致为：

```js
const action = {
    type: 'ADD_TODO',
    value: 'xxx'
};
dispatch(action);
```

通过dispatch发送至store，再由具体的reducer对action进行处理，将修改之后的state返回给store，由store自己更新state。

#### 3.2 在src目录下，创建store/index.js

Store 就是把它们联系到一起的对象。Store 有以下职责：

- 维持应用的 state；
- 提供 getState() 方法获取 state；
- 提供 dispatch(action) 方法更新 state；
- 通过 subscribe(listener) 注册监听器;
- 通过 subscribe(listener) 返回的函数注销监听器。

```js
import { createStore } from 'redux';
import reducer from './reducer';

const store = createStore(
    reducer,
    window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__()
);

export default store;
```

> 注：`window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__()` 为开启 Redux_Devtools
> 在使用其他中间件时，需要引入 applyMiddleware,下面会有 [示例](xxx)。

#### 3.3 测试store是否可用

第一步：在 reducer 的 defaultState 中加入一个属性 value

```js
const defaultState = {
    value: 'Hello World!'
}
```

第二步：在任意组件引入 store/index.js，并获取到该value值。这里以 src/App.js 为例：

```js
import React from 'react';
import './App.css';
import store from './store';

function App() {
  return (
    <div className="App">
      {store.getState().value}
    </div>
  );
}

export default App;
```

第三步：启动项目，访问首页即可看到 value 值。

### 4. 使用Immutable封装state

immutable 是 facebook 开源的一个项目，用于实现 js 中的数据不可变，解决引用带来的副作用。

非常类似Java中的封装特性，即将属性私有化，提供公有方法访问私有变量。

> 需要先引入 immutable

修改之前的 src/store/reducer.js，将 defaultState 使用 immutable 提供的 fromJS 方法进行封装。

```js
import { fromJS } from 'immutable';

const defaultState = fromJS({
    value: 'Hello World!'
});

export default (state = defaultState, action) => {
    return state;
}
```

完成之后，也需要在调用 store.getState().value 的地方，将其改为 store.getState().get('value')。如上面的 src/App.js：

```js
function App() {
  return (
    <div className="App">
      {store.getState().get('value')}
    </div>
  );
}
```

### 5. 拆分reducer，各组件分别管理各自的reducer

> 需要依赖 redux-immutable

使用 redux 自身的 combineReducers 也可以做到管理子 reducer，如下：

```js
import { combineReducers } from 'redux';
import { reducer as premiumReducer } from '../premium/store';

const reducer = combineReducers({
    premium: premiumReducer
});

export default reducer;
```

但是 combineReducers 只能处理普通的 JS 对象，而 Store 中的所有属性已经被 Immutable 封装，此时我们要想使用子 reducer 中的属性时，需要这样写 `store.getState().premium.get('value')` 的方式获取数据。

显然，premium 依然可以存在被修改的副作用。

因此，我们只需要将 combineReducers 改为从 `redux-immutable` 中导入即可。

```js
import { combineReducers } from 'redux-immutable';
import { reducer as premiumReducer } from '../premium/store';

const reducer = combineReducers({
    premium: premiumReducer
});

export default reducer;
```

获取数据的方式也会变为：`store.getState().getIn(['premium','value'])`

### 6. 增加Router路由功能

> 需要依赖 react-router-dom

第一步：创建另一个组件 src/premium/index.js

```js
import React from 'react';

function Premium() {
    return (
        <div>Premium page</div>
    );
}

export default Premium;
```

第二步：修改App.js，将其改为Router.js

定义两个路由， '/' 直接返回一个 Div dom；'/premium' 返回刚刚创建的组件。

```js
import React from 'react';
import { BrowserRouter, Route } from 'react-router-dom';
import Premium from './premium';

function Router() {
  return (
    <div>
      <BrowserRouter>
        <Route path='/' exact render={() => <div>Home Page</div>}/>
        <Route path='/premium' exact component={Premium}/>
      </BrowserRouter>
    </div>
  );
}

export default Router;
```

同时，src/index.js 也需要将 App 组件名换成 Router。

第三步：测试路由功能是否有效

启动项目，分别访问 `localhost:3000` 和 `localhost:3000/premium` 即可。

### 7. 使用React-Redux控制Store

通过 React-Redux 提供的 `<Provider store>` 标签，使 Provider 内的任何组件，都能够有权利使用 store 中的数据。

前提是组件需要先通过 connect 进行连接。

> 需要依赖 react-redux

#### 7.1 修改 Router.js (原App.js)

将需要用到 store 存储的组件，放在 Provider 标签内。并指定所使用的 store

```js
import React from 'react';
import { BrowserRouter, Route } from 'react-router-dom';
import Premium from './premium';
import { Provider } from 'react-redux';
import store from './store';

function Router() {
  return (
    <Provider store={store}>
      <BrowserRouter>
        <Route path='/' exact render={() => <div>Home Page</div>}/>
        <Route path='/premium' exact component={Premium}/>
      </BrowserRouter>
    </Provider>
  );
}

export default Router;
```

#### 7.2 修改 premium/index.js

Premium 组件，使用 connect 进行连接。

```js
import React from 'react';
import { connect } from 'react-redux';

function Premium(props) {
    const { value } = props;
    return (
        <div>{value}</div>
    );
}

const mapStateToProps = (state) => ({
    value: state.getIn(['premium','value'])
});

const mapDispatchToProps = (dispatch) => ({

});

export default connect(mapStateToProps, mapDispatchToProps)(Premium);
```

#### 7.3 connect(mapState,mapDispatch)(module)

mapStateToProps 函数，接收一个 state，即 store 中存储的 state，返回一个对象。该对象即组件的 props。  
所以可以理解为，mapStateToProps 将 store 中的数据存入 this.props 中。

mapDispatchToProps 函数，接收一个dispatch，用来派发 action 给 store。由 reducer 接收并处理 action。

module 即需要进行连接的组件。

### 8. 测试State与Action

一个简单的测试，使用一个input，值为store中的value，当input修改时，发送action修改store中的value，与此同时更改input的value。

依旧使用 /premium/store/reducer 中的 value 作为测试数据。

#### 8.1 修改 premium/index.js

```js
import React from 'react';
import { connect } from 'react-redux';

function Premium(props) {
    const { value, changeInputValue } = props;
    return (
        <div>
            <input
                value={value}
                onChange={changeInputValue}
            />
        </div>
    );
}

const mapStateToProps = (state) => ({
    value: state.getIn(['premium', 'value'])
});

const mapDispatchToProps = (dispatch) => ({
    changeInputValue(e) {
        const value = e.target.value;
        const action = {
          type: 'CHANGE_INPUT_VALUE',
          value: value
        };
        dispatch(action);
    }
});

export default connect(mapStateToProps, mapDispatchToProps)(Premium);
```

#### 8.2 修改 premium/store/reducer.js

reducer.js 接收action，并进行处理，修改 store 中存储的 state 属性。

```js
import { fromJS } from 'immutable';

const defaultState = fromJS({
    value: 'Hello World!'
});

export default (state = defaultState, action) => {
    switch (action.type) {
        case 'CHANGE_INPUT_VALUE':
            return state.set('value', action.value);
        default:
            return state;
    }
}
```

#### 8.3 测试

启动项目，并打开 Redux DevTools 控制台，更改 input 内容，观察 state 中的数据变动。

![Redux DevTools](https://koral-home.oss-cn-beijing.aliyuncs.com/blog/ReduxDevTools.png)

### 9. Redux-Thunk 异步加载数据

当某一时刻触发的事件，需要从后端接口请求数据渲染页面时。可以通过 Redux-Thunk 使 Action 支持异步函数(原 Action 只是返回一个带 type 属性的对象)。

> 需要依赖 redux-thunk

#### 9.1 添加 Redux-Thunk 中间件

在 Redux 的 Store 创建时，引入 thunk 中间件，并使用它。

配置方法来源于 [github：redux-devtools-extension](https://github.com/zalmoxisus/redux-devtools-extension#12-advanced-store-setup)

```js
import { createStore, applyMiddleware, compose } from 'redux';
import reducer from './reducer';
import thunk from 'redux-thunk';

const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

const store = createStore(
    reducer,
    composeEnhancers(
        applyMiddleware(thunk)
    )
);

export default store;
```

#### 9.2 Mock数据 /public/api/list.json

在 src 同级的 public 目录下创建文件 `/public/api/list.json`：

如果能使用后端语言直接返回也可以

```js
{
    "success":true,
    "data": ["张三","李四","王五","刘六"]
}
```

#### 9.3 修改 premium/index.js，发送ajax请求，获取list.json

> 需要依赖 axios

点击 `获取数据` 的按钮时，触发事件 getList。获取ajax请求，并生成一个 action 发送给 store，替换 reducer 中的 list。

```js
import React from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import { fromJS } from 'immutable';

function Premium(props) {
    const { list, getList } = props;
    return (
            <button onClick={getList}>获取数据</button>
            <br/>
            {
                list && list.map((item) => (<div key={item}>{item}</div>))
            }
        </div>
    );
}

const mapStateToProps = (state) => ({
    list: state.getIn(['premium','list'])
});

const mapDispatchToProps = (dispatch) => ({
    getList() {
        const action = () => {
            axios.get('/api/list.json').then(res => {
                const list = res.data.data;
                const action2 = {
                    type: 'UPDATE_LIST',
                    // 注意这里：因为store中的数据是Immutable类型的，所以其属性也应该是Immutable类型的
                    list: fromJS(list)
                };
                dispatch(action2);
            }).catch(err => {
                console.log(err);
            })
        }
        dispatch(action);
    }
});

export default connect(mapStateToProps, mapDispatchToProps)(Premium);
```

> 因为前面配置了 Redux-Thunk 中间件，所以 action 能够直接返回一个函数，并且在dispatch 时自动执行。

#### 9.4 premium/store/reducer支持UPDATE_LIST的Action

reducer 接收action，并将值设置到state中。

```js
const defaultState = fromJS({
    list: []
});

export default (state = defaultState, action) => {
    switch (action.type) {
        case 'UPDATE_LIST':
            return state.set('list',action.list);
        default:
            return state;
    }
}
```

#### 9.5 测试ajax异步获取数据

启动项目，点击 button 按钮。页面会显示 list.json 中的数据。

### 10. 优化Redux层

#### 10.1 将常量抽离

将 premium/reducer.js 和 /premium/index.js 中 action.type 属性，抽离为一个单独的文件，在使用时直接调用该常量文件进行获取。方便进行调试和代码报错排查。

#### 10.2 将Action的创建抽离

将 premium/index.js 中所有 action 的创建，抽离到一个 ActionCreator.js 中。

#### 10.3 将抽离之后的文件统一管理

抽离之后的文件大致分为如下：

- public
    1. api
        - list.json     // 测试ajax请求的数据
    2. favicon.ico
    3. index.html
    4. mainifest.json
- src
    1. premium
        1. store
            - actionCreator.js  // 管理Action的创建
            - constants.js      // 管理常量
            - reducer.js        // 处理action
            - index.js          // 将上面三个文件导出
        2. index.js         // 首页
        3. style.js
    2. store             // redux的存储层、redux-thunk、redux-devtools的配置
        1. index.js         // 使用当前目录下的reducer创建store
        2. reducer.js       // react-redux 引入组件的store/index.js，并管理子reducer
    3. index.js          // React 项目的入口
    4. index.css
    5. router.js         // 路由文件，包含store作用域的限制、路由的配置