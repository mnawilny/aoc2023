package com.nawilny.aoc2023.day17

import com.nawilny.aoc2023.commons.Input
import com.nawilny.aoc2023.commons.Input.println

fun main() {
    val input = Input.readFileLinesNormalized("day17", "input.txt")

    val map = Map(input)
    val start = System.currentTimeMillis()
    getMinHeatLossPath(map, 1..3).println()
    println("Part 1 calculated in ${(System.currentTimeMillis() - start) / 1000.0}s")
    getMinHeatLossPath(map, 4..10).println()
    println("Part 2 calculated in ${(System.currentTimeMillis() - start) / 1000.0}s")
}

private fun getMinHeatLossPath(map: Map, possibleSteps: IntRange): Int {
    val visitedPositions = mutableSetOf<Position>()
    val distances = mutableMapOf<Position, Int>()

    // workaround to attempt going both down and right at the start
    distances[Position(Point(0, 0), Direction.LEFT)] = 0
    distances[Position(Point(0, 0), Direction.UP)] = 0

    while (true) {
        val current = distances.filter { !visitedPositions.contains(it.key) }.minBy { it.value }
        val currentPosition = current.key
        val currentDistance = current.value
        if (currentPosition.p == map.end) {
            return currentDistance
        }
        generateNextMoves(currentPosition, possibleSteps).forEach { next ->
            val losses = next.map { map.getHeatLoss(it.p) }
            if (losses.all { it != null }) {
                val distance = currentDistance + losses.sumOf { it!! }
                if (!distances.contains(next.last()) || distances[next.last()]!! > distance) {
                    distances[next.last()] = distance
                }
            }
        }
        visitedPositions.add(currentPosition)
    }
}

private enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

private val rotations = mapOf(
    Direction.UP to listOf(Direction.LEFT, Direction.RIGHT),
    Direction.DOWN to listOf(Direction.LEFT, Direction.RIGHT),
    Direction.LEFT to listOf(Direction.UP, Direction.DOWN),
    Direction.RIGHT to listOf(Direction.UP, Direction.DOWN)
)

private fun generateNextMoves(position: Position, possibleSteps: IntRange): List<List<Position>> {
    return rotations[position.d]!!.flatMap { r ->
        possibleSteps.map { n ->
            (1..n).map { position.move(r, it) }
        }
    }
}

private data class Point(val x: Int, val y: Int)

private data class Position(val p: Point, val d: Direction) {
    fun move(d: Direction, n: Int): Position {
        val nextP = when (d) {
            Direction.UP -> Point(p.x, p.y - n)
            Direction.DOWN -> Point(p.x, p.y + n)
            Direction.LEFT -> Point(p.x - n, p.y)
            Direction.RIGHT -> Point(p.x + n, p.y)
        }
        return Position(nextP, d)
    }
}

private class Map(input: List<String>) {
    private val heatLoss = input.map { line -> line.map { it.digitToInt() } }

    val end = Point(input[0].length - 1, input.size - 1)

    fun getHeatLoss(p: Point): Int? {
        return if (p.x < 0 || p.y < 0 || p.y >= heatLoss.size || p.x >= heatLoss[p.y].size) {
            null
        } else {
            heatLoss[p.y][p.x]
        }
    }
}
