---
title: 《MockMvc》测试框架
date: 2018-10-11 11:23:10
tags:
  - MockMvc
categories:
  - 后端
  - java
  - 测试
---

基于 RESTful 风格的 SpringMVC 的测试，我们可以测试完整的 Spring MVC 流程，即从 URL 请求到控制器处理，再到视图渲染都可以测试。

<!-- more -->

## 快速入门-这个方法中包含了大多数的常见情况：

```java
    @RunWith(SpringRunner.class)
    @SpringBootTest
    @AutoConfigureMockMvc
    public class CourseControllerTest {

        @Autowired
        private MockMvc mockMvc;

        //ObjectMapper是一个可以重复使用的对象
        @Autowired
        private ObjectMapper mapper;

        @Test
        public void courseListTest() throws Exception{
            mockMvc.perform(MockMvcRequestBuilders.get("/course"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        }

        @Test
        public void createTest() throws Exception{
            String json = "{\"name\":\"Mock测试\",\"classHour\":\"4课时\",\"id\":\"rn:practice:Course:5af27fa5d34f435e581e5bbf\"}";
            //将json格式字符串转换成Course对象里的属性值
            Course course = mapper.readValue(json,Course.class);
            //perform,执行一个RequestBuilders请求，会自动执行SpringMVC的流程并映射到相应的控制器执行处理
            mockMvc.perform(
                    //构造一个post请求
                    MockMvcRequestBuilders.post("/course")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    //使用writeValueAsString()方法来获取对象的JSON字符串表示
                    .content(mapper.writeValueAsString(course)))
            //andExpect，添加ResultMathcers验证规则，验证控制器执行完成后结果是否正确，【这是一个断言】
            .andExpect(MockMvcResultMatchers.status().is(200))

            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))

            //假定返回的结果中，"name" 值为 "Mock测试2",如果不是的话，会抛出异常java.lang.AssertionError，并给出期望值和实际值
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Mock测试2"))

            //添加ResultHandler结果处理器，比如调试时 打印结果(print方法)到控制台
            .andDo(print())

            //返回相应的MvcResult
            .andReturn();
    }
```

## 原理：

## 一 MockMvcBuilder

MockMvcBuilder 是用来构造 MockMvc 的构造器，其主要有两个实现：StandaloneMockMvcBuilder 和 DefaultMockMvcBuilder，分别对应两种测试方式，即独立安装和集成 Web 环境测试（此种方式并不会集成真正的 web 环境，而是通过相应的 Mock API 进行模拟测试，无须启动服务器）。对于我们来说直接使用静态工厂 MockMvcBuilders 创建即可。

### 1.集成 Web 环境方式

    MockMvcBuilders.webAppContextSetup(WebApplicationContext context)：指定WebApplicationContext，将会从该上下文获取相应的控制器并得到相应的MockMvc；

    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration("classpath:config/IncotermsRestServiceTest-context.xml")
    @WebAppConfiguration
    public class IncotermsRestServiceTest {
        @Autowired
        private WebApplicationContext wac;
        private MockMvc mockMvc;
        @Before
        public void setup() {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();   //构造MockMvc
        }
        ...
    }

    注意：
        (1)@WebAppConfiguration：测试环境使用，用来表示测试环境使用的ApplicationContext将是WebApplicationContext类型的；value指定web应用的根；
        (2)通过@Autowired WebApplicationContext wac：注入web环境的ApplicationContext容器；
        (3)然后通过MockMvcBuilders.webAppContextSetup(wac).build()创建一个MockMvc进行测试；

### 2.独立测试方式

    MockMvcBuilders.standaloneSetup(Object... controllers)：通过参数指定一组控制器，这样就不需要从上下文获取了；
    public class PricingExportResultsRestServiceTest {
        @InjectMocks
        private PricingExportResultsRestService pricingExportResultsRestService;
        @Mock
        private ExportRateScheduleService exportRateScheduleService;
        @Mock
        private PricingUrlProvider pricingUrlProvider;
        private MockMvc mockMvc;
        @Before
        public void setup() {
            MockitoAnnotations.initMocks(this);
            mockMvc = MockMvcBuilders.standaloneSetup(pricingExportResultsRestService).build();  //构造MockMvc
        }
        ...
    }

    主要是两个步骤：
    (1)首先自己创建相应的控制器，注入相应的依赖
    (2)通过MockMvcBuilders.standaloneSetup模拟一个Mvc测试环境，通过build得到一个MockMvc

