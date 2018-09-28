1.@RestController==》将controller方法都以json格式输出
2.@SpringBootApplication   是SpringBoot的核心注解，目的是开启自动配置。
	关闭自动配置：@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
	运行原理：相当于是以下注解的集合：
		@Target(Element.TYPE)
		@Retention(RetentionPolicy.RUNTIME)
		@Documentd
		@Inherited
		@Import({EnableAutoConfigurationImportSelector.class,AutoConfigurationPackages.Register.class})
3.Thymeleaf模板引擎(同Freemarker)
	1.通过xmlns:th=http://www.thymeleaf.org 命名空间，转换位动态视图；
	2.使用“@{}” 方式引用web静态资源
	3.通过“${}”访问model中的属性，如：<span th:text="${singlePerson.name}"></span>
		注：需要处理的动态内容需要加上“th：”前缀
	4.迭代：使用“th：each”，如：th:each="person:${person}"  其中person作为迭代元素使用
	5.数据判断： th:if="${not #lists.isEmpty(people)}"
	6.js中访问model  <script th:inline="javascript"> var singlePerson=[[${singlePerson}]]
		1.使用th:inline="javascript" 使js能够访问model
		2.通过[[${}]]获取实际的值
4.注册Servlet、Filter、Listener
	Servlet:ServletRegistrationBean
		@Bean
		public ServletRegistrationBean serbletRegistrationBean(){
			return new ServletRegistrationBean(new XXServlet(),"/xx/*");//直接注册servlet及其请求路径
		}
	Filter:FilterRegisterationBean
		@Bean
		public FilterRegisterationBean filterRegisterationBean(){
			FilterRegisterationBean filterRegisterationBean = new FilterRegisterationBean();
			filterRegisterationBean.setFilter(new YYFilter());//设置过滤器
			filterRegisterationBean.setOrder(2);//执行顺序
			filterRegisterationBean.setName("MyFilter");//设置名称
			filterRegisterationBean.setUrlPatterns("/*");//设置过滤路径
			return filterRegisterationBean;
		}
	Listener:ServletListenerRegisterationBean
		@Bean
		public ServletListenerRegisterationBean<ZZListener> servletListenerRegisterationBean(){
			return new ServletListenerRegisterationBean<ZZListener>(new ZZListener);
		}
5.修改tomcat、jetty、undertow
	直接在pom文件中，修改依赖
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-web</artifactId>
			<exclusions>
				<exclusion>
					<groupId>org.springframework.boot</groupId>
					<artifactId>spring-boot-starter-tomcat</artifactId> //将web对应的服务器修改为其它
				</exclusion>
			</exclusions>
		<version>1.5.9.RELEASE</version>
	</dependency>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-tomcat</artifactId>
		<version>1.5.9.RELEASE</version>
	</dependency>
6.WebSocket
	1.websocket的配置
		1.使用@Configuration及@EnableWebSocketMessageBroker  来开启WebSocket支持
		2.继承AbstratWebSocketMessageBrokerConfigurer类
			重写registerStomEndpoint(StomEndpointRegister)与configureMessageBroker(essageBrokerRegistry)方法
			Sregistry.addEndpoint("/endpointWisely").withSockJS();  即通过endpointWisely连接SockJs
			Mregistry.enableSimpleBroker("/topic");  配置一个topic消息代理
	2.wscontroller
		@MessageMapping("/welcome") 同RequestMapping,映射地址
		@SendTo("/topic/getResponse") 当服务端有消息时，会对订阅了"/topic/getResponse"的客户端发送消息
		public WiselyResponse say(WiselyMessage message) throws Exception{
			return new WiselyResponse("Welcome " +message.getName()+" ! ");  返回发送的消息
		}
	3.ws.html
		1.连接并订阅
			var socket = new SockJS('/endpointWisely');  连接名称为“。。。”的endpoint
			var stompClient = Stomp.over(socket);     使用stomp子协议
			stompClient.connect({},function(name){
				stompClient.subscribe('topic/getResponse',function(response){//订阅
					show();//自定义的展示方法
				})
			})
			stompClient.send("/welcome",{},JSON.stringify({'name':name}));//发送消息
