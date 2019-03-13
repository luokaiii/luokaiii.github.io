/**
 * 阿里巴巴 Java 开发手册：
 *  不要在 foreach 循环中进行元素的 remove/add 操作。 remove 元素请使用 Iterator 方式，如果并发操作，需要对 Iterator 对象加锁。
 */
public class Three {

    public static void main(String[] args) {
        List<String> list = new ArrayList<String>(){{
            add("aa");
            add("bb");
            add("cc");
        }};

//        System.out.println(filter(list)); // false
        System.out.println(filter2(list));
        System.out.println(filter3(list));
    }

    /**
     * fail-fast 快速错误机制所检查出来的错误
     *  增强 for 循环其实相当于做了一次 iterator，且在执行 iterator() 方法时，就已经确定了循环的次数
     *  但是 remove/add 操作，修改了集合的个数，导致集合个数与循环次数不匹配，进而抛出异常
     */
    private static List<String> filter(List<String> input){
        for (String s : input) {
            if(s.equals("aa")){ // throw Exception : java.util.ConcurrentModificationException
                input.remove(s);
            }
        }
        return input;
    }

    private static List<String> filter2(List<String> input){
        Iterator<String> iterator = input.iterator();
        while(iterator.hasNext()){
            if(iterator.next().equals("aa")){
                iterator.remove(); // 直接使用 Iterator 的 remove() 方法，能够直接修改循环次数，所以不会发生异常
            }
        }
        return input;
    }

    private static List<String> filter3(List<String> input){
        input.removeIf(val -> val.equals("aa"));
        return input;
    }

    /**
     * 还有一种解决方式，就是在 foreach 循环中，使用break跳出循环
     * 即当满足条件后，不执行下一个 next() 方法，也就不会触发异常
     */
}
