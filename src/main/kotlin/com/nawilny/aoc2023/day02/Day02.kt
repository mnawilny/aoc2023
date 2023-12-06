package com.nawilny.aoc2023.day02

import com.nawilny.aoc2023.commons.Input
import com.nawilny.aoc2023.commons.Input.println
import kotlin.math.max

fun main() {
    val input = Input.readFileLinesNormalized("day02", "input.txt")
    val games = input.map { parseGame(it) }

    // part 1
    val maxCubes = Cubes(red = 12, green = 13, blue = 14)
    games.filter { it.canHaveOnly(maxCubes) }.sumOf { it.id }.println()

    // part 2
    games.sumOf { it.minSet().power() }.println()
}

data class Game(val id: Int, val revealedSubsets: List<Cubes>) {
    fun canHaveOnly(c: Cubes) = revealedSubsets.all { it.canHaveOnly(c) }
    fun minSet() = revealedSubsets.fold(Cubes(0, 0, 0)) { acc, c ->
        Cubes(red = max(acc.red, c.red), green = max(acc.green, c.green), blue = max(acc.blue, c.blue))
    }
}

data class Cubes(val red: Int, val green: Int, val blue: Int) {
    fun canHaveOnly(c: Cubes) = red <= c.red && green <= c.green && blue <= c.blue
    fun power() = red * green * blue
}

private fun parseGame(line: String): Game {
    val parts1 = line.split(": ")
    val id = parts1[0].substring(5).toInt()
    val revealedSubsets = parts1[1].split("; ").map { parseSubset(it) }
    return Game(id, revealedSubsets)
}

private fun parseSubset(str: String): Cubes {
    var red = 0
    var green = 0
    var blue = 0
    str.split(", ").map { it.split(" ") }.forEach {
        val number = it[0].toInt()
        when (it[1]) {
            "red" -> red += number
            "green" -> green += number
            "blue" -> blue += number
            else -> error("Unknown color '${it[1]}'")
        }
    }
    return Cubes(red = red, green = green, blue = blue)
}
