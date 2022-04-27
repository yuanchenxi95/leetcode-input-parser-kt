package utils

class UnionSet {
  val unionSet = mutableMapOf<Int, Int>()
  val length = mutableMapOf<Int, Int>()

  fun find(ii: Int): Int {
    val target = unionSet[ii]?.takeIf { it != ii } ?: return ii
    val jj = find(target)
    unionSet[ii] = jj
    return jj
  }

  fun union(ii: Int, jj: Int): Int {
    val left = find(ii)
    val right = find(jj)
    val newLength = getLength(right) + getLength(left)
    length[right] = newLength
    unionSet[left] = right
    return newLength
  }

  fun getLength(ii: Int): Int {
    return length.getOrDefault(ii, 1)
  }
}
