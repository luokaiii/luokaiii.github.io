---
title: 《Umi Hooks》官网笔记-useRequest
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
## useRequest

强大的管理异步数据请求的Hook.

**核心特性**

- 自动请求/手动请求
- SWR(state-while-revalidate)
- 缓存/预加载
- 屏幕聚焦重新请求
- 轮询
- 防抖
- 节流
- 并行请求
- loading delay
- 分页
- 加载更多，数据恢复 + 滚动位置恢复
- 错误重试
- 请求超时管理
- suspense
- ......

### 默认请求

```tsx
/**
 * title: 默认请求
 * desc: useRequest接收一个异步函数 getUsername，在组件初次加载时，自动触发该函数执行。同时 useRequest 会自动管理异步请求的 loading、data、error 等状态。
 */
import React from "react";
import { useRequest } from "@umijs/hooks";

function getUsername() {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({
        name: '张大三'
      })
    }, 1000);
  })
}

export default () => {
  const {data,loading,error} = useRequest(getUsername)

  if(error) {
    return <div> fail load data </div>
  }
  if(loading) {
    return <div>loading...</div>
  }
  return (
    <div>
      Username: {data.name}
    </div>
  );
};
```

### 手动触发

```tsx
/**
 * title: 手动触发
 * desc: 通过 options.manual=true 时，需要手动调用 run 时才会触发执行异步函数。
 */
import { useRequest } from '@umijs/hooks';
import { Button, Input, message } from 'antd';
import React, { useState } from 'react';

function changeValue(Username: string):Promise<{success: boolean}> {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({success: true})
    }, 1000);
  })
}

export default () => {
 const [data,setData] = useState('');
 const {loading,run} = useRequest(changeValue,{
   manual: true,
   onSuccess: (result, params) => {
     if(result.success) {
       setData('');
       message.success(`The username was changed to ${params[0]}`);
     }
   }
 })

  return (
    <div>
      <Input value={data} onChange={e => setData(e.target.value)} style={{width:240}}/>
      <Button loading={loading} onClick={() => run(data)}>修改</Button>
    </div>
  )
};
```

### 轮询

```tsx
/**
 * title: 轮询
 * desc: 通过设置 options.pollingInterval，进入轮询模式，定时触发函数执行。
 * - 通过设置 options.pollingWhenHidden=false，在屏幕不可见时，暂时暂停定时任务。
 * - 通过 run / cancel 来开始或停止轮询。
 * - 在 options.manual=true 时，需要第一次执行 run 后，才开始轮询。
 */
import { useRequest } from '@umijs/hooks';
import { Button, Spin } from 'antd';
import React from 'react';

function changeValue():Promise<string> {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve("张大三")
    }, 1000);
  })
}

export default () => {
 const { data, loading, run, cancel } = useRequest(changeValue,{
   pollingInterval: 1000,
   pollingWhenHidden: false
 })

  return (
    <div>
      <Spin spinning={loading}>Username: {data}</Spin>
      <Button.Group>
        <Button onClick={run}>开始</Button>
        <Button onClick={cancel}>暂停</Button>
      </Button.Group>
    </div>
  )
};
```

### 并行请求

```tsx
/**
 * title: 轮询
 * desc: 通过 options.fetchKey，将请求分类，每一类请求都有独立的状态，可以在 fetches 中找到所有请求。
 */
import { useRequest } from '@umijs/hooks';
import { Button, message } from 'antd';
import React from 'react';

function deleteUser(userId: string): Promise<{success: boolean}> {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({success: true})
    }, 1000);
  })
}

export default () => {
  const { run, fetches } = useRequest(deleteUser, {
    manual: true,
    fetchKey: id => id,
    onSuccess: (result, params) => {
      if(result.success) {
        message.success(`禁用用户[${params[0]}]`);
      }
    }
  });

  const users = [{id:'1',username:'A'},{id:'2',username:'B'},{id:'3',username:'C'}];

  return (
    <div>
      <ul>
        {users.map((user) => (
          <li key={user.id} style={{marginTop:8}}>
            <Button loading={fetches[user.id]?.loading} onClick={() => run(user.id)}>delete {user.username}</Button>
          </li>
        ))}
      </ul>
    </div>
  )
}
```

