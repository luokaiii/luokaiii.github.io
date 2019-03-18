/**
 *  加一
 *
 * 给定一个由整数组成的非空数组所表示的非负整数，在该数的基础上加一。
 *
 * 最高位数字存放在数组的首位， 数组中每个元素只存储一个数字。
 *
 * 你可以假设除了整数 0 之外，这个整数不会以零开头。
 *
 * 示例 1:
 *
 * 输入: [1,2,3]
 * 输出: [1,2,4]
 * 解释: 输入数组表示数字 123。
 * 示例 2:
 *
 * 输入: [4,3,2,1]
 * 输出: [4,3,2,2]
 * 解释: 输入数组表示数字 4321。
 */
public class Eight {

    public static void main(String[] args) {
        int[] nums1 = {9};
        int[] intersect = plusOne(nums1);
        for (int i : intersect) {
            System.out.print(i);
        }
    }

    /**
     * 解题思路：
     *  只有当所有数字都是9时，才会产生进位；即第[0]位在运算后为0
     *  如果任一位不为0，则加一返回
     */
    public static int[] plusOne(int[] digits) {
        if(digits == null || digits.length == 0) return digits;

        for (int i = digits.length - 1; i >= 0; i--) {
            if(digits[i] < 9){
                digits[i] = digits[i] + 1;
                return digits;
            }
            digits[i] = 0;
        }
        if(digits[0] == 0){
            digits = new int[digits.length + 1];
            digits[0] = 1;
        }
        return digits;
    }
}
