### 一、反射

通过 **Class.forName(name)**、**类名.class**、**this.getClass()** 获取反射类的字节码，然后可获取到字节码中的：变量、构造函数、方法等

### 二、动态代理

1. 静态代理

   1. 通常只代理一个类

2. 动态代理

   1. 代理一个接口下的多个实现类
   2. AOP 编程就是基于动态代理实现的，如 Spring、Hibernate

3. 实现一个 ArrayList 的动态代理：

   1. ```java
          List<String> list = new ArrayList<>();
          List<String> proxyInstance = (List<String>)Proxy.newProxyInstance(list.getClass().getClassLoader(), list.getClass().getInterfaces(), new InvocationHandler() {
              @Override
              public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                  return method.invoke(proxy, args);
              }
          });
          proxyInstance.add("你好");
      ```

### 三、设计模式 & 回收机制

设计模式可分为三大类：

创建型模式：**工厂方法模式**、**抽象工厂模式**、**单例模式**、**建造者模式**、原型模式

结构型模式：**适配器模式**、装饰器模式、**代理模式**、外观模式、桥接模式、组合模式、**享元模式**

行为型模式：**策略模式**、模板方法模式、**观察者模式**、迭代子模式、责任链模式、命令模式、备忘录模式、状态模式、访问者模式、中介者模式、解释器模式

1. 单例模式：

   1. 懒汉式：直接创建对象，通过方法返回
   2. 饿汉式：双重锁 + volatile

2. 工厂方法模式：

   1. **普通工厂模式**：建立一个工厂类，对实现同一接口的类进行实例创建

      1. ```java
         public interface Human {}
         
         public class Woman implements Human {}
         public class Man implements Human {}
         
         public class HumanFactory {
             public Human produce(String type) {
                 switch(type) {
                     case: "man":
                         return new Man();
                     case: "woman":
                         return new Woman();
                     default: 
                         throw new IllgeArgumentException();
                 }
             }
         }
         
         // 调用：new HumanFactory().produce("man");
         ```

   2. **多工厂模式/工厂方法模式**：提供多个工厂，分别创建对象

      1. ```java
         // 可以解决普通工厂模式，因type不正确的问题
         public class HumanFactory {
             public Human produceMan() {
                 return new Man();
             }
             
             public Human produceWoman() {
                 return new Woman();
             }
         }
         
         // 调用：new HumanFactory().produceMan();
         ```

   3. **静态工厂模式**：将工厂方法模式的方法置为静态，不需要创建实例，直接调用即可

      1. ```java
         public class HumanFactory {
             public static Human produceMan() {
                 return new Man();
             }
             
             public static Human produceWoman() {
                 return new Woman();
             }
         }
         
         // 调用：HumanFactory.produceMan();
         ```

   4. **抽象工厂模式**：将工厂类抽象化（如果Human继续实现子类的话，需要修改Factory对象，这违反了闭包原则；而使用抽象工厂模式，只需要再扩展一个工厂即可）

      1. ```java
         public interface HumanFactory {
             Human produce();
         }
         
         public class WomanFactory implments HumanFactory {
             @Override
             public Human produce() {
                 return new Woman();
             }
         }
         
         public class ManFactory implments HumanFactory {
             @Override
             public Human produce() {
                 return new Man();
             }
         }
         
         // 调用：
         // HumanFactory factory = new ManFactory();
         // Human human = factory.produce();
         ```

3. **建造者模式**：将各种产品集中起来，创建符合对象

   1. ```java
      public class Builder {
          private List<Human> womans = new ArrayList<>();
          private List<Human> mans = new ArrayList<>();
          
          // 加入 count 个女人
          public void jointWoman(int count) {
      		for(int i = 0; i < count; i++) {
                  womans.add(new Woman());
              }
          }
          
          public void jointMan(int count) {
      		for(int i = 0; i < count; i++) {
                  mans.add(new Man());
              }
          }
          
          // getter and setter ...
      }
      
      // 调用：new Builder().jointWoman(10);
      ```

4. **适配器模式**：将某个类转换为另一个类表示，消除由于接口不匹配所造成的兼容性问题。

   1. ```java
      // 动物具有吃的行为
      public class Animal {
          public void eat(){}
      }
      
      // 鸟类具有吃、飞的行为
      public interface Birds {
      	void eat();
          void flight();
      }
      ```

   2. **类的适配器模式**

      1. ```java
         // 适配器，吃的行为保持不变，飞的行为需要重新实现
         public void Adapter extends Animal implements Birds {
             @Override
             public void flight() {
                 // implements ...
             }
         }
         ```

   3. **对象的适配器模式**，思路与类适配器相似，从继承改为持有实例

      1. ```java
         public void Adapter implements Birds {
             private Source source;
             public Adapter(Source source) {
                 this.source = source;
             }
             
             @Override
             public void eat() {
                 this.source.eat();
             }
             
             @Override
             public void flight() {
                 // implements ...
             }
         }
         ```

   4. **接口的适配器模式**：当我们实现一个接口时，需要实现该接口的所有方法，有时我们不希望全部实现，因此可以借助抽象类来实现该接口的所有方法，再继承该抽象类，重写我们需要的方法即可。

