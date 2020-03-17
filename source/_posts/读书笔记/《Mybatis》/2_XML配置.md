---
title: 《MyBatis》读书笔记 - MyBatis 中的 XML 详细配置
date: 2019-09-05 10:10:20
tags: 
  - MyBatis
categories:
  - 读书笔记
  - MyBatis
visible: hide
---

# 二、MyBatis 中的 XML 详细配置

MyBatis 的配置文件包含了深深影响 MyBatis 行为的设置和属性信息。配置文档的顶层结构如下：

- configuration(配置)
  - properties(属性)
  - settings(设置)
  - typealiases(类型别名)
  - typeHandlers(类型处理器)
  - objectFactory(对象工厂)
  - plugins(插件)
  - environments(环境配置)
    - environment(环境变量)
      - transactionManager(事务管理器)
      - dataSource(数据源)
  - databaseIdProvider(数据库厂商标识)
  - mappers(映射器)

## 一、属性（Properties）

这些属性都是可外部配置且可动态替换的，如：

```xml
<properties resource="org/mybatis/example/config.properties">
	<property name="username" value="dev_user"/>
    <property name="password" value="12345678"/>
</properties>
```

可替换的动态配置属性值：

```xml
<dataSource type="POOLED">
	<property name="driver" value="${driver}"/>
    <property name="url" value="${url}"/>
    <property name="username" value="${username}"/>
    <property name="password" value="${password}"/>
</dataSource>
```

如果属性不值在一个地方进行了配置，那么 MyBatis 的加载顺序如下：

- 首先读取在 properties 元素体内属性值
- 根据 properties 元素中的 resource 属性读取类路径下的文件或 url 读取属性文件，并覆盖已读取的同名属性
- 最后读取作为方法参数传递的属性，并覆盖已读取的同名属性

因此，方法参数传递的属性优先级最高，其次是 resource/url 指定的配置文件，最后是 properties 中指定的属性。

在 MyBatis 3.4.2 开始，可以为占位符制定一个默认值，如：

```xml
<dataSource type="POOLED">
    <!-- 如果属性 username 没有配置，那么默认值为 ut_user -->
 	<property name="username" value="${username:ut_user}"/>
</dataSource>
```

这个特性默认是关闭的，应该添加一个指定的属性来开启这个特性，如：

```xml
<properties resoure="org/mybatis/example/config.properties">
    <!-- 启用默认值特性 -->
	<property name="org.apache.ibatis.parsing.PropertyParser.enable-default-value" value="true" />
</properties>
```

## 二、设置（settings）

MyBatis 中极为重要的调整设置，它们会改变 MyBatis 的运行时行为，下标描述了设置中各项的意图、默认值等。

