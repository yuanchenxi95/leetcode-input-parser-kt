class Solution {
    fun twoSum(nums: IntArray, target: Int): IntArray {
        val mem = mutableMapOf<Int, Int>()
        nums.forEachIndexed { index, i ->
            val prevIdx = mem[target - i]
            if (prevIdx != null) {
                return intArrayOf(prevIdx, index)
            }
            mem[i]=index
        }
        return intArrayOf()
    }
}