### 防抖

```tsx
/**
 * title: 防抖
 * descirption: 通过设置 options.debounceInterval，进入防抖模式。此时如果频繁触发 run，则会以防抖策略进行请求。
 */
import { useRequest } from '@umijs/hooks';
import { Select } from 'antd';
import React from 'react';

async function getEmail(search: string): Promise<string[]> {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve(['188','173','187','159','156']);
    }, 300);
  })
}

export default () => {
  const { data, loading, run ,cancel } = useRequest(getEmail, {
    debounceInterval: 500,
    manual: true
  })
  return (
    <div>
      <Select
        showSearch
        placeholder="select emails"
        filterOption={false}
        onSearch={run}
        onBlur={cancel}
        loading={loading}
        style={{width: 300}}
      >
        {data && data.map(i=> <Select.Option key={i}>{i}</Select.Option>)}
      </Select>
    </div>
  )
}
```

### 节流

```tsx
/**
 * title: 节流
 * descirption: 通过设置 options.throttleInterval，进入节流模式。此时如果频繁触发 run，则会以节流策略进行请求。例如： options.throttleInterval=2000，则频繁点击时，仍会以2s每次请求。
 */
import { useRequest } from '@umijs/hooks';
import { Select } from 'antd';
import React from 'react';

async function getEmail(search: string): Promise<string[]> {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve([String(Math.random())]);
    }, 300);
  })
}

export default () => {
  const { data, loading, run ,cancel } = useRequest(getEmail, {
    throttleInterval: 2000,
    manual: true
  })
  return (
    <div>
      <Select
        showSearch
        placeholder="select emails"
        filterOption={false}
        onSearch={run}
        onBlur={cancel}
        loading={loading}
        style={{width: 300}}
      >
        {data && data.map(i=> <Select.Option key={i}>{i}</Select.Option>)}
      </Select>
    </div>
  )
}
```

### 缓存 & SWR

```tsx
/**
 * title: 缓存 & SWR
 * descirption: 通过设置 options.cacheKey，useRequest 会将当前请求结束数据缓存起来。下次组件初始化时，如果有缓存数据，则优先返回缓存数据，然后在背后发送新请求，也就是 SWR 的能力。
 */
import { useRequest, useBoolean } from '@umijs/hooks';
import { Button, Spin } from 'antd';
import React from 'react';

async function getArticle(type?: string): Promise<{data:string,time:number}> {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({
        data: '这里是数据显示部分这里是数据显示部分这里是数据显示部分这里是数据显示部分这里是数据显示部分',
        time: new Date().getTime()
      })
    }, 1000);
  })
}

export default () => {
  const { state, toggle } = useBoolean();
  return (
    <div>
      <p>
        <Button onClick={() => toggle()}>show/hidden</Button>
      </p>
      {state && <Article />}
    </div>
  )
}

const Article = () => {
  const { data, loading } = useRequest(getArticle, {
    cacheKey: `article`
  })
  return (
    <Spin spinning={!data && loading}>
      <p>最后一次请求时间：{data?.time}</p>
      <p>{data?.data}</p>
    </Spin>
  )
}
```

### 预加载

