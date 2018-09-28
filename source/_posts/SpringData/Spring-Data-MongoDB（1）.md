# 前言

## Spring Data

Spring MongoDB 与Spring Framewrok 提供的 JDBC 十分相似，在熟悉本篇文章之前，需要先熟悉 MongoDB 和Spring 的概念

Spring Data 使用了 Spring 框架的核心功能，包括：
- IOC容器 (IOC container)
- 类型转换系统 (type conversion system)
- EL表达式 (expression language)
- JMX集成
- Dao异常层次结构

## MongoDB

MongoDB作为一种 NOSQL 工具，非 RDMBS 设计范式，官方文档：https://docs.mongodb.com/manual/reference/operator/query/in/index.html

RDMBS设计范式：http://blog.51cto.com/echoroot/1953996

## Spring Data MongoDB 2.0

- 升级至 java8
- 使用 Document API，而非 DBObject
- 支持聚合结果流 Stream
- Kotlin 扩展
- 支持隔离 Update 操作
- 使用 Spring 的 @NonNullApi 和 @Nullable 保证 Null 安全

## Spring Data MongoDB 支持的注解

```java
@Document   : 文档标识，将 java 类与 Collection 文档对应
@Id         : 文档的唯一标识，在 mongodb 中为 ObjectID，生成规则：时间戳+机器标识+进程ID+自增计数器(确保同一时间内ID不会冲突)
@Field      : 属性注解
@Indexed    : 索引
@CompoundIndex  : 混合索引
@GeoSpatialIndexed  : 声明该字段为地理信息的索引
@Transient  : 映射忽略的字段 (即不会保存到 mongodb)
@Query      ：查询
```

## 依赖
在使用 SpringDataMongoDB 之前，需要先声明对 SpringData 模块的依赖关系。

既然 SpringData 存储库抽象中的中央接口是 Repository 。 该接口的子类 CrudRepository 实现了实体类的 CRUD 功能，如果需要的话，也可以通过继承该接口来拓展 ：
```java
public interface CrudRepository<T, ID extends Serializable>
  extends Repository<T, ID> {

  <S extends T> S save(S entity);      

  Optional<T> findById(ID primaryKey); 

  Iterable<T> findAll();               

  long count();                        

  void delete(T entity);               

  boolean existsById(ID primaryKey);   

  // … more functionality omitted.
}
```

> 除了 CrudRepository 之外，还有 JpaRepository 和 MongoRepository。在 CrudRepository 中，有许多抽象方法添加了额外的方法来简化对实体的分页访问。

```java
/**
* PagingAndSortingRepository 方法提供了分页和排序的功能
*/
public interface PagingAndSortingRepository<T, ID extends Serializable>
  extends CrudRepository<T, ID> {

  Iterable<T> findAll(Sort sort);

  Page<T> findAll(Pageable pageable);
}
```

> 还有包含：删除查询、计数、查询等相关接口

## 定义自己的 Repository

声明扩展 Repository 或者其子接口之一的接口，并嵌入需要处理的对象和 ID 类型：
```java
interface UserRepository extends MongoRepository<User,String>{  // UserRepository 默认继承了父类的 CRUD 方法
    List<User> findByName(String name); // 扩展的查询方法
}
```
## repository 方法的 Null 处理

从 Spring Data 2.0 开始，返回单个聚合实例的 Repository 的 CRUD 方法 可以使用 Java8 中的Optional 来只是可能缺少的值，支持返回一下的包装类型：

- com.google.common.base.Optional
- scala.Option
- io.vavr.control.Option
- javaslang.control.Option (不推荐)

或者，不使用包装类型，直接返回查询结果为 Null。 使用 Optional 的好处在于保证了方法返回的对象永远不会为 Null，而是相应的 空表示。

## 多 Spring Data 模块的 Repository

``` java
interface MyRepository extends JpaRepository<User, Long> { }

@NoRepositoryBean
interface MyBaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
  …
}

interface UserRepository extends MyBaseRepository<User, Long> { // MyRepository 与 MyBaseRepository 都继承了 JpaRepository ，所以它们是有效的子类
  …
}
```

``` java
interface AmbiguousRepository extends Repository<User, Long> {
 …
}

@NoRepositoryBean
interface MyBaseRepository<T, ID extends Serializable> extends CrudRepository<T, ID> {
  …
}

interface AmbiguousUserRepository extends MyBaseRepository<User, Long> {
    // 而且多个 SpringData 模块导致无法区分这些 Repository 应该绑定到哪个特定的 仓库
  …
}
```

## 定义查询方法

> 仓库代理有两种方式导出指定的查询。
- 从名字直接导出查询 ： 类似 findByName(String name);
- 手工定义的查询 : 类似 @query("{ name : ?0}")

> 查询定义策略：
    通过 xml 文件中的 query-lookup-strategy 参数或者 Enable 注解中的 queryLookupStrategy 参数。
- CREATE 尝试从方法名中构造指定仓库的查询方法
- USE_DECLARED_QUERY 尝试找到声明的查询，若无则抛出异常
- CREATE_IF_NOT_FOUND 先查找声明的查询，如不能找到，将生成一个基于命名的查询（默认查询策略，一般不用变）














































文章总结于：https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#dependencies.spring-framework