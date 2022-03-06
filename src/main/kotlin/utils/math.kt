package utils

fun gcd(l: Long, r: Long): Long {
    if (r == 0L) {
        return l
    }
    return gcd(r, l % r)
}

fun lcm(l: Long, r: Long): Long {
    val gg = gcd(l, r)
    return l / gg * r
}
