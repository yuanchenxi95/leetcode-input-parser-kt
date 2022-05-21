package utils

typealias Point = Pair<Int, Int>

fun bfs(
    mm: Int,
    nn: Int,
    getCost: (cur: Point, next: Point) -> Int,
    reduce: (acc: Int, cur: Int) -> Int,
    start: Point = Pair(0, 0),
    target: Point = Pair(nn - 1, mm - 1),
    next: List<Point> = listOf(
        Pair(0, 1),
        Pair(1, 0),
        Pair(-1, 0),
        Pair(0, -1)
    ),
    targetWeight: Int = 1
): Map<Point, Int> {
    fun getNeighbors(cur: Point): List<Pair<Int,Int>> {
        return next.map {
            Pair(it.first + cur.first, it.second + cur.second)
        }.filter {
            it.first in 0 until nn && it.second in 0 until mm
        }
    }

    val res = mutableMapOf<Point,Int>()
    res[target] = targetWeight

    fun helper(cur: Point): Int {
        res[cur]?.let {
            return it
        }
        var curCost = 0
        res[cur] = curCost

        for (neighbor in getNeighbors(cur)) {
            val pathWeight = getCost(cur, neighbor)
            if (pathWeight < 0) {
                continue
            }
            val prevValue = res.getOrDefault(neighbor, helper(neighbor))
            curCost = reduce(curCost, prevValue)
        }
        res[cur] = curCost
        return curCost
    }

    helper(start)
    return res
}

fun genGetValue(matrix: Array<IntArray>): (Point) -> Int {
    return {it ->
        matrix[it.second][it.first]
    }
}
