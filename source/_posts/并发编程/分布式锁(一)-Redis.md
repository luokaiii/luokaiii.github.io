---
title: 使用Redis实现分布式锁
date: 2019-03-11 18:06:03
categories: 
 - Java
---

# 使用Redis实现分布式锁

## DistributedLock

```java
/**
 * 顶级接口
 */
public interface DistributedLock {

    public static final long TIMEOUT_MILLIS = 30000;

    public static final int RETRY_TIMES = Integer.MAX_VALUE;

    public static final long SLEEP_MILLIS = 500;

    public boolean lock(String key);

    public boolean lock(String key,int retryTimes);

    public boolean lock(String key, int retryTimes, long sleepMillis);

    public boolean lock(String key, long expire);

    public boolean lock(String key, long expire, int retryTimes);

    public boolean lock(String key, long expire, int retryTimes, long sleepMillis);

    public boolean releaseLock(String key);
}
```

## AbstractDistributedLock

```java
/**
 * 抽象基本的方法，关键方法由子类去实现
 * 如果还有其他分布式锁的实现，直接继承该类，进行实现即可.
 */
public abstract class AbstractDistributedLock implements DistributedLock {

    @Override
    public boolean lock(String key) {
        return lock(key, TIMEOUT_MILLIS, RETRY_TIMES, SLEEP_MILLIS);
    }

    @Override
    public boolean lock(String key, int retryTimes) {
        return lock(key, TIMEOUT_MILLIS, retryTimes, SLEEP_MILLIS);
    }

    @Override
    public boolean lock(String key, int retryTimes, long sleepMillis) {
        return lock(key, TIMEOUT_MILLIS, retryTimes, sleepMillis);
    }

    @Override
    public boolean lock(String key, long expire) {
        return lock(key, expire, RETRY_TIMES, SLEEP_MILLIS);
    }

    @Override
    public boolean lock(String key, long expire, int retryTimes) {
        return lock(key, expire, retryTimes, SLEEP_MILLIS);
    }

}
```

## RedisDistributedLock

```java
/**
 * Redis 分布式锁实现
 */
public class RedisDistributedLock extends AbstractDistributedLock {
    private final Logger logger = LoggerFactory.getLogger(RedisDistributedLock.class);

    private RedisTemplate<Object, Object> redisTemplate;

    private ThreadLocal<String> lockFlag = new ThreadLocal<String>();

    private static final String UNLOCK_LUA;

    static {
        UNLOCK_LUA = "if redis.call(\"get\",KEYS[1]) == ARGV[1] " +
                "then " +
                "    return redis.call(\"del\",KEYS[1]) " +
                "else " +
                "    return 0 " +
                "end ";
    }

    public RedisDistributedLock(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean lock(String key, long expire, int retryTimes, long sleepMillis) {
        boolean result = setRedis(key, expire);
        // 如果获取锁失败，按照传入的重试次数进行重试
        while((!result) && retryTimes-- > 0){
            try {
                logger.debug("lock failed, retrying..." + retryTimes);
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                return false;
            }
            result = setRedis(key, expire);
        }
        return result;
    }

    private boolean setRedis(String key, long expire) {
        try {
            String result = redisTemplate.execute((RedisCallback<String>) connection -> {
                JedisCommands commands = (JedisCommands) connection.getNativeConnection();

                String uuid = UUID.randomUUID().toString();
                lockFlag.set(uuid);
                return commands.set(key, uuid, "NX", "PX", expire);
            });
            return !StringUtils.isEmpty(result);
        } catch (Exception e) {
            logger.error("set redis occured an exception", e);
        }
        return false;
    }

    @Override
    public boolean releaseLock(String key) {
        // 释放锁的时候，有可能因为持锁之后方法执行时间大于锁的有效期，此时有可能已经被另外一个线程持有锁，所以不能直接删除
        try {
            List<String> keys = new ArrayList<>();
            keys.add(key);
            List<String> args = new ArrayList<>();
            args.add(lockFlag.get());

            // 使用lua脚本删除redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
            // spring自带的执行脚本方法中，集群模式直接抛出不支持执行脚本的异常，所以只能拿到原redis的connection来执行脚本

            Long result = redisTemplate.execute((RedisCallback<Long>) connection -> {
                Object nativeConnection = connection.getNativeConnection();
                // 集群模式和单机模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
                // 集群模式
                if (nativeConnection instanceof JedisCluster) {
                    return (Long) ((JedisCluster) nativeConnection).eval(UNLOCK_LUA, keys, args);
                }

                // 单机模式
                else if (nativeConnection instanceof Jedis) {
                    return (Long) ((Jedis) nativeConnection).eval(UNLOCK_LUA, keys, args);
                }
                return 0L;
            });

            return result != null && result > 0;
        } catch (Exception e) {
            logger.error("release lock occured an exception", e);
        } finally {
            // 清除掉ThreadLocal中的数据，避免内存溢出
            lockFlag.remove();
        }
        return false;
    }
}

```

