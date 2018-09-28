# 根据踏瑞云的权限架构，所认知的SpringSecurity

## 一、 实体类 

## 1.1 CoreUser
    用于存储用户的基本信息，如用户名、密码、真实姓名、手机号、邮箱、是否是管理员、是否禁用、学号、备注、学校备注、组织、权限集合

## 1.2 Organization
    组织，包含编号、管理员备注、产品、是否禁用

## 1.3 OrganizationMemeber
    组织成员，包含 用户Id、组织Id、组织权限role

## 1.4 OrganizationMemeberRole
    组织权限：Administrator 管理员、Teacher 教师、Student 学生

## 1.5 OrganizationAuthorization
    组织授权： 授权Id、过期时间、授权的课程集合

## 二、权限类

## 2.1 CoreUserDetails

    该类包含了 CoreUser 的信息，和权限的方法，因此需要继承 CoreUser 类和实现 CoreUserDetails 类：UserDetails extends CoreUser implements UserDetails
    即：CoreUserDetails = CoreUser用户信息 + UserDetails的方法 + Organization组织 + OrganizationMember组织内的权限 + OrganizationAuthorization组织授权课程
    新增参数：
        organizations：List<Organization>  组织集合
        loginName : String  用户名
        organizationMemebers：List<OrganizationMember>    成员与组织对应的权限
        organizationAuthorizations： List<OrganizationAuthorization>    授权内容
    三个构造方法：
        1. 无参构造
        2. 含有 登录名 loginName 的构造函数
        3. 含有 组织集合、组织授权集合、登录名 的构造函数
    实现了 UserDetails 的 7 个方法
        getAuthorities()        得到权限集合
        getPassword()           密码
        getUsername()           用户名
        isAccountNonExpired()   账户是否未过期
        isAccountNonLocked()    账户是否未被锁
        isCredentialsNonExpired()   证书是否未过期
        isEnabled()             是否可用

## 2.2 LoginUserService

    用户登录的 Service，实现 UserDetailsService 类的 UserDetails loadUserByUsername(String loginName) 方法，该方法返回 UserDetails，因此需要 CoreUserDetails 实现 CoreUserDetails 类，来将 CoreUserDetails 返回。
    在实现的方法中：
        1. 通过 loginName (手机号、邮箱、用户名) 查询数据库中是否存在该用户 --> 得到 CoreUser
        2. 构造一个 带 loginName 的 CoreUserDetails，将 CoreUser 注入 CoreUserDetails <<**1**>>
        3. 查询用户对应的 组织权限(memberList)，并添加到 CoreUserDetails，即 coreUserDetails.setOrganizationMembers(memberList) <<**2**>>
        4. 提取 用户组织权限 中的所有组织Id，得到所有的组织信息(organizations)，添加到 CoreUserDetails，即coreUserDetails.setOrganizations(organizations) <<**3**>>
        5. 通过 组织Id ，得到组织下所有的授权信息(authorizations)，添加到 CoreUserDetails，即 coreUserDetails.setOrganizationAuthorizations(authorizations) <<**4**>>
        6. 最后，将注入之后的 coreUserDetails 返回

## 2.3 MethodSecurityConfig

> 开启方法级别的权限验证。
    // @EnableGlocalMethodSecurity()在任何 @Configuration 实例上使用，用来开启基于注解的安全验证
    //      prePostEnabled = true ； 表示在方法执行之前进行验证
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    @Configuration
    // GlobalMethodSecurityConfiguration 表示基于方法的安全验证
    public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

        @Override
        protected MethodSecurityExpressionHandler createExpressionHandler() {
            // 返回一个 表达式验证的方法级控制器
            return new ResourceMethodSecurityExpressionHandler();
        }
    }

## 2.4 ResourceMethodSecurityExpressionHandler

    实现 DefaultMethodSecurityExpressionHandler 控制器的 createSecurityExpressionRoot 方法，重写ResourceMethodSecurityExpressionRoot

    public class ResourceMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

        private AuthenticationTrustResolver trustResolver =
                new AuthenticationTrustResolverImpl();

        @Override
        protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
            ResourceMethodSecurityExpressionRoot root =
                    new ResourceMethodSecurityExpressionRoot(authentication);
            root.setPermissionEvaluator(getPermissionEvaluator());
            root.setTrustResolver(this.trustResolver);
            root.setRoleHierarchy(getRoleHierarchy());
            return root;
        }
    }

## 2.5 ResourceMethodSecurityExpressionRoot

    自定义 MethodSecurity 的表达式，eg：

    public class ResourceMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {
        private static final Logger logger = LoggerFactory.getLogger(ResourceMethodSecurityExpressionRoot.class);
        private Object filterObject;
        private Object returnObject;
        private Object target;
        public ResourceMethodSecurityExpressionRoot(Authentication authentication) {
            super(authentication);
        }
        // 用来在 @PreAuthorize 注解中使用的方法
        public boolean canReadCourse(String courseId) {
            logger.debug("method params courseId : {}", courseId);
            logger.debug("current principal {}", getPrincipal());
            return true;
        }
        /** setter and getter */
    }

# 三、大致的流程：

    1. 用户登录，判定用户名密码是否正确，登录成功此时会将登录名交给 UserSetailsService
    2. 在 loadUserByUsername(String loginName) 方法中，填充用户的组织等信息，并返回
        此时的用户已经通过 验证了
    2. 在请求资源时，在需要判定的方法上加上注解：@PreAuthorize("hasRole('Administrator')") 

# 四、常用的注解

    access()                    SpringEL表达式结果为true时可访问
    anonymous()                 匿名可访问
    denyAll()                   用户不可访问
    fullyAuthenticated()        用户完全认证可访问（非Remeber me下自动登录）
    hasAnyAuthority(String...)  参数中任意 权限 的用户可访问
    hasAnyRole(String...)       参数中任意 角色 的用户可访问
    hasAuthority(String)        某一 权限 的用户可访问
    hasRole(String)             某一 角色 的用户可访问
    permitAll()                 所有用户可访问
    rememberMe()                允许通过 remeber me 登录的用户访问
    hasIpAddress(String)        用户来自参数中的ip时可访问