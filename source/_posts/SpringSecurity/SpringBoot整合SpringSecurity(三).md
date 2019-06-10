---
title: SpringBoot整合SpringSecurity(三)-动态验证权限
date: 2018-11-01 10:18:03
categories: 
 - Java成神之路
 - Study社区
---
# SpringBoot整合SpringSecurity(三)-动态验证权限

## 用户 - 角色 - 权限关系

![用户角色权限关系图](https://koral-home.oss-cn-beijing.aliyuncs.com/blog/%E6%9D%83%E9%99%90%E8%A7%92%E8%89%B2%E5%85%B3%E7%B3%BB.png)

> 其中roles、permission可以理解为1对多的关系，具体实现我就不写在这里了。
>
> ## 注入用户权限
>
> 在登录时，通过实现 UserDetailsService 将 Admin 用户对象转换为 SpringSecurity 可识别的 UserDetails 对象。
>
> 我们需要在加载 Admin 的同时，将 Admin用户 对应的 Role 角色，Role角色对应的 Permission权限加载到 authorizations中。
>
> 修改后的 AdminDetails 如下：
>
> ```java
> public class AdminDetails extends Admin implements UserDetails {
> 
>  private String loginName;
> 
>  private List<Role> authRoles;
> 
>  private List<Permission> authPermissions;
> 
>  public AdminDetails() {
>  }
> 
>  public AdminDetails(String loginName) {
>      this.loginName = loginName;
>  }
> 
>  @Override
>  public Collection<? extends GrantedAuthority> getAuthorities() {
>      List<Permission> permissions = getAuthPermissions();
>      final List<GrantedAuthority> authorities = new ArrayList<>();
> 
>      if (!ObjectUtils.isEmpty(permissions)) {
>          for (Permission permission : permissions) {
>              authorities.add(new SimpleGrantedAuthority(permission.getValue()));
>          }
>      }
> 
>      return authorities;
>  }
> 
>  @Override
>  public String getPassword() {
>      return super.getPassword();
>  }
> 
>  @Override
>  public String getUsername() {
>      return super.getUsername();
>  }
> 
>  @Override
>  public boolean isAccountNonExpired() {
>      return true;
>  }
> 
>  @Override
>  public boolean isAccountNonLocked() {
>      return true;
>  }
> 
>  @Override
>  public boolean isCredentialsNonExpired() {
>      return true;
>  }
> 
>  @Override
>  public boolean isEnabled() {
>      return BooleanUtils.isNotFalse(super.getStatus());
>  }
> 
>  public String getLoginName() {
>      return loginName;
>  }
> 
>  public List<Role> getAuthRoles() {
>      return authRoles;
>  }
> 
>  public void setAuthRoles(List<Role> authRoles) {
>      this.authRoles = authRoles;
>  }
> 
>  public List<Permission> getAuthPermissions() {
>      return authPermissions;
>  }
> 
>  public void setAuthPermissions(List<Permission> authPermissions) {
>      this.authPermissions = authPermissions;
>  }
> }
> ```
>
> ## LoginDetailsService 加载权限信息
>
> LoginDetailsService 实现 UserDetailsService 的 loadUserByUsername 方法，通过用户名从数据库中获取到 Admin 用户，并将其包装为一个 UserDetails 的实现类返回。
>
> 在UserDetails 的实现类中，将用户具有的 Permission权限 加载为 Authorities。
>
> 用于后面将 Request 请求和 Authorities 做对比，达到请求拦截验证的效果。
>
> ```java
> @Service
> public class LoginDetailService implements UserDetailsService {
> 
>     private final AdminRepository adminRepository;
> 
>     private final RoleRepository roleRepository;
> 
>     private final PermissionRepository permissionRepository;
> 
>     @Autowired
>     public LoginDetailService(AdminRepository adminRepository,
>                               RoleRepository roleRepository,
>                               PermissionRepository permissionRepository) {
>         this.adminRepository = adminRepository;
>         this.roleRepository = roleRepository;
>         this.permissionRepository = permissionRepository;
>     }
> 
>     @Override
>     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
>         Admin admin = adminRepository.findByIdentity(username);
> 
>         if (admin == null) {
>             throw new RuntimeException("当前用户不存在");
>         }
> 
>         AdminDetails details = new AdminDetails(username);
>         BeanUtils.copyProperties(admin, details);
> 
>         // set admin roles
>         List<String> roleCodes = details.getRoles();
>         Iterable<Role> roles = roleRepository.findByCodeInAndStatus(roleCodes, true);
>         ArrayList<Role> authRoles = Lists.newArrayList(roles);
>         details.setAuthRoles(authRoles);
> 
>         // set admin permissions
>         final List<String> permissionIds = new ArrayList<>();
>         for (Role authRole : authRoles) {
>             permissionIds.addAll(authRole.getPermissions());
>         }
>         Iterable<Permission> permissions = permissionRepository.findAllById(permissionIds);
>         ArrayList<Permission> authPermissions = Lists.newArrayList(permissions);
>         details.setAuthPermissions(authPermissions);
> 
>         return details;
>     }
> }
> ```
>
> ## 实现 AccessDecisionManager
>
> AccessDecisionManager 的 decide() 方法，决定了客户端发起的HTTP请求(如：GET:/role)是否能够通过，否则返回 AccessDeniedException 无权限异常(即403异常)。
>
> ```java
> @Service
> public class MyAccessDecisionManager implements AccessDecisionManager {
> 
>     /**
>      * 判断是否拥有权限的决策方法
>      *
>      * @param authentication   由 LoginService 循环添加到 GrantedAuthority 对象中的权限信息集合
>      * @param object           包含客户端发起的请求的 request 信息
>      * @param configAttributes 为 InvocationSecurityMetadataSource 的 getAttributes(Object object) 方法返回的结果，此方法是为了判定用户请求的url是否在权限表中，如果存在，则返回decide方法，用来判定用户是否有此权限。如果不在权限表中则放行。
>      */
>     @Override
>     public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
>         if (null == configAttributes || configAttributes.size() <= 0) {
>             return;
>         }
>         ConfigAttribute c;
>         String needRole;
>         for (ConfigAttribute attribute : configAttributes) {
>             c = attribute;
>             needRole = c.getAttribute();
>             for (GrantedAuthority authority : authentication.getAuthorities()) {
>                 if (needRole.trim().equals(authority.getAuthority())) {
>                     return;
>                 }
>             }
>         }
>         throw new AccessDeniedException("no right");
>     }
> 
>     @Override
>     public boolean supports(ConfigAttribute attribute) {
>         return true;
>     }
> 
>     @Override
>     public boolean supports(Class<?> clazz) {
>         return true;
>     }
> }
> 
> ```
>
> ## 实现 FilterInvocationSecurityMetadataSource
>
> ```java
> @Service
> public class MyInvocationSecurityMetadataSourceService implements FilterInvocationSecurityMetadataSource {
> 
>     @Autowired
>     private PermissionRepository permissionRepository;
> 
>     private HashMap<String, Collection<ConfigAttribute>> map = null;
> 
>     /**
>      * 加载权限表中所有权限
>      */
>     public void loadResourceDefine() {
>         map = new HashMap<>();
>         Collection<ConfigAttribute> array;
>         List<Permission> permissions = permissionRepository.findAll();
>         for (Permission permission : permissions) {
>             array = new ArrayList<>();
>             array.add(new SecurityConfig(permission.getValue()));
>             array.add(new SecurityConfig(permission.getMethod().toString()));
>             // 用权限的url作为key，用configAttribute作为value
>             map.put(permission.getUri(), array);
>         }
>     }
> 
>     @Override
>     public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
>         if (map == null) loadResourceDefine();
> 
>         // Object包含用户请求的request信息
>         HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
>         AntPathRequestMatcher matcher;
>         String resUrl;
> 
>         for (String s : map.keySet()) {
>             resUrl = s;
>             matcher = new AntPathRequestMatcher(resUrl);
>             if (matcher.matches(request)) {
>                 return map.get(resUrl);
>             }
>         }
> 
>         return null;
>     }
> 
>     @Override
>     public Collection<ConfigAttribute> getAllConfigAttributes() {
>         return null;
>     }
> 
>     @Override
>     public boolean supports(Class<?> clazz) {
>         return true;
>     }
> }
> 
> ```
>
> ## 实现 AbstractSecurityInterceptor
>
> ```java
> @Service
> public class MyFilterSecurityInterceptor extends AbstractSecurityInterceptor implements Filter {
> 
>     @Autowired
>     private FilterInvocationSecurityMetadataSource securityMetadataSource;
> 
>     @Autowired
>     public void setMyAccessDecisionManager(MyAccessDecisionManager myAccessDecisionManager) {
>         super.setAccessDecisionManager(myAccessDecisionManager);
>     }
> 
>     @Override
>     public void init(FilterConfig filterConfig) throws ServletException {
> 
>     }
> 
>     @Override
>     public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
>         FilterInvocation filterInvocation = new FilterInvocation(request, response, chain);
>         invoke(filterInvocation);
>     }
> 
>     private void invoke(FilterInvocation invocation) throws IOException, ServletException {
>         InterceptorStatusToken token = super.beforeInvocation(invocation);
>         try {
>             invocation.getChain().doFilter(invocation.getRequest(), invocation.getResponse());
>         } finally {
>             super.afterInvocation(token, null);
>         }
>     }
> 
>     @Override
>     public void destroy() {
> 
>     }
> 
>     @Override
>     public Class<?> getSecureObjectClass() {
>         return FilterInvocation.class;
>     }
> 
>     @Override
>     public SecurityMetadataSource obtainSecurityMetadataSource() {
>         return this.securityMetadataSource;
>     }
> }
> 
> ```
>
> ## 最后，注册过滤器
>
> ```java
> @Configuration
> @EnableWebSecurity
> public class SecurityConfig extends WebSecurityConfigurerAdapter {
> 
>     private final MyFilterSecurityInterceptor myFilterSecurityInterceptor;
> 
>     @Autowired
>     public SecurityConfig(MyFilterSecurityInterceptor myFilterSecurityInterceptor) {
>         this.myFilterSecurityInterceptor = myFilterSecurityInterceptor;
>     }
> 
>     @Override
>     protected void configure(HttpSecurity http) throws Exception {
>         http.authorizeRequests()
>                 .anyRequest().authenticated() // 所有请求都需要验证，即验证后可以访问所有请求
>                 .and()
>                 .formLogin().permitAll() // 登录页无需验证
>                 .and()
>                 .logout().permitAll() // 注销请求无需验证
>                 .and()
>                 .csrf().disable(); // 关闭跨站攻击(否则会导致swagger无法进行测试等问题)
>         // 注册过滤器
>         http.addFilterBefore(myFilterSecurityInterceptor, FilterSecurityInterceptor.class);
>     }
>     
>     // ......
> }
> ```
>
> 