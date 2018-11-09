#### FIX(1)：Job 中无法使用 Spring 的依赖注入

__原因___：`quart2.x` 之后，`org.quartz.CronTrigger` 变成了接口，从而无法使用 `IOC` 配置 Quartz的触发器 `trigger` 。

__解决方法__：

1. 使用 `spring-quartz` 提供的 `AdaptableJobFactory`，修改 `JobInstance` 实例的注入：

   ```java
   /**
    * @description: 解决Job无法注入Service
    * @author: Koral
    * @date: 2018-11-02 16:11
    */
   @Component
   public class JobFactory extends AdaptableJobFactory {
       private final AutowireCapableBeanFactory capableBeanFactory;
   
       @Autowired
       public JobFactory(AutowireCapableBeanFactory capableBeanFactory) {
           this.capableBeanFactory = capableBeanFactory;
       }
   
       @Override
       protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
           //调用父类的方法  
           Object jobInstance = super.createJobInstance(bundle);
           //进行注入  
           capableBeanFactory.autowireBean(jobInstance);
           return jobInstance;
       }
   }
   ```

2. 将刚刚实现的 `JobFactory` 注入到配置中：

   ```java
   第一种（通过配置类注入）：
   @Configuration
   public class SchedulerConfig {
       private final JobFactory jobFactory;
   
       @Autowired
       public SchedulerConfig(JobFactory jobFactory) {
           this.jobFactory = jobFactory;
       }
   
       @Bean(name="SchedulerFactory")
       public SchedulerFactoryBean schedulerFactoryBean() throws IOException{
           SchedulerFactoryBean factory = new SchedulerFactoryBean();
           factory.setJobFactory(jobFactory);
           factory.setQuartzProperties(quartzProperties());
           return factory;
       }
       
       // omit 其他config
   }
   第二种（通过XML文件注入）：
   在 spring的配置文件 (spring.xml)中注入： 
       <bean id="jobFactory" class="com.yj.quartzjob.JobFactory"></bean>  
       <bean id="schedulerFactoryBean" class="org.springframework.scheduling.quartz.SchedulerFactoryBean">  
           <property name="jobFactory" ref="jobFactory"></property>  
       </bean>  
   ```

3. 在我们的 `Job` 中使用 `IOC` ：

   ```java
   @Component
   public class MapperJob implements Job {
       @Autowired
       private UserServiceImpl userService;
   
       @Override
       public void execute(JobExecutionContext context) {
           userService.exec();
       }
   }
   ```

   注意：这里不能使用构造函数注入！Job必须有一个无参构造！

4. 除了 `spring-boot-starter-quartz` 还必须引入 `spring-boot-starter-web`