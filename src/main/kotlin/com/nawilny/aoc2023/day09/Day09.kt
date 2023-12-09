package com.nawilny.aoc2023.day09

import com.nawilny.aoc2023.commons.Input
import com.nawilny.aoc2023.commons.Input.println

fun main() {
    val input = Input.readFileLinesNormalized("day09", "input.txt")
        .map { l -> l.split(" ").map { it.toLong() } }

    input.sumOf { extrapolate(it) { value, list -> value + list.last() } }.println()
    input.sumOf { extrapolate(it) { value, list -> list.first() - value } }.println()
}

private fun extrapolate(l: List<Long>, nexValueGenerator: (Long, List<Long>) -> Long): Long {
    val steps = mutableListOf<List<Long>>()
    steps.add(l)
    while (steps.last().any { it != 0L }) {
        steps.add(generateDifferences(steps.last()))
    }
    return steps.asReversed().fold(0L, nexValueGenerator)
}

private fun generateDifferences(l: List<Long>): List<Long> {
    var last = l.first()
    val result = mutableListOf<Long>()
    l.drop(1).forEach {
        result.add(it - last)
        last = it
    }
    return result
}