| 设置名                           | 描述                                                         |
| -------------------------------- | ------------------------------------------------------------ |
| cacheEnabled                     | 全局地开关映射器中已经配置的缓存                             |
| lazyLoadingEnabled               | 延迟加载的全局开关，所有关联对象都会延迟加载，可以通过 fetchType 来覆盖该项 |
| aggressiveLazyLoading            | 开启时，任何方法的调用都会加载该对象的所有属性。否则，每个对象会按需加载 |
| multipleResultSetsEnabled        | 是否允许单一语句返回多结果集（需要驱动支持）                 |
| useColumnLabel                   | 使用列标签代替列名                                           |
| useGeneratedKeys                 | 允许 JDBC 支持自动生成主键                                   |
| autoMappingBehavior              | 指定MyBatis应如何自动映射列到字段或属性。NONE：取消自动映射；PARTIAL：自动映射没有定义嵌套结果集的；FULL：自动映射任意复杂的结果集 |
| autoMappingUnknownColumnBehavior | 指定发现自动映射目标未知列（或未知属性）的行为。NONE：不做任何反应；WARNING：输出提醒日志；FAILING：映射失败（抛出SqlSessionException） |
| defaultExecutorType              | 配置默认的执行器。SIMPLE：普通执行器；REUSE 执行器会重用预处理语句；BATCH：执行器将重用语句并执行批量更新 |
| defaultStatementTimeout          | 设置超时时间，决定驱动等待数据库响应的描述                   |
| defaultFetchSize                 | 为驱动的结果集获取数量（fetchSize）设置一个提示值            |
| defaultResultSetType             | 指定每个语句的省略滚动策略                                   |
| safeRowBoundsEnabled             | 允许在嵌套语句中使用分页（RowBounds）。false为允许           |
| safeResultHandlerEnabled         | 允许在嵌套局域中使用分页(RowHandler)。false为允许            |
| mapUnderscoreToCamelCase         | 是否开启自动驼峰命名规则映射，即从列名 A_COLUMN 到 Java 属性名 aColumn 的类似映射 |
| localCacheScope                  | MyBatis 利用本地缓存机制防止循环引用和加速重复嵌套查询。SESSION：默认，会缓存一个会话中执行的所有查询；STATEMENT：仅用在局域执行上，对相同的 SqlSession 的不同调用不会共享数据 |
| jdbcTypeForNull                  | 当没有参数提供特定的 JDBC 类型时，为空值指定 JDBC 类型       |
| lazyLoadTriggerMethods           | 指定哪个对象的方法触发一次延迟加载                           |
| defaultScriptingLanguage         | 指定动态 SQL 生成的默认语言                                  |
| defaultEnumTypeHandler           | 指定 Enum 使用的默认 TypeHandler(3.4.5)                      |
| callSettersOnNulls               | 指定当结果集中值为 null 时，是否调用映射对象的 setter 方法。 |
| returnInstanceForEmptyRow        | 当返回的列都是空时，MyBatis默认返回 null。开启后，会返回一个空实例 |
| logPrefix                        | 指定 MyBatis 增加到日志名称的前缀                            |
| logImpl                          | 指定 MyBatis 所用日志的具体实现，未指定时将自动查找          |
| proxyFactory                     | 指定 MyBatis 创建具有延迟加载能力的对象所用到的代理工具      |
| vfsImpl                          | 指定 VFS 的实现                                              |
| useActualParamName               | 允许使用方法签名中的名称作为语句参数名称。需要java 8编译，并加上 -parameters 选项 |
| configurationFactory             | 指定一个提供 Configuration 实例的类。这个被返回的 Configuration 实例用来加载被反序列化对象的延迟加载属性值。这个类必须包含一个签名为 static Configuration getConfiguration() 的方法(3.2.3) |

一个完整的 settings 元素示例如下：

```xml
<settings>
	<setting name="cacheEnabled" value="true"/>
    <setting name="lazyLoadingEnabled" value="true"/>
    <setting name="multipleResultSetEnabled" value="true"/>
    <setting name="useColumnLabel" value="true"/>
    <setting name="useGeneratedKeys" value="false"/>
    <setting name="autoMappingBehavior" value="PARTIAL"/>
    <setting name="autoMappingUnknownColumnBehavior" value="WARNING"/>
    <setting name="defaultExecutorType" value="EXAMPLE"/>
    <setting name="defaultStatementTimeout" value="25"/>
    <setting name="defaultFetchSize" value="100"/>
    <setting name="safeRowBoundsEnabled" value="false"/>
    <setting name="mapUnderscoreToCamelCase" value="false"/>
    <setting name="localCacheScope" value="SESSION"/>
    <setting name="jdbcTypeForNull" value="OTHER"/>
    <setting name="lazyLoadTriggerMethods" value="equals,clone,hashCode,toString"/>
</settings>
```

## 三、类型别名（typeAliases）

类型别名用来减少类完全限定名的冗余，如：

```xml
<typeAliases>
	<typeAlias alias="Author" type="cn.luokaiii.adminservice.model.Author"/>
    <typeAlias alias="Teacher" type="cn.luokaiii.adminservice.model.Teacher"/>
</typeAliases>
```

也可以指定一个报名，MyBatis 会在包名下搜索 Java Bean，在没有注解的情况下，会使用Bean 的首字母小写的非限定类名来作为它的别名，如：

```xml
<typeAliases>
	<package name="domain.blog" />
</typeAliases>
```

或者使用注解的方式，如：

```java
@Alias("author")
public class Author {
    ......
}
```

Java 类型内建的相应类型别名（不区分大小写）：

| 别名       | 映射的类型 |
| ---------- | ---------- |
| _byte      | byte       |
| _long      | long       |
| _short     | short      |
| _int       | int        |
| _integer   | int        |
| _double    | double     |
| _float     | float      |
| _boolean   | boolean    |
| string     | String     |
| byte       | Byte       |
| long       | Long       |
| short      | Short      |
| int        | Integer    |
| integer    | Integer    |
| double     | Double     |
| float      | Float      |
| boolean    | Boolean    |
| date       | Date       |
| decimal    | BigDecimal |
| bigdecimal | BigDecimal |
| object     | Object     |
| map        | Map        |
| hashMap    | HashMap    |
| list       | List       |
| arraylist  | ArrayList  |
| collection | Collection |
| iterator   | Iterator   |

