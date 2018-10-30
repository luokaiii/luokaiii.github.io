---
title: 学习之路(一)JAVA8新特性-用法详解
date: 2018-10-26 18:06:03
categories: 
 - Java成神之路
 - Study社区
---
# JAVA8新特性-用法详解

本文从各个方面讲解 Java 8 的新特性，从源码解析到个性小案例都有。包含的技术点：[Predicate接口](##Predicate接口)、[Function接口](##Function接口)、[Supplier接口](##Supplier接口)、[Comsumer接口](##Comsumer接口)、[Comparator比较](##Comparator比较)、[Optional接口](##Optional接口)、[Stream接口](##Stream接口) [Filter过滤](##Filter过滤)、[Sort排序](##Sort排序)、[数值型Stream](##数值型Stream)、[Match匹配](##Match匹配)、[Count计数](##Count计数)、[Reduce归约](##Reduce归约)及[并行与串行Stream](##并行与串行)。

![JAVA8新特性-用法详解
](https://upload-images.jianshu.io/upload_images/13603359-1671eca11ae9f3b9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

<!-- More -->

## Predicate接口

Predicate 接口只能接收一个参数，且返回 boolean 类型。类似于 Assert 的自定义断言

```java

例子：
    Predicate<String> predicate = new Predicate<String>() {
        @Override
        public boolean test(String s) {
            return s.equals("张三");
        }
    };
    Predicate<String> predicate = s -> s.equals("张三"); // 同第一个
    Predicate<String> predicate2 = Objects:nonNull;
    Predicate<String> predicate3 = String::isEmpty;
使用：
    predicate.test("李四"); // false
    predicate.test("张三"); // true
    predicate.negate.test("李四"); // true,取反操作

```

## Function接口

Function 函数，接收一个参数并返回一个结果。
Function<String, Integer>中，第一个泛型表示入参的类型，第二个泛型表示出参的类型。
Function 接口还附带了一些能够组合其他函数的方法(如：andThen、compose),必须实现的方法为 apply(T).

```java

例子：
    /**
     * 接收一个字符串，将其转换为整型后返回
     */
    final Function<String, Integer> function1 = new Function<String, Integer>() {
        @Override
        public Integer apply(String s) {
            return Integer.valueOf(s);
        }
    };
    final Function<String, Integer> function2 = s -> Integer.valueOf(s); // 同第一条
    final Function<String, Integer> function3 = Integer::valueOf; // 同第二条

    final Function<String, String> function4 = function3.andThen(String::valueOf);

    System.out.println(function3.apply("33").getClass());
// 输出结果：class java.lang.Integer
    System.out.println(function4.apply("33").getClass());
// 输出结果：class java.lang.String

```

## Supplier接口

Supplier 接口返回一个任意泛型的值。和 Predicate 不同的是，该接口不需要参数。

```java

例子：
    Supplier<CoreUser> supplier = new Supplier<CoreUser>() {
        @Override
        public CoreUser get() {
            return new CoreUser();
        }
    };
    Supplier<CoreUser> supplier = () -> new CoreUser(); // 同第一条
    Supplier<CoreUser> supplier = CoreUser::new; // 同第二条
使用：
    supplier.get(); // 返回 CoreUser 对象
```

## Comsumer接口

Consumer 接口，执行在单个参数上的操作，无返回值。

```java

例子：
    final Consumer<String> consumer = new Consumer<String>() {
        @Override
        public void accept(String s) {
            System.out.println(s);
        }
    };
    final Consumer<String> consumer = s -> System.out.println(s); // 同第一条
    final Consumer<String> consumer = System.out::println; // 同第二条
使用：
    consumer.accept("你好，世界！");
// 输出结果：你好，世界！

```

## Comparator比较

在讲 `sort排序` 之前，先说一下 Comparator 接口，Comparator 是一个特别经典的方法，用于两个数值之间的比较。进入 java.util.Comparator 的内部，我们看到只定义了一个 compare() 方法待实现：

```java
public interface Comparator<T> {
    // 在其子类 SortedOps 中，使用 AbstractPipeline 比较器和Comparator来实现比较。
    int compare(T o1, T o2);
}
```

```java
例子：
    private final Comparator<User> comparatorDESC = (a, b) -> Long.compare(a.getAge(), b.getAge());
// 通过 User 的 age 属性，来定义一个比较器
```

## Optional接口

Optional 接口是用来防止 `NullPointerException` 异常的辅助类型，是一个非常重要的概念。比如在新版本的 SpringData 中最常用的ID查询方法 `findOne()`：

```java
/**
 * Spring Data MongoDB 的 findOne()方法
 */
<S extends T> Optional<S> findOne(Example<S> example);
```

以下列举几个 Optional 的常用方法：

```java
/**
 * Optional 为一个对象容器，value为容器中存储的对象
 *  当对象为空时，{@code isPresent()} 将会返回 true，反之为 false
 */
public final class Optional<T> {

    private static final Optional<?> EMPTY = new Optional<>();

    private final T value;

    private Optional() {
        this.value = null;
    }

    /**
     * 返回一个空的容器
     */
    public static<T> Optional<T> empty() {
        @SuppressWarnings("unchecked")
        Optional<T> t = (Optional<T>) EMPTY;
        return t;
    }

    private Optional(T value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * 返回一个包含 value 的 Optional 对象
     */
    public static <T> Optional<T> of(T value) {
        return new Optional<>(value);
    }

    /**
     * 返回一个包含 不为空的value 的 Optional 对象
     */
    public static <T> Optional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    /**
     * 取出 value，并返回。
     *  如果 value 为空，则抛出异常
     */
    public T get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    /**
     * 判断 value 是否为空
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * 如果 value 存在，则通过 Consumer 接口接收 value 对象
     */
    public void ifPresent(Consumer<? super T> consumer) {
        if (value != null)
            consumer.accept(value);
    }

    /**
     * 如果 value 存在，则校验 Predicate 中的条件并返回
     */
    public Optional<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!isPresent())
            return this;
        else
            return predicate.test(value) ? this : empty();
    }

    /**
     * 如果 value 存在，则将提供的映射函数应用于该值。
     *  通过 function 中过滤的结果应该为一个 Stream流，因此返回的实际结果为 findFirst()，相当于：
     * <pre>{@code
     *     Optional<FileInputStream> fis =
     *         names.stream().filter(name -> !isProcessedYet(name))
     *                       .findFirst()
     *                       .map(name -> new FileInputStream(name));
     * }</pre>
     */
    public<U> Optional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return empty();
        else {
            return Optional.ofNullable(mapper.apply(value));
        }
    }

    /**
     * 类似于 map() 方法，但是存在过滤
     */
    public<U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return empty();
        else {
            return Objects.requireNonNull(mapper.apply(value));
        }
    }

    /**
     * 返回结果
     */
    public T orElse(T other) {
        return value != null ? value : other;
    }

    /**
     * 与 orElse() 方法相反
     */
    public T orElseGet(Supplier<? extends T> other) {
        return value != null ? value : other.get();
    }
}
```

从 Optional 接口的源码中可以看出，当返回对象 Optional 时，可以有效的避免出现 `NullPointerException` 异常。
在 Java8 之前如果一个函数的返回值为空，则在调用时可能会出现 `NullPointerException` 异常。而在 Java8 中，则推荐返回 Optional 对象。

下面我们举个栗子：

```java
正常的调用方法：
public static void main(String[] args){
    User user = null;
    // 这里必定会出现 NullPointerException，因为 user 为 null
    String username = user.getName();
    System.out.println(username);
}
```

```java
public static void main(String[] args){
    // 生成一个 value 为空的 Optional 对象
    Optional<User> userOptional = Optional.of(null);
    // 如果 value 存在，则将 user 交给 Consumer 类处理
    //   这里使用了 lambda 表达式，隐式的实现了 Consumer 接口
    userOptional.ifPresent(user -> {
        String username = user.getName();
        System.out.println(username);
    });

}
```

当你需要将 userOptional 中的 value 返回时，可以通过以下的方式返回:

```java
public User getUser(){
    Optional<User> userOptional = Optional.of(null);
    userOptional.ifPresent(user -> {
        String username = user.getName();
        System.out.println(username);
    });
    // 如果 value 存在则返回，否则返回 null
    return userOptional.orElse(null);
}
```

## Stream接口

java.util.Stream 表示能够在一组元素中，执行一个的操作序列。比如 java.util.Collection 的子类：List或者Set。

### 中间操作和最终操作

先说明一下中间操作和最终操作的概念，在讲解具体的操作方法时，我们再来观察方法属于那种操作。

中间操作：表示执行完该操作之后，返回的还是 Stream 本身，可以将这样的多个操作串联起来。

最终操作：表示返回一个特定类型的计算结果。

`假设我们存在一个这样的 List<User> 集合，User对象包含 username 和 age 属性:`

```java
List<User> userList = new ArrayList<>();
userList.add(new User("李某京",20));
userList.add(new User("杜某波",22));
userList.add(new User("刘某萍",22));
userList.add(new User("韩某欣",21));
userList.add(new User("罗某强",18));
```

## Filter过滤

`中间操作`，通过一个 Predicate 接口来过滤并保留符合条件的元素。

```java
源码：
    /**
     * 返回一个 和给定条件匹配的流元素 组成的流
     * @param predicate 应用于每个元素以确定它是否正确
     * @return the new stream
     */
    Stream<T> filter(Predicate<? super T> predicate);
```

```java
例子：
    userList
        .stream()
        .filter(user-> user.getAge() >= 21)
        .forEach(System.out::println);
// 输出结果："杜某波"、"刘某萍"
```

## Sort排序

`中间操作`，通过 Sort() 方法，返回排序之后的 Stream.
如果不指定一个自定义的 Comparator 对象，则会使用默认的排序(对象的默认排序是什么鬼)。

通过向下挖掘发现，其实调用的是下面的方法：
`将对象与指定的对象进行比较以确定顺序。返回一个负整数，零，或正整数`
![Comparator](https://upload-images.jianshu.io/upload_images/13603359-3a0796fc4857525d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

```java
默认排序：
    // 默认排序为自然排序
    Comparator<? super T> comp = (Comparator<? super T>) Comparator.naturalOrder();
```

```java
例子：
    userList
        .stream()
        // 相当于 .sorted((a,b) -> a.getAge().compareTo(b.getAge()))
        .sorted(Comparator.comparing(User::getAge))
        .forEach(user -> System.out.println(user.getUserName()));
// 输出结果：罗某强、李某京、韩某欣、杜某波、刘某萍
```

注意： 上面的方法使用默认的 sorted() 方法，会抛出 `ClassCastException`异常。
原因是： User 对象没有实现 Comparable，如果你想要自定义 User 对象的实现的话，需要让 User 类实现该接口：

```java
public class User implements Comparable{
    @Override
    public int compareTo(Stock o){
        // 实现的方法
    }
}
``

好像并没有什么卵用，除非你的排序比较多，而且只能用 compareTo 方法中定义的排序方式，还不如定义一个全局的 Comparable 对象。

## Map映射

`中间操作`，map会将元素根据指定的Function接口来依次将元素转成另外一个对象。

```java

源码：
    /**
     * 返回由应用给定值的结果组成的流
     * @param <R> 流中内容的类型
     * @param mapper 应用于每个元素的函数
     * @return the new stream
     */
    <R> Stream<R> map(Function<? super T, ? extends R> mapper);

```

```java

例子：
    userList
        .stream()
        // 相当于 .map(user -> user.getUserName())
        .map(User::getUserName)
        .forEach(System.out::println);
// 输出结果：李某京、杜某波、刘某萍、韩某欣、罗某强

```

与 map() 方法类似的还有：mapToInt()、mapToLong()、mapToDouble()、flatMap()、flatMapToInt()、flatMapToLong()、flatMapToDouble()，各自返回对应的 IntStream、LongStream、DoubleStream 等。

## 数值型Stream

在上面的 `Map映射` 中，我们提到了 IntStream、LongStream和DoubleStream 三种数值型的 Stream 对象。

它们同 Stream 一样继承了 BaseStream，且拓展了一些数值的操作方法，如：
`sum()`、`min()`、`max()`、`average()`等方法，非常适用于对集合进行操作。

```java

例子:
    userList
        .stream()
        .mapToInt(User::getAge)
        .sum();
// 输出结果：上面几人的和（不想算)

```

## Match匹配

`最终操作`,Match 是 Stream 用来检测指定的 Predicate 是否匹配整个 Stream，返回一个 Boolean 值。

```java

常见的方法：
    anyMatch(); // 任一匹配
    allMatch(); // 全匹配
    noneMatch(); // 全不匹配

```

```java

例子：
    // userList 中是否存在 "age = 22" 的 user
    boolean match = userList
                    .stream()
                    .anyMatch(user -> user.getAge() == 22);

    // userList 中的所有人都满足 "age = 22"
    boolean match2 = userList
                    .stream()
                    .allMatch(user -> user.getAge() == 22);

    // userList 中没有人满足 "age = 22"
    boolean match3 = userList
                    .stream()
                    .noneMatch(user -> user.getAge() == 22);

```

## Count计数

`最终操作`，返回 Stream 中元素的个数。

```java

例子：
    Long count = userList
                .stream()
                .count();

```

## Reduce归约

`最终操作`，将 Stream 中的元素归约为一个元素。归约后的结果为 Optional。

官方解释：虽然这似乎是执行聚合的一种更为迂回的方式与简单地在循环中突变一个正在运行的总数相比，reduce操作并行化更优雅，不需要额外的操作同步，大大降低了数据竞争的风险。

```java

例子：
    final List<Integer> strings = Arrays.asList(1, 2);
    final Optional<Integer> reduce = strings.stream().reduce((integer, integer2) -> integer + integer2);
    reduce.ifPresent(System.out::println);

```

## 并行与串行

Stream API 中除了提供串行流之外，还提供了并行流。它们之间的区别在于，串行流都是单线程执行，而并行流可以多线程执行。

在 `java.util.COllection<E>` 中，新增了两个默认方法：

1. default Stream stream() ： 返回串行流
2. default Stream parallelStream() : 返回并行流

```java

    @Test
    public void test(){
        // 创建一个没有重复的大表
        int max = 1000000;
        List<String> values = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            final UUID uuid = UUID.randomUUID();
            values.add(uuid.toString());
        }

        parallel(values);
        serial(values);
    }

    /**
     * 串行操作
     */
    private void serial(List<String> values){
        // 查看排序时间
        final long startTime = System.nanoTime();
        final long count = values.stream().sorted().count();
        System.out.println(count);
        final long endTime = System.nanoTime();

        final long millis = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println(String.format("sequential sort took: %d ms",millis));
    }

    /**
     * 并行操作
     */
    private void parallel(List<String> values){
        final long startTime = System.nanoTime();
        final long count = values.parallelStream().count();
        System.out.println(count);
        final long endTime = System.nanoTime();

        final long millis = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println(String.format("parallel sort took: %d ms",millis));
    }

输出结果：   1000000
            parallel sort took: 65 ms
            1000000
            sequential sort took: 1391 ms

```

## 回顾

我们通过一个实例(来自官方)来回顾刚刚的讲解：

```java

    int sum = widgets
            // 先获得到 widgets(工具集合) 的 Stream 对象
            .stream()
            // 过滤出红色小工具
            // 通过 filter() 方法过滤出 color 属性为 RED 的部分，返回结果依然为 Stream
            .filter(w -> w.getColor() == RED)
            // 获得到他们的重量集合
            // 通过 mapToInt() 方法返回 widget的weight属性，并包装为一个 IntStream 对象
            .mapToInt(w -> w.getWeight())
            // 求和
            .sum();

```

上面这个例子可以理解为：通过对象流Stream的各种方法，获得一个只包含红色的小工具的总重量。