7.Spring Data JPA
	1.使用@EnableJpaRepositories("com.wisely.repos")来开启Spring Data JPA支持
		其中的value参数用来扫描数据访问层所在包下的数据访问的接口定义
	2.定义查询方法
		常规查询：find、read、readBy、query、queryBy、get、getBy
		查询关键字：And(和)、Or(或)、Is = Equals(等)、Between(位于之间)、LessThan(小于)、LessThanEqual(小于等于)、GreaterThan(大于)
			GreaterThanEqual(大于等于)、After(日期大于)、Before(日期之前)、IsNull、IsNotNull = NotNull
			Like、Not Like、Starting With(前面加%)、EndingWith(后面加%)、Containing(前后都加%)、OrderBy(排序)
			Not、In、NotIn、True、False、IgnoreCase
		限制查询结果：
			Top、First
			eg:		findFirst10ByName();	findTop30();
		@NamedQuery:一个名称映射一个查询语句
			eg:	@NamedQuery(name = "Person.findByName",query = "select p from Person p where p.name=?1")
				写在实体类名上
		@Query查询
			直接在@Query的value中书写sql语句，参数可以使用索引("?1")或者命名(":name")
		更新查询：使用@Modifying 和 @Query 来组合更新查询
	3.排序与分页
		Sort排序对象： 	List<Person> findByName(String name,Sort sort);
		Pageable对象：	List<Person> findByName(String name,Pageable pageable);
		使用：	List<Person> persons = personRepository.findByName("xx",new Sort(Direction.ASC,"age"));
				List<Person> persons = personRepository.findByName("xx",new Pageable(0,10));
	4.repository默认的方法：
		save()	保存；		
		findAll()	查询所有；		
		findAll(new Sort(Sort.Direction.ASC,"age"))		排序查询所有；
		findAll(new PageRequest(0,10))		分页查询；
		findAll(new PageRequest(1,2,new Sort(Sort.Direction.DESC,"age")))	分页排序
8.SpringBoot的注解式事务  @Transactional
	属性：
		1.propagation 定义事务的生命周期
			REQUIRED 			如果没有事务则新建
			REQUIRED_NEW		始终开启新事务
			SUPPORTS			有就用，没有就不用
			NESTED				类似于REQUIRED_NEW，但是不支持jps与hibernate
			NOT_SUPPORTS		不在事务中执行
			NEVER				强制不在事务中执行，有事务则抛出异常
			MANDATORY			强制在事务中执行，没有则抛出异常
		2.isolation  事务的隔离机制，决定了事务的完整性
			READ_UNCOMMITED  	读不提交(脏读、幻读、不可重复读)
			READ_COMMITED		读提交(不可重复读和幻读)，解决脏读
			REPEATABLE_READ		A读取某条数据时，B不许修改(幻读)
			SERIALIZABLE		序列化
		3.timeout	事务过期时间
		4.readOnly	指当前事务是否是只读事务
		5.rollbackFor	指定哪个异常会回滚
		6.noRollBackFor	指定哪个异常不会回滚
		
		
		
		
		
		
		
		
		
		
		
		
1.@GetMapping注解：相当于 @RequestMapping(Method=RequestMethod.GET)
	该注解将HTTP GET映射到特定的方法上
2.@RequestParam注解：将Request参数绑定到处理函数的参数中
	如：public String getUserId(@RequestParam("id") int id); 这样就可以将localhost:8080/?id=123中的id参数赋给方法中的id
3.@JsonIgnoreProperties(ignoreUnknown = true)
	将这个注解卸载类上之后，就会忽略类中不存在的字段
	使用@JsonIgnoreProperties({"internalId","secretKey"})指定的字段不会被序列化和反序列化
4.数据校验
	1.在实体类的属性前添加如：@NotEmpty、@Min(value=18,message="未成年禁止入内")等
	2.在Controller层的方法，要校验的参数上添加@Valid注解
		如果需要返回错误信息，则需要传入BindingResult对象，用于获取校验失败情况下的反馈信息
5.ModelMapper对象
	一个从对象到对象的框架，能将javaBean对象从一种表现形式转化为另一种表现形式，采用“约定”来配置
	eg:		Person person = modelMapper.map(personDTO,Person.class);			//将PersonDTO的对象，转换成Person对象
			Person personInDB = personService.getById(userId,PersonDTO.getId())	//在service层对userid和person.id做了处理，从数据库取出person.id对应的Person，userid则是权限判断
			return modelMapper.map(personService.save(userId,person),PersonDTO.class);	//再将保存返回的person转为personDTO
6.@PathVariable注解
	可以绑定占位符传过来的值到方法的参数上
7.@RequestMapping的变形
	@GetMapping、@PostMapping、@PutMapping、@DeleteMapping
8.@RequestParam和@PathVariable的区别
	1.@RequestParam和@PathVariable都是从request中接收请求的，都可以接收参数
	2.@RequestParam  支持defaultValue（默认值）、name、value、required参数
	3.@PathVariable  能够识别URL里面的一个模板 eg:@RequestMapping("/hello/{id}")
	4.@PathParam	 同@PathVariable，但是属于JBoss的
	5.@QueryParam	 同@RequestParam,属于JAX-RS
	6.@ResponseBody  服务器返回的时候以一种什么样的方式进行返回
	7.@RequestBody	 
