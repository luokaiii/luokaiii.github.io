---
title: 《MyBatis》读书笔记 - MyBatis XML 映射文件
date: 2019-09-05 14:10:20
tags: 
  - MyBatis
categories:
  - 读书笔记
  - MyBatis
visible: hide
---

# 三、MyBatis XML 映射文件

MyBatis 通过映射 XML 文件的方式，减少了 JDBC 的样板代码，十分的简单。

SQL 映射文件只有少数几个顶级约束：

- cache - 对给定命名空间的缓存配置
- cache-ref - 对其他命名空间缓存配置的引用
- resultMap - 描述如何从数据库结果集中加载对象
- ~~parameterMap - 废弃，参数映射~~
- sql - 可重用语句块
- insert - 插入语句
- update - 更新语句
- delete - 删除语句
- select - 查询语句

## 一、select

查询语句，如：

```xml
<select id="selectPerson" parameterType="int" resultType="hashmap">
	SELECT * FROM PERSON WHERE ID = #{id}
</select>
```

select 元素允许你配置很多属性来配置每条语句的作用细节

```xml
<select
  		id="selectPerson"
        parameterType="int"
        parameterMap="deprecated"
        result="hashMap"
        resultMap="personResultMap"
        flushCache="false"
        useCache="true"
        timeout="10"
        fetchSize="256"
        statementType="PREPARED"
        resultSetType="FORWARD_ONLY"
        >

</select>
```

| 属性             | 描述                                                         |
| ---------------- | ------------------------------------------------------------ |
| id               | 命名空间中的唯一标识符，用来引用该语句                       |
| parameterType    | 传入的参数的完全限定名，可选。MyBatis会通过类型处理器推断具体传入的参数类型 |
| ~~parameterMap~~ | ~~引用外部 parametermap 的方法~~                             |
| resultType       | 返回的期望乐行的类的完全限定名或别名。如果结果为集合，则应该设置为集合包含的类型，而不是集合本身。 |
| resultMap        | 外部 resultMap 的命名引用。与resultType不能同时使用          |
| flushCache       | 默认为false。为true时，只要语句被调用，都会导致本地缓存和二级缓存被清空 |
| useCache         | 默认对select元素为true。将本条语句的结果使用二级缓存缓存起来 |
| timeout          | 驱动程序等待数据库返回请求结果的秒数，而后抛出异常           |
| fetchSize        | 尝试让驱动程序每次批量返回的结果行数和设置的值相等           |
| statementType    | STATEMENT、PREPARED(默认)、CALLABLE中的一个                  |
| resultSetType    | FORWARD_ONLY、SCROLL_SENSITIVE、SCROLL_INSENSITIVE、DEFAULT中的一个 |
| databaseId       | 数据库厂商标识                                               |
| resultOrdered    | 仅针对嵌套select，如果为true，则假设包含了嵌套结果集或是分组，这样返回一个主结果行的时候，就不会发生有对前面结果集的引用的情况。使得在获取嵌套结果集时不至于导致内存不够用 |
| resultSets       | 针对多结果集情况。将列出语句执行后返回的结果集并给每个结果集一个名称 |

## 二、insert、update、delete

与 select 参数相似，以一个示例来说明：

```xml
<insert id="insertAuthor" useGeneratedKeys="true" keyProperty="id">
   insert into  Author(username,password,email) values 
   <foreach item="item" collection="list" separator=",">
   	(#{item.username},#{item.password},#{item.email})
   </foreach>
</insert>

<update id="updateAuthor">
	update Author set
     username=#{username},
     password=#{password},
     email=#{email}
    where id=#{id}
</update>

<delete id="deleteAuthor">
	delete from Author where id=#{id}
</delete>
```

## 三、sql

该元素可以被用来定义可重用的 SQL 代码段，这些 SQL 代码可以被包含在其他语句中。它可以在加载的时候被静态地设置参数。在不同的包含语句中可以设置不同的值到参数占位符上，如：

