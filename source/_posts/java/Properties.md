# Properties的使用
1. 在xml 配置文件中使用（ ${} ）

```java
<bean id="xxx" class="com.javadoop.Xxx">
     <property name="url" value="${javadoop.jdbc.url}" />
</bean>
```

2. 通过 @Value 注解注入

```java
@Value("${javadoop.jdbc.url}")
private String url;
```

3. 通过 Environment 获取
注意：只有使用注解 @PropertySource 的时候才可以使用，否则会 null ; 如果是 SpringBoot 的application.properties 注册 的，也可以

```java
@Autowired
private Environment env;

public String getUrl() {
   return env.getProperty("javadoop.jdbc.url");
}
```

# Properties的使用

1. 通过 XML 配置

```java
<context:property-placeholder location="classpath:sys.properties" />

```

2. 通过 @PropertySource 配置

```java
@PropertySource("classpath:sys.properties")
@Configuration
public class JavaDoopConfig {

}
```

3. PropertyPlaceholderConfigurer ( Spring 3.1 之前)

```java
@Bean
public PropertySourcesPlaceholderConfigurer properties() {
   PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
   Resource[] resources = new ClassPathResource[]{new ClassPathResource("sys.properties")};
   pspc.setLocations(resources);
   pspc.setIgnoreUnresolvablePlaceholders(true);
   return pspc;
}
```

# Spring Boot

快速生成一个 SpringBoot 项目：start.spring.io
此时会默认生成一个 application.properties 的配置文件，只需配置，SpringBoot 会帮我们注册。

如果需要换配置文件，则在启动时指定即可：
```java
java -Dspring.config.location=classpath:sys.properties -jar app.jar
```

## application-{env}.properties

在 application.properties 的基础上，我们还需要新建 application-dev.properties 和application-prd.properties，用于配置环境相关的信息，然后启动的时候指定环境：

```java
java -Dspring.profiles.active=prd -jar app.jar
```

如果 application.properties 和 application-prd.properties 有key冲突，application-prd.properties 的优先级较高。

## @ConfigurationProperties

这个注解是 SpringBoot 才有的

application.properties
```java
javadoop.database.url=jdbc:mysql:
javadoop.database.username=admin
javadoop.database.password=admin123456
```

java文件
```java
@Configuration
@ConfigurationProperties(prefix = "javadoop.database")
public class DataBase {
   String url;
   String username;
   String password;
   // getters and setters
}
```

这样，就在 Spring 的容器中自动注册了一个类型为 DataBase 的 bean 了，而且属性都已经 set 好了

属性配置的覆盖顺序：
启动参数 > application-{env}.properties > application.properties

使用启动参数动态设置属性：
```java
java -Djavadoop.database.password=admin4321 -jar app.jar
```