## 二 MockMvc

#### 先看一个测试例子 1：

    　　@Test
        public void createIncotermSuccess() throws Exception {
            IncotermTo createdIncoterm = new IncotermTo();
            createdIncoterm.setId(new IncotermId(UUID.fromString("6305ff33-295e-11e5-ae37-54ee7534021a")));
            createdIncoterm.setCode("EXW");
            createdIncoterm.setDescription("code exw");
            createdIncoterm.setLocationQualifier(LocationQualifier.DEPARTURE);
    when(inventoryService.create(any(IncotermTo.class))).thenReturn(createdIncoterm);   mockMvc.perform(post("/secured/resources/incoterms/create").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .content("{\"code\" : \"EXW\", \"description\" : \"code exw\", \"locationQualifier\" : \"DEPARTURE\"}".getBytes()))
                    //.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("id.value").exists())
                    .andExpect(jsonPath("id.value").value("6305ff33-295e-11e5-ae37-54ee7534021a"))
                    .andExpect(jsonPath("code").value("EXW"));
        }

    perform：执行一个RequestBuilder请求，会自动执行SpringMVC的流程并映射到相应的控制器执行处理；
    andExpect：添加ResultMatcher验证规则，验证控制器执行完成后结果是否正确；
    andDo：添加ResultHandler结果处理器，比如调试时打印结果到控制台；
    andReturn：最后返回相应的MvcResult；然后进行自定义验证/进行下一步的异步处理；

