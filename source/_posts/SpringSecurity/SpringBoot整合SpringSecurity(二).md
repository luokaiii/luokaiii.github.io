---
title: SpringBoot整合SpringSecurity(二)-使用数据库进行验证
date: 2018-11-01 10:18:03
categories: 
 - Java成神之路
 - Study社区
---
# SpringBoot整合SpringSecurity(二)-使用数据库进行验证

本文使用的是 SpringBoot + SpringSecurity，做一个 Web端的权限验证框架。大致分为以下几个部分：

1. 初始化Security框架
2. 使用数据库验证
3. 重写`hasAnyAuthority`，改为动态验证
4. 整合 `CAS` 实现多模块权限验证
5. 测试集群状况下的身份验证
6. 解决多重登录问题

下面我们开始第二部分，使用数据库中的用户进行验证。

## 一、创建登录用户

```java
public class CoreUser{

    private Integer id;
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 权限
     */
    private List<String> roles;

    // todo getter and setter
}
```

用户对象我们只用了必不可缺的几个字段。其中的 `roles`，代表着用户具有的那些权限。本章暂未用到，但是后面会用到，没有权限还用什么权限框架呢。

dao层和service层这里就略过了，我是通过 JPA 来使用的。

## 二、重写UserDetailsService

通过重写 `UserDetailsService`的`loadUserByUsername(username)`方法，将数据库中对象查询并生成对应的 `UserDetails`。

如果需要扩展其他权限，可以通过继承 `UserDetails`，来生成自己的`OtherUserDetails`。

```java
@Service
public class LoginUserService implements UserDetailsService {

    private final CoreUserService coreUserService;

    @Autowired
    public LoginUserService(CoreUserService coreUserService) {
        this.coreUserService = coreUserService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final CoreUser user = coreUserService.findByUsername(username);

        if (user == null)
            throw new UsernameNotFoundException("没有找到该用户");

        // 注入用户的权限
        final List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        if (user.getRoles() != null)
            user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

        return new User(username, user.getPassword(), authorities);
    }
}
```

## 三、修改SecurityConfig

通过重写 `configure(AuthenticationManagerBuilder auth)`方法，指定 `userDetailsService`为我们定义的 `loginUserService`。
因为我们没有登录页面，所以还是使用 `SpringSecurity` 的默认登录页。

```java
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final LoginUserService loginUserService;

    @Autowired
    public SecurityConfiguration(LoginUserService loginUserService) {
        this.loginUserService = loginUserService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(loginUserService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public NoOpPasswordEncoder passwordEncoder(){
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }
}
```

## 四、测试

打开浏览器，测试 `localhost:8080/hello`，因未登录跳转至 `localhost:8080/login`，输入我们在数据库中定义的username、password即可。

注册就是添加一条数据，这里就不做演示了。