```tsx
/**
 * title: 预加载
 * descirption: 同一个 cacheKey 的请求，是全局共享的。利用该特性，可以很方便的实现预加载。
 */
import { useRequest, useBoolean } from '@umijs/hooks';
import { Button, Spin } from 'antd';
import React from 'react';

async function getArticle(type?: string): Promise<{data:string,time:number}> {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({
        data: '这里是数据显示部分这里是数据显示部分这里是数据显示部分这里是数据显示部分这里是数据显示部分',
        time: new Date().getTime()
      })
    }, 1000);
  })
}

export default () => {
  const { state, toggle } = useBoolean();
  const {run} = useRequest(getArticle, {
    cacheKey: 'article',
    manual: true
  })
  return (
    <div>
      <p>
        <Button onMouseEnter={() => run()} onClick={() => toggle()}>show/hidden</Button>
      </p>
      {state && <Article />}
    </div>
  )
}

const Article = () => {
  const { data, loading } = useRequest(getArticle, {
    cacheKey: `article`
  })
  return (
    <Spin spinning={!data && loading}>
      <p>最后一次请求时间：{data?.time}</p>
      <p>{data?.data}</p>
    </Spin>
  )
}
```

### 屏幕聚焦重新请求

```tsx
/**
 * title: 屏幕聚焦重新请求
 * descirption: 设置 options.refreshOnWindowFocus=true，则在浏览器窗口 refocus 和 revisible 时，会重新发起请求。可以通过设置 options.focusTimespan 来设置请求间隔，默认为5000ms。
 */
import { useRequest } from '@umijs/hooks';
import { Spin } from 'antd';
import React from 'react';

function getUsername(): Promise<Number> {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve(Math.random());
    }, 1000);
  })
}

export default () => {
  const { data, loading } = useRequest(getUsername,{
    refreshOnWindowFocus: true,
    focusTimespan: 5000
  })
  return (
    <div>
     <Spin spinning={loading}>
      Random: {data}
     </Spin>
    </div>
  )
}
```

### 突变

```tsx
/**
 * title: 突变
 * descirption: 你可以通过 mutate，直接修改 data。mutate 函数参数可以为 newData 或 (oldData) => newData。
 */
import { useRequest } from '@umijs/hooks';
import { Button, Input, message  } from 'antd';
import React,{ useState } from 'react';

function getUsername(): Promise<string> {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve(String(Math.random()));
    }, 1000);
  })
}

function changeUsername(username: string): Promise<{success: boolean}> {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({success: true})
    }, 1000);
  })
}

export default () => {
  const [state, setState] = useState('')
  const { data, mutate } = useRequest(getUsername, {
    onSuccess: result => {
      setState(result)
    }
  });

  const { loading, run } = useRequest(changeUsername, {
    manual: true,
    onSuccess: (result, params) => {
      if(result.success) {
        mutate(params[0]);
        message.success(`changed to ${params[0]}`)
      }
    }
  })
  return (
    <div>
     <p>Username: {data}</p>
     <Input 
      onChange={e => setState(e.target.value)}
      value={state}
      placeholder='please enter username'
      style={{width:240}}
     />
     <Button onClick={() => run(state)} loading={loading}>
       Edit
      </Button>
    </div>
  )
}
```

### Loading Delay

```tsx
/**
 * title: Loading Delay
 * descirption: 通过设置 options.loadingDelay，可以延迟 loading 变为 true 的时间，有效防止闪烁。
 */
import { useRequest } from '@umijs/hooks';
import { Button, Spin  } from 'antd';
import React,{  } from 'react';

async function getCurrentTime(): Promise<number> {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve(new Date().getTime());
    }, 1000);
  })
}

export default () => {
  const getTimeAction = useRequest(getCurrentTime);

  const withLoadingDelayAction = useRequest(getCurrentTime, {
    loadingDelay: 200
  })

  const trigger = () => {
    getTimeAction.run();
    withLoadingDelayAction.run();
  }

  return (
    <div>
      <Button onClick={trigger}>获取数据</Button>
      <div>
        <Spin spinning={getTimeAction.loading}>
          无延迟请求结果：{getTimeAction.data}
        </Spin>
      </div>
      <div>
        <Spin spinning={withLoadingDelayAction.loading}>
          有延迟请求结果：{withLoadingDelayAction.data}
        </Spin>
      </div>
    </div>
  )
}
```

### refreshDeps

