---
title: 《MyBatis》读书笔记 - MyBatis 动态SQL
date: 2019-09-05 16:10:20
tags: 
  - MyBatis
categories:
  - 读书笔记
  - MyBatis
visible: hide
---

# 四、MyBatis 动态SQL

MyBatis 提供了可以被用在任意 SQL 映射语句中的动态SQL。动态 SQL 元素和 JSTL 或基于 XML 的文本处理器相似。

MyBatis 采用基于 OGNL 的表达式来淘汰其他元素。

- if
- choose(when,otherwise)
- trim(where,set)
- foreach

## if

动态 SQL 通常要做的事情是根据条件包含 where 子句的一部分，如：

```xml
<select id="findActive" resultType="Blog">
	select * from blog
    where  state='active'
    <if test="title != null">
    	and title like #{title}
    </if>
</select>
```

需要多个条件，可以使用 and 连接

## choose、when、otherwise

```xml
<select id="findActive" resultType="Blog">
	select * from blog
    where state="active"
    <choose>
    	<when test="title != null">
            and title like #{title}
        </when>
        <when test="author != null and author.name != null">
        	and author_name like #{author.name}
        </when>
        <otherwise>
        	and featured = 1
        </otherwise>
    </choose>
</select>
```

## trim、where、set

如果上面的 where 语句中，去掉了 “state=active”，当所有条件都不匹配时，sql 就不正常了。此时，可以使用 where 标签来解决

```xml
<select id="findActive" resultType="Blog">
 	select * from blog
    <where>
    	<if test="state != null">
        	state = #{state}
        </if>
        <if test="title != null">
        	and title like #{title}
        </if>
    </where>
</select>
```

where 元素会在至少有一个子元素的条件返回 SQL 子句的情况下才去插入 where 子句。

若语句开头为 AND 或 OR ，where 元素也会将它们去除。

和 where 元素等价的自定义 trim 元素：

```xml
<trim prefix="where" prefixOverrides="AND | OR">
 	...
</trim>
```

set 元素可以用于动态包含需要更新的列，而舍去其他的，如：

```xml
<update id="updateActive">
	update Author
    <set>
    	<if test="username != null">username=#{username},</if>
        <if test="password != null">password=#{password},</if>
        <if test="email != null">email=#{email}</if>
    </set>
</update>
```

## foreach

动态 SQL 的另一个常用操作是对一个集合进行遍历，通常是在构建 IN 条件语句的时候，如：

```xml
<select id="selectPostIn" resultType="Post">
	select * from post p
    where id in
    <foreach item="item" index="index" collection="list"
             open="(" separator="," close=")">
    	#{item}
    </foreach>
</select>
```

> foreach 可迭代的对象如 List、Set、Map或数组都可以作为参数。

## script

如果要在注解中使用动态SQL，可以使用 script 元素：

```java
    @Update({"<script>",
      "update Author",
      "  <set>",
      "    <if test='username != null'>username=#{username},</if>",
      "    <if test='password != null'>password=#{password},</if>",
      "    <if test='email != null'>email=#{email},</if>",
      "    <if test='bio != null'>bio=#{bio}</if>",
      "  </set>",
      "where id=#{id}",
      "</script>"})
    void updateAuthorValues(Author author);
```

## bind

bind 元素可以从 OGNL 表达式中创建一个变量并将其绑定到上下文中：

```xml
<select id="selectBlogs" resultType="Blog">
	<bind name="pattern" value="'%' + _parameter.getTitle() + '%'"/>
    select * from blog
    where title like #{pattern}
</select>
```

## 多数据库支持

```xml
<insert id="insert">
  <selectKey keyProperty="id" resultType="int" order="BEFORE">
    <if test="_databaseId == 'oracle'">
      select seq_users.nextval from dual
    </if>
    <if test="_databaseId == 'db2'">
      select nextval for seq_users from sysibm.sysdummy1"
    </if>
  </selectKey>
  insert into users values (#{id}, #{name})
</insert>
```

