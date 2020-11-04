### 一、面向对象

1. 面向对象的特性：继承（继承父类信息）、封装（隐藏属性，提供方法）、多态（重载，重写）、抽象
2. 访问权限修饰符：public、protected、default、private
3. 理解clone对象：clone 是浅拷贝（只拷贝引用，不拷贝内容）

### 二、JAVA语法

1. java中的goto语句：goto和const为保留字，无法使用
2. & 与 && 的区别：__& 是逻辑与，&& 是 短路与运算__
3. x.equals(y)：比较的是两个值的 hashCode
4. 继承 String：不可以，__String 是 final 的__
5. 当对象作为参数被传递到方法后，在方法内可改变对象的属性，并返回变化后的结果，这是“值传递”还是“引用传递”？：__值传递，JAVA语言的方法调用只支持参数的值传递。__
6. 重载与重写的区别：**重载（方法名一直，但参数顺序、类型、个数不同）**，**重写（参数、返回类型必须一致；final、static不可重写；访问权限不能比父类的更低）**
7. 为什么不能根据返回类型进行重载：返回值只是函数运行后的一个“状态”，是被调用者与调用者间通信的关键，不能作为某个方法的“标识”（即编译器无法区分）
8. 抽象类与接口的异同：
   1. 抽象类：可以有构造器；可以定义成员变量；可以包含静态方法；方法可抽象也可不抽象；一个类只能继承一个抽象类
   2. 接口：不能定义构造器；只能定义常量；不能有静态方法；一个类可实现多个接口
9. 抽象方法与静态 static、本地 native、同步 synchronized 这三个修饰符可同时存在吗？
   1. 都不能，static 不能被继承；native 是由 C 代码编写的； synchronized 关注的是方法内部的实现细节
10. 静态变量与实例变量的区别：
    1. 静态变量：类变量，属于类，在内存中只有一个拷贝
    2. 实例变量：依存于实例对象，当实例对象创建后才能访问
    3. 静态变量可以实现多个对象共享内存
11. == 和 equals 的区别：
    1. == 基本数据类型比较值；引用类型比较地址
    2. equals 不重写的情况下，比较地址
12. break 和 continue 的区别

### 四、Java的异常处理

1. 异常分类：编译时异常、运行时异常
2. finally 中的代码，会先于 return 前执行。因此，如果 finally 中 也进行了 return，则 catch 或 try 中的 return 不会返回
3. error 与 exception 的区别：Error 是虚拟机相关的错误；Exception 是程序可捕获的异常。
4. 常见的 **RuntimeException：NullPointerException、ClassNotFoundException、IndexOutOfBoundsException、IllegalArgumentException、ClassCastException、SQLException、NoSuchMethodException**
5. throw 与 throws 的区别：
   1. throw：方法内部抛出的异常，表示抛出异常的动作
   2. throws：声明在方法后，告知调用者有异常需要处理，异常并不一定会发生
6. final、finally、finalize的区别：
   1. final：声明属性、方法、类，表示属性不可变，方法不可覆盖，类不可继承
   2. finally：异常处理语句，总是执行
   3. finalize：Object 的方法，当对象“死亡”时由垃圾回收器调用
7. String 的 + 和 StringBuilder.append() 方法的执行效率：
   1. 在正常的 String a = "a" + "b" + "c" 中，字符串是被转换成 StringBuilder 后执行的，此时效率相同
   2. 当在循环中执行 a 的 + 操作时，会在每次循环中都创建一个 StringBuilder，此时应该直接使用 append 方法效率会更高些
   3. StringBuffer 与 StringBuilder 功能一致，StringBuffer 是线程安全的，因此 StringBuilder 效率会更高
8. Java8 日期：
   1. LocalDate
   2. LocalTime
   3. LocalDateTime
   4. Instant

### 五、Java 数据类型