## 四、类型处理器（typeHandlers）

无论是 MyBatis 在预处理语句（PreparedStatement）中设置一个参数时，还是从结果集中取出一个值时，都会用类型处理器将获取的值以合适的方式转换为 Java 类型。

| 类型处理器                 | Java 类型                     | JDBC 类型           |
| -------------------------- | ----------------------------- | ------------------- |
| BooleanTypeHandler         | Java.lang.Boolean,boolean     | BOOLEAN             |
| ByteTypeHandler            | java.lang.Byte,byte           | NUMERIC,BYTE        |
| ShortTypeHandler           | java.lang.Short,short         | NUMERIC,SMALLINT    |
| IntegerTypeHandler         | java.lang.Integer,int         | NUMERIC,INTEGER     |
| FloatTypeHandler           | java.lang.Float,float         | NUMERIC,FLOAT       |
| DoubleTypeHandler          | java.lang.Double,double       | NUMERIC,DOUBLE      |
| BigDecimalTypeHandler      | java.math.BigDecimal          | NUMERIC,DECIMAL     |
| StringTypeHandler          | java.lang.String              | CHAR,VARCHAR        |
| ClobReaderTypeHandler      | java.io.Reader                | -                   |
| ClobTypeHandler            | java.lang.String              | CLOB,LONGVARCHAR    |
| NStringTypeHandler         | java.lang.String              | NVARCHAR,NCHAR      |
| NClobTypeHandler           | java.lang.String              | NCLOB               |
| BlobInputStreamTypeHandler | java.io.InputStream           | -                   |
| ByteArrayTypeHandler       | byte[]                        |                     |
| DateTypeHandler            | byte[]                        | L                   |
| DateOnlyTypeHandler        | java.util.Date                | TIMESTAMP           |
| TimeOnlyTypeHandler        | java.util.Date                | DATE                |
| SqlTimestampTypeHandler    | java.sql.Timestamp            | TIMESTAMP           |
| SqlDateTypeHandler         | java.sql.Date                 | DATE                |
| SqlTimeTypeHandler         | java.sql.Time                 | TIME                |
| ObjectTypeHandler          | Any                           | OTHER               |
| EnumTypeHandler            | Enum                          | VARCHAR             |
| EnumOrdinalTypeHandler     | Enum                          | NUMERIC,DOUBLE      |
| SqlxmlTypeHandler          | java.lang.String              | SQLXML              |
| InstantTypeHandler         | java.time.Instant             | TIMESTAMP           |
| LocalDateTimeTypeHandler   | java.time.LocalDateTime       | TIMESTAMP           |
| LocalDateTypeHandler       | java.time.LocalDate           | DATE                |
| OffsetTimeTypeHandler      | java.time.OffsetDateTime      | TIMESTAMP           |
| ZonedDateTimeTypeHandler   | java.time.ZonedDateTime       | TIMESTAMP           |
| YearTypeHandler            | java.time.Year                | INTEGER             |
| MonthTypeHandler           | java.time.Month               | INTEGER             |
| YearMonthTypeHandler       | java.time.YearMonth           | VARCHAR,LONGVARCHAR |
| JapaneseDateTypeHandler    | java.time.chrono.JapaneseDate | DATE                |

可以通过重写类型处理器或创建自己的类型处理器来处理不支持的或非标准的类型。具体做法为：实现 org.apache.ibatis.type.TypeHandler 或集成 org.apache.ibatis.type.BaseTypeHandler，然后将它映射到一个 JDBC 类型：

```java
@MappedJdbcTypes(JdbcType.VARCHAR)
public class ExampleTypeHandler extends BaseTypeHandler<String> {
  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
    ps.setString(i, parameter);
  }

  @Override
  public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return rs.getString(columnName);
  }

  @Override
  public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return rs.getString(columnIndex);
  }

  @Override
  public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return cs.getString(columnIndex);
  }
}
```

```xml
<typeHandlers>
	<typeHandler handler="org.mybatis.example.ExampleTypeHandler"/>
</typeHandlers>
```

## 五、对象工厂（objectFactory）

MyBatis 每次创建结果对象的新实例时，都会使用一个对象工厂（ObjectFactory）实例来完成。要么通过默认构造方法，要么在参数映射存在的时候通过参数构造方法来实例化。

如何想覆盖对象工厂的默认行为，可以通过创建自己的对象工厂来实现，如：

