package com.nawilny.aoc2023.day18

import com.nawilny.aoc2023.commons.Input
import com.nawilny.aoc2023.commons.Input.println

fun main() {
    val input = Input.readFileLinesNormalized("day18", "input.txt")

    val trench = Trench()
    input.map { parseCommand(it) }.forEach { trench.dig(it) }
    trench.volume().println()

    val trench2 = Trench()
    input.map { parseColourCommand(it) }.forEach { trench2.dig(it) }
    trench2.volume().println()
}

private class Trench {
    private data class VLine(val x: Int, val y1: Int, val y2: Int)

    private val trenchLines = mutableSetOf<VLine>()
    private var currentPosition = Point(0, 0)

    fun dig(c: Command) {
        when (c.direction) {
            Direction.U -> {
                trenchLines.add(VLine(currentPosition.x, currentPosition.y - c.distance, currentPosition.y))
                currentPosition = Point(currentPosition.x, currentPosition.y - c.distance)
            }

            Direction.D -> {
                trenchLines.add(VLine(currentPosition.x, currentPosition.y, currentPosition.y + c.distance))
                currentPosition = Point(currentPosition.x, currentPosition.y + c.distance)
            }

            Direction.L -> {
                currentPosition = Point(currentPosition.x - c.distance, currentPosition.y)
            }

            Direction.R -> {
                currentPosition = Point(currentPosition.x + c.distance, currentPosition.y)
            }
        }
    }

    fun volume(): Long {
        var volume = 0L
        for (y in trenchLines.minOf { it.y1 }..trenchLines.maxOf { it.y2 }) {
            val yLines = trenchLines.filter { it.y1 <= y && it.y2 >= y }.sortedBy { it.x }
            var inside = false
            var insideLine = false
            var x = Long.MIN_VALUE

            var lineVolume = 0L
            var previousLine: VLine? = null

            fun handleHorizontalLine(line: VLine, changeInsidePredicate: () -> Boolean) {
                if (insideLine) {
                    lineVolume += line.x - x
                    insideLine = false
                    if (changeInsidePredicate()) {
                        inside = !inside
                    }
                } else {
                    if (inside) {
                        lineVolume += line.x - x
                    } else {
                        lineVolume += 1
                    }
                    insideLine = true
                }
            }

            fun handleVerticalLine(line: VLine) {
                if (inside) {
                    lineVolume += line.x - x
                } else {
                    lineVolume += 1
                }
                inside = !inside
            }

            yLines.forEach { line ->
                when (y) {
                    line.y1 -> handleHorizontalLine(line) { y == previousLine!!.y2 }
                    line.y2 -> handleHorizontalLine(line) { y == previousLine!!.y1 }
                    else -> handleVerticalLine(line)
                }
                x = line.x.toLong()
                previousLine = line
            }
            volume += lineVolume
        }
        return volume
    }

}

private data class Point(val x: Int, val y: Int)

private enum class Direction {
    L, R, U, D
}

private data class Command(val direction: Direction, val distance: Int)

private fun parseCommand(l: String): Command {
    val parts = l.split(" ")
    val direction = Direction.valueOf(parts[0])
    val distance = parts[1].toInt()
    return Command(direction, distance)
}

private fun parseColourCommand(l: String): Command {
    val parts = l.split(" ")
    var colour = parts[2].substring(2..parts[2].length - 2)
    val direction = when (colour.last()) {
        '0' -> Direction.R
        '1' -> Direction.D
        '2' -> Direction.L
        '3' -> Direction.U
        else -> error("Invalid direction ${colour.last()}")
    }
    val distance = colour.dropLast(1).toInt(radix = 16)
    return Command(direction, distance)
}
