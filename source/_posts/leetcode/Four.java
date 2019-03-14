/**
 * 旋转数组
 * 
 * 给定一个数组，将数组中的元素向右移动 k 个位置，其中 k 是非负数。
 * <p>
 * 示例 1:
 * <p>
 * 输入: [1,2,3,4,5,6,7] 和 k = 3
 * 输出: [5,6,7,1,2,3,4]
 * 解释:
 * 向右旋转 1 步: [7,1,2,3,4,5,6]
 * 向右旋转 2 步: [6,7,1,2,3,4,5]
 * 向右旋转 3 步: [5,6,7,1,2,3,4]
 * 示例 2:
 * <p>
 * 输入: [-1,-100,3,99] 和 k = 2
 * 输出: [3,99,-1,-100]
 * 解释:
 * 向右旋转 1 步: [99,-1,-100,3]
 * 向右旋转 2 步: [3,99,-1,-100]
 * 说明:
 * <p>
 * 尽可能想出更多的解决方案，至少有三种不同的方法可以解决这个问题。
 * 要求使用空间复杂度为 O(1) 的原地算法。
 */
public class Four {

    public static void main(String[] args) {
        int[] nums = {1, 2};

        rotate(nums, 3);

        for (int num : nums) {
            System.out.print(num);
        }
        System.out.println();
    }

    /**
     * 1. 倒置所有的元素
     * 2. 倒置 前k个元素
     * 3. 倒置 nums.length - k 个元素
     */
    private static void rotate(int[] nums, int k) {
        if(nums == null || nums.length < 2) return; // 如果不能翻转，直接返回

        if( k > nums.length) k %= nums.length; // 右移次数大于所有元素个数，直接取余即可

        reverse(nums, 0, nums.length - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, nums.length - 1);
    }

    private static void reverse(int[] nums, int begin, int end) {
        while (begin <= end) {
            int temp = nums[begin];
            nums[begin] = nums[end];
            nums[end] = temp;
            begin++;
            end--;
        }
    }
}