```java
public class ExampleObjectFactory extends DefaultObjectFactory {
    // 包含两个创建方法
    // 处理默认构造方法
    public Object create(Class type){
        return super.create(type);
    }
    
    // 处理带参数的构造方法
    public Object create(Class type,List<Class> constructorArgTypes,List<Object> constructorArgs){
        return super.create(type,constructorArgTypes,constructorArgs);
    }
    
    // 可以被用来配置 ObjectFactory，在初始化ObjectFactory后，objectFactory元素体中定义的属性会被传递给 setProperties 方法
    public void setProperties(Properties properties){
        super.setProperties(properties);
    }
    
    public <T> boolean isCollection(Class<T> type){
        return Collection.class.isAssignableFrom(type);
    }
}
```

```xml
<objectFactory type="org.mybatis.example.ExampleObjectFactory">
	<property name="someProperty" value="100"/>
</objectFactory>
```

## 六、插件（Plugins）

MyBatis 允许你在已映射语句执行过程中的某一点进行拦截调用。默认情况下，MyBatis 允许使用插件来拦截的方法调用包含：

- Executor（update、query、flushStatements、commit、rollback、getTransaction、close、isClosed）
- ParameterHandler（getParameterObject、setParameters）
- ResultSetHandler（handleResultSets、handleOutputParameters）
- StatementHandler（prepare、parameterize、batch、update、query）

这些都是很底层的类和方法，如果在试图修改或重写已有方法的行为时，很可能会破坏 MyBatis的核心模块，因此在使用插件的时候要当心。

## 七、环境配置（environments）

MyBatis 可以配置成适应多种环境，这种机制有助于将 SQL 映射应用于多种数据库之中。

如开发、测试、生产环境需要不同的配置；或者现在相同 Schema 的多个库中使用相同的 SQL 映射。

> 尽管可以配置多个环境，但是每个 SqlSessionFactory 实例只能选择一种环境

环境元素定义了如何配置环境：

```xml
<enviroments default="development">
	<enviroment id="development">
    	<transactionManager type="JDBC">
        	<property name="..." value="..."/>
        </transactionManager>
        <dataSource type="POOLED">
        	<property name="driver" value="..."/>
            ...
        </dataSource>
    </enviroment>
</enviroments>
```

这里的关键：

- 默认使用的环境ID（default="development"）
- 每个元素都有一个环境ID
- 事务管理器的配置
- 数据源的配置

### 1. 事务管理器（transactionManager）

MyBatis 有两种类型的事务管理器：

1. JDBC - 直接使用 JDBC 的提交和回滚设置，依赖于从数据源得到的连接来管理事务作用域
2. MANAGED - 让容器来管理事务的整个生命周期。默认会关闭连接，然而一些容器不希望这样，因此需要将 closeConnection 设置为 false 来阻止它默认的关闭行为

> 如果使用 Spring+MyBatis，则没必要配置事务管理器，因为 Spring 模块会使用自带的管理器来覆盖前面的配置。

### 2. 数据源（DataSource）

dataSource 元素使用标准的 JDBC 数据源接口来配置 JDBC 连接对象的资源。

MyBatis 提供了三种内建的数据源类型。

#### UNPOOLED

每次被请求时打开和关闭连接

#### POOLED

使用连接池的方式，避免创建新实例时所需的初始化和认证时间

#### JNDI

为了能在如 EJB 或应用服务器这类容器汇总使用，容器可以集成或在外部配置数据源

#### 第三方数据源

可以通过实现接口 `org.apache.ibatis.datasource.DataSourceFactory` 来使用第三方数据源。

## 八、数据库厂商标识（databaseIdProvider）

## 九、映射器（mappers）

mappers 定义 SQL 映射语句，告诉 MyBatis 去哪里找这些语句。如：

```xml
<!-- 使用相对于类路径的资源引用 -->
<mappers>
	<mapper resource="org/mybatis/builder/AuthorMapper.xml"/>
</mappers>
```

```xml
<!-- 使用完全限定资源定位符 URL -->
<mappers>
	<mapper url="file:///var/mappers/AuthorMapper.xml"/>
</mappers>
```

```xml
<!-- 使用映射器接口实现类的完全限定类名 -->
<mappers>
	<mapper class="org.mybatis.builder.AuthorMapper"/>
</mappers>
```

```xml
<!-- 将包内的映射器接口实现全部注册为映射器 -->
<mappers>
	<package name="org.mybatis.builder"/>
</mappers>
```

