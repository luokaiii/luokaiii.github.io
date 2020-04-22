---
title: 《Umi Hooks》官网笔记-useSelections
date: 2020-04-21 10:00:00
categories:
  - 前端
  - Umi/Hooks
tags:
  - Umi/Hooks
  - hide
---
# useSelections

常见联动的 checkbox 逻辑封装，支持多选、单选、全选逻辑，还提供了是否选择、是否全选、是否半选的状态。

## 一、API

```js
const {
    // 已经选择的元素，array
    selected,
    // 是否被选择，func
    isSelected,
    // 选择元素，func
    select,
    // 取消选择元素，func
    unSelect,
    // 反选元素，func
    toggle,
    // 选择全部元素，func
    selectAll,
    // 取消选择全部元素，func
    unSelectAll,
    // 反选全部元素，func
    toggleAll,
    // 是否全选，boolean
    allSelected,
    // 是否一个都没有选择，boolean
    noneSelected,
    // 是否半选
    partiallySelected,
    // 设置选择的元素
    setSelected
} = useSelections<T>(items: T[], defaultSelected?: T[]);
```

## 二、示例

常见的checkbox联动

```tsx
import React, { useMemo, useState } from 'react';
import { Checkbox, Row, Col } from 'antd';
import { useSelections } from '@umijs/hooks';

export default () => {
  const [hideOdd, setHideOdd] = useState(false);

  const list = useMemo(() => {
    if (hideOdd) {
      return [2, 4, 6, 8];
    }
    return [1, 2, 3, 4, 5, 6, 7, 8];
  }, [hideOdd]);

  const {
    selected,
    allSelected,
    isSelected,
    toggle,
    toggleAll,
    partiallySelected,
  } = useSelections(list, [1]);

  return (
    <div>
      <div>Selected: {selected.join(',')}</div>
      <div style={{ borderBottom: '1px solid #e8e8e8', padding: '10px 0' }}>
        <Checkbox checked={allSelected} onClick={toggleAll} indeterminate={partiallySelected}>
          全选
        </Checkbox>
        <Checkbox checked={hideOdd} onClick={() => setHideOdd((v) => !v)}>
          隐藏部分
        </Checkbox>
      </div>
      <Row style={{ padding: '10px 0' }}>
        {list.map((o) => (
          <Col span={12} key={o}>
            <Checkbox checked={isSelected(o)} onClick={() => toggle(o)}>
              {o}
            </Checkbox>
          </Col>
        ))}
      </Row>
    </div>
  );
};
```