```xml
<sql id="userColumns"> ${alias}.id,${alias}.username,${alias}.password</sql>
```

```xml
<select id="selectUsers" resultType="map">
	select
     <includ refid="userColumns"><property name="alias" value="t1"/></includ>
     <includ refid="userColumns"><property name="alias" value="t1"/></includ>
    from some_table t1
    cross join some_table t2
</select>
```

> cross join 表示笛卡尔积，t1+t2的所有情况

属性值也可以被用在 include 元素的 refid 属性里或 include 元素的内部语句中，如：

```xml
<sql id="sometable">
	${prefix}Table
</sql>

<sql id="someinclud">
  	from
     <includ refid="${include_target}"/>
</sql>

<select id="select" resultType="map">
	select
     field1,field2,field3
    <includ refid="someinclud">
     <property name="prefix" value="Some"/>
     <property name="includ_target" value="sometable"/>
    </includ>
</select>
```

## 四、参数

```xml
<insert id="selectUser" parameterType="User">
	insert into users (id,username,password)
    values (#{id},#{username},#{password})
</insert>
```

上面的语句中，User 类型的参数对象传递到了语句中，id、username、password 属性会被查找，然后将它们的值传入预处理语句的参数中。

参数也可以指定一个特殊的数据类型：

> #{property,javaType=int,jdbcType=NUMERIC}

也可以指定一个特殊的类型处理器类：

> #{age,javaType=int,jdbcType=NUMERIC,typeHandler=MyTypeHandler}

需要保留小数点的数值类型：

> #{height,javaType=double,jdbcType=NUMERIC,numericScale=2}

尽管该选项功能强大，但大多数情况下只需要简单地指定属性名，MyBatis 会自己推断类型，顶多`需要为可能为空的列指定 jdbcType`。

### 字符串替换

默认情况下，使用 `#{}` 格式的语法会导致 MyBatis 创建 PreparedStatement 参数占位符并安全地设置参数(就像？一样)。不过有时想直接在 SQL 中插入一个不转义的字符串，如：

> order by ${columnName}

这样，MyBatis 不会修改或转义字符串。

有时，替换字符串会非常有用，如：

```java
@Select("select * from user where id=#{id}")
User findById(@Param("id") long id);

@Select("select * from user where name=#{name}")
User findByName(@Param("name") String name);

@Select("select * from user where email=#{email}")
User findByEmail(@Param("email") long email);
```

可以只写一个方法：

```java
@Select("select * from user where ${column}=#{email}")
User findByColumn(@Param("column") String column, @Param("email") long email);
```

使用的时候，可以如下调用：

```java
User u1 = userMapper.findByColumn("id",1L);
User u2 = userMapper.findByColumn("name","zhangsan");
User u3 = userMapper.findByColumn("email","xx@qq.com");
```

## 五、结果映射

resultMap 可以将返回结果映射到JavaBean或POJO上。如：

```xml
<select id="selectUsers" resultType="cn.luokaiii.model.User">
	select id,username
    from tb_user
    where id=#{id}
</select>
```

也可以使用类型别名，来代替完全限定名：

```xml
<typeAlias type="cn.luokaiii.model.User" alias="User"/>

<select id="selectUsers" resultType="User">
	select id,username
    from tb_user
    where id=#{id}
</select>
```

如果列名与属性名没有精确匹配，可以在 SELECT 语句中对列使用别名来匹配标签，如：

```xml
<select id="selectUsers" resultType="cn.luokaiii.model.User">
	select 
     id        as "id",
     username  as "userName"
    from tb_user
    where id=#{id}
</select>
```

或者使用外部的 `resultMap` 来映射：

```xml
<resultMap id="userResultMap" type="User">
	<id property="id" column="user_id"/>
    <result property="username" column="user_name"/>
    <result property="password" column="hashed_password"/>
</resultMap>
```