```tsx
/**
 * title: refreshDeps
 * descirption: 当某些 state 变化时，我们需要重新执行异步请求。refreshDeps是一个语法糖，当 refreshDeps 变化时，会使用之前的 params 重新执行 service。
 */
import { useRequest } from '@umijs/hooks';
import { Select, Spin  } from 'antd';
import React,{ useState } from 'react';

const userSchool = (id: string) => {
  switch(id) {
    case '1':
      return 'Tsinghua';
    case '2':
      return 'Beijing';
    case '3':
      return 'Zhejiang';
    default: 
      return '';
  }
}

async function getUserSchool(userId: string): Promise<string> {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve(userSchool(userId));
    }, 1000);
  })
}

export default () => {
  const [userId, setUserId] = useState('1');
  const { data, loading } = useRequest(() => getUserSchool(userId), {
    refreshDeps: [userId]
  })

  return (
    <div>
      <Select onChange={setUserId} value={userId}>
        <Select.Option value="1">User 1</Select.Option>
        <Select.Option value="2">User 2</Select.Option>
        <Select.Option value="3">User 3</Select.Option>
      </Select>
      <Spin spinning={loading}>
        School: {data}
      </Spin>
    </div>
  )
}
```

## 基础API

```js
const {
    // service返回的数据，默认为undefined
    // 如果有 formatResult，则数据为格式化后的数据
    data,
    // service 抛出的异常，默认为 undefined
    error,
    // 加载状态，service 是否正在执行
    loading,
    // 手动触发service执行，参数会传递给service
    // debounce与throttle模式，返回值为 Promise<null>
    run,
    // 当次执行的 service 参数数组。如 run(1,2,3)，则params=[1,2,3]
    params,
    // 取消当前请求
    // 如果有轮询，停止
    cancel,
    // 使用上一次的 params，重新执行service
    refresh,
    // 直接修改state
    mutate,
    // 默认情况下，新请求会覆盖旧请求。如果设置了 fetchKey，则可以实现多请求并行，由fetches存储每个fetchKey对应的状态
    // 外层的状态为最新触发的 fetches 数据
    fetches
} = useRequest(service, {
    // false：初始化时自动执行service；true：需要手动调用run触发执行。
    manual,
    // 默认的data
    initialData,
    // 在 manual=false 时，refreshDeps变化，会触发service重新执行
    refreshDeps,
    // service resolve 触发，参数为data和params；如果有 formatResult，则data为格式化后的数据
    onSuccess,
    // service 报错是触发，参数为 error 和 params
    onError,
    // 格式化请求结果
    formatResult,
    // 请求唯一标识，如果设置了，会启用缓存机制
    // 会缓存每次请求的 data、error、params、loading
    // 在缓存机制下，同样的请求会先返回缓存中的数据，同时在背后发送新的请求，待新数据返回后，重新触发数据更新
    cacheKey,
    // 设置显示 loading 的延迟时间，避免闪烁
    loadingDelay,
    // 如果 manual=false，自动执行 run 时，默认带上的参数
    defaultParams,
    // 轮询间隔，单位毫秒
    pollingInterval,
    // 页面隐藏时，是否继续轮询。默认为true
    // false：在页面隐藏时停止轮询，页面重新显示时继续上次轮询
    pollingWhenHidden,
    // 根据params获取当前请求的key，在 fetches 中同时维护不同的key值的请求状态
    fetchKey,
    // 屏幕重新获取焦点或重新显示时，是否重新发起请求
    // false：默认，不会发起请求
    refreshOnWindowFocus,
    // 指定时间间隔内，屏幕重新聚焦，不会重新发起请求
    // 需要配合 refreshOnWindowFocus 一起使用
    focusTimespan,
    // 防抖间隔，毫秒
    debounceInterval,
    // 节流间隔，毫秒
    throttleInterval
})
```

## 扩展用法

基于基础的 useRequest，进一步封装，实现更高级的定制需求。当前 useRequest 内置了 `集成请求库`、`分页`、`加载更多` 三个场景。

可参考 useRequest、usePaginated、useLoadMore 的实现。

### 集成请求库

