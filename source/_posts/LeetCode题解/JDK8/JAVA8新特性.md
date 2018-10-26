---
title: (一)JAVA8新特性-用法详解
date: 2018-10-26 18:06:03
categories: 
 - Java成神之路
 - Study社区
---
# JAVA8新特性-用法详解

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
// 输出结果：上面几人的和（不想算）
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






## 回顾

我们通过一个实例来回顾刚刚的讲解：

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

## 未完待续..........。