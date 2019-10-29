## 第二章 Spring Framework 的核心：IoC容器的实现

通过 Spring 的核心 IoC 容器和 AOP 的设计和实现可以了解 Spring 所倡导的开发思路，如 **使用 POJO 开发企业应用**、提供**一直的编程模型**、**强调对接口编程**等。

Spring 核心的模式实现，是为应用提供 IoC 容器和 AOP 框架：

IoC 容器很好地降低了框架的侵入性，在简化用户开发的同时，依然能够使用强大的服务；

AOP 技术决定了 Spring 作为一个平台的地位，使 Spring 成为一个兼容并包的开放体系，通过 AOP 技术，使第三方解决方案能够尽可能地结合到 Spring 平台上。

## 一、Spring IoC容器概述 

### 1.1 什么是依赖反转？

如果一个对象 与其合作对象的应用 或 依赖关系的管理 由具体对象来完成，那么会导致代码的高度耦合和可测试性的降低。

在面向对象系统中，对象封装了数据和对数据的处理，对象的依赖关系常常体现了对数据和方法的依赖上。

这些依赖关系通过把对象的依赖注入交给IoC容器来完成，则可以很大程度上简化该复杂性。

> 控制反转是关于一个对象如何获取它所依赖的对象的应用。在这里，反转指的是责任的反转（将对象的依赖转到 IoC 容器中了）。

### 1.2 Ioc 的应用场景

上面介绍的 IoC 设计模式，是解耦组件之间复杂关系的利器，Spring IoC 模块就是这一模式的实现。

在原有的 EJB 模式中，开发者需要编写满足 EJB 规范的组件，才能运行在 EJB 容器中，从而获取事务管理、生命周期管理等组件开发的基本服务。

Spring 提供的服务与 EJB 并无太大区别，但两者在设计模式上有很大不同。

Spring 通过 IoC 模式管理依赖关系，并通过依赖注入和 AOP 切面增强了为 JavaBean 这样的  POJO 对象赋予事务管理、生命周期管理等基本功能。

也就是说 Spring 把 EJB 组件还原成了 POJO 对象或 JavaBean 对象，降低了应用开发对传统 J2EE 技术规范的依赖。

一方面，通过 IoC 容器的资源控制反转，使依赖关系的适配和管理更加灵活；另一方面，如果在反转的实现中，通过可读文本来完成配置，则在变动时无需修改和重新编译源代码，符合设计模式中的开闭原则，能够提高组件系统设计的灵活性。

## 二、IoC 容器系列的设计与实现：BeanFactory 和 ApplicationContext

在 Spring IoC 容器的设计中，包含两个主要的容器：BeanFactory 接口的简单容器系列，只实现了容器的最基本功能；ApplicationContext 应用上下文，作为容器的高级形态而存在，在简单容器的基础上增加了许多面向框架的特性。

## 2.1 Spring 的 IoC 容器

BeanFactory 定义了作为 IoC 容器所需要的最基本的功能规范。且 Spring 通过定义 BeanDefinition 来管理基于 Spring 应用中的各种对象以及它们之间的相互依赖关系。

BeanDefinition 抽象了对 Bean 的定义，即依赖反转模式中管理的对象依赖关系的数据抽象，也是容器实现依赖反转功能的核心数据结构。

