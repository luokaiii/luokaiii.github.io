---
title: SpringBoot整合Quartz(一)-简单介绍Demo
date: 2018-11-01 10:18:03
categories: 
 - Java成神之路
 - Study社区
---
# SpringBoot整合Quartz(一)-简单介绍Demo

计划：(从Quartz的属性、方法、配置文件、配置类、持久化、集群等几个方面，写一些心得)

Quartz 作为一款由 Java 编写的开源作业调度框架,[官网传送门](http://www.quartz-scheduler.org/index.html)。与我而言，作用是定时执行某一个任务。

![Quartz](https://upload-images.jianshu.io/upload_images/13603359-55f97ba1d65bc00f.png)

<!-- More -->

## 引入依赖

因为我们使用的是 `SpringBoot` 和 `Quartz` 整合。

如果没有使用 `spring-boot-gradle-plugin` 指定SpringBoot的版本，则需要在下面的依赖汇中加入版本号。

```java
// Gradle 方式
compile('org.springframework.boot:spring-boot-starter-quartz')
```

```java
// Maven 方式
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-quartz</artifactId>
</dependency>
```

## 配置文件-Quartz.properties

通过配置文件 quartz.properties，初始化 Quartz 的一些配置.

```properties
// ThreadPool 实现的类名
org.quartz.threadPool.class：org.quartz.simpl.SimpleThreadPool
// 线程数量
org.quartz.threadPool.threadCount ： 10
// 线程优先级
// threadPriority 属性的最大值是常量 java.lang.Thread.MAX_PRIORITY，等于10。最小值为常量 java.lang.Thread.MIN_PRIORITY，为1
org.quartz.threadPool.threadPriority ： 5
// 自创建父线程
//org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread： true
```

## 配置Config

```java
import org.quartz.Scheduler;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.Properties;

/**
 * @description:
 * @date: 2018-11-05 17:27
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Bean(name = "SchedulerFactory")
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setQuartzProperties(quartzProperties());
        return factory;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        //在quartz.properties中的属性被读取并注入后再初始化对象
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    /**
     * quartz初始化监听器
     */
    @Bean
    public QuartzInitializerListener executorListener() {
        return new QuartzInitializerListener();
    }

    /**
     * 通过SchedulerFactoryBean获取Scheduler的实例
     */
    @Bean(name = "Scheduler")
    public Scheduler scheduler() throws IOException {
        return schedulerFactoryBean().getScheduler();
    }
}

```

## 常用属性与方法

### 1.Job类

继承 `Job` 类，表明子类是一个任务（`Job 的子类必须存在一个无参构造。`）。实现 `execute(JobExecutionContext context)` 方法，来定义任务的执行内容。

```java
/**
 * 例子：定义一个执行输出语句的任务
 * @date: 2018-11-05 17:31
 */
public class HelloJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("Hello执行：" + LocalDateTime.now());
    }
}
```

### 2.JobDetails接口

Job 在执行时，通过 JobDetails 反射出具体的执行对象。包含了 Job 在执行时的具体属性。

JobDetailImpl 实现 JobDetails，定义所有的属性，包括以下几个：

| 属性名        | 说明                                                         |
| ------------- | ------------------------------------------------------------ |
| name          | Job任务的名称                                                |
| group         | 组。注意：同组中只能存在一个 JobClass 在执行。               |
| description   | 描述。                                                       |
| jobClass      | 具体的 Job。必须是 Job 的实现类。                            |
| jobDataMap    | 自定义的属性。可将任务的 key-value 存入 map 中，实现  Job 属性的扩展。 |
| durability    | 是否持久化（默认false）。非持久化的 Job，会在 Trigger 执行完毕后，自动删除。 |
| shouldRecover | 是否可恢复（默认false）。当设置为“是”，执行 Scheduler 发生崩溃等异常情况时，Job 会在 Scheduler 重启时，重新执行。 |
| key           | 键，标识                                                     |

```java
/**
 * 通过 JobBuilder 获得一个 JobDetails 对象
 */
public JobDetail getJobDetail(String jobClassName, String jobGroupName){
    return JobBuilder.newJob(getClass(jobClassName).getClass()).withIdentity(jobClassName, jobGroupName).build();
}

/**
 * 通过class 实例对象
 */
private static BaseJob getClass(String classname) throws Exception {
        Class<?> class1 = Class.forName(classname);
        return (BaseJob) class1.newInstance();
    }
```

### 3.Trigger

触发器类，包含着 Job 的触发执行时间规则。

| 子类          | 说明                                                   |
| ------------- | ------------------------------------------------------ |
| SimpleTrigger | 当仅需要触发一次，或者以固定时间间隔周期执行时，最合适 |
| CronTrigger   | 通过 cron表达式 定义时间规则的调度方案                 |

```java
/**
 * 创建一个 Trigger 触发器
 */
public Trigger getTrigger(String jobClassName, String jobGroupName, String cronExpression){
    //表达式调度构建器(即任务执行的时间)
    CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

        //按新的cronExpression表达式构建一个新的trigger
    CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobClassName, jobGroupName)
            .withSchedule(scheduleBuilder).build();
    return trigger;
}
```

### 4.JobStore

存储Job和运行期间的状态。
默认使用的是 `RAMJobStore` ,使用内存来配置、构造和运行，但是当程序停止或者重启后，任务就会丢失。
后面我们会通过修改 JobStore 来使，Job和Trigger持久化到数据库中。也会更利于我们使用集群

### 5.Scheduler

调度器，是 Quartz 调用执行 Job，以及设置 trigger 的主要接口。

| 常用方法                                                 | 说明                                  |
| -------------------------------------------------------- | ------------------------------------- |
| start()                                                  | 开启调度线程，也就是启动Scheduler。   |
| shutdown()                                               | 关闭。                                |
| setJobFactory(JobFactory factory)                        | 指定生成 Job 的JobFactory。           |
| scheduleJob(JobDetail jobDetail, Trigger trigger)        | 接收一个Job，按照指定的 trigger执行。 |
| deleteJob(JobKey jobKey)                                 | 删除 Job。                            |
| pauseJob(JobKey jobKey)                                  | 暂停 Job。                            |
| pauseTrigger(TriggerKey triggerKey)                      | 暂停 Trigger。                        |
| resumeJob(JobKey jobKey)                                 | 重启。                                |
| rescheduleJob(TriggerKey triggerKey, Trigger newTrigger) | 修改。                                |

## 测试

```java
@Test
public void quartzTest() throws SchedulerException{
    // 1. 创建工厂类 SchedulerFactory
    SchedulerFactory factory = new StdSchedulerFactory();
    // 2. 通过 getScheduler() 方法获得 Scheduler 实例
    Scheduler scheduler = factory.getScheduler();

    // 3. 使用上文定义的 HelloJob
    JobDetail jobDetail = JobBuilder.newJob(HelloJob.class)
            //job 的name和group
            .withIdentity("jobName", "jobGroup")
            .build();

    //3秒后启动任务
    Date statTime = new Date(System.currentTimeMillis() + 3000);

    // 4. 启动 Scheduler
    scheduler.start();

    // 5. 创建Trigger
    //使用SimpleScheduleBuilder或者CronScheduleBuilder
    Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("jobTriggerName", "jobTriggerGroup")
            .withSchedule(CronScheduleBuilder.cronSchedule("0/2 * * * * ?")) //两秒执行一次
            .build();

    // 6. 注册任务和定时器
    scheduler.scheduleJob(jobDetail, trigger);

}
```