1. 基本数据类型：byte(1字节)、short(2字节)、int(4字节)、long(8字节)、float(4字节)、double(8字节)、char(2字节)、boolean(1字节)
2. **String 是引用数据类型，底层为char数组**
3. Integer f1=100,f2=100,f3=150,f4=150，f1==f2和f3==f4的结果如何？
   1. f1==f2：如果整形数值在-128~127之间，不会new新的Integer，而是直接引用常量池中的 Integer 对象
   2. f3!=f4，超出常量池提供的范围了，两个都是新的new对象引用
4. String、StringBuffer、StringBuilder的区别：
   1. String：字符串常量，不可变
   2. StringBuffer：线程安全的（方法上有 synchronized），执行效率较慢，可使用 append()，insert() 修改对象
   3. StringBuilder：线程不安全，适用于单线程下操作字符串缓冲区大量数据

### 六、JAVA IO

1. 字符流：
   1. Reader 输入流
      1. InputStreamReader 字节输入流转字符输入流
         1. FileReader 读取字符文件的便捷类
      2. BufferedReader 缓冲流
   2. Writer 输出流
      1. BufferedWriter 缓冲流
      2. OutputStreamWriter 字节输出流转字符输出流
         1. FileWriter 写入字符文件的边界类
2. 字节流：
   1. InputStream 输入流
      1. FileInputStream
      2. BufferedInputStream
   2. OutputStream 输出流
      1. FileOutputStream
      2. BufferedOutputStream
3. 字节流和字符流的区别：
   1. 字节流
      1. 读取时，读一个字节返回一个字节
      2. 可处理所有类型数据，除纯文本外，都可以考虑字节流
   2. 字符流
      1. 读取时，读一个或多个字节时，先查询指定编码表，将查到的字符串返回。
      2. 只能处理字符数据，因此纯文本数据，优先考虑字符流
4. Java 序列化
   1. 序列化就是将对象内容进行流化，实现 Serializable 接口

### 七、Java 集合

1. 集合的安全性

   1. ArrayList 不安全
   2. HashSet 不安全
   3. HashMap 不安全
   4. Vector 安全
   5. HashTable 安全

2. 想要的 **线程安全的集合**：

   1. Collections.synchronizedCollection(c)
   2. Collections.synchronizedList(list)
   3. Collections.synchronizedSet(c)
   4. Collections.synchronizedMap(m)
   5. 原理很简单，即核心方法上都使用了 synchronized 关键字

3. **ArrayList 实现原理**：

   1. 通过 **构造参数**，可以发现，ArrayList 的底层是一个 **Object 数组**

   2. **add方法**：

      1. ```java
         public boolean add(E object) {
             // 将成员变量 array 赋值给 a，将成员变量 size 赋值给 s
             Object[] a = array;
             int s = size;
             // 判断集合长度和数组长度是否相同（相等则表明数组满了，需要重新分配新数组）
             if(s == a.length) {
                 // 创建新的数组，计算新数组长度
                 Object[] newArray = new Object[s + (s < (MIN_CAPACITY_INCREMNT) ? MIN_CAPACITY_INCREMENT : s >> 1)];
                 // 复制数组
                 System.arraycopy(a, 0, newArray, 0 ,s);
             }
             // 将新添加的 object，赋值给 a[s]
             a[s] = object;
             // 长度加一
             size = s + 1;
             // 记录集合修改的次数（用于避免在使用迭代器迭代集合时并发修改异常）
             modCount++;
             return true;
         }
         ```

   3. **remove方法**：

      1. ```java
         public E remove(int index) {
             // 将 array 和 size 分别赋值给 a,s
             Object[] a = array;
             int s = size;
             // 判断 index 是否大于集合长度
             if (index >= s) {
                 throw IndexOutOfBoundsException(index, s);
             }
             // 取出index下标下的元素，作为返回值
             E result = [E] a[index];
             // 将 index 后的元素向前移动一位
             System.arraycopy(a, index + 1, a, index, --s - index);
             // 将最后一个元素置为 null（因为所有元素前移了一位）
             a[s] = null;
             // 重新给成员变量 size 赋值
             size = s;
             // 记录修改次数
             modCount++;
             return result;
         }
         ```

   4. **clear 方法**：

      1. ```java
         public void clear() {
             if (size != 0) {
                 // 使用 Arrays 的 fill 方法，将从 0 到 size 的所有元素置为 null
                 Arrays.fill(array, 0, size, null);
                 size = 0;
                 modCount++;
             }
         }
         ```