如果service是 `string`、`object`、`(...args) => string|object`，会自动使用 umi-request 来发送网络请求。umi-request 是类似 axios、fetch 的请求库。

```js
// 用法1
const { data, error, loading } = useRequest('/api/currentUser');

// 用法2
const { data, error, loading } = useRequest({
    url: '/api/currentUser',
    method: 'GET'
})

// 用法3
const { data, error, loading } = useRequest((userId) => `/api/user/${userId}`)

// 用法4
const { data, error, loading } = useRequest((username) => ({
    url: '/api/changeUsername',
    method: 'PUT',
    data: {username}
}), {
    manual: true
})
```

使用默认的 umi-request：

```tsx
import { useRequest } from '@umijs/hooks';
import React from 'react';


export default () => {
  const {data,error,loading} = useRequest('https://helloacm.com/api/random/?n=8&x=4')

  if(error) {
    return <div>error</div>
  }

  if(loading) {
    return <div>loading</div>
  }
  
  return (
    <div>
      {data}
    </div>
  )
}
```

使用 axios：

```tsx
import { useRequest } from '@umijs/hooks';
import React from 'react';
import axios from 'axios';


export default () => {
  const {data,error,loading} = useRequest('https://helloacm.com/api/random/?n=8&x=4',{
    requestMethod: (params: any) => axios(params)
  })

  if(error) {
    return <div>error</div>
  }

  if(loading) {
    return <div>loading</div>
  }
  
  return (
    <div>
      {data}
    </div>
  )
}
```

### 分页

通过设置 options.paginated = true，useRequest 将以分页模式运行，此时会有以下特性：

- useRequest 会自动管理分页 ` current`、`pageSize`。service 第一个参数为 `{current, pageSize}`。
- service 返回的数据结构必须为 `{list: Item[], total: number}`，如果不满足，可通过 `options.formatResult` 进行转换
- 会额外返回 `pagination` 字段，包含所有分页信息，及操作分页的函数
- `refreshDeps` 变化，会重置 `current` 到第一页，并重新发起请求，一般可以把 pagination 依赖的条件放这里。

#### API

```js
const {
    ...,
    // 分页数据及操作分页的方法
    pagination: {
        current: number;
        pageSize: number;
        total: number;
        totalPage: number;
        onChange: (curent: number, pageSize: number) => void;
    	changeCurrent: (current: number) => void;
        changePageSize: (pageSize: number) => void
    };
    // 适配 antd table 组件的数据结构，可直接用在 AntD Table 组件上
    tableProps: {
        dataSource: Item[];
        loading: boolean;
        onChange: (
        	pagination: any,
            filters?: any,
            sorter?: any,
        ) => void;
        pagination: {
            current: number;
            pageSize: number;
            total: number;
        }
    }
} = useRequest(service, {
    ...,
    // 是否开启分页模式，开启后service第一个参数为 {current,pageSize,sorter,filters}
    // service 响应结果必须为 {list: Item[], total: number}
    pagnizated,
    // 默认每页的数量，10
    defaultPageSize,
    // 分页模式下，refreshDeps 变化，会重置 current 到第一页，并重新发起请求。一般把依赖条件放这里
    refreshDeps
})
```



**普通分页场景：自动管理 current、pageSize**

```tsx
import { useRequest } from '@umijs/hooks';
import React from 'react';
import { List, Pagination } from 'antd';
import Mock from 'mockjs';

interface UserListItem {
  id: string,
  name: string,
  gender: 'male' | 'female',
  email: string,
  disabled: boolean
}

const userList = (current, pageSize) => (
  Mock.mock({
    total: 55,
    [`list|${pageSize}`]: [{
      id: '@guid',
      name: '@cname',
      'gender|1': ['male', 'female'],
      email: '@email',
      disabled: false
    }],
  })
)

async function getUserList(params: { current: number, pageSize: number, gender?: string }): Promise<{ total: number, list: UserListItem[] }> {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve(userList(params.current, params.pageSize))
    }, 1000)
  });
}

export default () => {
  const { data, loading, pagination } = useRequest(
    ({ current, pageSize }) => getUserList({ current, pageSize }),
    {
      paginated: true,
    }
  );
  return (
    <div>
      <List
        dataSource={data?.list}
        loading={loading}
        renderItem={(item: any) => (
          <List.Item>
            {item.name} - {item.email}
          </List.Item>
        )}
      />
      <Pagination
        {...(pagination as any)}
        showQuickJumper
        showSizeChanger
        onShowSizeChange={pagination.onChange}
        style={{ marginTop: 16, textAlign: 'right' }}
      />
    </div>
  );
};
```

