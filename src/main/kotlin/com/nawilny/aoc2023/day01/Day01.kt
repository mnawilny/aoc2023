package com.nawilny.aoc2023.day01

import com.nawilny.aoc2023.commons.Input
import com.nawilny.aoc2023.commons.Input.println

val stringDigits = mapOf(
    "one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5,
    "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9, "zero" to 0
)

fun main() {
    val input = Input.readFileLinesNormalized("day01", "input.txt")

    // part 1
    val numbers1 = input.map { line -> line.filter { it.isDigit() } }
        .map { it.first().digitToInt() * 10 + it.last().digitToInt() }
    numbers1.sum().println()

    // part 2
    val numbers2 = input.map { getFirstDigit(it) * 10 + getLastDigit(it) }
    numbers2.sum().println()
}

private fun getFirstDigit(line: String): Int {
    return getDigitFromSide(line) { l, c -> l + c }
}

private fun getLastDigit(line: String): Int {
    return getDigitFromSide(line.reversed()) { l, c -> c + l }
}

private fun getDigitFromSide(line: String, combineFunc: (String, Char) -> String): Int {
    var subline = ""
    line.forEach { c ->
        if (c.isDigit()) {
            return c.digitToInt()
        } else {
            subline = combineFunc(subline, c)
            stringDigits.entries.forEach {
                if (subline.contains(it.key)) {
                    return it.value
                }
            }
        }
    }
    error("Line '$line' does not contain any digits")
}
