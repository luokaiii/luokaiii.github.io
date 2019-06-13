---
title: MarkDown语法
date: 2018-09-01 14:46:03
categories: 
 - MarkDown语法
---

# sfMarkDown语法d

“=”是最高阶标题，Setext形式，标记等号上一行的段落

“-”是二阶标题，同“=”的作用

----------

> 区块引用则使用 email 形式的'>'角括号
>
> 中间使用一行回车来换行，如果不带">"括号，则分块
> 这都是一个块

## 修饰和强调：

> Markdown使用星号和底线
> 来标记需要 *强调* 的 ___区域___
>
> 一个 * 或 _ 会将包含的字体*倾斜*，两个则会_加粗_,三个则会 ___加粗和倾斜___

## 列表：

### 无序列表使用星号、加号和减号来作为列表的项目标记，这些符号都是可以使用的：

* Candy
* Jack

+ cc
+ dd

- dd
- ee

### 输出的结果都会是HTML中的ul

### 有序列表则是使用一般的数字接着一个英文句点作为项目标记：

> 1. Red
> 2. Green
> 3. Blue

### 输出的结果是HTML中的ol

## 链接：

### MarkDown 支持两种形式的链接语法：行内和参考两种形式，两种都是使用角括号来把文字转成链接。

> 行内形式是直接在后面用括号直接接上链接：
>
> This is an [example link]  (http://example.com/ "With a Title") .
>
> 相当于HTML中的\<a>标签

## 图片：

### 图片的语法和链接很像

> 行内形式(title 是选择性的)
>
> ![alt text](/path/to/image.jpg "Title")
>
> 如果想直接显示代码，在括号前加上转义字符即可

## 代码：

### 在一般的段落文字中，你可以使用反引号 ` 来标记代码区段，区段内的 & < > 都会被自动转换成HTML实体，这项特性让你可以很容易在代码区段插入HTML代码：

> I strongly recommend against using any `<blink>` tags
> 
> I wish SmartyPants used named entities like `&mdash;`
>
> instead of decimal-encoded entites like `&#8212;`

    sdfjlkjdf<p>esdf</p><a>sfd</a>

``sdfjlkjdf<p>esdf</p><a>sfd</a>``

    sdfjlkjdf<p>esdf</p><a>sfd</a>