**AntD Table**

特别支持了 antd table 所需的分页格式，及 `sorter`、`filters` 等。可以通过 `result.tableProps`、`result.params[0]?.filters`，`result.params[0]?.sorter` 访问到这些属性。

```tsx
import { useRequest } from '@umijs/hooks';
import React from 'react';
import { Table, Button } from 'antd';
import Mock from 'mockjs';

interface UserListItem {
  id: string,
  name: string,
  gender: 'male' | 'female',
  email: string,
  disabled: boolean
}

const userList = (current, pageSize) => (
  Mock.mock({
    total: 55,
    [`list|${pageSize}`]: [{
      id: '@guid',
      name: '@cname',
      'gender|1': ['male', 'female'],
      email: '@email',
      disabled: false
    }],
  })
)

async function getUserList(params: { current: number, pageSize: number, gender?: string }): Promise<{ total: number, list: UserListItem[] }> {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve(userList(params.current, params.pageSize))
    }, 1000)
  });
}

export default () => {
  const { tableProps, params, refresh } = useRequest(({ current, pageSize, sorter: s, filters: f }) => {
    const p: any = { current, pageSize };
    if (s?.field && s?.order) {
      p[s.field] = s.order;
    }
    if (f) {
      Object.entries(f).forEach(([filed, value]) => {
        p[filed] = value;
      });
    }
    console.log(p);
    return getUserList(p);
  }, {
    paginated: true,
    defaultPageSize: 5
  });

  // you can read sorter and filters from params[0]
  const { sorter = {}, filters = {} } = params[0] || ({} as any);

  const columns = [
    {
      title: 'name',
      dataIndex: 'name',
    },
    {
      title: 'email',
      dataIndex: 'email',
    },
    {
      title: 'id',
      dataIndex: 'id',
      sorter: true,
      sortOrder: sorter.field === 'id' ? sorter.order : false,
    },
    {
      title: 'gender',
      dataIndex: 'gender',
      filters: [{ text: 'male', value: 'male' }, { text: 'female', value: 'female' }],
      filteredValue: filters.gender,
    },
  ];

  return (
    <div>
      <Button onClick={refresh} style={{ marginBottom: 16 }}>刷新</Button>
      <Table columns={columns} rowKey="id" {...tableProps} />
    </div>
  );
};
```

**带缓存的Pagination**

在 cacheKey 场景下，run 的参数 params 是可以缓存的，利用这个特点，我们可以实现pagnization 相关条件的缓存。

