---
title: 学习之路(二)JAVA8新特性-新的日期API
date: 2018-10-29 18:06:03
categories: 
 - Java成神之路
 - Study社区
---
# JAVA8新特性-新的日期API

Java 8 另一个新增的重要特性就是引入了新的日期API，用更加简洁的方式处理时间和日期。具体的 API 放在 `java.time` 下。

## 旧API的缺陷

在 Java 8 之前，所有关于时间和日期的 API 都存在各方面的缺陷，比如：

1. 日期类和时间类分开定义在 java.util.Date 和 java.util.Calendar 中，易用性差，不支持时区，而且是非线程安全的。
2. 格式化日期类 DateFormat 位于 java.text 包中，而且是一个抽象类，我们必须实例化一个 SimpleDateFormat 对象才能格式化日期，并且 DateFormat 也不是线程安全的。这意味着如果你在多线程程序中调用同一个 DateFormat 对象，会得到意想不到的结果。
3. Calendar 中的月份是从 0 开始的，需要加一才能表示当前月份。日期的计算方式十分繁琐，且易出错。
4. 且 java.util.Date 同时包含日期和时间，而 java.sql.Date 中只包含日期，类名相同易混淆。
5. 所有日期类都不是线程安全的，这是日期类最大的问题之一。
6. 日期类不支持国际化，没有时区支持，虽然 java 引入了 java.util.Calendar 和 java.util.TimeZone ，但是并没有解决上面的问题。

由于以上这些问题，Java 需要一套用于处理日期和时间标准的框架，于是在 Java8 中引入了新的日期API。
与此同时还诞生了如Joda-Time，date4j等第三方的日期处理框架。
虽然 Java8 引用了新的时间处理类，但是 Joda-Time 的作者也参与了该设计。
如果您的项目依然是 JDK8 一下的版本，Joda-Time也是个不错的选择。

## 新API的优点

Java 8 中的日期和时间类，主要就是为了解决 7 以前的问题。下面就让我们来看看如何处理的。

1. 不变性：新的日期/时间API中，所有的类都是不可变的，这对多线程环境有好处。
2. 关注点分离：新的API将人可读的日期时间和机器时间（unix timestamp）明确分离，它为日期（Date）、时间（Time）、日期时间（DateTime）、时间戳（unix timestamp）以及时区定义了不同的类。
3. 高效的使用了工厂模式和策略模式，类之间的协同工作变得简单。在所有类中都定义了 format() 和 parse() 方法，明确定义用以完成相同的行为，而不是像以前一个有一个单独的类。
4. 实用操作：所有新的日期/时间API类都实现了一系列方法用以完成通用的任务，如：加、减、格式化、解析、从日期/时间中提取单独部分，等等。
5. 可扩展性：新的日期/时间API是工作在ISO-8601日历系统上的，但我们也可以将其应用在非IOS的日历上。

## API

java日期/时间API包含以下相应的包。

1. java.time包：这是新的Java日期/时间API的基础包，所有的主要基础类都是这个包的一部分，如：LocalDate, LocalTime, LocalDateTime, Instant, Period, Duration等等。所有这些类都是不可变的和线程安全的，在绝大多数情况下，这些类能够有效地处理一些公共的需求。
2. java.time.chrono包：这个包为非ISO的日历系统定义了一些泛化的API，我们可以扩展AbstractChronology类来创建自己的日历系统。
3. java.time.format包：这个包包含能够格式化和解析日期时间对象的类，在绝大多数情况下，我们不应该直接使用它们，因为java.time包中相应的类已经提供了格式化和解析的方法。
4. java.time.temporal包：这个包包含一些时态对象，我们可以用其找出关于日期/时间对象的某个特定日期或时间，比如说，可以找到某月的第一天或最后一天。你可以非常容易地认出这些方法，因为它们都具有“withXXX”的格式。
5. java.time.zone包：这个包包含支持不同时区以及相关规则的类。

## 示例

以 `2018-10-22 18:22:30:999999999` 为例：

### LocalDate日期

```java
final LocalDate localDate = LocalDate.of(2018, 10, 22);
final LocalDate now = LocalDate.now(); // 获取当前日期 2018-10-22
final LocalDate ofYearDay = LocalDate.ofYearDay(2018, 295); // 第year年的第dayOfYear天 2018-10-22

localDate.getMonth(); // 月 10
localDate.getYear(); // 年 2018
localDate.getDayOfMonth(); // 日 22
localDate.getDayOfWeek(); // 星期 MONDAY
localDate.getDayOfYear(); // 天 295
```

### LocalTime时间

其中纳秒最多可达 999999999.

```java
final LocalTime of = LocalTime.of(18, 22); // 时-分 18:22
final LocalTime of1 = LocalTime.of(18, 22, 30); // 时-分-秒 18:22:30
final LocalTime of2 = LocalTime.of(18, 22, 30, 999999999); // 时-分-秒-纳秒 18:22:30.999999999

of.getHour(); // 时 18
of.getMinute(); // 分 22
of.getSecond(); // 秒 30
of.getNano(); // 纳秒 999999999
```