```xml
<select id="selectUsers" resultMap="userResultMap">
	select id,username
    from tb_user
    where id=#{id}
</select>
```

### 高级结果映射

```xml
<!-- 非常复杂的语句 -->
<select id="selectBlogDetails" resultMap="detailedBlogResultMap">
  select
       B.id as blog_id,
       B.title as blog_title,
       B.author_id as blog_author_id,
       A.id as author_id,
       A.username as author_username,
       A.password as author_password,
       A.email as author_email,
       A.bio as author_bio,
       A.favourite_section as author_favourite_section,
       P.id as post_id,
       P.blog_id as post_blog_id,
       P.author_id as post_author_id,
       P.created_on as post_created_on,
       P.section as post_section,
       P.subject as post_subject,
       P.draft as draft,
       P.body as post_body,
       C.id as comment_id,
       C.post_id as comment_post_id,
       C.name as comment_name,
       C.comment as comment_text,
       T.id as tag_id,
       T.name as tag_name
  from Blog B
       left outer join Author A on B.author_id = A.id
       left outer join Post P on B.id = P.blog_id
       left outer join Comment C on P.id = C.post_id
       left outer join Post_Tag PT on PT.post_id = P.id
       left outer join Tag T on PT.tag_id = T.id
  where B.id = #{id}
</select>
```

```xml
<!-- 非常复杂的结果映射 -->
<resultMap id="detailedBlogResultMap" type="Blog">
  <constructor>
    <idArg column="blog_id" javaType="int"/>
  </constructor>
  <result property="title" column="blog_title"/>
  <association property="author" javaType="Author">
    <id property="id" column="author_id"/>
    <result property="username" column="author_username"/>
    <result property="password" column="author_password"/>
    <result property="email" column="author_email"/>
    <result property="bio" column="author_bio"/>
    <result property="favouriteSection" column="author_favourite_section"/>
  </association>
  <collection property="posts" ofType="Post">
    <id property="id" column="post_id"/>
    <result property="subject" column="post_subject"/>
    <association property="author" javaType="Author"/>
    <collection property="comments" ofType="Comment">
      <id property="id" column="comment_id"/>
    </collection>
    <collection property="tags" ofType="Tag" >
      <id property="id" column="tag_id"/>
    </collection>
    <discriminator javaType="int" column="draft">
      <case value="1" resultType="DraftPost"/>
    </discriminator>
  </collection>
</resultMap>
```

### 结果映射（resultMap）

- constructor - 用于在实例化类时，注入结果到构造方法中
  - idArg - ID参数，标记出ID，可以帮助提高整体性能
  - arg - 将被注入到构造方法的一个普通结果
- id - 一个ID结果
- result - 注入到字段或 JavaBean 属性的普通结果
- association - 一个复杂类型的关联；许多结果将包装成这种类型
  - 嵌套结果映射 - 集合本身可以是一个 resultMap 元素，或者从别处引用一个
- collection - 一个复杂类型的集合
  - 嵌套结果映射 - 集合本身可以是一个 resultMap 元素，或者从别处引用一个
- discriminator - 使用结果值来决定使用哪个 resultMap
  - case - 基于某些值的结果映射
    - 嵌套结果映射 - case 本身可以是一个 resultMap 元素，因此可以具有相同的结构和元素，或者从别处引用一个

### id & result

id 和 result 元素都将一个列的值映射到一个简单数据类型的属性或字段。

```xml
<id property="id" column="post_id"/>
<result property="subject" column="post_subject"/>
```

### 构造方法

有时，需要使用不可变类来接收，即在构造方法中注入允许在初始化时设置的值，而不暴露出公有方法。

```java
public class User {
    // ...property
    public User(Integer id, String userName, int age){
        // ...set
    }
    
    // private setter and getter
}
```

```xml
<constructor>
	<idArg column="id" javaType="int" name="id"/>
    <arg column="username" javaType="String" name="userName"/>
    <arg column="age" javaType="_int" name="age"/>
</constructor>
```