#### 看一个具体的例子 2：

    @Test
    public void testView() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/1"))
                .andExpect(MockMvcResultMatchers.view().name("user/view"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        Assert.assertNotNull(result.getModelAndView().getModel().get("user"));
    }

> 整个过程：
> 1、mockMvc.perform 执行一个请求；  
>  2、MockMvcRequestBuilders.get("/user/1")构造一个请求  
>  3、ResultActions.andExpect 添加执行完成后的断言  
>  4、ResultActions.andDo 添加一个结果处理器，表示要对结果做点什么事情，比如此处使用 MockMvcResultHandlers.print()输出整个响应结果信息。  
>  5、ResultActions.andReturn 表示执行完成后返回相应的结果。

    整个测试过程非常有规律：
    1、准备测试环境
    2、通过MockMvc执行请求
    3.1、添加验证断言
    3.2、添加结果处理器
    3.3、得到MvcResult进行自定义断言/进行下一步的异步请求
    4、卸载测试环境

## 三 RequestBuilder/MockMvcRequestBuilders

从名字可以看出，RequestBuilder 用来构建请求的，其提供了一个方法 buildRequest(ServletContext servletContext)用于构建 MockHttpServletRequest；其主要有两个子类 MockHttpServletRequestBuilder 和 MockMultipartHttpServletRequestBuilder（如文件上传使用），即用来 Mock 客户端请求需要的所有数据。

### 1.MockMvcRequestBuilders 主要 API

    MockHttpServletRequestBuilder get(String urlTemplate, Object... urlVariables)：根据uri模板和uri变量值得到一个GET请求方式的MockHttpServletRequestBuilder；如get(/user/{id}, 1L)；

    MockHttpServletRequestBuilder post(String urlTemplate, Object... urlVariables)：同get类似，但是是POST方法；

    MockHttpServletRequestBuilder put(String urlTemplate, Object... urlVariables)：同get类似，但是是PUT方法；

    MockHttpServletRequestBuilder delete(String urlTemplate, Object... urlVariables) ：同get类似，但是是DELETE方法；

    MockHttpServletRequestBuilder options(String urlTemplate, Object... urlVariables)：同get类似，但是是OPTIONS方法；

    MockHttpServletRequestBuilder request(HttpMethod httpMethod, String urlTemplate, Object... urlVariables)： 提供自己的Http请求方法及uri模板和uri变量，如上API都是委托给这个API；

    MockMultipartHttpServletRequestBuilder fileUpload(String urlTemplate, Object... urlVariables)：提供文件上传方式的请求，得到MockMultipartHttpServletRequestBuilder；

    RequestBuilder asyncDispatch(final MvcResult mvcResult)：创建一个从启动异步处理的请求的MvcResult进行异步分派的RequestBuilder；

### 2.MockHttpServletRequestBuilder 和 MockMultipartHttpServletRequestBuilder API

## (1)MockHttpServletRequestBuilder API

    MockHttpServletRequestBuilder header(String name, Object... values)/MockHttpServletRequestBuilder headers(HttpHeaders httpHeaders)：添加头信息；

    MockHttpServletRequestBuilder contentType(MediaType mediaType)：指定请求的contentType头信息；

    MockHttpServletRequestBuilder accept(MediaType... mediaTypes)/MockHttpServletRequestBuilder accept(String... mediaTypes)：指定请求的Accept头信息；

    MockHttpServletRequestBuilder content(byte[] content)/MockHttpServletRequestBuilder content(String content)：指定请求Body体内容；

    MockHttpServletRequestBuilder cookie(Cookie... cookies)：指定请求的Cookie；

    MockHttpServletRequestBuilder locale(Locale locale)：指定请求的Locale；

    MockHttpServletRequestBuilder characterEncoding(String encoding)：指定请求字符编码；

    MockHttpServletRequestBuilder requestAttr(String name, Object value) ：设置请求属性数据；

    MockHttpServletRequestBuilder sessionAttr(String name, Object value)/MockHttpServletRequestBuilder sessionAttrs(Map<string, object=""> sessionAttributes)：设置请求session属性数据；

    MockHttpServletRequestBuilder flashAttr(String name, Object value)/MockHttpServletRequestBuilder flashAttrs(Map<string, object=""> flashAttributes)：指定请求的flash信息，比如重定向后的属性信息；

    MockHttpServletRequestBuilder session(MockHttpSession session) ：指定请求的Session；

    MockHttpServletRequestBuilder principal(Principal principal) ：指定请求的Principal；

    MockHttpServletRequestBuilder contextPath(String contextPath) ：指定请求的上下文路径，必须以“/”开头，且不能以“/”结尾；

    MockHttpServletRequestBuilder pathInfo(String pathInfo) ：请求的路径信息，必须以“/”开头；

    MockHttpServletRequestBuilder secure(boolean secure)：请求是否使用安全通道；

    MockHttpServletRequestBuilder with(RequestPostProcessor postProcessor)：请求的后处理器，用于自定义一些请求处理的扩展点；

## (2)MockMultipartHttpServletRequestBuilder

    继承自MockHttpServletRequestBuilder，又提供了如下API
    MockMultipartHttpServletRequestBuilder file(String name, byte[] content)/MockMultipartHttpServletRequestBuilder file(MockMultipartFile file)：指定要上传的文件；

## 四 ResultActions

    调用MockMvc.perform(RequestBuilder requestBuilder)后将得到ResultActions，通过ResultActions完成如下三件事：
    ResultActions andExpect(ResultMatcher matcher) ：添加验证断言来判断执行请求后的结果是否是预期的；
    ResultActions andDo(ResultHandler handler) ：添加结果处理器，用于对验证成功后执行的动作，如输出下请求/结果信息用于调试；
    MvcResult andReturn() ：返回验证成功后的MvcResult；用于自定义验证/下一步的异步处理；

## 五 ResultMatcher/MockMvcResultMatchers

    1.ResultMatcher用来匹配执行完请求后的结果验证，其就一个match(MvcResult result)断言方法，如果匹配失败将抛出相应的异常；spring mvc测试框架提供了很多***ResultMatchers来满足测试需求。注意这些***ResultMatchers并不是ResultMatcher的子类，而是返回ResultMatcher实例的。Spring mvc测试框架为了测试方便提供了MockMvcResultMatchers静态工厂方法方便操作；

2.具体的 API 如下：

    HandlerResultMatchers handler()：请求的Handler验证器，比如验证处理器类型/方法名；此处的Handler其实就是处理请求的控制器；

    RequestResultMatchers request()：得到RequestResultMatchers验证器；

    ModelResultMatchers model()：得到模型验证器；

    ViewResultMatchers view()：得到视图验证器；

    FlashAttributeResultMatchers flash()：得到Flash属性验证；

    StatusResultMatchers status()：得到响应状态验证器；

    HeaderResultMatchers header()：得到响应Header验证器；

    CookieResultMatchers cookie()：得到响应Cookie验证器；

    ContentResultMatchers content()：得到响应内容验证器；

    JsonPathResultMatchers jsonPath(String expression, Object ... args)/ResultMatcher jsonPath  (String expression, Matcher matcher)：得到Json表达式验证器；

    XpathResultMatchers xpath(String expression, Object... args)/XpathResultMatchers xpath(String expression, Map<string, string=""> namespaces, Object... args)：得到Xpath表达式验证器；

    ResultMatcher forwardedUrl(final String expectedUrl)：验证处理完请求后转发的url（绝对匹配）；

    ResultMatcher forwardedUrlPattern(final String urlPattern)：验证处理完请求后转发的url（Ant风格模式匹配，@since spring4）；

    ResultMatcher redirectedUrl(final String expectedUrl)：验证处理完请求后重定向的url（绝对匹配）；

    ResultMatcher redirectedUrlPattern(final String expectedUrl)：验证处理完请求后重定向的url（Ant风格模式匹配，@since spring4）

## 六 一些常用的测试

### 1.测试普通控制器

    mockMvc.perform(get("/user/{id}", 1)) //执行请求
            .andExpect(model().attributeExists("user")) //验证存储模型数据
            .andExpect(view().name("user/view")) //验证viewName
            .andExpect(forwardedUrl("/WEB-INF/jsp/user/view.jsp"))//验证视图渲染时forward到的jsp
            .andExpect(status().isOk())//验证状态码
            .andDo(print()); //输出MvcResult到控制台

### 2.得到 MvcResult 自定义验证

    MvcResult result = mockMvc.perform(get("/user/{id}", 1))//执行请求
            .andReturn(); //返回MvcResult
    Assert.assertNotNull(result.getModelAndView().getModel().get("user")); //自定义断言

### 3.验证请求参数绑定到模型数据及 Flash 属性

    mockMvc.perform(post("/user").param("name", "zhang")) //执行传递参数的POST请求(也可以post("/user?name=zhang"))
                .andExpect(handler().handlerType(UserController.class)) //验证执行的控制器类型
                .andExpect(handler().methodName("create")) //验证执行的控制器方法名
                .andExpect(model().hasNoErrors()) //验证页面没有错误
                .andExpect(flash().attributeExists("success")) //验证存在flash属性
                .andExpect(view().name("redirect:/user")); //验证视图

### 4.文件上传

    byte[] bytes = new byte[] {1, 2};
    mockMvc.perform(fileUpload("/user/{id}/icon", 1L).file("icon", bytes)) //执行文件上传
            .andExpect(model().attribute("icon", bytes)) //验证属性相等性
            .andExpect(view().name("success")); //验证视图

### 5.JSON 请求/响应验证

    String requestBody = "{\"id\":1, \"name\":\"zhang\"}";
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON).content(requestBody)
                .accept(MediaType.APPLICATION_JSON)) //执行请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) //验证响应contentType
                .andExpect(jsonPath("$.id").value(1)); //使用Json path验证JSON 请参考http://goessner.net/articles/JsonPath/
        String errorBody = "{id:1, name:zhang}";
        MvcResult result = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON).content(errorBody)
                .accept(MediaType.APPLICATION_JSON)) //执行请求
                .andExpect(status().isBadRequest()) //400错误请求
                .andReturn();
        Assert.assertTrue(HttpMessageNotReadableException.class.isAssignableFrom(result.getResolvedException().getClass()));//错误的请求内容体

### 6.异步测试

    //Callable
    MvcResult result = mockMvc.perform(get("/user/async1?id=1&name=zhang")) //执行请求
            .andExpect(request().asyncStarted())
            .andExpect(request().asyncResult(CoreMatchers.instanceOf(User.class))) //默认会等10秒超时
            .andReturn();

    mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1));

### 7.全局配置

    mockMvc = webAppContextSetup(wac)
            .defaultRequest(get("/user/1").requestAttr("default", true)) //默认请求 如果其是Mergeable类型的，会自动合并的哦mockMvc.perform中的RequestBuilder
            .alwaysDo(print())  //默认每次执行请求后都做的动作
            .alwaysExpect(request().attribute("default", true)) //默认每次执行后进行验证的断言
            .build();

    mockMvc.perform(get("/user/1"))
            .andExpect(model().attributeExists("user"));
