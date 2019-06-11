```java
/**
 * 移动零
 * <p>
 * 给定一个数组 nums，编写一个函数将所有 0 移动到数组的末尾，同时保持非零元素的相对顺序。
 * <p>
 * 示例:
 * <p>
 * 输入: [0,1,0,3,12]
 * 输出: [1,3,12,0,0]
 * 说明:
 * <p>
 * 必须在原数组上操作，不能拷贝额外的数组。
 * 尽量减少操作次数。
 */
public class Nine {

    public static void main(String[] args) {
        int[] nums1 = {0, 1, 0, 3, 12};
        moveZeroes1(nums1);
        for (int i : nums1) {
            System.out.print(i);
        }
    }

    /**
     * 解题思路：
     *  直接遍历数组，将不为0的元素依次排序，剩余的元素标记为0即可。
     *  这种方法的执行次数较少，不关注移动元素的过程，只关注最终结果
     */
    public static void moveZeroes(int[] nums) {
        if(nums == null || nums.length == 0) return;

        int index = 0;
        for (int i = 0; i < nums.length; i++)
            if (nums[i] != 0) {
                nums[index++] = nums[i];
            }
        for (int i = index; i < nums.length; i++) {
            nums[i] = 0;
        }
    }

    /**
     * 解题思路：
     *  从后向前遍历，记录出现0前的位数，将后几位依次前移
     */
    public static void moveZeroes1(int[] nums){
        if(nums == null || nums.length == 0) return;
        int count = 0;
        int size  = 1;
        for (int i = nums.length - 1; i >= 0 ; i--) {
            if(nums[i] == 0 && count > 0){
                for (int j = nums.length - count; j < nums.length; j++) {
                    nums[j - 1] = nums[j];
                }
                nums[nums.length - size++] = 0;
                count ++;
            }else{
                count ++ ;
            }
        }
    }

}

```
