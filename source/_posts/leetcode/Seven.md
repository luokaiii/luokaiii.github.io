```java
/**
 * 两个数组的交集 II
 *
 * 给定两个数组，编写一个函数来计算它们的交集。
 *
 * 示例 1:
 *
 * 输入: nums1 = [1,2,2,1], nums2 = [2,2]
 * 输出: [2,2]
 * 示例 2:
 *
 * 输入: nums1 = [4,9,5], nums2 = [9,4,9,8,4]
 * 输出: [4,9]
 * 说明：
 *
 * 输出结果中每个元素出现的次数，应与元素在两个数组中出现的次数一致。
 * 我们可以不考虑输出结果的顺序。
 * 进阶:
 *
 * 如果给定的数组已经排好序呢？你将如何优化你的算法？
 * 如果 nums1 的大小比 nums2 小很多，哪种方法更优？
 * 如果 nums2 的元素存储在磁盘上，磁盘内存是有限的，并且你不能一次加载所有的元素到内存中，你该怎么办？
 */
public class Seven {

    public static void main(String[] args) {
        int[] nums1 = {4,5,9};
        int[] nums2 = {4,5,8,9,4};
        long start = System.currentTimeMillis();
        int[] intersect = intersect(nums1, nums2);
        for (int i : intersect) {
            System.out.print(i);
        }
        System.out.println("方法共执行了："+(System.currentTimeMillis() - start));
    }

    /**
     * 使用集合的特性
     */
    public static int[] intersect(int[] nums1, int[] nums2) {
        if(nums1 == null || nums2 == null || nums1.length == 0 || nums2.length == 0) return new int[0];

        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<Integer> integers = new ArrayList<>();

        for (int i : nums1) {
            list.add(i);
        }

        for (int i : nums2) {
            int i1 = list.indexOf(i);
            if(i1 != -1){
                list.remove(i1);
                integers.add(i);
            }
        }

        int[] result = new int[integers.size()];
        int point = 0;
        for (Integer integer : integers) {
            result[point++] = integer;
        }

        return result;
    }

    /**
     * 效率高于第一种方法
     */
    public static int[] intersect2(int[] nums1, int[] nums2) {
        // 先将数组排序，数组将按照从小到大的顺序排列
        Arrays.sort(nums1);
        Arrays.sort(nums2);
        // 定义下标指针
        int i = 0, j = 0, k = 0;
        // 创建一个数组，长度为小数组的长度
        int[] result = new int[nums1.length > nums2.length ? nums2.length : nums1.length];
        // 两个数组同时遍历，比较 i与j所在的值，只会有三种情况：大、小、等
        while (i < nums1.length && j < nums2.length) {
            if (nums1[i] == nums2[j]) { // 等于则保存起来，i,j 向前+1
                result[k++] = nums1[i];
                i++;
                j++;
            }else if(nums1[i] < nums2[j]){ // i<j i向前+1
                i++;
            }else if(nums1[i] > nums2[j]){
                j++;
            }
        }
        return result;
    }
}

```
