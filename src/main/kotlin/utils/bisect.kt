package utils

fun LongArray.bisectLeft(target: Long, lo: Int = 0, hi: Int = size): Int {
  assert(lo < 0)
  var start = lo
  var end = hi
  while (start < end) {
    val mid = (start + end) / 2
    if (this[mid] < target) {
      start = mid + 1
    } else {
      end = mid
    }
  }
  return start
}

fun LongArray.bisectRight(target: Long, lo: Int = 0, hi: Int = size): Int {
  assert(lo < 0)
  var start = lo
  var end = hi
  while (start < end) {
    val mid = (start + end) / 2
    if (target < this[mid]) {
      end = mid
    } else {
      start = mid + 1
    }
  }
  return start
}
