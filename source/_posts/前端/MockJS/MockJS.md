---
title: 《MockJS》官网笔记
date: 2020-04-01 10:00:00
categories:
  - 前端
  - MockJS
tags:
  - MockJS
top: true
---
# MockJS 模拟数据

MockJS 是一个前端用于模拟数据的类库。

<!-- More -->

## 一、安装

```sh
$ npm install mockjs
```

## 二、语法规范

Mock.js 的语法规范包括两部分：

1. 数据模板定义规范（Data Template Definition，DTO）
2. 数据占位符定义规范（Data Placeholder Definition，DPD）

### 2.1 数据模板定义规范 DTO

数据模板中每个属性由3部分构成：属性名、生成规则、属性值：

```properties
'name|rule': value
```

1. 属性值是字符串 String
   - `'name|min-max': string`：通过重复 string 生成一个字符串，重复次数为min-max之间
   - `'name|count: string'`：通过重复 string 生成一个字符串，重复次数等于 count
2. 属性值是数字 Number
   - `'name|+1': number`：属性值自动加1，初始值为number
   - `'name|min-max': number`：生成一个min-max 之间的证书，属性值number 只是用来确定类型
   - `'name|min-max.dmin-dmax': number`：生成一个浮点数，整数部分为min-max，小数部分保留dmin-dmax位
3. 属性值是布尔型 Boolean
   - `'name|1': boolean`：随机生成一个布尔值，值的概率各为为 1/2
   - `'name|min-max': value`：随机生成一个布尔值，值为 value 的概率为 min/(min+max)；值不为value 的概率为 max/(min+max)
4. 属性值是对象 Object
   - `'name|count': Object`：从属性值 Object 中随机选取 count 个属性
   - `'name|min-max': Object`：从属性值 Object 中随机选取 min-max 个属性
5. 属性值是数组 Array
   - `'name|1': array`：从属性值 array 中随机选取1个元素，作为最终值
   - `'name|+1': array`：从属性值 array 中顺序选取1个元素，作为最终值
   - `'name|min-max': array`：通过重复属性值 array 生成一个新数组，重复次数为min-max
   - `'name|count': array`：通过重复属性值 array 生成一个新数组，重复次数为 count
6. 属性值是函数 Function
   - `'name': function`：执行函数 function，取其返回值作为最终的属性值，函数的上下文为属性 name 所在对象
7. 属性值是正则表达式 RegExp
   - `'name': regexp`：根据正则表达式 regExp 反向生产成可以匹配的字符串

### 2.2 数据占位符定义规范 DPD

占位符只是在属性值字符串占个位置，并不出现在最终属性值中。

注：

- 占位符可以引用数据模板中的属性
- 占位符会优先引用数据模板中的数据

```js
Mock.mock({
    name: {
        first: '@FIRST',
        middle: '@FIRST',
        last: '@LAST',
        full: '@first @middle @last'
    }
})
// => 
{
    "name": {
        "first": "Charles",
        "middle": "Brenda",
        "last": "Lopez",
        "full": "Charles Brenda Lopez"
    }
}
```

## 三、Mock.mock()

### 3.1 参数

1. rurl：可选。表示需要拦截的 URL，可以是 URL 字符串或 URL 正则，如 `/\/domain\/list.json/`、`/domain/list.json`
2. rtype：可选。表示需要拦截的 Ajax 请求类型。如 GET、POST、PUT、DELETE 等
3. template：可选。表示数据模板，即上面的 DTO，如 `@EMAIL`、`{'data|1-10':[{}]}`
4. function(options)：可选，表示用于生成相应数据的函数
5. options：指向本次请求的 Ajax 选项集，含有 url、type、body 三个属性。

### 3.2 方法

1. Mock.mock(rurl?, rtype?, template|function(options))：根据数据模板生成模拟数据
2. Mock.mock(template)：根据数据模板生成模拟数据
3. Mock.mock(rurl, template)：记录数据模板。当拦截到匹配 `rurl` 的 Ajax 请求时，将根据数据模板 `template` 生成模拟数据，并作为响应数据返回。
4. Mock.mock(rurl, function(options))：记录用于生成响应数据的函数。当拦截到匹配 `rurl` 的 Ajax 请求时，函数 `function(options)` 将被执行，并把执行结果作为响应数据返回。
5. Mock.mock(rurl, rtype, template)：记录数据模板。当拦截到匹配 `rurl` 和 `rtype` 的 Ajax 请求时，将根据数据模板 `template` 生成模拟数据，并作为响应数据返回。
6. Mock.mock(rurl, rtype, function(options))：记录用于生成响应数据的函数。当拦截到匹配 `rurl` 和 `rtype` 的 Ajax 请求时，函数 `function(options)` 将被执行，并把执行结果作为响应数据返回。

## 四、Mock.setup()

### 4.1 Mock.setup(settings)

配置拦截 Ajax 请求时的行为。支持的配置由：timeout

**timeout**：指定被拦截的Ajax请求的响应时间(毫秒)，如 `400`、`200-600`，分别表示400毫秒与200至600毫秒

