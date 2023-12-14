package com.nawilny.aoc2023.day14

import com.nawilny.aoc2023.commons.Input
import com.nawilny.aoc2023.commons.Input.println

fun main() {
    val input = Input.readFileLinesNormalized("day14", "input.txt")
    val platform = parseInput(input)

    platform.tiltNorth().calculateLoad().println()

    val repeats = platform.findTiltCycleRepeats()

    val cycleStartPosition = repeats.second
    val cycleLength = repeats.first.size - cycleStartPosition
    val N = 1000000000
    val cycleEndPosition = (N - cycleStartPosition) % cycleLength
    repeats.first[cycleStartPosition + cycleEndPosition].calculateLoad().println()
}

private data class Point(val x: Int, val y: Int)

private data class Platform(val width: Int, val length: Int, val cubeRocks: List<Point>, val roundRocks: List<Point>) {

    fun tiltNorth() = tilt(roundRocks.sortedBy { it.y }) { Point(it.x, it.y - 1) }
    fun tiltSouth() = tilt(roundRocks.sortedByDescending { it.y }) { Point(it.x, it.y + 1) }
    fun tiltWest() = tilt(roundRocks.sortedBy { it.x }) { Point(it.x - 1, it.y) }
    fun tiltEast() = tilt(roundRocks.sortedByDescending { it.x }) { Point(it.x + 1, it.y) }

    fun tiltCycle() = tiltNorth().tiltWest().tiltSouth().tiltEast()

    fun tilt(sortedRoundRocks: List<Point>, move: (Point) -> Point): Platform {
        val newRoundRocks = mutableListOf<Point>()
        sortedRoundRocks.forEach { p ->
            var next = p
            var previous = p
            while (next.y in 0 until length && next.x in 0 until width && !cubeRocks.contains(next) && !newRoundRocks.contains(
                    next
                )
            ) {
                previous = next
                next = move(next)
            }
            newRoundRocks.add(previous)
        }
        return Platform(width, length, cubeRocks, newRoundRocks)
    }

    fun findTiltCycleRepeats(): Pair<List<Platform>, Int> {
        val previousPositions = mutableMapOf<Platform, Int>()
        val previousPositionsList = mutableListOf<Platform>()
        var p = this
        previousPositions[this] = 0
        previousPositionsList.add(p)
        for (i in 1..1000000) {
            p = p.tiltCycle()
            if (previousPositions.contains(p)) {
                return Pair(previousPositionsList, previousPositions[p]!!)
            }
            previousPositions[p] = i
            previousPositionsList.add(p)
        }
        error("Could not find tilt cycle repeats")
    }

    fun calculateLoad(): Int {
        return roundRocks.sumOf { length - it.y }
    }

    fun print() {
        for (y in 0 until length) {
            print('|')
            for (x in 0 until width) {
                val p = Point(x, y)
                print(
                    when {
                        cubeRocks.contains(p) -> '#'
                        roundRocks.contains(p) -> 'O'
                        else -> '.'
                    }
                )
            }
            println('|')
        }
    }
}

private fun parseInput(input: List<String>): Platform {
    val width = input[0].length
    val length = input.size
    val cubeRocks = mutableListOf<Point>()
    val roundRocks = mutableListOf<Point>()
    input.withIndex().forEach { line ->
        line.value.withIndex().forEach { c ->
            val p = Point(c.index, line.index)
            when (c.value) {
                '#' -> cubeRocks.add(p)
                'O' -> roundRocks.add(p)
            }
        }
    }
    return Platform(width, length, cubeRocks, roundRocks)
}