4. 集合的并发：

   1. 普通集合：性能最高，但不保证多线程的安全性和并发的可靠性
   2. 同步集合（线程安全）：仅仅是给集合添加了 synchronized 关键字，严重牺牲了性能，且并发效率低
   3. 并发集合：通过复杂策略来保证多线程的安全，又提高了并发时的效率，如 ConcurrentHashMap、ConcurrentLinkedQueue、ConcurrentLinkedDeque 等。

5. ConcurrentHashMap：

   1. 锁分段技术，最多允许16个线程并发无阻塞的操作集合对象

6. List 的三个子类：

   1. ArrayList，底层是数组，查询快，增删慢
   2. LinkedList：底层是链表，增删快，查询慢
   3. Vector：底层是数组，线程安全，查询慢，增删慢

7. List、Map、Set 的区别：

   1. | 区别     | List         | Set          | Map                      |
      | -------- | ------------ | ------------ | ------------------------ |
      | 数据结构 | 单列数据集合 | 单列数据集合 | 键值双列数据集合         |
      | 顺序     | 有序         | 无序         | 无序                     |
      | 重复     | 可重复       | 不可重复     | key不可重复，value可重复 |

   2. | 区别      | ArrayList  | LinkedList   | Vector   |
      | --------- | ---------- | ------------ | -------- |
      | 数据结构  | 数组       | 循环双向链表 | 数组     |
      | 查询      | 快         | 慢           | 慢       |
      | 安全/效率 | 非线程安全 | 非线程安全   | 线程安全 |
      | 增删      | 慢         | 快           | 慢       |

   3. | 区别     | HashMap              | HashTable              | LinkedHashMap                   |
      | -------- | -------------------- | ---------------------- | ------------------------------- |
      | 数据结构 | 基于hash表           |                        | HashMap的子类，保存了插入的顺序 |
      | 线程安全 | 不安全               | 安全                   |                                 |
      | 效率     | 高效                 | 低                     |                                 |
      |          | 允许key、value为null | 不允许key、value为null |                                 |

   4. HashSet：底层由HashMap实现，

   5. LinkedHashSet：继承自 HashSet，底层由 LinkedHashSet 实现

### 八、Java 多线程和并发库

1. 传统线程机制

   1. 覆盖 Thread 的 run 方法

      1. ```java
         new Thread() {
             @Override
             public void run() {
                 // do something
             }
         }
         ```

   2. 传递 Runnable 给 Tread 对象

      1. ```java
         new Thread(new Runnable(){
             public void run() {
                 // do domething
             }
         })
         ```

   3. 定时器 Timer 与 TimerTask

      1. 覆盖 TimerTask 的 run 方法
      2. 使用 Timer 注册 TimerTask 的任务

   4. 线程互斥与同步：**互斥是一种特殊的同步**

   5. **线程局部变量 ThreadLocal**

      1. ThreadLocal 用于实现线程内数据共享
      2. 每个线程都有独享的 ThreadLocalMap 对象，该对象的 Key 是 ThreadLocal，值是具体值
      3. 应用场景：订单处理、银行转账 等，多个操作需要在同一个事务中完成，而这些操作分布在不同的模块类中

   6. **多线程共享数据**：

      1. 多线程行为一致，操作同一数据源。可使用同一个 Runnable 对象。
      2. 多线程行为不一致，操作同一数据源。需要用不同的 Runable。

