package com.nawilny.aoc2023.day23

import com.nawilny.aoc2023.commons.Input
import java.util.*

fun main() {
    val input = Input.readFileLinesNormalized("day23", "input.txt")
    val hikingMap = HikingMap(input)

    println(findLongestPath(hikingMap.start, emptySet(), hikingMap))

    val nodes = convertToNodes(hikingMap)
    println(findLongestPathUsingNodes(hikingMap.start, hikingMap.end, emptySet(), 0, nodes))
}

private fun convertToNodes(hikingMap: HikingMap): Map<Point, Node> {
    val nodes = mutableMapOf<Point, Node>()
    nodes[hikingMap.end] = Node(hikingMap.end, emptyList())
    val pointsToAnalyse: Queue<Point> = LinkedList()
    pointsToAnalyse.add(hikingMap.start)

    while (pointsToAnalyse.isNotEmpty()) {
        val p = pointsToAnalyse.remove()
        val crossings = findFirstCrossings(p, hikingMap)
        nodes[p] = Node(p, crossings)
        crossings.forEach {
            if (!nodes.contains(it.first)) {
                pointsToAnalyse.add(it.first)
            }
        }
    }
    return nodes
}

private fun findFirstCrossings(from: Point, hikingMap: HikingMap): List<Pair<Point, Int>> {
    val next = hikingMap.getNextMoves(from, true)
    return next.map { p ->
        val currentPath = mutableSetOf(p, from)
        var currentPoint = p
        var pointNext = hikingMap.getNextMoves(currentPoint, true).filter { !currentPath.contains(it) }
        while (pointNext.size == 1) {
            currentPoint = pointNext.first()
            currentPath.add(currentPoint)
            pointNext = hikingMap.getNextMoves(currentPoint, true).filter { !currentPath.contains(it) }
        }
        Pair(currentPoint, currentPath.size - 1)
    }
}

private data class Node(val point: Point, val next: List<Pair<Point, Int>>)

private fun findLongestPath(from: Point, path: Set<Point>, hikingMap: HikingMap): Int {
    val currentPath = path.toMutableSet()
    currentPath.add(from)
    var currentPoint = from
    while (true) {
        val next = hikingMap.getNextMoves(currentPoint, false).filter { !currentPath.contains(it) }
        if (next.isEmpty()) {
            return 0
        } else if (next.contains(hikingMap.end)) {
            return currentPath.size
        } else if (next.size == 1) {
            currentPoint = next.first()
            currentPath.add(currentPoint)
        } else {
            return next.maxOf { findLongestPath(it, currentPath, hikingMap) }
        }
    }
}

private fun findLongestPathUsingNodes(
    from: Point, to: Point, path: Set<Point>, currentDistance: Int, nodes: Map<Point, Node>
): Int {
    if (from == to) {
        return currentDistance
    }
    val next = nodes[from]!!.next.filter { !path.contains(it.first) }
    return if (next.isEmpty()) {
        0
    } else {
        next.maxOf { findLongestPathUsingNodes(it.first, to, path.plus(it.first), currentDistance + it.second, nodes) }
    }
}

private data class Point(val x: Int, val y: Int)

private data class HikingMap(val input: List<String>) {
    val start = Point(input.first().indexOf('.'), 0)
    val end = Point(input.last().indexOf('.'), input.size - 1)

    fun getNextMoves(point: Point, ignoreSlopes: Boolean): List<Point> {
        return if (ignoreSlopes) {
            getNextIgnoringSlopes(point)
        } else {
            when (input[point.y][point.x]) {
                '>' -> listOf(Point(point.x + 1, point.y))
                '<' -> listOf(Point(point.x - 1, point.y))
                'v' -> listOf(Point(point.x, point.y + 1))
                '^' -> listOf(Point(point.x, point.y - 1))
                else -> getNextIgnoringSlopes(point)
            }
        }
    }

    private fun getNextIgnoringSlopes(point: Point): List<Point> {
        return listOf(
            Point(point.x + 1, point.y),
            Point(point.x - 1, point.y),
            Point(point.x, point.y + 1),
            Point(point.x, point.y - 1)
        ).filter { it.y >= 0 && it.y < input.size && input[it.y][it.x] != '#' }
    }
}