5. **装饰器模式**：为对象增加新的功能，而且是动态的，且装饰对象与被装饰对象需要实现同一个接口

   1. ```java
      public interface Human {
          void eat();
      }
      
      public class Woman implements Human {
          @Override
          public void eat() {
              // 正常吃饭
          }
      }
      
      // 装饰器
      public class WomanDecorator implements Human {
          private Human human;
          
          public WomanDecorator(Human human) {
              this.human = human;
          }
          
          @Override
          public void eat() {
              // 吃饭前洗个手
      		this.human.eat();
              // 吃完了洗手
          }
      }
      
      // 调用：new WomanDecorator(new Woman()).eat(); -- 对吃饭这一行为进行装饰
      ```

6. **策略模式**：策略模式定义了一系列算法，并将每个算法封装起来，使它们可以相互替换，且算法的变化不会影响到使用算法的客户。策略模式的决定权在用户，系统本身提供不同的算法实现，外部用户只需要决定用哪个算法即可。

7. **观察者模式**：类似邮件订阅和RSS订阅，当一个对象变化时，其它依赖该对象的对象都会收到通知，并且随之变化。对象间是一对多的关系。

   1. ```java
      public interface Observer {
          void update();
      }
      
      public class Observer1 implements Observer {
          @Override 
          public void update() {
              // 更新观察者1
          }
      }
      
      public class Observer2 implements Observer {
          @Override 
          public void update() {
              // 更新观察者2
          }
      }
      
      public interface Subject{
          // 增加观察者
          void add(Observer obs);
          // 删除观察者
          void del(Observer obs);
          // 通知所有观察者
          void notifyAll();
          // 自身的操作
          void operation();
      }
      
      public abstract class AbstractSubject implements Subject {
          private Vector<Observer> vector = new Vector<>();
          
          @Override 
          public void add(Observer obs) {
      		vector.add(obs);
          }
          
          @Override 
          public void del(Observer obs) {
      		vector.del(obs);
          }
          
          @Override 
          public void notifyAll() {
      		Enumeration<Observer> enumes = vector.elements();
              while(enumes.hasMoreElements) {
                  enumes.nextElement().update();
              }
          }
      }
      
      public class MySubject extends AbstractSubject {
          @Override 
          public void operation() {
      		// do something
              notifyAll();
          }
      }
      
      // 调用： 
      // Subject sub = new MySubject();
      // sub.add(new Observer1());
      // sub.add(new Observer2());
      // sub.operation();
      ```

8. **JVM 垃圾回收机制和常见算法**

   1. 搜索算法：
      1. **引用计数法**：每次引用该对象时，计数器+1；引用失效-1；（**不能解决循环引用问题**）
      2. **根搜索算法**：以 GC Roots 为起点向下搜索，搜索的路径称为引用链，当对象没有被引用链连接时，说明对象不可用
   2. 回收算法：
      1. **标记-清除法**：标记之后直接清除（效率不高，且会产生大量不连续空间）
      2. **复制算法**：将内存分为两块(现在的复制算法比例是 8:1)，每次使用其中一块，当回收时，将存活的对象复制到另一块上，再将内存块整个清理掉（运行效率高，但内存利用率不高）
      3. **标记-整理法**：标记后将存活对象向内存的一端移动，然后回收边界外的内存（提高了内存利用率，适用于收集对象存活时间较长的老年代）
      4. **分代收集**：将对象根据存活时间分为新生代（使用复制算法）和老年代（使用标记整理算法）

### 四、类加载器

1. 类的初始化
   1. 创建类的实例（new的时候）
   2. 访问某个类或接口的静态变量，或者对静态变量赋值
   3. 调用类的静态方法
   4. 反射
   5. 初始化类的子类时
   6. JVM启动时标明的启动类，即文件名和类名相同的类

### 五、GC基础知识

1. GC 机制存在的原因：
   1. 安全性考虑
   2. 减少内存泄漏
   3. 减少程序员工作量
2. GC 什么时候回收垃圾
   1. 可以理解为如何判断对象“死亡”，即对象的搜索算法
3. **遇到过的内存溢出的原因有哪些，解决方法有哪些？**
   1. 原因：
      1. 内存中加载的数据量过大，如一次从数据库取出过多数据
      2. 集合中对对象的引用，使用完后未清空，使JVM不能回收
      3. 代码中存在死循环或循环过多产生重复的对象实体
      4. 第三方软件中的bug
      5. 启动参数内存值设置过小
   2. 解决方法：
      1. 修改JVM启动参数，增加内存
      2. 检查错误日志，查看OutOfMemory前是否有其他异常或错误
      3. 对代码走查和分析，找出可能的位置，重点排查以下几点：
         1. 数据库查询，是否存在获取全部数据的查询，如一次请求10万次记录
         2. 检查代码是否存在死循环或递归调用
         3. 检查是否有大循环产生新对象实体
         4. 检查List、Map使用后未清除的问题
         5. 使用内存查看工具动态查看内存使用情况

### 六、Java8新特性

1. Lambda表达式：
   1. Comparable、Stream、Sort、Distnict、Function、Map、Reduce、Optional