## 五、Mock.Random

Mock.Random 是一个工具类，用于生成各种随机数据。

Mock.Random 的方法在数据模板中称为 占位符，书写格式为(参数[, 参数])

## 5.1 全部方法

| 类型          | 方法                                                         |
| ------------- | ------------------------------------------------------------ |
| Basic         | boolean, natural, integer, float, character, string, range, date, time, datetime, now |
| Image         | image, dataImage                                             |
| Color         | color                                                        |
| Text          | paragraph, sentence, word, title, cparagraph, csentence, cwrod, ctitle |
| Name          | first, last, name, cfirst, clast, cname                      |
| Web           | url, domain, email, ip, tld                                  |
| Address       | area, region                                                 |
| Helper        | capitalize, upper, lower, pick, shuffle                      |
| Miscellaneous | guid, id                                                     |

### 5.2 Basic

1. `Random.boolean(min?, max?, current?)`：返回一个随机布尔值。min、max为current的出现概率，默认为1，即各为50%
2. `Random.natural(min?, max?)`：返回一个随机的自然数(大于等于0的整数)
3. `Random.integer(min?, max?)`：返回一个随机的整数。-9007199254740992 ~ 9007199254740992
4. `Random.float(min?, max?, dmin?, dmax?)`：返回一个随机浮点数
5. `Random.character(pool?)`：返回一个随机字符串，pool为字符池，将从中选择一个字符返回
6. `Random.string(pool?, min?, max?)`：返回一个随机字符串，min、max表示字符串的最小、大长度
7. `Random.range(start, stop?, step?)`：返回一个整型数组，start起始值，stop结束值，step步长

### 5.3 Date

1. `Random.date(format?)`：返回一个随机的日期字符串
   - format默认为 yyyy-MM-dd
2. `Random.time(format?)`：返回一个随机的时间字符串
   - format默认为 HH:mm:ss
3. `Random.datetime(format?)`：返回一个随机日期和时间字符串
   - format：默认为 yyyy-MM-dd HH:mm:ss
4. `Random.now(unit?, format?)`：返回当前的日期和时间字符串
   - unit：year、month、week、day、hour、minute、second、week
   - format：默认yyyy-MM-dd HH:mm:ss

### 5.4 Image

1. `Random.image(size?, background?, foreground?, format?, text?)`：生成一个随机的图片地址。用于生成高度自定义的图片地址，一般应该使用 Random.dataImage()
   - size：指示图片的宽高，如 300x250
   - background：指示图片的背景色，默认为 #000000
   - foreground：指示图片的前景色，默认为 #FFFFFF
   - format：指示图片的格式，默认为 png，可选 png、gif、jpg
   - text：指示图片上的文字，默认值为参数size
2. `Random.dataImage(size?, text?)`：生成一段随机的 Base64 图片编码。
   - size：指示图片的宽高
   - text：指示图片上的文字

### 5.5 Name

1. `Random.first()`：随机生成一个常见的英文名
2. `Random.last()`：随机生成一个常见的英文姓
3. `Random.name(middle?)`：随机生成一个常见的英文姓名
4. `Random.cfrist()`：随机生成一个常见的中文名
5. `Random.clast()`：随机生成一个常见的中文姓
6. `Random.cname()`：随机生成一个常见的中文姓名

### 5.6 Web

1. `Random.url(protocol?, host?)`：随机生成一个 url
   - protocol：指定 URL协议，如 http
   - host：指定 URL域名和端口号，如 sss.com
2. `Random.protocol()`：随机生成一个 URL 协议
3. `Random.domain()`：随机生成一个域名
4. `Random.tld()`：随机生成一个顶级域名
5. `Random.email(domain?)`：随机生成一个邮件地址
6. `Random.ip()`：随机生成一个IP地址

### 5.7 Address

1. `Random.region()`：随机生成一个（中国）大区
2. `Random.province()`：随机生成一个（中国）省份
3. `Random.city(prefix?)`：随机生成一个（中国）市，prefix指定是否生成所属省
4. `Random.county(prefix?)`：随机生成一个（中国）县，prefix指定是否生成所属省、市
5. `Random.zip()`：随机生成一个邮编

### 5.8 helper

1. `Random.capitalize(word)`：把字符串的首字母大写
2. `Random.upper(str)`：转大写
3. `Random.lower(str)`：转小写
4. `Random.pick(arr)`：从数组中随机选取一个元素返回
5. `Random.shuffle(arr)`：打乱数组中元素的顺序，并返回

### 5.9 Miscellaneous

1. `Random.guid()`：随机生成一个GUID
2. `Random.id()`：随机生成一个18位身份证
3. `Random.increment(step?)`：生成一个全局的自增整数

## 六、Mock.valid()

`Mock.valid(template, data)`：校验真实数据 data 与数据模板 template 是否匹配。

## 七、Mock.toJSONSchema()

`Mock.toJSONSchema(template)`：把 Mock.js 风格的数据模板 template 转换成 JSON Schema。