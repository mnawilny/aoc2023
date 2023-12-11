package com.nawilny.aoc2023.day11

import com.nawilny.aoc2023.commons.Input
import com.nawilny.aoc2023.commons.Input.println
import kotlin.math.abs

fun main() {
    val input = Input.readFileLinesNormalized("day11", "input.txt")
    val galaxies = parseGalaxies(input)

    solveForExpansionMultiplier(galaxies, 2).println()
    solveForExpansionMultiplier(galaxies, 1000000).println()
}

private fun solveForExpansionMultiplier(galaxies: List<Galaxy>, multiplier: Int): Long {
    return expand(galaxies, multiplier).pairs().sumOf { distance((it)) }
}

private data class Galaxy(val x: Long, val y: Long)

private fun distance(g: Pair<Galaxy, Galaxy>): Long {
    return abs(g.first.x - g.second.x) + abs(g.first.y - g.second.y)
}

private fun parseGalaxies(input: List<String>): List<Galaxy> {
    return input.withIndex().flatMap { line ->
        line.value.withIndex().filter { it.value == '#' }.map { Galaxy(it.index.toLong(), line.index.toLong()) }
    }
}

private fun expand(galaxies: List<Galaxy>, multiplier: Int): List<Galaxy> {
    val emptyXs = emptyCoordinates(galaxies) { it.x }
    val emptyYs = emptyCoordinates(galaxies) { it.y }
    return galaxies.map { Galaxy(newCoordinate(it.x, emptyXs, multiplier), newCoordinate(it.y, emptyYs, multiplier)) }
}

private fun emptyCoordinates(galaxies: List<Galaxy>, coordinate: (Galaxy) -> Long): List<Long> {
    return (0..galaxies.maxOf { coordinate(it) }).filter { c -> galaxies.none { coordinate(it) == c } }
}

private fun newCoordinate(c: Long, emptyValues: List<Long>, multiplier: Int): Long {
    return c + (emptyValues.count { it < c } * (multiplier - 1))
}

private fun <T> List<T>.pairs(): List<Pair<T, T>> {
    return this.withIndex().flatMap { el ->
        this.withIndex().filter { it.index > el.index }.map { Pair(el.value, it.value) }
    }
}