9.@Documentd注解
	映射实体类与MongoDB文件
10.http状态码
	1XX 临时响应，并需要请求者继续执行操作的状态代码
	2XX 请求成功
	3XX 重定向代码
	4XX 表示请求出错
	5XX 服务器内部错误
11.自定义注解
	@Retention注解
		定义被它所注解的注解保留多久，一共有三种策略：
			public enum RetentionPolicy{
				SOURCE,CLASS,RUNTIME
			}
		1.SOURCE  被编译器忽略
		2.CLASS  注解会被保留在Class文件中，但在运行时并不会被vm保留(默认)
		3.RUNTIME  保留至运行时，可以通过反射去获取注解信息。
	@Target注解
		说明该注解可以被声明在哪些元素之前
			public enum ElementType{
				TYPE,FIELD,METHOD,PARAMETER,CONSTRUCTOR,LOCAL_VARIABLE,
				ANNOTATION_TYPE,PACKAGE,TYPE_PARAMETER,TYPE_USE
			}
		1.TYPE  类之前
		2.FIELD  类的字段前
		3.METHOD  类的方法前
		4.PARAMETER  方法参数前
		5.CONSTRUCTOR  构造方法前
		6.LOCAL_VARIABLE  局部变量之前
		7.ANNOTATION_TYPE  注解类型之前
		8.PACKAGE  包名前
12.Spring框架下的AOP Annotation
	1.切入点语法：
		execution(public * * (..))
		execution(* set* (..))
		execution(* com.xyz.servie.AccountService.* (..))
		execution(* com.xyz.service.*.* (..))
		within(com.xyz.service.*)	//service包中
		within(com.xyz.service..*)	//service或其子包
	2.声明通知
		1.@Before 前置通知
		2.@AfterReturning  后置通知
		3.AfterThrowing  异常通知，在一个方法抛出异常后执行
		4.@After  最终通知，必会执行的
		5.@Around  环绕通知，在方法执行前及执行后，常用于线程安全的情况下，共享数据
13.Spring Security
	1.安全处理方法：
		1.access(String)				Spring EL表达式结果为true时可访问
		2.anonymouns()					匿名可访问
		3.denyAll()						用户不能访问
		4.fullyAuthenticated()			用户完全认证可访问(非remeber me)
		5.hasAnyAuthority(String..)		如果用户有参数，则其中任一权限可访问
		6.hasAnyRole(String..)					如果用户有参数，则其中任一角色可访问
		7.hasAuthority(String)			如果用户有参数，则其权限可访问
		8.hasRole(String)				若用户由参数中的角色可访问
		9.permitAll()					用户可任意访问
		10.rememberMe()					允许通过remember-me登陆的用户访问
		11.authenticated()				用户登陆后可访问
