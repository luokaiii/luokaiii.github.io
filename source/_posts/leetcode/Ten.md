```java
/**
 * 两数之和
 * <p>
 * 给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那 两个 整数，并返回他们的数组下标。
 * <p>
 * 你可以假设每种输入只会对应一个答案。但是，你不能重复利用这个数组中同样的元素。
 * <p>
 * 示例:
 * <p>
 * 给定 nums = [2, 7, 11, 15], target = 9
 * <p>
 * 因为 nums[0] + nums[1] = 2 + 7 = 9
 * 所以返回 [0, 1]
 */
public class Ten {

    public static void main(String[] args) {
        int[] nums = {2, 7, 11, 15};
        int[] ints = twoSum1(nums, 9);
        for (int anInt : ints) {
            System.out.print(anInt);
        }
    }

    /**
     * 解题思路：
     * 遍历两遍数组，比较i、j之和是否等于target
     */
    private static int[] twoSum(int[] nums, int target) {
        if (nums == null || nums.length < 2) return new int[]{-1, -1};
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1};
    }

    /**
     * 解题思路：
     *  遍历数组，保存当前的值与下标(以值为key，下标为value)
     *  计算target与当前值的差，如果差在map中，则返回map中差的下标以及当前的下标
     */
    private static int[] twoSum1(int[] nums, int target) {
        int consult;
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            consult = target - nums[i];
            if (map.containsKey(consult)) {
                return new int[]{map.get(consult), i};
            }
            map.put(nums[i], i);
        }
        return null;
    }
}
```
