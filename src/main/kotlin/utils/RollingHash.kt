package utils

class RollingHash(val data: IntArray, val mods: LongArray = longArrayOf(MD3)) {

    val hashes = mutableListOf<LongArray>()
    val powerCaches = mutableListOf<LongArray>()
    val maxValue: Long
    val fastPowCache = mutableMapOf<Triple<Long, Long, Int>, Long>()

    init {
        maxValue = 1L + data.maxOrNull()!!
        if (mods.isEmpty()) {
            throw IllegalArgumentException("Number of mods is 0")
        }

        for (md in mods) {
            val curHashes = LongArray(data.size + 1)
            val powerCache = LongArray(data.size + 1)
            hashes.add(curHashes)
            powerCaches.add(powerCache)
            powerCache[0] = 1
            for (jj in data.indices) {
                val num = data[jj]
                val prevHash = curHashes[jj]
                val curHash = (prevHash * maxValue + num) % md
                curHashes[jj + 1] = curHash
                powerCache[jj + 1] = powerCache[jj] * maxValue % md
            }
        }
    }

    fun equalsRange(leftRange: IntRange, rightRange: IntRange): Boolean {
        val leftHashes = getHashes(leftRange)
        val rightHashes = getHashes(rightRange)
        return leftHashes.contentEquals(rightHashes)
    }

    fun getHashes(range: IntRange): LongArray {
        val resHashes = LongArray(mods.size)
        for (ii in mods.indices) {
            val curHashes = hashes[ii]
            val md = mods[ii]
            val lastHash = curHashes[range.last + 1]
//            val firstHash = (fastPow(maxValue, md, range.last - range.first + 1) * curHashes[range.first]) % md
            val firstHash = getPow(ii, range) * curHashes[range.first] % md

            val hashDiff = (firstHash + md - lastHash) % md
            if (hashDiff < 0) {
                throw AssertionError("hash diff is negative: $hashDiff")
            }
            resHashes[ii] = hashDiff
        }
        return resHashes
    }

    private fun getPow(mdIdx: Int, range: IntRange): Long {
        return powerCaches[mdIdx][range.last - range.first + 1]
    }


    private fun fastPow(num: Long, md: Long, toThePower: Int): Long {
        if (num == 0L) {
            throw AssertionError("num is zero")
        }
        if (toThePower == 0) {
            return 1
        }
        val key = Triple(num, md, toThePower)
        val seen = fastPowCache[key]
        if (seen != null) {
            return seen
        }
        val res = if (toThePower and 1 == 1) {
            (fastPow(num, md, toThePower - 1) * num) % md
        } else {
            val nextNum = num * num
            if (nextNum < 0) {
                throw AssertionError("$num is less than zero")
            }
            fastPow(nextNum % md, md, toThePower / 2)
        }

        fastPowCache[key] = res
        return res
    }

    companion object {
        val MD1 = 396299083L
        val MD2 = 214679107L
        val MD3 = 1_000_000_007L

    }
}
