package com.nawilny.aoc2023.day13

import com.nawilny.aoc2023.commons.Input.divideByNewLines
import com.nawilny.aoc2023.commons.Input.println
import com.nawilny.aoc2023.commons.Input.readFileLines
import kotlin.math.min

fun main() {
    val input = divideByNewLines(readFileLines("day13", "input.txt"))

    input.sumOf { getSymmetryValue(it)!! }.println()
    input.sumOf { getSymmetryValueWhenFixingSmudge(it) }.println()
}

private fun getSymmetryValueWhenFixingSmudge(patterns: List<String>): Int {
    val oldReflectionLine = getSymmetryValue(patterns)
    patterns.indices.flatMap { row ->
        patterns[0].indices.map { col ->
            val copy = copyWithSwappedCharacter(patterns, row, col)
            val s = getSymmetryValue(copy, oldReflectionLine)
            if (s != null) {
                return s
            }
        }
    }
    error("No symmetry")
}

private fun copyWithSwappedCharacter(patterns: List<String>, row: Int, col: Int): List<String> {
    val copy = mutableListOf<String>()
    patterns.withIndex().forEach { r ->
        if (r.index == row) {
            val newChar = if (r.value[col] == '#') '.' else '#'
            val newRow = StringBuilder(r.value)
            newRow[col] = newChar
            copy.add(newRow.toString())
        } else {
            copy.add(r.value)
        }
    }
    return copy
}

private fun getSymmetryValue(patterns: List<String>, ignoredValue: Int? = null): Int? {
    for (col in 1 until patterns[0].length) {
        if (ignoredValue != col && isVerticalSymmetry(patterns, col)) {
            return col
        }
    }
    for (row in 1 until patterns.size) {
        if (ignoredValue != row * 100 && isHorizontalSymmetry(patterns, row)) {
            return row * 100
        }
    }
    return null
}

private fun isHorizontalSymmetry(patterns: List<String>, h: Int): Boolean {
    for (i in 0..(min(h - 1, patterns.size - h - 1))) {
        if (patterns[h - i - 1] != patterns[h + i]) {
            return false
        }
    }
    return true
}

private fun isVerticalSymmetry(patterns: List<String>, c: Int): Boolean {
    for (i in 0..(min(c - 1, patterns[0].length - c - 1))) {
        val slice1 = patterns.map { it[c - i - 1] }
        val slice2 = patterns.map { it[c + i] }
        if (slice1 != slice2) {
            return false
        }
    }
    return true
}