2. 多线程基础知识 - 线程并发库 （java.util.concurrent）

   1. **java.util.concurrent （多线程并发库）**

      1. 包含许多线程安全、测试良好、高性能的并发构建块

      2. **线程池工厂类 Executors**：

         1. 线程池的作用：限制系统中执行线程的数量；可手动调节设置的线程数

         2. 使用的原因：减少创建和销毁线程的次数；可手动调节工作的线程数

         3. 解释：线程池来源于对象池的思想，即开辟一块内存空间，存放多个线程由池管理器进行执行调度。当有线程任务时，从池中取一个，执行完成后回归线程池，这样可以避免反复创建线程对象所带来的性能开销，节省了系统资源

         4. ```java
            /**
             * 线程池的种类
             */
            // 可变线程池
            ExecutorService pool = Executors.newCachedThreadPool();
            
            // 延迟线程池
            ScheduledExecutorService pool = Executors.newScheduledThreadPool(int);
            
            // 固定线程数线程池
            ExecutorService pool = Executors.newFixedThreadPool(int);
            
            // 单一线程池
            ExecutorService pool = Executors.newSingleThreadEecutor();
            
            // 执行任务
            pool.execute(Runnable); // 异步执行Runnable，无法得知执行结果
            pool.submit(Runnable); // 返回一个Future对象，但Runnable的run方法无法返回结果
            pool.submit(Callable); // 返回一个Future对象，且Callable的call方法可以返回Object
            pool.invokeAny(Set<Callable<String>>); // 执行一系列的Callable，且只返回其中任意一个的结果
            pool.invokeAll(Set<Callable<String>>); // 执行一系列的Callable，且返回所有的Future结果
            
            // 关闭连接池
            pool.shutdown();
            pool.shutdownNow();
            ```

         5. ```java
            /**
             * ThreadPoolExecutor 线程池执行者
             * - corePoolSize
             * - maximumPoolSize
             */
            ExecutorService threadPoolExecutor = new ThreadPoolExecutor(
                // 池中的核心线程数，可空闲
                corePoolSize,
                // 最大线程数
                maxPoolSize,
                // 当线程数大于核心线程数时，多余的空闲线程可闲置的时长
                keepAliveTime,
                // 时长单位
                TimeUnit.MILLISECONDS,
                // 用于保持任务的队列
                new LinkedBlockingQueue<Runnable>()
            );
            
            /**
             * 有界的阻塞队列，内部使用数组实现，一旦创建则大小无法修改
             * 通过全局独占锁保证只有一个线程进行入队、出队操作
             */
            BlockingQueue queue = new ArrayBlockingQueue(int, boolean);
            bool = queue.offer(E); // 向队尾插入元素，返回入队是否成功
            queue.put(E); // 向队尾添加元素，并等待插入成功后返回
            E e = queue.poll(); // 从队首取出元素，移除元素，没有则等待
            E e = queue.take(); // 从队首取出元素，移除元素，没有则阻塞
            E e = queue.peek(); // 从队首获取元素，不移除元素
            int size = queue.size(); // 获取队列元素个数
            
            /**
             * 延时无界阻塞队列
             * 常用于：缓存设计（缓存的对象超过了空闲时间，需要从缓存中移除）、任务调度系统（准确把握任务的执行时间）
             * Delayed 扩展了 Comparable 接口，以延时的时间值为基准
             */
            BlockingQueue queue = new DelayQueue();
            
            /**
             * 以 Integer.MAX_VALUE 为界的链式结构，也可指定链表大小
             * 使用两个独占锁 putLock、takeLock 分别维护入队、出队的并发安全
             */
            BlockingQueue queue = new LinkedBlockingQueue();
            boolean bool = queue.offer(E);
            boolean bool = queue.offer(E, Long, TimeUnit);
            E e = queue.poll();
            E e = queue.poll(Long, TimeUnit);
            boolean bool = queue.put(E); // 与offer不同之处在于，当队列满了会一直等待其他线程调用 notFull.signal 才会唤醒
            E e = queue.take(); // 与poll不同之处在于，当队列为空会一直等待其他线程调用 notEmpty.signal 才会唤醒
            int size = queue.size(); // 获取原子变量
            boolean bool = queue.remove(E e); // 删除元素，返回删除是否成功
            
            /**
             * 带优先级的无界阻塞队列，通过实现 Comparable 接口实现元素排序
             * 只有 notEmpty，没有 notFull，内部使用一个二叉树最小堆算法来维护内部数组
             */
            BlockingQueue queue = new PriorityBlockingQueue();
            boolean bool = queue.offer(E); // 入队，因为无界，所以一直返回true；在扩容时释放锁，并使用 cas 控制当前线程扩容，保证扩容时可以出队，但无法入队
            
            
            // 单一元素的队列，插入元素时已存在和提取元素时不存在的情况，都将阻塞
            BlockingQueue queue = new SynchronousQueue();
            ```

         6. ```java
            /**
             * 非阻塞队列
             * - ConcurrentLinkedQueue
             * - ConcurrentHashMap
             * - ConcurrentSkipListMap
             */
            ```

   2. **java.util.concurrent.atomic（多线程原子操作工具类）**

      1. 对多线程的基本数据、数组中的基本数据、对象中的基本数据进行多线程操作
      2. **AtomicBoolean**：原子性布尔
      3. **AtomicInteger**：原子性整型
      4. **AtomicIntegerArray**：原子性整型数组
      5. **AtomicLong**：原子性长整型
      6. **AtomicLongArray**：原子性长整型数组

   3. **java.util.concurrent.lock（多线程的锁机制）**

      1. 包含 Lock（重入锁、公平锁等）、ReadWriteLock（读写锁）、Condition（等待变量）

   4. **悲观锁、乐观锁、CAS**：

      1. **悲观锁**：共享资源每次只给一个线程使用，其它线程阻塞，如 synchronized 和 ReentrantLock 等独占锁
      2. **乐观锁**：只有在更新才判断别人有没有更新这个数据，如 java.util.concurrent.atomic 包下的原子变量类
      3. **CAS**：Compare And Swap，基本的乐观锁

