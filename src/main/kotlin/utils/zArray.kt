package utils

fun zArray(value: String): IntArray {
    val nn = value.length
    val res = IntArray(nn)

    var ll = 0
    var rr = 0
    var kk = 0

    for (ii in 1 until nn) {
        if (ii > rr) {
            ll = ii
            rr = ii

            while (rr < nn && value[rr - ll] == value[rr]) {
                rr += 1
            }

            res[ii] = rr - ll
            rr -= 1
        } else {
            kk = ii - ll

            if (res[kk] < rr - ii + 1) {
                res[ii] = res[kk]
            } else {
                ll = ii
                while (rr < nn && value[rr - ll] == value[rr]) {
                    rr += 1
                }
                res[ii] = rr - ll
                rr -= 1
            }
        }

    }

    return res
}