```tsx
import { useBoolean, useRequest, useUpdateEffect } from '@umijs/hooks';
import { Button, List, Pagination, Select } from 'antd';
import React, { useState } from 'react';
import Mock from 'mockjs';

interface UserListItem {
  id: string,
  name: string,
  gender: 'male' | 'female',
  email: string,
  disabled: boolean
}

const userList = (current, pageSize) => (
  Mock.mock({
    total: 55,
    [`list|${pageSize}`]: [{
      id: '@guid',
      name: '@cname',
      'gender|1': ['male', 'female'],
      email: '@email',
      disabled: false
    }],
  })
)

async function getUserList(params: { current: number, pageSize: number, gender?: string }): Promise<{ total: number, list: UserListItem[] }> {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve(userList(params.current, params.pageSize))
    }, 1000)
  });
}

export default () => {
  const { state, toggle } = useBoolean();
  return (
    <div>
      <p>You can click the button multiple times, the conditions of pagination will be cached.</p>
      <p>
        <Button onClick={() => toggle()}>show/hidden</Button>
      </p>
      {state && <PaginationComponent />}
    </div>
  )
};

const PaginationComponent = () => {
  const { params, run, data, loading, pagination } = useRequest(
    (p, gender?: string) => getUserList({ ...p, gender }),
    {
      cacheKey: 'paginationDemo',
      paginated: true
    }
  );

  const [gender, setGender] = useState<string>(params[1]);

  useUpdateEffect(() => {
      // reload when gender change
      run({
        current: 1,
        pageSize: 10
      }, gender);
  }, [gender])


  return (
    <div>
      <Select
        value={gender}
        style={{ width: 180, marginBottom: 24 }}
        onChange={(g: string) => setGender(g)}
        placeholder="select gender"
        allowClear
      >
        <Select.Option value="male">male</Select.Option>
        <Select.Option value="female">female</Select.Option>
      </Select>
      <List
        dataSource={data && data.list}
        loading={loading}
        renderItem={item => (
          <List.Item>
            {item.name} - {item.email}
          </List.Item>
        )}
      />
      <Pagination
        {...(pagination as any)}
        showQuickJumper
        showSizeChanger
        onShowSizeChange={pagination.onChange}
        style={{ marginTop: 16, textAlign: 'right' }}
      />
    </div>
  );
};
```

### 加载更多

通过设置 `options.loadMore=true`，useRequest 将以loadMore模式运行，此时会有以下特性：

- useRequest 会自动管理列表数据，返回的 `data.list` 为所有请求数据的 list 合并数组。service 的参数为 `result.data | undefined`。
- service 返回的数据结构必须包含 `{list: Item[]}`，如果不满足，可以通过 `options.formatResult` 转换一次
- useRequest 会额外返回 `result.loadingMore` 和 `result.loadMore`。
- 通过设置 `options.ref`、`options.isNoMore`，可以实现上拉加载更多功能。
- `refreshDeps`变化，会清空当前数据，并重新发起请求，一般可以把 loadMore 依赖的条件放这里。

#### API

```js
const {
    ...,
    // 触发加载更多的方法
    loadMore,
    // 是否正在加载更多
    loadingMore,
    // 是否有更多数据，需要配合 options.isNoMore 使用
    noMore,
    // 触发重新加载
    reload
} = useRequest(service, {
    ...,
    // 是否开启加载更多模式
    loadMore,
    // 容器的ref，如果存在，则滚动至底时，自动触发 loadMore
    ref,
    // 判断是否还有更多数据的函数
    isNoMore,
    // 下拉自动加载，距离底部距离阈值
    threshold,
    // refreshDeps变化，会清空当前数据，重新请求
    refreshDeps
})
```



**加载更多-基本用法**

```tsx
import { useBoolean, useRequest } from '@umijs/hooks';
import { Button, Spin, List, Typography } from 'antd';
import React from 'react';


interface Item {
  id?: string,
  name: string
}

interface Result {
  list: Item[],
  nextId: string | undefined
}

const resultData = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9'];

export async function getLoadMoreList(nextId: any, limit: any): Promise<Result> {
  let start = 0;
  if (nextId) {
    start = resultData.findIndex(i => i === nextId);
  }
  const end = start + limit;
  const list = resultData.slice(start, end).map(id => ({
    id,
    name: `project ${id} (server time: ${Date.now()})`
  }));
  const nId = resultData.length >= end ? resultData[end] : undefined;
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({
        list,
        nextId: nId
      });
    }, 1000);
  });
}


export default () => {
  const { state, toggle } = useBoolean(true);
  return (
    <div>
      <p>You can click the button multiple times, the loadmore will be cached.</p>
      <p>
        <Button onClick={() => toggle()}>show/hidden</Button>
      </p>
      {state && <LoadMoreComponent />}
    </div>
  )
};

const LoadMoreComponent = () => {
  const { data, loading, loadMore, loadingMore } = useRequest((d: Result | undefined) => getLoadMoreList(d?.nextId, 3), {
    loadMore: true,
    cacheKey: 'loadMoreDemoCacheId',
  });

  return (
    <div>
      <Spin spinning={loading}>
        <List
          dataSource={data?.list}
          renderItem={item => (
            <List.Item key={item.id}>
              <Typography.Text mark>[{item.id}]</Typography.Text> {item.name}
            </List.Item>
          )}
        />
      </Spin>
      <Button
        onClick={loadMore}
        loading={loadingMore}
        disabled={!data?.nextId}
      >
        click to load more
      </Button>
    </div>
  );
};
```