### 关联

关联（association）元素处理“有一个”类型的关系。

```xml
<association property="author" cloumn="blog_author_id" javaType="Author">
	<id property="id" column="author_id"/>
    <result property="username" column="author_username"/>
</association>
```



MyBatis 有两种方式加载关联：

- 嵌套 Select 查询：通过执行另一个 SQL 映射语句来加载期望的复杂类型
- 嵌套结果映射：使用嵌套的结果映射来处理连接结果的重复子集

### 关联的嵌套Select查询

```xml
<resultMap id="blogResult" type="Blog">
	<association property="author" column="author_id" javaType="Author" select="selectAuthor"/>
</resultMap>

<select id="selectBlog" resultMap="blogResult">
	select * from blog where id=#{id}
</select>

<select id="selectAuthor" resultType="Author">
	select * from Author where id=#{id}
</select>
```

selectBlog 用来加载博客，selectAuthor 用来加载作者，而 blogResult 结果映射描述了应该使用 selectAuthor 语句加载它的 author 属性。

这种方式虽然简单，但是`在大型数据集或大型数据表上表现不佳`。这个问题被称为“N+1查询问题”：

- 执行了一个单独的 SQL 语句来获取结果的一个列表（就是+1）
- 对列表返回的每条记录，执行一个 select 查询语句来加载详细信息（就是N）

虽然 MyBatis 能够对这样的查询进行延迟加载，但是如果你在加载完记录表后，立即进行了遍历。此时就会触发所有的延迟加载查询，性能会变得很糟糕。

> 解决方法：使用联合查询来避免N+1问题

```xml
<select id="selectBlog" resultMap="blogResult">
  select
    B.id            as blog_id,
    B.title         as blog_title,
    B.author_id     as blog_author_id,
    A.id            as author_id,
    A.username      as author_username,
    A.password      as author_password,
    A.email         as author_email,
    A.bio           as author_bio
  from Blog B left outer join Author A on B.author_id = A.id
  where B.id = #{id}
</select>
```

```xml
<resultMap id="blogResult" type="Blog">
  <id property="id" column="blog_id" />
  <result property="title" column="blog_title"/>
  <association property="author" column="blog_author_id" javaType="Author" resultMap="authorResult"/>
</resultMap>

<resultMap id="authorResult" type="Author">
  <id property="id" column="author_id"/>
  <result property="username" column="author_username"/>
  <result property="password" column="author_password"/>
  <result property="email" column="author_email"/>
  <result property="bio" column="author_bio"/>
</resultMap>
```

### 集合

集合元素和关联元素几乎是一样的

```xml
<collection property="posts" ofType="domain.blog.Post">
	<id property="id" column="post_id"/>
    <result property="subject" column="post_subject"/>
    <result property="body" column="post_body"/>
</collection>
```

#### 集合的嵌套 Select 查询

```xml
<resultMap id="blogResult" type="Blog">
	<collection property="posts" javaType="ArrayList" column="id" ofType="Post" select="selectPostsForBlog"/>
</resultMap>

<select id="selectBlog" resultMap="blogResult">
	select * from blog where id=#{id}
</select>

<select id="selectPostsForBlog" resultType="Post">
	select * from post where blog_id=#{id}
</select>
```

> ` javaType="ArrayList" ofType="Post"` 等同于结果为：ArrayList<Post>

#### 集合的嵌套结果映射

```xml
<select id="selectBlog" resultMap="blogResult">
  select
  B.id as blog_id,
  B.title as blog_title,
  B.author_id as blog_author_id,
  P.id as post_id,
  P.subject as post_subject,
  P.body as post_body,
  from Blog B
  left outer join Post P on B.id = P.blog_id
  where B.id = #{id}
</select>
```

```xml
<resultMap id="blogResult" type="Blog">
  <id property="id" column="blog_id" />
  <result property="title" column="blog_title"/>
  <collection property="posts" ofType="Post">
    <id property="id" column="post_id"/>
    <result property="subject" column="post_subject"/>
    <result property="body" column="post_body"/>
  </collection>
</resultMap>
```

