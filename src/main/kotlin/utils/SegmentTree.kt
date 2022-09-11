package utils

/**
Example Usage:
Question: https://leetcode.com/problems/longest-increasing-subsequence-ii
```kotlin
class Solution {
    fun lengthOfLIS(nums: IntArray, k: Int): Int {
        val nn = nums.maxOf { it } + 1
        val sgt = SegmentTree(nn)
        for (num in nums) {
            val prev = sgt.query(maxOf(0, num - k), num, ::maxOf, 0)
            sgt.update(num, prev + 1, ::maxOf)
        }
        return sgt.tree.maxOf { it }
    }
}
```
*/

class SegmentTree(val nn: Int) {
    val tree = IntArray(nn * 4)

    fun query(left: Int, right: Int, reduce: (Int, Int) -> Int, init: Int): Int {
        var ll = left + nn
        var rr = right + nn
        var res = init
        while (ll < rr) {
            if (ll and 1 == 1) {
                res = reduce(res, tree[ll])
                ll += 1
            }
            if (rr and 1 == 1) {
                rr -= 1
                res = reduce(res, tree[rr])
            }
            ll = ll shl 1
            rr = rr shl 1
        }
        return res
    }

    fun update(idx: Int, value: Int, reduce: (Int, Int) -> Int) {
        var ii = idx
        tree[ii] = value
        while (ii > 1) {
            ii = ii shr 1
            tree[ii] = reduce(tree[ii * 2], tree[ii * 2 + 1])
        }
    }
}