3. 多线程

   1. wait 与 sleep 的区别：wait 会释放锁（用于线程间交互），sleep 不会释放锁（用于暂停执行）
   2. synchronized 和 volatile 的作用
   3. 什么是线程池，作用是什么？
   4. 常用的线程池有哪些？：
      1. newSingleThreadExecutor：单线程线程池，所有任务按提交顺序执行
      2. newFixedThreadPool：固定大小线程池
      3. newCachedThreadPool：可缓存的线程池，大小依赖于JVM
      4. newScheduledThreadPool：无限大小的线程池，支持定时及周期性执行任务的需求
   5. 对线程池的理解（线程池的使用、好处、启动策略/执行流程）
      1. 降低资源消耗、提高响应速度、提高线程的可管理性
   6. 如何控制方法并发访问线程的个数？
      1. 使用 Semaphore（信号量） 控制
   7. 三个线程a,b,c并发运行，b,c需要a的数据，如何实现？
      1. 使用 Semaphore，初始值为0，当a 赋值完成后，向 semaphore release 两个信号量，且b,c一直通过 acquire() 等待获取
   8. 死锁，什么情况下发生，如何解决？
      1. 多个线程竞争同一个资源而造成的一种僵局，需要满足 **互斥条件**、**不剥夺条件**、**请求和保持条件**、**循环等待条件**
      2. 解决：加锁顺序、加锁时限
   9. 多线程间如何通信？
      1. 共享变量
      2. wait/notify 机制
   10. 线程的调度方法：
       1. wait()：线程阻塞，释放所持有的锁
       2. sleep()：进入睡眠状态，需要处理 InterruptedException 异常
       3. notify()：唤醒一个等待线程，并不能确定唤醒的哪个线程
       4. notifyAll()：唤醒所有等待线程，让它们竞争锁

4. 内部类

   1. 静态嵌套类
   2. 内部类