### LocalDateTime

LocalDateTime 相当于 LocalDate + LocalTime 的合体，即包含日期和时间的类。

```java
final LocalDateTime dateTime = LocalDateTime.of(2018, 10, 22, 18, 22, 30, 9999); // 年-月-日T时:分:秒:纳秒 2018-10-22T18:22:30.000009999
final LocalDateTime now = LocalDateTime.now(); // 当前时间 2018-10-22T18:22:30.000009999

// LocalDateTime 转为 LocalDate 和 LocalTime
dateTime.toLocalDate();
dateTime.toLocalTime();

// 将 LocalDate 转为 LocalDateTime
final LocalDate localDate = LocalDate.now(); // 2018-10-22
final LocalTime localTime = LocalTime.now(); // 18:22:30.000009999
final LocalDateTime localDateTime = localDate.atTime(localTime); // 2018-10-22T18:22:30.000009999
```

## Instant

精确到纳秒的时间戳，与 `System.currentTimeMillis()` 类似。Instant 具有两个属性：seconds 毫秒数，nanos 纳秒值。

```java
Instant instant = Instant.ofEpochSecond(120, 100000);
// 1970-01-01T00:02:00.000100Z
```

## Duration

Duration 表示一个时间段，因此没有 `now()` 方法，与 Instant 类似。

```java
LocalDateTime from = LocalDateTime.of(2017, Month.JANUARY, 5, 10, 7, 0);    // 2017-01-05 10:07:00
LocalDateTime to = LocalDateTime.of(2017, Month.FEBRUARY, 5, 10, 7, 0);     // 2017-02-05 10:07:00
Duration duration = Duration.between(from, to);     // 表示从 2017-01-05 10:07:00 到 2017-02-05 10:07:00 这段时间

long days = duration.toDays();              // 这段时间的总天数
long hours = duration.toHours();            // 这段时间的小时数
long minutes = duration.toMinutes();        // 这段时间的分钟数
long seconds = duration.getSeconds();       // 这段时间的秒数
long milliSeconds = duration.toMillis();    // 这段时间的毫秒数
long nanoSeconds = duration.toNanos();      // 这段时间的纳秒数
```

也可以通过 `of()` 方法创建，接收时间长度和时间单位作为参数：

```java
Duration duration1 = Duration.of(5, ChronoUnit.DAYS);       // 5天
Duration duration2 = Duration.of(1000, ChronoUnit.MILLIS);  // 1000毫秒
```

## 日期操作和格式化

### 增加和减少日期

```java

    LocalDateTime now = LocalDateTime.now();

    LocalDate date1 = date.plusYears(1); // 增加一年 2019-10-22
    LocalDate date2 = date.minusMonths(2);  // 减少两个月 2018-08-22
    LocalDate date3 = date.plus(5, ChronoUnit.DAYS); // 增加5天 2019-10-27
```

### 时区操作

新的时区类java.time.ZoneId是原有的java.util.TimeZone类的替代品。ZoneId对象可以通过ZoneId.of()方法创建，也可以通过ZoneId.systemDefault()获取系统默认时区。

```html
所有的时区:
<p>
This maps as follows:
<ul>
<li>EST - -05:00</li>
<li>HST - -10:00</li>
<li>MST - -07:00</li>
<li>ACT - Australia/Darwin</li>
<li>AET - Australia/Sydney</li>
<li>AGT - America/Argentina/Buenos_Aires</li>
<li>ART - Africa/Cairo</li>
<li>AST - America/Anchorage</li>
<li>BET - America/Sao_Paulo</li>
<li>BST - Asia/Dhaka</li>
<li>CAT - Africa/Harare</li>
<li>CNT - America/St_Johns</li>
<li>CST - America/Chicago</li>
<li>CTT - Asia/Shanghai</li>
<li>EAT - Africa/Addis_Ababa</li>
<li>ECT - Europe/Paris</li>
<li>IET - America/Indiana/Indianapolis</li>
<li>IST - Asia/Kolkata</li>
<li>JST - Asia/Tokyo</li>
<li>MIT - Pacific/Apia</li>
<li>NET - Asia/Yerevan</li>
<li>NST - Pacific/Auckland</li>
<li>PLT - Asia/Karachi</li>
<li>PNT - America/Phoenix</li>
<li>PRT - America/Puerto_Rico</li>
<li>PST - America/Los_Angeles</li>
<li>SST - Pacific/Guadalcanal</li>
<li>VST - Asia/Ho_Chi_Minh</li>
</ul>
</p>
```

```java
ZoneId shanghaiZoneId = ZoneId.of("Asia/Shanghai");
ZoneId systemZoneId = ZoneId.systemDefault();
// 输出的话：[Asia/Shanghai]
```