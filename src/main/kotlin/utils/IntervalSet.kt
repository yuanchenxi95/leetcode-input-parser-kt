package utils

import java.util.*

class IntervalSet {
    val treeMap = TreeMap<Int, Int>()

    fun add(left: Int, right: Int): Int {
        val previousSum = querySum(left, right)
        if (previousSum == rangeSum(left, right)) {
            return 0
        }
        treeMap[left] = right
        merge(left, right)
        treeMap.lowerEntry(left)?.let {
            merge(it.key, it.value)
        }

        return rangeSum(left, right) - previousSum
    }

    private fun merge(left: Int, value: Int) {
        var newValue = value
        while (true) {
            val entry = treeMap.floorEntry(newValue)
            if (entry.key == left) {
                break
            }
            treeMap.remove(entry.key)
            newValue = maxOf(entry.value, newValue)
            treeMap[left] = newValue
        }
    }

    fun queryRange(left: Int, right: Int): Boolean {
        val curSum = querySum(left, right)
        return curSum == rangeSum(left, right)
    }

    fun querySum(left: Int, right: Int): Int {
        var res = 0
        val start = treeMap.floorKey(left) ?: left
        val end = treeMap.floorKey(right) ?: right

        for (entry in treeMap.subMap(start, true, end, true)) {
            val cur = rangeSum(maxOf(left, entry.key), minOf(right, entry.value))
            if (cur <= 0) {
                continue
            }
            res += cur
        }
        return res
    }

    fun rangeSum(left: Int, right: Int): Int {
        return right + 1 - left
    }

}
