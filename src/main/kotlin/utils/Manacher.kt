package utils

class Manacher(private val content: String) {
    private val evenPal = manacherEven(content)
    private val oddPal = manacherOdd(content)

    fun getMaxPalOdd(idx: Int): Int {
        return oddPal[idx]
    }

    fun getMaxPalEven(idx: Int): Int {
        val res = evenPal[idx * 2 + 2]
        return res / 2
    }

    private fun manacherEven(ss: String): List<Int> {
        val s2 = StringBuilder()
        for (ch in ss) {
            s2.append('#')
            s2.append(ch)
        }
        s2.append('#')
        return manacherOdd(s2.toString())
    }

    private fun manacherOdd(ss: String): List<Int> {
        val nn = ss.length
        val s2 = "!$ss*"
        val pal = IntArray(nn + 2)
        var left = 1
        var right = 1
        for (ii in 1..nn) {
            pal[ii] = maxOf(
                0,
                minOf(right - ii, pal[left + (right - ii)])
            )
            while (s2[ii - pal[ii]] == s2[ii + pal[ii]]) {
                pal[ii] += 1
            }
            if (ii + pal[ii] > right) {
                left = ii - pal[ii]
                right = ii + pal[ii]
            }
        }
        return pal.slice(1..nn)
    }

}
