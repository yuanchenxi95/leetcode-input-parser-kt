package utils

class FenwickTree(val n: Int) {
    val nums = mutableMapOf<Int, Long>()

    fun sum(rr: Int): Long {
        var r = rr
        var res = 0L
        while (r >= 0) {
            res += nums.getOrDefault(r, 0)
            r = (r and (r + 1)) - 1
        }
        return res
    }

    fun add(idx: Int, delta: Long) {
        var ii = idx
        while (ii < n) {
            nums[ii] = nums.getOrDefault(ii, 0) + delta
            ii = ii or (ii + 1)
        }
    }
}
