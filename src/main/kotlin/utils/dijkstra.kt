package utils

import java.util.PriorityQueue

fun buildGraph(
  edges: Array<IntArray>, reversed: Boolean = false, bidirectional: Boolean = false,
): Map<Int, Map<Int, Long>> {
  val res = mutableMapOf<Int, MutableMap<Int, Long>>()
  for (edge in edges) {
    val left = edge[0]
    val right = edge[1]
    val weight = edge[2].toLong()

    fun addEdge(from: Int, to: Int) {
      val neighbors = res.getOrPut(to) { mutableMapOf() }
      val storedWeight = neighbors.getOrDefault(from, Long.MAX_VALUE)
      neighbors[from] = minOf(storedWeight, weight)
    }

    if (!reversed || bidirectional) {
      addEdge(left, right)
    }
    if (reversed || bidirectional) {
      addEdge(right, left)
    }
  }
  return res
}

fun dijkstra(graph: Map<Int, Map<Int, Long>>, start: Int): Map<Int, Long> {
  val pq = PriorityQueue<Pair<Int, Long>>(compareBy { it.second })
  val visited = mutableSetOf<Int>()
  val res = mutableMapOf<Int, Long>()
  pq.add(Pair(start, 0))
  while (pq.isNotEmpty()) {
    val (idx, weight) = pq.poll()
    if (idx in visited) {
      continue
    }
    visited.add(idx)
    res[idx] = weight
    for ((neighbor, w) in graph.getOrDefault(idx, mapOf())) {
      pq.add(Pair(neighbor, weight + w))
    }
  }
  return res
}
