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
}
