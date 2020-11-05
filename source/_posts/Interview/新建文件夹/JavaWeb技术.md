### 一、JDBC

1. JDBC操作数据库的流程：
   1. 反射 Class.forName() 加载数据库连接驱动
   2. DriverManager.getConnection() 获取数据连接对象
   3. 根据 SQL 获取 sql 会话对象，即 Statement
   4. 执行 SQL 处理结果集
   5. 关闭结果集、关闭会话、关闭连接
2. **数据库连接池的机制**是什么：
   1. 从连接池中获取或创建连接
   2. 使用后归还连接
   3. 系统关闭前，断开所有连接并释放系统资源
   4. 能处理无效连接，限制连接池中的连接数

### 二、Http协议

1. Http 长连接与短连接
   1. Http1.1版本，默认保持长连接，即数据传输完成后保持TCP不断开
   2. Http1.0版本，默认使用短连接，即每次HTTP操作，都建立一次连接，任务完成就中断连接
2. Http常见状态码
   1. 200，成功
   2. 301，永久移除
   3. 302，重定向
   4. 400，请求错误
   5. 401，未授权
   6. 403，拒绝服务，未授权
   7. 404，不存在
   8. 500，服务器错误
   9. 503，服务器当前无法处理，稍后可处理

### 三、Cookie 与 Session

1. **分布式Session**如何实现
   1. 实现 HttpSession 接口，重写 session 的所有方法，将 session 以 hash 值的方式存在 redis 中，一个 session 的 key 就是 sessionID，setAttribute 重写之后就是更新 redis 中的数据，getAttribute 重写之后就是获取 redis 中的数据，等等。
   2. 实现 HttpServletRequest 接口，在 Filter 将 原生Request与自定义的Request结合，重写 getSession 方法，使其返回自定义的 HttpSession，再将替换过的Request，放入 chain.doFilter() 中。
   3. 这样通过 request.getSession() 获取到的就是我们自定义的 HttpSession 了。这时对 session 的所有操作都变成了对 redis 的操作，且 session共享对用户是透明的，filter 也是可配置的、可插拔的、无侵入性的。
   4. 如果redis 服务器出现了问题，此时可以使用 cookie 认证，继续新增 HttpSession 的 Cookie 版本实现，同 redis 的一样，从写 setAttribute、getAttribute 等方法
   5. 在之前修改的 filter 中，通过自定义属性 sessionType，来获取 Redis 版 Session 或者 Cookie 版 Session。
   6. sessionType 通过环境变量来维护，通过修改 sessionType 后重启服务器完成 session 的转换。或者通过 zookeeper 向订阅的所有服务器发送通知，当切换 sessionType 后，所有服务器订阅到修改后的配置
2. 单点登录，如果禁用了 cookie 怎么办
   1. 单点登录原理：后端生成一个 sessionID，设置到 cookie 中，浏览器随后的所有请求都带上 cookie，然后服务器从 cookie 中获取 sessionID，再查询用户信息
   2. 因此，保持登录的关键不是 cookie，而是通过cookie保存和传输 sessionID
   3. 还可以使用 HTTP 请求头来传输，只是没有 cookie 一样自动携带，而需要手动处理

### 四、JSP

1. JSP 的域对象：
   1. pageContext
   2. requestContext
   3. sessionContext
   4. applicationContext

### 五、JavaWeb 高级

1. Filter 和 Listener
2. ajax：异步
3. Linux的一些常用命令
   1. ls、mkdir、rmdir、tar、gzip、grep、pwd、touch、vim、tail
   2. ps、kill