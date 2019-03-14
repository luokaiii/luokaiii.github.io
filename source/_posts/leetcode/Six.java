/**
 * 只出现一次的数字
 * 
 * <p>
 * 给定一个非空整数数组，除了某个元素只出现一次以外，其余每个元素均出现两次。找出那个只出现了一次的元素。
 * <p>
 * 说明：
 * 你的算法应该具有线性时间复杂度。 你可以不使用额外空间来实现吗？
 * <p>
 * 示例 1:
 * <p>
 * 输入: [2,2,1]
 * 输出: 1
 * 示例 2:
 * <p>
 * 输入: [4,1,2,1,2]
 * 输出: 4
 */
public class Six {

    public static void main(String[] args) {
        int[] nums = {2, 2, 1};
        int[] nums2 = {4, 1, 2, 1, 2};
        System.out.println(singleNumber(nums));
        System.out.println(singleNumber(nums2));
    }

    /**
     * 使用异或的方式，取出唯一不同的那个值
     * eg : 20 ^ 18 ^ 3
     *     = 10100 ^ 10010 ^ 00011
     *     = 00101
     *     = 5
     * 即 : 20 ^ 20 ^ 3
     *     = 10100 ^ 10100 ^ 00011
     *     = 00011
     *     = 3
     * 可以得出： [a,b,a,c,b]
     *      = a ^ a ^ b ^ b ^ c
     *      = c
     */
    public static int singleNumber(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        if (nums.length == 1) return nums[0];

        int result = 0;
        for (int num : nums) {
            result = result ^ num;
        }
        return result;
    }
}
