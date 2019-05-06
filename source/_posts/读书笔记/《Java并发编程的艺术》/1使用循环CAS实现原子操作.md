```java
/**
 * 使用Java实现原子操作：
 *  通过 锁和循环CAS 的方式实现原子操作。
 */
public class ConcurrenceTest {

    private AtomicInteger atomicInteger = new AtomicInteger(0);
    private int i = 0;

    public static void main(String[] args) {
        ConcurrenceTest concurrence = new ConcurrenceTest();
        // 使用600个线程
        ArrayList<Thread> threads = new ArrayList<>(600);

        long start = System.currentTimeMillis();
        // 创建100个线程，每个线程进行10000次加操作
        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    concurrence.count();
                    concurrence.safeCount();
                }
            });
            threads.add(thread);
        }

        // 启动线程
        for (Thread thread : threads) {
            thread.start();
        }

        // 等待所有线程执行完成
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("非线程安全的：" + concurrence.i);
        System.out.println("线程安全的：" + concurrence.atomicInteger);
        System.out.println("程序运行时间：" + (System.currentTimeMillis() - start));
    }

    // 使用 CAS(Compare and swap) 实现线程安全的计数器
    private void safeCount() {
        // 自旋：不停的比较原值是否变更，如果未变动，则+1重新赋值，否则一直进行比较。
        //  属于轻量级锁的一种，但是得不到锁竞争的锁，会一直自旋并消耗CPU
        for (; ; ) {
            int i = atomicInteger.get();
            boolean b = atomicInteger.compareAndSet(i, ++i);
            if (b) break;
        }
    }

    // 非线程安全的计数器
    private void count() {
        i++;
    }
}

```