## DistributedLockAutoConfiguration

```java
/**
 * 装配分布式锁
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class DistributedLockAutoConfiguration {

    /**
     * 当RedisTemplate存在时才创建Bean
     */
    @Bean
//    @ConditionalOnBean(name = "redisTemplate")
    public DistributedLock redisDistributedLock(RedisTemplate<Object,Object> redisTemplate){
        return new RedisDistributedLock(redisTemplate);
    }
}
```

## RedisLock

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RedisLock {

    /**
     * 锁的资源，redis的Key
     */
    String value() default "default";

    /**
     * 持锁时间，单位毫秒
     */
    long keepMills() default 30000;

    /**
     * 当获取失败时的动作
     */
    LockFailAction action() default LockFailAction.CONTINUE;

    /**
     * 重试的间隔时间，设置GIVEUP则忽略此项
     */
    long sleepMills() default 200;

    /**
     * 重试的次数
     */
    int retryTimes() default 5;

    public enum LockFailAction{
        GIVEUP, // 放弃
        CONTINUE // 继续
    }

}
```

## DistributedLockAspectConfiguration

```java
/**
 * 定义切面，直接注入 DistributedLock 即可使用分布式锁.
 */
@Aspect
@Configuration
@ConditionalOnClass(DistributedLock.class)
@AutoConfigureAfter(DistributedLockAutoConfiguration.class)
public class DistributedLockAspectConfiguration {

    private final Logger logger = LoggerFactory.getLogger(DistributedLockAspectConfiguration.class);

    private final DistributedLock distributedLock;

    @Autowired
    public DistributedLockAspectConfiguration(DistributedLock distributedLock) {
        this.distributedLock = distributedLock;
    }

    @Pointcut("@annotation(com.backsupport.user.redis.RedisLock)")
    private void lockPoint() {
    }

    @Around("lockPoint()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        RedisLock redisLock = method.getAnnotation(RedisLock.class);
        String key = redisLock.value();
        if (key.contains("${") && key.contains("}")) {
            String substring = key.substring(2, key.length() - 1);
            Object[] args = pjp.getArgs();
            Parameter[] parameters = method.getParameters();
            if (args != null) {
                for (int i = 0; i < parameters.length; i++) {
                    if (substring.equals(parameters[i].getName())) {
                        key = (String) args[i];
                    }
                }
            }
        }
        int retryTimes = redisLock.action().equals(RedisLock.LockFailAction.CONTINUE) ? redisLock.retryTimes() : 0;
        boolean lock = distributedLock.lock(key, redisLock.keepMills(), retryTimes, redisLock.sleepMills());
        if (!lock) {
            logger.debug("get lock failed : " + key);
            return null;
        }

        //得到锁,执行方法，释放锁
        logger.debug("get lock success : " + key);
        try {
            return pjp.proceed();
        } catch (Exception e) {
            logger.error("execute locked method occured an exception", e);
        } finally {
            boolean releaseResult = distributedLock.releaseLock(key);
            logger.debug("release lock : " + key + (releaseResult ? " success" : " failed"));
        }
        return null;
    }
}

```

## 测试

```java
@Slf4j // lombok的注解，也可以自己声明一个
@SpringBootApplication
public class Run {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RedisLock(value = "${id}")
    @GetMapping("/{id}")
    public void test(@PathVariable String id) {
        try {
            log.info("获取到资源")
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        SpringApplication.run(Run.class, args);
    }
}
```

> [参考](https://gitee.com/itopener/springboot)