#### 集合的多结果集（ResultSet）

可以通过执行存储过程来实现，存储过程会执行两个查询并返回两个结果集。

```sql
SELECT * FROM BLOG WHERE ID = #{id}

SELECT * FROM POST WHERE BLOG_ID = #{id}
```

在映射时，必须通过 resultSets 属性为每个结果集指定一个名字，使用逗号隔开。

```xml
<select id="selectBlog" resultSets="blogs,posts" resultMap="blogResult">
  {call getBlogsAndPosts(#{id,jdbcType=INTEGER,mode=IN})}
</select>
```

指定结果集的数据进行填充

```xml
<resultMap id="blogResult" type="Blog">
  <id property="id" column="id" />
  <result property="title" column="title"/>
  <collection property="posts" ofType="Post" resultSet="posts" column="id" foreignColumn="blog_id">
    <id property="id" column="id"/>
    <result property="subject" column="subject"/>
    <result property="body" column="body"/>
  </collection>
</resultMap>
```

### 鉴别器（discriminator）

有时，一个数据库查询可能会返回多个不同的结果集。鉴别器元素就是被设计出来应对这种情况的，也能处理如类的继承层次结构的问题。类似于 java 语言中的 switch 语句。

```xml
<resultMap id="vehicleResult" type="Vehicle">
  <id property="id" column="id" />
  <result property="vin" column="vin"/>
  <result property="year" column="year"/>
  <result property="make" column="make"/>
  <result property="model" column="model"/>
  <result property="color" column="color"/>
  <discriminator javaType="int" column="vehicle_type">
    <case value="1" resultMap="carResult"/>
    <case value="2" resultMap="truckResult"/>
    <case value="3" resultMap="vanResult"/>
    <case value="4" resultMap="suvResult"/>
  </discriminator>
</resultMap>
```

### 自动映射

在简单的场景下，MyBatis 可以为你自动映射查询结果。但如果遇到复杂的场景，则需要自行构建一个结果映射。

## 六、缓存

MyBatis 内置了一个强大的事务性查询缓存机制，可以非常方便地配置和定制。

默认情况下，只启用了本地的会话缓存，仅对一个会话中的数据进行缓存。要启用全局的二级缓存，只需要在你的 SQL 映射文件中添加一行：

> <cache/>

其效果如下：

- 映射语句文件中的所有 select 结果都将被缓存
- 映射语句文件中的所有 insert、update、delete 都会刷新缓存
- 缓存会使用 最少使用算法 来清除不需要的缓存
- 缓存不会定时进行刷新
- 缓存会保存列表或对象的1024个引用
- 缓存会被视为读/写缓存，意味着获取到的对象并不是共享的，可以安全地被调用者修改，而不干扰其他调用者或县城所做的潜在修改

> 缓存只作用于 cache 标签所在的映射文件中的语句。混用 API和XML，在共用接口中的语句将不会被默认缓存。 可以修改cache 元素的属性：

```xml
<cache 
       eviction="FIFO" // FIFO缓存
       flushInerval="60000" // 每隔60秒刷新
       size="512" 		// 最多可存512个引用
       readOnly="true"  // 返回对象只读
/>
```

可用的清除策略：

- LRU - 最近最少使用：移除最长时间不被使用的对象
- FIFO - 先进先出：按对象进入缓存的顺序来移除他们
- SOFT - 软引用：基于垃圾回收器状态和软引用规则移除对象
- WEAK - 弱引用：更积极地基于垃圾回收期状态和弱引用规则移除对象

#### 使用自定义缓存

通过实现 Cache 接口或者第三方缓存方案，来完全覆盖缓存行为。

```xml
<cache type="com.domain.something.MyCustomCache"/>
```

> 上面说到的缓存配置（如清除策略、可读写等），不适用于自定义缓存