14.JMS
	1.安装ActiveMQ
	2.在application.properties中配置ActiveMQ的消息代理地址
		spring.activemq.broker-url=tcp://localhost:61616
	3.定义发送端
		需要实现MessageCreator接口，并重写其createMessage方法
		eg: 	class Msg implements MessageCreator{
					@override
					public Message createMessage() throws JMSException{
						return session.createTextMessage("发送了消息");
					}
				}
	4.定义发送及目的地
		1.使用CommandLineRunner接口，用于程序启动后执行的代码，通过重写其run方法执行
		2.注入Springboot提供的JmsTemplate的Bean
		3.通过JmsTemplate的send方法向 “my-destination” 目的地发送Msg 的消息
		eg:		class Ch934Application implements CommandLineRunner{
					@AutoWired
					JmsTemplate jmsTemplate;
					
					@Override
					public void run(String... args) throws Exception{
						jmsTemplate.send("my-destination”,new Msg());
					}
				}
		4.定义消息监听
			使用@JmsListener注解，来简化JMS开发。只需要在这个注解的属性destination指定要监听的目的地，即可接收该目的地发送的消息。
		eg:		class Receiver{
					@JmsListener(destination = "my-destination”)
					public void receiveMessage(String message){
						sout("收到："+message+"，消息")
					}
				}

	
	
	
	
	
	
	
	
	
	
	
	
	
		#SpringBoot的配置文件.properties
# Logging
	logging.config=classpath:logback-spring.xml
# Mysql Configuration
	spring.datasource.url=jdbc:mysql://localhost:3306/pgc  						#数据库地址
	spring.datasource.username=root												#账号
	spring.datasource.password=password											#密码
# Keep the connection alive if idle for a long time (needed in production)
	spring.datasource.testWhileIdle=true										#指定连接是否被空闲来连接回收器检验
	spring.datasource.validationQuery=SELECT 1									#验证从连接池取出的连接
# Show or not log for each sql query
	spring.jpa.show-sql=true													#运行时输出sql语句
# Hibernate ddl auto (save, save-drop, update)
	spring.jpa.hibernate.ddl-auto=update										#hibernate数据定义的规则，更新
# Set to true if we need to populate the database using 'data.sql'.
	spring.datasource.initialize=false											#指定初始化数据源，是否用data.sql来初始化，默认: true
# Naming strategy
	spring.jpa.hibernate.naming-strategy=org.hibernate.cfg.ImprovedNamingStrategy	#指定命名策略
# The SQL dialect makes Hibernate generate better SQL for the chosen database
	spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5Dialect	#hibernate方言
# Mongodb Configuration
	spring.data.mongodb.uri=mongodb://localhost:27000/pgc						#mongodb的uri
# OSS Configuration
	oss.endpoint=oss-cn-shanghai.aliyuncs.com									#oss阿里云的相关配置
	oss.access.key.id=LTAIxYKpaBqbAIK3
	oss.access.key.secret=WuTDXWS8PBqSdqMuMxrPfT5ryYmmd0
	oss.bucket.name=new-pgc
	spring.http.multipart.maxFileSize=100MB
	spring.http.multipart.maxRequestSize=100MB
# SSO Configuration
	security.enabled=true
#默认callback
	security.defaultCallback=/
#sso验证token地址
	security.ssoServerValidate=http://106.15.179.107:7127/api/c/sso/validate-token
#如果本地应用没有登陆就去验证
	security.ssoServerAuth=http://106.15.179.107:7127/api/c/sso/auth
#本地应用密码
	security.ssoKey=574163310
#本地应用地址, should be defined by the same field under each app
# security.appHost=http://localhost:8000
#gizp
	spring.resources.chain.gzipped=true
	
	
注：
1.Java Web获取IP，及ip所在地址
	在请求头getHeader()中，"x-forwarded-for"、"Proxy-Client-IP"、"WL-Proxy-Client-IP"
	请求地址:getRemoteAddr();
2.请求参数与路径变量
	1.请求参数	
		采用key = value形式，并用 "&" 分隔
			eg:localhost:8080/user?name=spring&pwd=123
		在传统的servlet中，可以通过HttpServletRequest的getParameter()方法取值
		在SpringMVC中则提供了一个注解"@RequestParam"来注释方法参数
	2.路径参数
		类似请求参数，但是没有key部分，只是一个值
			eg:localhost:8080/user/spring
		为了使用路径变量，首先需要在@RequestMapping注解的值属性中添加一个变量，该变量必须放在花括号之间
			eg:@RequestMapping(value="/user/{pwd}")
		使用时，在方法签名中加上@PathVariable注解
			eg:public String test(@PathVariable(name="pwd") string password)
3.lambda表达式
	1.用lambda实现Runnable
		new Thread( () -> sout("Lambda expression rocks！！") ).start();
		等价于：
		new Thread(
			new Runnable(){
				@Override
				public void run(){
					sout("Lambda expression rocks！！") ;
				}
			}
		).start();
	2.对列表进行迭代
		list.forEach( n -> sout(n) );
		如果输出和参数相同，则可以省略参数
		list.forEach( System.out::println() );
	3.函数式接口Predicate
		可以向API添加逻辑，用更少的代码支持更多的动态行为。
		eg:	public void filter(String name,Predicate condition){
				if(condition.test(name)){
					sout(name+" ");
				}
			}
		那么在调用时，我们就可以通过predicate来进行判断
		eg:
			List languages = Arrays.asList("Java", "Scala", "C++", "Haskell", "Lisp");
			filter(langugages,str -> str.startWith("J"));
	4.Map和Reduce(是Stream的方法)
		1.map允许你将对象进行转换，将列表中的每个元素转换为修改之后的值。	
		eg:	List costBeforeTax = Arrays.asList(100, 200, 300, 400, 500);
			costBeforeTax.stream().map( (cost)->cost+.12*cost )
										.forEach(System.out::println);
		2.reduce可以对所有值进行合并，比较类似SQL中的sum()、avg()、count()等
		eg:对修改后的数据进行计算，接收多个值，返回一个值
			costBeforeTax.stream().map( (cost)->cost+.12*cost )
										.reduce( (sum,cost)-> sum+cost).get();
	5.filter过滤
		使用lambda表达式和流API过滤大规模数据集合，流提供了一个filter()方法，接受一个Predicate对象，即可以传入一个lambda表达式作为过滤逻辑
		eg:	List<String> filtered = strList.stream().filter( x-> x.length()>10).collect(Collectors.toList);
			得到的结果为：过滤后的strList，长度都是大于10的
	6.distinct()去重
		numbers.stream().distinct().collect(Collectors.toList);
	7.集合的最大值、最小值、总和及平均值
		IntStream、LongStream和DoubleStream等流的类中，有个方法叫做summaryStatistics()，可以返回IntSummaryStatistics、LongSummaryStatistics或者DoubleSummaryStatistic
		eg:	IntSumnumberList stats = numberList.stream().mapToInt((x)->x).summaryStatistics()
			stats存在	getMax、getMin、getSum、getAverage、getCount方法
	8.Lambda表达式与匿名类
		this关键字：
			匿名类的this关键字指向匿名类
			lambda表达式的this关键字指向包围lambda表达式的类
		编译方式：
			java编译器将lambda表达式编译成类的私有方法，使用java7的invokedynamic字节码指令来动态绑定这个方法。
	9.限制：
		1.lambda表达式只能引用final或final局部变量，即在lambda内部不能修改定义在域外的变量。
	10.避免Null
		当出现多层嵌套时，调用其中的方法可能会抛出NullPointException异常,所以我们可以通过Optional类型来预防null检查
		eg:new Outer().getNested().getInner().getFoo()中可能会出现Null异常
			修改过后：
			Optional.of(new Outer())
					.map(Outer::getNested)
					.map(Nested::getInner)
					.map(Inner::getFoo)
					.ifPresent(System.out::println);
	11.Optional类
		是一个可以为null的容器对象，如果值存在则isPresent()方法返回true，调用get()方法会返回该对象。
		Optional是个容器，它可以保存类型T的值，或者仅仅保存null。
		Optional类的引入很好的解决空指针异常。
4.Git使用规范流程
	1.新建分支
		每次开发新功能，都应该新建一个单独的分支
		#获取主分支最新代码
			git checkout master	
			git pull
		#新建一个开发分支
			git checkout -b myfeature
	2.提交分支commit
		分支修改后，就可以提交commit了
			git add -all			#all表示保存所有变化，也是默认的
			git status				#查看发生变动的文件
			git commit --verbose	#verbose参数，会列出diff的结果
	3.撰写提交信息
		提交commit时，必须给出完整的提交信息
		 eg：第一行是不超过50字的提要
			 空一行
			 *罗列出改动原因、主要变动、需要注意的问题（可多行）
			 空一行
			 提供对应的网址
	4.与主干同步
		git fetch master
	5.合并commit
		分支开发完成后，可能有一堆commit，但是合并到主干时，最好只有一个commit，清晰易管理
			git rebase -i origin/master		#i参数表示互动，此时会打开一个互动界面
		可用的命令：
			1.pick		#正常选中
			2.reword	#选中，并且修改提交信息
			3.edit		#钻中，rebase时会暂停，允许你修改这个commit
			4.squash	#选张，会将当前commit与上一个commit合并
			5.fixup		#与squash相同，但不会保存当前commit的提交信息
			6.exec		#执行其他shell命令
		其中，squash和fixup可以用来合并commit
		另外，PonyFoo提出另一种合并commit的简洁方法，就是先撤销过去5个commit，再建一个新的
			git reset HEAD~5
			git add
			git commit -am "Here's the bug fix that closes #28"
			git push --force
	6.推送到远程仓库
		合并commit之后，就可以推送当前分支到远程仓库了
			git push --force origin myfeature
		"--force"表示强行推送，因为rebase以后，分支历史改变了，跟远程分支不一定兼容，可能需要强行推送
	7.发出Pull Request
		提交到远程仓库以后，就可以发出Pull Request到master分支，然后请求别人进行代码review，确认可以合并到master
4.2	Git分支管理策略
	Vincent Driessen提出了一个分支管理的策略，使得版本库的演进保持简洁，主干清晰。
	1.主分支Master
		代码库有且仅有的一个主分支，提供给用户使用的正式版本，都在此分支上发布
	2.开发分支Develop
		这个分支可以用来生成代码的最新隔夜版本(nightly)，如果想正式对外发布，就在Master分支上，对Develop分支进行合并(merge)
		git merge --no-ff develop
	3.临时性分支
		1.feature 	功能分支
		2.release	预发布分支
		3.fixbug	修补bug分支
		这三种分支都属于临时性需要，使用完以后，应该删除，代码库的常设分支只有Master和Develop

5.	组合(Composition)与聚合(Aggregation)的区别
	1.Composition 表示的是'Part-od'的关系
		比如：引擎Engine是汽车Car的一部分，脱离了汽车的引擎毫无作用，没有实在的意义
	2.而Aggregation表示的是'Has-a'的关系
		比如：Person有一个Address，但是Address的存在不依赖于Person，即地址本身就有其独立存在的意义，不受人的约束。
	3.就强弱关系而言：
		Composition应该更强一些
6.交并补
	1.List集合的交并补
		list1.addAll(list2);	//并集
		list1.retainAll(list2);	//交集,返回Boolean
		list1.removeAll(list2);	//差集
		list2.removeAll(list1);
		list1.addAll(list2);	//无重复并集
7.SpringMVC迁移至SpringBoot
	
8.lucene和solr
	1.lucene
		lucene是一个开放源代码的 全文检索 引擎工具包,并不是一个完整的全文检索引擎，而是一个全文检索引擎的架构，提供了完整的查询引擎和索引引擎，部分文本引擎。目的在于为软件开发人员提供一个简单易用的工具包，以方便的在目标系统中实现全文检索的功能，或者以此为基础建立起完整的全文检索引擎。
		
	2.solr
		是一个高性能，给予lucene的全文搜索服务器。同时对其进行了拓展，提供了比Lucene更为丰富的查询语言，同时实现了可配置、可拓展并对查询性能进行了优化，并且提供了一个完整的功能管理界面，是一款非常优秀的全文搜索引擎。它对外提供类似于WebService的API接口。用户可以通过http请求，向搜索引擎服务器提交一定格式的XML文件，生成索引；也可以通过Http Solr Get操作提出查找请求，并得到XML格式的返回结果、

	3.Solr和Lucene的区别
		1.搜索服务器：
			lucene本质上是搜索库，不是独立的应用程序
		2.企业级
			lucene专注于搜索底层的建设，而Solr专注于企业应用。
		3.管理
			lucene不负责支撑搜索服务所必须的管理
		一句话总结：Solr是Lucene面向企业搜索应用的扩展

9.Gradle
	1.简介：
		是一个基于JVM的构建工具


10.Nginx
	1.反向代理-解决前端跨域问题
		跨域-浏览器为了安全问题而限制了跨域访问，如果a，b页面的协议、域名、端口、子域名不同，所进行的访问行动都是跨域的。
		由配置来完成：
			1.让nginx监听localhost的80端口，网站A与网站B的访问都是经过localhost的80端口进行访问。
			2.配置一个特殊的'/api'目录的访问，并且对url执行了重写
				rewrite ^/api/(.*)$/$1 break;
			代表重写拦截进来的请求，并且只能对域名后边以'/api'开头的起作用，break表示匹配一个之后停止匹配
			
11.详解SpringData
	1.概念
		Spring Data的目的是为了简化构建基于Spring框架应用的数据访问技术，包括非关系型数据库、Map-Reduce框架、云数据服务等等；另外也包含对关系数据库的访问支持。
	2.Spring Data的子项目：
		1.Commons	提供共享的基础框架，适合各个子项目使用，支持跨数据库持久化
		2.Hadoop	基于Spring的Hadoop作业配置和一个POJO编程模型的MapReduce作业
			MapReduce是一种编程模型，用于大规模数据集(大于1TB)的并行运算
		3.Key-value 集成了Redis和Riak，提供多个常用场景下的简单封装
		4.Document  集成文档数据库，CouchDB和MongoDB并提供基本的配置映射和资料库的支持
		5.Graph		集成Neo4j提供强大的基于POJO的编程模型
		6.Graph Roo AddOn - Rooney support for Neo4j
		7.JDBC Extensions	支持Oracle RAD、高级队列和高级数据类型
		8.JPA 		简化创建JPA数据访问层和跨存储的持久层功能
		9.Mapping 	基于Grails的提供对象映射框架，支持不同的数据库
		10.Examples	示例程序、文档和图数据库
		11.Guidance 高级文档
	3.Repository
		1.如果dao接口继承了Repository，则该接口会被IOC容器识别为一个Repository Bean注入到IOC容器中，进而可以再该接口中定义满足一定规则的接口
			或者通过一个朱姐@RepositoryDefination注解来替代Repository接口
		2.在Repository接口中声明方法
			1.查询方法以 find|read|get开头
			2.设计条件查询，条件的属性需要定义关键字连接
			3.条件的属性以字母大写
			4.支持属性的级联查询，若当前类由符合条件的属性，则优先使用，则不使用级联属性
		3.通过自定义的JPQL完成update和delete操作
			注意：JPQL不支持Insert操作
			在@Query注解中编写JPQL语句，单必须使用@Modify进行修饰，以通知SpringData，这是一个Update或者delete
			因为update和delete需要使用事务，而默认情况下springdata的每个方法上都有一个只读事务，不能完成修改操作
			因此需要使用@Modify修饰


23种设计模式:
	1.装饰者模式
		允许向一个现有的对象添加新的功能，同时又不改变其结构。装饰者可以在所委托被装饰者的行为之前或者之后加上自己的行为，以达到特定的目的。
		参考：https://blog.csdn.net/wwh578867817/article/details/51480441
		eg： 
			假设我们去咖啡店点了一杯咖啡，可以加奶加糖，咖啡、奶、糖各有不同的价格。
				此时咖啡就是我们的组件，奶和糖就是我们的装饰者
		实现细节：
			Component 抽象构件角色：真实对象和装饰对象有相同的接口。这样，客户端对象就能够以与真实对象相同的方式同装饰对象交互
			ConcreteComponent具体构件角色（真实对象）：io流中的FileInputStream、　　　　FileOutputStream
			Decorator 装饰对象：持有一个抽象构件的引用。装饰对象接受所有客户端的请求，并把这些请求转发给真实的对象。这样，就能在真实对象调用前后增加新的功能。
			ConcreteDecorator具体装饰角色：负责给构件对象增加新的责任。


OAuth 2.0 关于授权的开放网络标准
	1.几个专用名词
		Third-party application:	第三方应用程序，又称“客户端”(client)
		HTTP service:				HTTP服务提供商
		Resource Owner：			资源所有者，即“用户”(user)
		User Agent:					用户代理，即浏览器
		Authorization server：		认证服务器，即服务提供商专门用来处理认证的服务器
		Resource server:			资源服务器，即服务提供商存放用户生成的资源的服务器。与认证服务器可以是同一台，也可以不是
	2.OAuth的思路
		OAuth在客户端与服务提供商之间，设置了一个授权层，客户端不能直接登录“服务提供商”，只能登录授权层，以此将用户与客户端区分开来。“客户端”登录授权层所用的令牌（token），与用户的密码不同。用户可以在登录的时候，指定授权层令牌的权限范围和有效期。
		“客户端”登录授权层以后，“服务提供商”根据令牌的权限范围和有效期，向“客户端”开放用户存储的资料
	
					----	(A)Authorization Request--->   	|Resource|
					<---	(B)Authorization Grant	----    | Owner  |
							
		  +------+	----	(c)Authorization Grant	--->	|Authorization|
		  |Client|  <---	(D)Access Token			----	|	Server    |
		  +------+				
					----	(E)Access Token			--->	|Resource  |
					<---	(F)Protected Resrouce	----	|	Server |
		解析：
			（A）用户打开客户端以后，客户端要求用户给予授权
			（B）用户同意给予客户端授权
			（C）客户端使用上一步获得的授权，向认证服务器申请令牌
			（D）认证服务器对客户端进行认证以后，确认无误，同意发放令牌
			（E）客户端使用令牌，向资源服务器申请获取资源
			（F）资源服务器确认令牌无误，同意向客户端开发资源
	3.其中B步骤的客户端授权模式
		1.授权码模式（authorization code）

		2.简化模式（implicit）
		
		3.密码模式（resource owner password credentials）
		
		4.客户端模式（client credentials）
		
		具体参数查看：http://www.ruanyifeng.com/blog/2014/05/oauth_2_0.html
	4.更新令牌
		如果用户访问的时候，客户端的“访问令牌”已经过期，则需要使用“更新令牌”申请一个新的访问令牌。
		客户端发出更新令牌的HTTP请求，包含以下参数：
			granttype：表示使用的授权模式，此处的值固定为"refreshtoken"，必选项。
			refresh_token：表示早前收到的更新令牌，必选项。
			scope：表示申请的授权范围，不可以超出上一次申请的范围，如果省略该参数，则表示与上一次一致。
		eg： 
			POST /token HTTP/1.1
			 Host: server.example.com
			 Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
			 Content-Type: application/x-www-form-urlencoded
			 grant_type=refresh_token&refresh_token=tGzv3JOkF0XG5Qx2TlKWIA

Swagger
	1.概念：
		Swagger是一个规范和完整的框架，用于生成、描述、调用和可视化RESTful风格的Web服务。总体目标是使客户端和文件系统作为服务器以同样的速度来更新。文件的方法，参数和模型紧密集成到服务器端的代码，允许API来始终保持同步
	2.SpringBoot集成Swagger
		1.添加maven依赖，在pom文件中添加：
			springfox-swagger2
		2.添加swagger配置文件，如下：
			@Configuration
			@EnableSwagger2
			public class SwaggerConfiguration {
				@Bean
				public Docket api() {
					return new Docket(DocumentationType.SWAGGER_2)
							.ignoredParameterTypes(HttpSession.class)
							.apiInfo(apiInfo())
							.select()			//选择那些路径和api会生成document
							.apis(RequestHandlerSelectors.any())	//对所有api进行监控
							.paths(PathSelectors.any())		//对所有路径进行监控
							.build();
				}

				private ApiInfo apiInfo() {
					return new ApiInfoBuilder()
							.title("pgc接口调用说明")
							.description("该文档仅供内部人员使用.")
							.termsOfServiceUrl("http://xxxx")
							.version("1.0")
							.build();
				}
			}
		3.此时访问项目下的api-docs，能返回json形式的界面
	3.添加SwaggerUI
		springfox-swagger-ui
		生成一个可读性良好的API页面。
		此时可以访问项目下的swagger-ui.html
	4.Swagger对RESTful风格的api支持的比较好。
				 
SpringBoot拦截器之WebMvcConfigurerAdapter

	1.几个比较常见的：
		/* 这里配置视图解析器 */
		void configureViewResolvers(ViewResolverRegistry registry);
		
		/* 配置内容裁决的一些选项*/
		void configureContentNegotiation(ContentNegotiationConfigurer configurer);
		
		/* 视图跳转控制器 */
		void addViewControllers(ViewControllerRegistry registry);
		
		/* 静态资源处理 */
		void addResourceHandlers(ResourceHandlerRegistry registry);
		
		/* 默认静态资源处理器 */
		void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer);
	具体方法查看：https://blog.csdn.net/wujiaqi0921/article/details/78324722
	
SpringBoot 序列化器注解使用方法 
	1.@JsonIgnoreProperties
		类注解，作用是json序列化时将java bean中的一些属性忽略掉，序列化和反序列化都受影响
	2.@JsonIgnore 
		属性或方法注解（最好在属性上），作用同JsonIgnoreProperties
	3.@JsonFormat
		属性或方法注解（最好在属性上），可以方便的把Date类型直接转化为我们想要的模式，例如@JsonFormat(pattern="yyyy-MM-dd HH-mm-ss")
	4.@JsonSerialize
		用于属性或getter方法上，用于在序列化时嵌入我们自定义的代码
	5.@JsonDeserialize
		用于属性或getter方法上，用于在反序列化时可以嵌入我们自定义的代码
	
SpringBoot 使用 JavaMailSender发送邮件	
-----.SpringBoot
		添加依赖
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-mail</artifactId>
		</dependency>
	2.配置属性
		spring.mail.host=smtp.qq.com
		spring.mail.username=用户名
		spring.mail.password=密码
		spring.mail.properties.mail.smtp.auth=true
		spring.mail.properties.mail.smtp.starttls.enable=true
		spring.mail.properties.mail.smtp.starttls.required=true
	3.测试
		@Autowired
		private JavaMailSender mailSender;
		
		SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("1153693791@qq.com");
        message.setTo("1153693791@qq.com");
        message.setSubject("主题："+title);
        message.setText(content);
		mailSender.send(message);
	
Spring JPA 开启审计功能：
	JPA Audit:
		在Spring jpa中，支持在字段或者方法上进行注解@CreateDate,@CreatedBy,@LastModifiedDate,@LastModifiedBy
	使用：
		1.申明实体类，需要在类上添加注解@EntityListeners(AuditingEntityListener.class)，
			@Entity
			@Table(name = "store_source_bind")
			@EntityListeners(AuditingEntityListener.class)
			public class StoreSourceBind {
		
		2.在需要的字段上加上@CreateDate,@CreatedBy,@LastModifiedDate,@LastModifiedBy
		@Column(name = "create_time")
		@CreatedDate
		private Date createTime;
		
		3.继承AuditorAware<T>来指定返回的创建信息:
			@Component("auditorAware")
			public class SpringSecurityAuditorAware implements AuditorAware<String> {

				private HttpSession session;

				@Override
				public String getCurrentAuditor() {
					return Utils.getUserIdFromSession(session);
				}
			}
		4.在application启动类中加上注解EnableJpaAuditing或者EnableMongoAuditing（MongoDB的审计），
		
		@EnableJpaAuditing
		@EnableMongoAuditing(auditorAwareRef = "auditorAware")//开启审计功能
		public class WalletApplication {
			public static void main(String[] args) {
				new SpringApplicationBuilder(WalletApplication.class).web(true).run(args);
			}
		}

SpringBoot 定时任务：

	1.开启定时任务的配置
	  在启动文件上，添加注解 @EnableScheduling
	2.创建定时任务
	  在需要定时执行的文件上方添加注解：
	  	@Scheduled(fixedRate = 5000)
	3. @Scheduled 详解：
		1.fixedRate = 5000 ：上次开始执行时间点之后5秒执行
		2.initialDelay = 1000 , fixedRate = 5000 
			第一次延迟 1 秒后执行，之后按照 5 秒的规则执行
		3.cron = "*/5 ***") :
			通过 cron 表达式定义规则
	
	
	
	
	
	
	
	
	
	
	
	