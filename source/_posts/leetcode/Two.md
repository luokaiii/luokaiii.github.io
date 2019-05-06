```java
/**
 * 数组(二)：买卖股票的最佳时机 II
 *
 * 给定一个数组，它的第 i 个元素是一支给定股票第 i 天的价格。
 *
 * 设计一个算法来计算你所能获取的最大利润。你可以尽可能地完成更多的交易（多次买卖一支股票）。
 *
 * 注意：你不能同时参与多笔交易（你必须在再次购买前出售掉之前的股票）。
 *
 * 示例 1:
 *
 * 输入: [7,1,5,3,6,4]
 * 输出: 7
 * 解释: 在第 2 天（股票价格 = 1）的时候买入，在第 3 天（股票价格 = 5）的时候卖出, 这笔交易所能获得利润 = 5-1 = 4 。
 *      随后，在第 4 天（股票价格 = 3）的时候买入，在第 5 天（股票价格 = 6）的时候卖出, 这笔交易所能获得利润 = 6-3 = 3 。
 * 示例 2:
 *
 * 输入: [1,2,3,4,5]
 * 输出: 4
 * 解释: 在第 1 天（股票价格 = 1）的时候买入，在第 5 天 （股票价格 = 5）的时候卖出, 这笔交易所能获得利润 = 5-1 = 4 。
 *      注意你不能在第 1 天和第 2 天接连购买股票，之后再将它们卖出。
 *      因为这样属于同时参与了多笔交易，你必须在再次购买前出售掉之前的股票。
 * 示例 3:
 *
 * 输入: [7,6,4,3,1]
 * 输出: 0
 * 解释: 在这种情况下, 没有交易完成, 所以最大利润为 0。
 */
public class Two {

    public static void main(String[] args) {
        int[] a = {7, 1, 5, 3, 6, 4};
        int[] b = {1, 2, 3, 4, 5};
        int[] c = {7, 6, 4, 3, 1};
        System.out.println(greed(a));
        System.out.println(greed(b));
        System.out.println(greed(c));
    }

    /**
     * 贪婪算法：不从整体最优上考虑，只考虑当前最优。使用前需要考虑是否满足无后效性
     * 如我们花钱时的找零， 100 - 3 = 50 + 20 * 2 + 5 + 1 * 2
     *  在本题中，表现为：
     *   如果第二天大于第一天收益，则算作收益
     *
     */
    private static int greed(int[] prices) {
        int result = 0;
        if(prices.length <= 0) return result;
        for (int i = 0; i < prices.length -1; i++) {
            if(prices[i+1] > prices[i]){
                result += prices[i+1] - prices[i];
            }
        }

        return result;
    }
}
```

下面是 `LeetCode` 上执行用时最少的代码：

```java
class Solution {
    public int maxProfit(int[] prices) {
        int i = 0;
        int valley = 0;
        int peak = 0;
        int maxprofit = 0;
        while (i < prices.length - 1) {
            while (i < prices.length - 1 && prices[i] >= prices[i + 1])
                i++;
            valley = prices[i];
            while (i < prices.length - 1 && prices[i] <= prices[i + 1])
                i++;
            peak = prices[i];
            maxprofit += peak - valley;
        }
        return maxprofit;
    }
} 
```