**加载更多-上拉加载**

如果options 中存在 ref，则在滚动到底部时，自动触发 loadMore。当然此时必须设置 isNoMore，以便让 useRequest 知道何时停止。

```tsx
import { useRequest } from '@umijs/hooks';
import { Avatar, Button, List } from 'antd';
import React, { useRef } from 'react';

interface Item {
  id: number;
  title: string;
}

interface Result {
  total: number;
  list: Item[];
}

const dataSource = [
  {
    id: 1,
    title: 'Ant Design Title 1',
  },
  {
    id: 2,
    title: 'Ant Design Title 2',
  },
  {
    id: 3,
    title: 'Ant Design Title 3',
  },
  {
    id: 4,
    title: 'Ant Design Title 4',
  },
  {
    id: 5,
    title: 'Ant Design Title 5',
  },
  {
    id: 6,
    title: 'Ant Design Title 6',
  },
  {
    id: 7,
    title: 'Ant Design Title 7',
  },
  {
    id: 8,
    title: 'Ant Design Title 8',
  },
  {
    id: 9,
    title: 'Ant Design Title 9',
  },
  {
    id: 10,
    title: 'Ant Design Title 10',
  },
];

const asyncFn = ({ pageSize, offset }: any): Promise<Result> =>
  new Promise(resolve => {
    setTimeout(() => {
      resolve({
        total: dataSource.length,
        list: dataSource.slice(offset, offset + pageSize),
      });
    }, 1000);
  });

export default () => {
  const containerRef = useRef<HTMLDivElement>(null);
  const { data, loading, loadingMore, reload, loadMore, noMore } = useRequest((d: Result | undefined) => asyncFn({
    offset: d?.list?.length || 0,
    pageSize: 3,
  }), {
    loadMore: true,
    ref: containerRef,
    isNoMore: d => (d ? d.list.length >= d.total : false)
  });

  const { list = [] } = data || {};

  const renderFooter = () => (
    <>
      {!noMore && (
        <Button onClick={loadMore} loading={loadingMore}>
          {loadingMore ? 'Loading more' : 'Click to load more'}
        </Button>
      )}

      {noMore && <span>No more data</span>}

      <span style={{ float: 'right', fontSize: 12 }}>total: {data?.total}</span>
    </>
  );

  return (
    <div ref={containerRef} style={{ height: 300, overflowY: 'auto' }}>
      <List
        header={
          <Button onClick={reload} loading={loading}>
            Reload
          </Button>
        }
        footer={!loading && renderFooter()}
        loading={loading}
        bordered
        dataSource={list}
        renderItem={item => (
          <List.Item>
            <List.Item.Meta
              avatar={
                <Avatar src="https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png" />
              }
              title={<a>{item.title}</a>}
              desc="umijs/hooks is a react hooks library"
            />
          </List.Item>
        )}
      />
    </div>
  );
};

```

## 全局配置

### UseApiProvider

通过 `UseApiProvider` 在项目最外层设置全局 options。

```js
import { useApiProvider } from '@umijs/use-request';

export function ({children}) => {
    return (
    	<UseAPIProvider value={{
              refreshOnWindowFocus: true,
              requestMethod: (params) => axios(params),
              ...
		}}>
          {children}
        </UseApiProvider>
	)
}
```

