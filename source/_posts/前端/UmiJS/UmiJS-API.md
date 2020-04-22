---
title: 《UmiJS》官网笔记
date: 2020-04-02 10:00:00
categories:
  - 前端
  - UmiJS
tags:
  - UmiJS
top: true
---
# UmiJS API

## 一、基本API

#### dynamic

动态加载组件。

```tsx
import React from 'react';
import { dynamic } from 'umi';

const delay = (timeout: number) =>
  new Promise(resolve => setTimeout(resolve, timeout));

export default dynamic({
  loader: async function() {
    await delay(1000);
    return () => <div>1s之后显示</div>;
  },
});
```

#### history

可用于获取当前路由信息。

```tsx
import { history } from 'umi';

// history 栈里的实体个数
console.log(history.length);

// 当前 history 跳转的 action，有 PUSH、REPLACE 和 POP 三种类型
console.log(history.action);

// location 对象，包含 pathname、search 和 hash
console.log(history.location.pathname);
console.log(history.location.search);
console.log(history.location.hash);
```

也可用于路由跳转

```tsx
import { history } from 'umi';

// 跳转到指定路由
history.push('/list');

// 带参数跳转到指定路由
history.push('/list?a=b');
history.push({
  pathname: '/list',
  query: {
    a: 'b',
  },
});

// 跳转到上一个路由
history.goBack();
```

也可用于路由监听

```tsx
import { history } from 'umi';

const unlisten = history.listen((location, action) => {
  console.log(location.pathname);
});
unlisten();
```

## 二、路由

### Link

```tsx
import React from 'react';
import { Link } from 'umi';

export default () => {
  return (
    <div>
      {/* 跳转指定路由 */}
      <Link to="/about">About</Link>
      {/* 跳转至附带参数的路由 */}
      <Link to="/about?id=123">About 123</Link>
      {/* 跳转至指定路由，附带query、hash、state */}
      <Link
        to={{
          pathname: '/list',
          search: '?sort=name',
          hash: '#the-hash',
          state: { fromDashboard: true },
        }}
      >
        List
      </Link>
      {/* 跳转至指定路由，附带所有当前 location 上的参数 */}
      <Link
        to={location => {
          return { ...location, pathname: '/profile' };
        }}
      >
        Profile
      </Link>
      {/* 跳转至指定路由，但会替换当前history stack中的记录 */}
      <Link to="/course" replace>
        Course
      </Link>
      {/* innerRef 允许你获取基础组件 */}
      <Link to="/course" innerRef={node => {}}>
        Course
      </Link>
    </div>
  );
};
```

#### NavLink

特殊版本的 `<Link />`。当指定路由命中时，可以附着特定样式。（可以用作顶部导航的选中定位；或者菜单栏的选中定位等）

```tsx
import { NavLink } from 'umi';

export default () => {
  return (
    <div>
      {/* 和 Link 等价 */}
      <NavLink to="/about">About</NavLink>

      {/* 当前路由为 /faq 时，附着 class selected */}
      <NavLink to="/faq" activeClassName="selected">
        FAQs
      </NavLink>

      {/* 当前路由为 /faq 时，附着 style */}
      <NavLink
        to="/faq"
        activeStyle={{
          fontWeight: "bold",
          color: "red",
        }}
      >
        FAQs
      </NavLink>

      {/* 当前路由完全匹配为 /profile 时，附着 class */}
      <NavLink exact to="/profile" activeClassName="selected">
        Profile
      </NavLink>

      {/* 当前路由为 /profile/ 时，附着 class */}
      <NavLink strict to="/profile/" activeClassName="selected">
        Profile
      </NavLink>

      {/* 当前路由为 /profile，并且 query 包含 name 时，附着 class */}
      <NavLink
        to="/profile"
        exact
        activeClassName="selected"
        isActive={(match, location) => {
          if (!match) {
            return false;
          }
          return location.search.includes("name");
        }}
      >
        Profile
      </NavLink>
    </div>
  );
};
```

#### Prompt

用户离开页面时的提示选择。

```tsx
import React from 'react';
import { Prompt } from 'umi';

export default function index() {
  return (
    <div>
      <h1>FAQS</h1>
      {/* 离开页面时提示 */}
      <Prompt message="你确定要离开么？" />
      {/* 用户跳转指定页面是提示 */}
      <Prompt
        message={location => {
          return location.pathname === '/me'
            ? '您确定要跳转到个人中心吗'
            : true;
        }}
      />
      {/* 根据一个状态来确定是否提示 */}
      <Prompt when={true} message="您确定要半途而废吗？" />
    </div>
  );
}
```

#### withRouter 

高阶组件，可以通过 `withRouter` 获取到 `history`、`location`、`match` 对象。

```tsx
import { withRouter } from "umi";

export default withRouter(({ history, location, match }) => {
  return (
    <div>
      <ul>
        <li>history: {history.action}</li>
        <li>location: {location.pathname}</li>
        <li>match: {`${match.isExact}`}</li>
      </ul>
    </div>
  );
});
```

#### useHistory

hooks，用于获取 `history` 对象

#### useLocation

hooks，用于获取 `location` 对象

#### useParams

hooks，用于获取 `params` 对象。`params` 对象为动态路由（如 `/user/:id`）里的参数键值对。

#### useRouteMatch

hooks，用于当前路由的信息匹配。

```tsx
import { 
  useHistory,
  useLocation,
  useParams,
  useRouteMatch
} from "umi";

// 假设当前路由为：/user/10086?type=user&deleted=false
export default () => {
  const history = useHistory()
  const location = useLocation()
  const params = useParams()
  const match = useRouteMatch()
  return (
    <div>
      <ul>
        <li>history: {history.action}</li>
        <li>location: {location.pathname}</li>
        <li>params: {JSON.stringify(params)}</li>
        <li>match: {JSON.stringify(match.params)}</li>
      </ul>
    </div>
  );
};
```