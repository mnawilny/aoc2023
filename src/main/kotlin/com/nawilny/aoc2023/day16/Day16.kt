package com.nawilny.aoc2023.day16

import com.nawilny.aoc2023.commons.Input
import com.nawilny.aoc2023.commons.Input.println

fun main() {
    val input = Input.readFileLinesNormalized("day16", "input.txt")

    val wall = Wall(input)

    wall.simulate(Beam(Position(0, 0), Direction.RIGHT))
    wall.energized().println()

    val startPositions = mutableListOf<Beam>()
    (0 until wall.height).forEach {
        startPositions.add(Beam(Position(0, it), Direction.RIGHT))
        startPositions.add(Beam(Position(wall.width - 1, it), Direction.LEFT))
    }
    (0 until wall.width).forEach {
        startPositions.add(Beam(Position(it, 0), Direction.DOWN))
        startPositions.add(Beam(Position(it, wall.height - 1), Direction.UP))
    }
    startPositions.maxOf {
        wall.reset()
        wall.simulate(it)
        wall.energized()
    }.println()
}

private enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    fun next(p: Position) = when (this) {
        UP -> Position(p.x, p.y - 1)
        DOWN -> Position(p.x, p.y + 1)
        LEFT -> Position(p.x - 1, p.y)
        RIGHT -> Position(p.x + 1, p.y)
    }
}

private data class Position(val x: Int, val y: Int)

private data class Beam(val p: Position, val d: Direction) {
    fun forward() = Beam(d.next(p), d)
}

private class Wall(val lines: List<String>) {

    val width = lines[0].length
    val height = lines.size
    private val analysed = mutableSetOf<Beam>()

    fun simulate(beam: Beam) {
        if (analysed.contains(beam)) {
            return
        }
        if (beam.p.x < 0 || beam.p.x >= width || beam.p.y < 0 || beam.p.y >= height) {
            return
        }
        analysed.add(beam)
        when (lines[beam.p.y][beam.p.x]) {
            '.' -> simulate(beam.forward())
            '\\' -> {
                val newDirection = when (beam.d) {
                    Direction.UP -> Direction.LEFT
                    Direction.DOWN -> Direction.RIGHT
                    Direction.LEFT -> Direction.UP
                    Direction.RIGHT -> Direction.DOWN
                }
                simulate(Beam(beam.p, newDirection).forward())
            }

            '/' -> {
                val newDirection = when (beam.d) {
                    Direction.UP -> Direction.RIGHT
                    Direction.DOWN -> Direction.LEFT
                    Direction.LEFT -> Direction.DOWN
                    Direction.RIGHT -> Direction.UP
                }
                simulate(Beam(beam.p, newDirection).forward())
            }

            '|' -> {
                if (beam.d == Direction.UP || beam.d == Direction.DOWN) {
                    simulate(beam.forward())
                } else {
                    simulate(Beam(beam.p, Direction.UP).forward())
                    simulate(Beam(beam.p, Direction.DOWN).forward())
                }
            }

            '-' -> {
                if (beam.d == Direction.LEFT || beam.d == Direction.RIGHT) {
                    simulate(beam.forward())
                } else {
                    simulate(Beam(beam.p, Direction.LEFT).forward())
                    simulate(Beam(beam.p, Direction.RIGHT).forward())
                }
            }

            else -> error("Invalid map element '${lines[beam.p.y][beam.p.x]}'")
        }
    }

    fun energized() = analysed.map { it.p }.toSet().size

    fun reset() {
        analysed.clear()
    }
}