![IoC容器的接口设计图](https://i.loli.net/2019/10/28/GBZJu6QvjUnb1x5.png)

#### 1. BeanFactory 的应用场景

BeanFactory 接口定义了 IoC 容器最基本的形式，也是最基本的规范，勾画了 IoC 的基本轮廓。

> FactoryBean 和 BeanFactory 的区别：
>
> 虽然看起来挺像，但是本质是不同的。BeanFactory 是 管理所有 Bean 的工厂，而 FactoryBean 是一个工厂类型的 Bean。

BeanFactory 设计了 getBean 方法，用来通过名称获取 IoC 容器中管理的 Bean。

有了BeanFactory 的定义，用户可以执行以下操作：

- containsBean，判断容器是否含有指定名称的Bean
- isSingleton，查询指定名称的 Bean 是否是 Singleton 类型
- isPrototype，查询指定名称的 Bean 是否是 prototype 类型的
- isTypeMatch，查询指定名称的 Bean 是否是特定的 Class 类型
- getType，查询Bean 的 Class 类型
- getAliases，查询 Bean 的所有别名

通过以上的一系列接口，可以使用不同的 Bean 的检索方法，很方便地从 IoC 容器中得到需要的 Bean，从而忽略具体的 IoC 容器的实现。

#### 2. BeanFactory 容器的设计原理

`BeanFactory` 接口提供了使用 IoC 容器的规范。我们以 `XmlBeanFactory` 为例，从其源码来看看 `BeanFactory` 的设计思路。

```java
public class XmlBeanFactory extends DefaultListableBeanFactory {
    /**
     * 初始化一个 XmlBeanDefinitionReader，用来处理 BeanDefinition
     */
    private final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this);
    
    /**
     * 通过 Resource 获取存储 BeanDefinition 的 .xml 文件
     * 用来定位需要的 BeanDefinition 信息来对 Bean 完成容器的初始化和依赖注入过程
     */
    public XmlBeanFactory(Resource resource) throws BeansException {
        this(resource, null);
    }
    
    /**
     * 通过指定的 xml 文件加载 BeanDefinition
     */
    public XmlBeanFactory(Resource resource, BeanFactory parentBeanFactory) throws BeansException {
        super(parentBeanFactory);
        this.reader.loadBeanDefinitions(resource);
    }
}
```

参考 XMLBeanFactory 的实现，大致可以将 IoC 容器的创建分为如下几个步骤：

1. 创建 IoC 配置文件的抽象资源
2. 创建一个 BeanFactory 
3. 创建一个载入 BeanDefinition 的读取器
4. 从定义好的资源读入配置信息
5. 完成 IoC 容器中 Bean 的初始化

```java
ClassPathResource res = new ClassPathResource("beans.xml");
DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
reader.loadBeanDefinitions(res);
```

#### 3. ApplicationContext 的应用场景

ApplicationContext 除了能够使用 IoC 容器的基本功能之外，还为用户提供了以下附加服务：

```java
/**
 * 在 ApplicationContext 中提供的附加服务，使基本 IoC 容器的功能更加丰富。与简单的 BeanFactory 相比，它的使用是一种面向框架的使用风格
 * ListableBeanFactory：访问应用中 Bean 工厂的方法
 * MessageSource：解析消息的能力，支持国际化
 * ResourceLoader：加载文件资源的能力
 * ApplicationEventPublisher：发布应用事件的能力，这些事件和 Bean 的生命周期结合，为管理 Bean 提供了便利
 */
public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory, MessageSource, ApplicationEventPublisher, ResourcePatternResolver {   
    @Nullable   
    String getId();  
    String getApplicationName();  
    String getDisplayName();  
    long getStartupDate();  
    @Nullable   
    ApplicationContext getParent();   
    AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException;
}
```

#### 4. ApplicationContext 的设计原理

在 ApplicationContext 容器的实现中，以 FileSystemXmlApplicationContext 的实现为例：

```java
public class FileSystemXmlApplicationContext extends AbstractXmlApplicationContext {
    /**
     * 除了实例化应用上下文的支持，还支持启动 IoC 容器的 refresh() 
     * 在这个过程中，还通过下面的方法读取 文件系统中XML形式的 BeanDefinition 资源
     */
    public FileSystemXmlApplicationContext(
			String[] configLocations, boolean refresh, @Nullable ApplicationContext parent)
			throws BeansException {

		super(parent);
		setConfigLocations(configLocations);
		if (refresh) {
			refresh();
		}
	}
    
    /**
     * 获得 FileSystemResource 的资源定位
     */
    @Override
	protected Resource getResourceByPath(String path) {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		return new FileSystemResource(path);
	}

}
```

## 三、IoC 容器的初始化过程

IoC 容器的初始化是由前面介绍的 refresh() 方法来启动的。该启动包括 BeanDefinition 的 Resource 定位、载入和注册三个过程。

### 3.1 BeanDefinition 的 Resource 定位

当我们手动使用 IoC 容器的时候，是通过 `new Resource("beans.xml")` 的方式获取 xml 中的信息。

以 FileSystemXmlApplicationContext 为例，getResourceByPath 返回的是一个 FileSystemResource 对象，作为 Resource 的实现类。

其他 ApplicationContext 的子实现类也会生成对应的 Resource 实现类。

### 3.2 BeanDefinition 的载入和解析

对于 IoC 容器而言，BeanDefinition 信息的载入，相当于把定义的 BeanDefinition 转换为 Spring 内部表示的数据结构的过程。**在 IoC 容器中是通过 HashMap 来保持和维护 BeanDefinition 数据的**。

以 `AbstractApplicationContext` 的 refresh() 方法为例：

```java
	@Override
	public void refresh() throws BeansException, IllegalStateException {
		synchronized (this.startupShutdownMonitor) {
			// Prepare this context for refreshing.
			prepareRefresh();

			// 创建或刷新 BeanFactory
			ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

			// 准备 BeanFactory 的一些初始化操作
			prepareBeanFactory(beanFactory);

			try {
				// 允许子类对 BeanFactory 进行一些后置操作
				postProcessBeanFactory(beanFactory);

				// 注册 BeanFactory 到 context 中
				invokeBeanFactoryPostProcessors(beanFactory);

				registerBeanPostProcessors(beanFactory);

				initMessageSource();

				initApplicationEventMulticaster();

                // 由子类实现该刷新方法
				onRefresh();

				// 检查并注册监听器
				registerListeners();

				// 实例化所有剩余单例
				finishBeanFactoryInitialization(beanFactory);

				// 完成刷新，并发布事件
				finishRefresh();
			}

			catch (BeansException ex) {
				if (logger.isWarnEnabled()) {
					logger.warn("Exception encountered during context initialization - " +
							"cancelling refresh attempt: " + ex);
				}

				// Destroy already created singletons to avoid dangling resources.
				destroyBeans();

				// Reset 'active' flag.
				cancelRefresh(ex);

				// Propagate exception to caller.
				throw ex;
			}

			finally {
				// Reset common introspection caches in Spring's core, since we
				// might not ever need metadata for singleton beans anymore...
				resetCommonCaches();
			}
		}
	}
```

具体刷新方法，可以参考 `AbstractApplicationContext` 的实现子类 `AbstractRefreshableApplicationContext`：

```java
@Override
protected final void refreshBeanFactory() throws BeansException {
    // 如果存在 BeanFactory 则先关闭销毁
    if (hasBeanFactory()) {
        destroyBeans();
        closeBeanFactory();
    }
    try {
        DefaultListableBeanFactory beanFactory = createBeanFactory();
        beanFactory.setSerializationId(getId());
        customizeBeanFactory(beanFactory);
        loadBeanDefinitions(beanFactory);
        synchronized (this.beanFactoryMonitor) {
            this.beanFactory = beanFactory;
        }
    }
    catch (IOException ex) {
        throw new ApplicationContextException("I/O error parsing bean definition source for " + getDisplayName(), ex);
    }
}
```

### 3.3 BeanDefinition 在 IoC 容器中的注册

在 DefaultListableBeanFactory 中，通过一个 HashMap 来持有载入的 BeanDefinition 的。

DefaultListableBeanFactory 实现了 BeanDefinitionRegistry 的接口，用来完成 BeanDefinition 向容器的注册。注册的过程就是把解析得到的 BeanDefinition 设置到 HashMap 中去，如果遇到同名的 BeanDefinition，在处理时就需要依据 allowBeanDefinitionOverriding 的配置来完成。

> 每个图都是我自己画的