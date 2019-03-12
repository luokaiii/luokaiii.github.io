/**
 * 数组(一)：从排序数组中删除重复项
 * 给定一个排序数组，你需要在原地删除重复出现的元素，使得每个元素只出现一次，返回移除后数组的新长度。
 *
 * 不要使用额外的数组空间，你必须在 `原地修改` 输入数组并在使用 `O(1)额外空间` 的条件下完成。
 */
public class One {

    public static void main(String[] args) {
        int[] a = {0, 1, 1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 4};
        int[] b = {1, 1, 2, 2, 2, 3, 3, 3, 3};
        System.out.println(distinct(a));
        System.out.println(distinct(b));
    }

    /**
     * 使用快慢指针算法：
     *  i 为快指针，不停向前增长，遍历数组
     *  j 为慢指针，当 nums[i] != nums[j] 时，j向前加一，且将 nums[i] 赋值给 nums[j]
     * eg:
     *  测试参数：[1,1,2]
     *  第一次循环：i=0,j=0 ; nums[i] == nums[j] => [1,1,2]
     *  第二次循环：i=1,j=0 ; nums[j] == nums[i] => [1,1,2]
     *  第三次循环：i=2,j=0 ; nums[j] != nums[i] => [1,2,1] (此时nums[0]与nums[1]已经进行了交换)
     */
    private static int distinct(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        int i = 0;
        int j = 0;

        for (; i < nums.length; i++) {
            if (nums[j] != nums[i]) {
                nums[++j] = nums[i];
            }
        }
        return j + 1;
    }
}