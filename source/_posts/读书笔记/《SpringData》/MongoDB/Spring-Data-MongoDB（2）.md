---
title: SpringDataMongoDB(二)
date: 2018-09-01 14:46:03
tags:
  - Spring Data MongoDB
  - hide
categories:
  - 读书笔记
  - Spring Data MongoDB
---
# Spring Data MongoDB （二） 查询

## 创建查询（去重、区间、忽略大小写等）

查询的构建机制，将截断前缀 find...By、 read...By、 query...By、 count...By、 get...By 等，从剩余部分开始解析，省略号中可以使用如：Distinct、Between、LessThan、GreaterThan、Like等表达式

``` java
interface PersonRepository extends Repository<User, Long> {

  List<Person> findByEmailAddressAndLastname(EmailAddress emailAddress, String lastname);

  // 使用 Distinct 去重
  List<Person> findDistinctPeopleByLastnameOrFirstname(String lastname, String firstname);
  List<Person> findPeopleDistinctByLastnameOrFirstname(String lastname, String firstname);

  // 使用 IgnoreCase 忽略大小写查询
  List<Person> findByLastnameIgnoreCase(String lastname);
  // 使用 AllIgnoreCase 全部忽略大小写
  List<Person> findByLastnameAndFirstnameAllIgnoreCase(String lastname, String firstname);

  // 使用 OrderBy (Field) ASC/DESC 进行排序
  List<Person> findByLastnameOrderByFirstnameAsc(String lastname);
  List<Person> findByLastnameOrderByFirstnameDesc(String lastname);
}
```

## 属性表达式（子属性查询）

即一个被管理实体的属性，在查询时，会去查找该属性类的嵌套属性类。如：Person 有一个 Health 属性类，二Health 也有一个 HeartIm 属性类，则通过方法名查询为：

``` java
List<Person> findByHealthHeartIm(HeartIm heartIm);    // 相当于 {health.heart:?0}
```

其查询顺序为，先匹配 healthheartIm 属性是否存在，若否，匹配 healthHeart.Im ，最后才是 health.Heart.Im。再没有则接着向下拆分。为了解决模糊不清的含义，我们可以在方法名中使用 “_” 手动创建分割点。

``` java
List<Person> findByHealth_HeartIm(HeartIm heartIm);
```

## 特殊参数（分页、排序）

除了在查询中定义处理方法参数之外，还有一些特殊的类型，如：Pageable 和 Sort，用于分页和排序:

``` java
// Page 接口中返回了元素的总数、可分页数等，其实是通过底层触发 count 方法进行了总数查询
Page<User> findByLastname(String lastname, Pageable pageable);

// Slice 仅仅知道是否有下一个可用的 Slice，在遍历大结果集时非常有效
Slice<User> findByLastname(String lastname, Pageable pageable);

List<User> findByLastname(String lastname, Sort sort);

List<User> findByLastname(String lastname, Pageable pageable);
```

## 限制查询结果（Top、First等）

查询方法的结果可以通过关键字：first、top 来限制，紧跟随的数值会限定长度，默认为1

```java
User findFirstByOrderByLastnameAsc();

User findTopByOrderByAgeDesc();

Page<User> queryFirst10ByLastname(String lastname, Pageable pageable);

Slice<User> findTop3ByLastname(String lastname, Pageable pageable);

List<User> findFirst10ByLastname(String lastname, Sort sort);

List<User> findTop10ByLastname(String lastname, Pageable pageable);
```

## 查询结果流（Stream）

查询的结果可以使用 java8 的 Stream<T> 来处理，这样可以使用 stream 的良好性能。

```java
@Query("select u from User u")
Stream<User> findAllByCustomQueryAndStream();

Stream<User> readAllByFirstnameNotNull();

@Query("select u from User u")
Stream<User> streamAllPaged(Pageable pageable);
```

因为 Stream 使用了底层的资源，所以在使用之后必须关闭：

```java
try (Stream<User> stream = repository.findAllByCustomQueryAndStream()) {  
    stream.forEach(…);
}
```

而且，并不是所有的 Spring Data 模块都支持 Stream

## 异步查询结果

Repository 的查询方法可以异步执行，这意味着该方法在调用时会立即返回，但是 `实际的查询要提交给 Spring 的任务TaskExecutor`

```java
/** Future 的常用方法： 
isCancelled():boolean    
isDone():boolean    
get():V     
get(long timeout,@NotNull TimeUnit unit):V
*/
@Async
Future<User> findByFirstname(String firstname);     // java.util.concurrent.Future          

@Async
CompletableFuture<User> findOneByFirstname(String firstname);  // Java 8 的 java.util.concurrent.CompletableFuture

@Async
ListenableFuture<User> findOneByLastname(String lastname);  // org.springframework.util.concurrent.ListenableFuture
```

## 生成 Repository 实例

使用 xml 配置的方式 指定repositories 扫描的包路径：

``` java
<repositories base-package="com.acme.repositories" />
```

使用注解的方式：

``` java
@Configuration
@EnableJpaRepositories("com.acme.repositories")
class ApplicationConfiguration {
    @Bean
    EntityManagerFactory entityManagerFactory() { // … }
}
```

> 以上是 Spring Data 的公共基础部分，再往下就是 MongoDBFactory 等的底层实现了。才疏学浅，看不下去啊。就到这里吧，第八章。

文章总结于：https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#dependencies.spring-framework