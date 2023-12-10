package com.nawilny.aoc2023.day10

import com.nawilny.aoc2023.commons.Input

fun main() {
    val input = Input.readFileLinesNormalized("day10", "input.txt")
    val map = Map(input)

    val loopPositions = map.getLoopPositions()
    println(loopPositions.size / 2)
    println(map.countElementsWithinLoop(loopPositions))
}

private data class Position(val x: Int, val y: Int) {
    fun next(dir: Direction): Position {
        return when (dir) {
            Direction.N -> Position(x, y - 1)
            Direction.S -> Position(x, y + 1)
            Direction.W -> Position(x - 1, y)
            Direction.E -> Position(x + 1, y)
        }
    }
}

private enum class Direction() {
    N, S, W, E;

    fun opposite() = when (this) {
        N -> S
        S -> N
        W -> E
        E -> W
    }
}

private class Map(val input: List<String>) {

    val start: Position
    val startChar: Char

    val tileDirectionsMap = mapOf(
        '|' to setOf(Direction.N, Direction.S),
        '-' to setOf(Direction.W, Direction.E),
        'J' to setOf(Direction.N, Direction.W),
        'L' to setOf(Direction.N, Direction.E),
        '7' to setOf(Direction.S, Direction.W),
        'F' to setOf(Direction.S, Direction.E)
    )

    init {
        start = findStart()
        val startMoves = getPossibleMoves(start)
        startChar = tileDirectionsMap.entries.find { it.value == startMoves }?.key
            ?: error("Unambiguous start position")
    }

    fun getLoopPositions(): Set<Position> {
        var fromDir = tileDirectionsMap[startChar]!!.first()
        var pos = start.next(fromDir)
        val loopPositions = mutableSetOf(start)
        while (pos != start) {
            loopPositions.add(pos)
            fromDir = getNextDirection(pos, fromDir)
            pos = pos.next(fromDir)
        }
        return loopPositions
    }

    fun countElementsWithinLoop(loopPositions: Set<Position>): Int {
        var counter = 0
        var inLoop = false
        var onALoopFromDirection: Direction? = null
        input.withIndex().forEach { line ->
            line.value.withIndex().forEach { c ->
                val pos = Position(c.index, line.index)
                val tile = getTile(pos) ?: error("Not valid position $pos")

                if (onALoopFromDirection != null) {
                    val tileDirections = tileDirectionsMap[tile]!!
                    if (tileDirections.contains(Direction.N) || tileDirections.contains(Direction.S)) {
                        if (tileDirections.contains(onALoopFromDirection)) {
                            inLoop = !inLoop
                        }
                        onALoopFromDirection = null
                    }
                } else if (loopPositions.contains(pos)) {
                    when (tile) {
                        '|' -> inLoop = !inLoop
                        'F' -> onALoopFromDirection = Direction.N
                        'L' -> onALoopFromDirection = Direction.S
                    }
                } else if (inLoop) {
                    counter++
                }
            }
        }
        return counter
    }

    private fun findStart(): Position {
        input.withIndex().forEach { line ->
            val start = line.value.withIndex().find { it.value == 'S' }
            if (start != null) {
                return Position(start.index, line.index)
            }
        }
        error("No start position found")
    }

    private fun getNextDirection(position: Position, from: Direction): Direction {
        val tile = getTile(position) ?: error("Not valid position $position")
        val tileDirections = tileDirectionsMap[tile]!!
        if (!tileDirections.contains(from.opposite())) {
            error("Cannot move to tile '$tile' from $from")
        }
        return tileDirections.first { it != from.opposite() }
    }

    private fun getPossibleMoves(position: Position) = Direction.values().filter { canMove(position, it) }.toSet()

    private fun canMove(position: Position, dir: Direction): Boolean {
        val next = getTile(position.next(dir)) ?: return false
        val tileDirections = tileDirectionsMap[next]!!
        return tileDirections.contains(dir.opposite())
    }

    private fun getTile(position: Position): Char? {
        return if (position.y < 0 || position.y >= input.size) {
            null
        } else {
            val line = input[position.y]
            if (position.x < 0 || position.x >= line.length) {
                null
            } else {
                val tile = line[position.x]
                if (tile == 'S') {
                    startChar
                } else {
                    tile
                }
            }
        }
    }

}
