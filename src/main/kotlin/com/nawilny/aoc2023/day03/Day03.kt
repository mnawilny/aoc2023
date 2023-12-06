package com.nawilny.aoc2023.day03

import com.nawilny.aoc2023.commons.Input

fun main() {
    val input = Input.readFileLinesNormalized("day03", "input.txt")
    println(solvePart1(input))
    println(solvePart2(input))
}

private fun solvePart1(input: List<String>): Int {
    var currentNumber: Int? = null
    var nextToSymbol = false
    var sum = 0
    input.withIndex().forEach { line ->
        line.value.withIndex().forEach { char ->
            if (char.value.isDigit()) {
                if (currentNumber == null) {
                    currentNumber = char.value.digitToInt()
                    if (isSymbol(getChar(input, line.index - 1, char.index - 1))
                        || isSymbol(getChar(input, line.index - 1, char.index))
                        || isSymbol(getChar(input, line.index, char.index - 1))
                        || isSymbol(getChar(input, line.index + 1, char.index - 1))
                        || isSymbol(getChar(input, line.index + 1, char.index))
                    ) {
                        nextToSymbol = true
                    }
                } else {
                    currentNumber = (currentNumber!! * 10) + char.value.digitToInt()
                    if (isSymbol(getChar(input, line.index - 1, char.index))
                        || isSymbol(getChar(input, line.index + 1, char.index))
                    ) {
                        nextToSymbol = true
                    }
                }
            } else {
                if (currentNumber != null) {
                    if (isSymbol(char.value)
                        || isSymbol(getChar(input, line.index - 1, char.index))
                        || isSymbol(getChar(input, line.index + 1, char.index))
                    ) {
                        nextToSymbol = true
                    }
                    if (nextToSymbol) {
                        sum += currentNumber!!
                    }
                    currentNumber = null
                    nextToSymbol = false
                }
            }
        }
    }
    return sum
}

private fun solvePart2(input: List<String>): Int {
    var sum = 0
    input.withIndex().forEach { line ->
        line.value.withIndex().forEach { char ->
            if (char.value == '*') {
                val numbers = mutableListOf<Int>()
                val above = getNumber(input, line.index - 1, char.index)
                if (above != null) {
                    numbers.add(above)
                } else {
                    numbers.addOrSkip(getNumber(input, line.index - 1, char.index - 1))
                    numbers.addOrSkip(getNumber(input, line.index - 1, char.index + 1))
                }
                val below = getNumber(input, line.index + 1, char.index)
                if (below != null) {
                    numbers.add(below)
                } else {
                    numbers.addOrSkip(getNumber(input, line.index + 1, char.index - 1))
                    numbers.addOrSkip(getNumber(input, line.index + 1, char.index + 1))
                }
                numbers.addOrSkip(getNumber(input, line.index, char.index - 1))
                numbers.addOrSkip(getNumber(input, line.index, char.index + 1))
                if (numbers.size == 2) {
                    sum += numbers[0] * numbers[1]
                }
            }
        }
    }
    return sum
}

private fun getChar(input: List<String>, lineIndex: Int, charIndex: Int): Char? {
    if (lineIndex < 0 || charIndex < 0 || lineIndex >= input.size) {
        return null
    }
    val line = input[lineIndex]
    return if (charIndex >= line.length) {
        null
    } else {
        line[charIndex]
    }
}

private fun isSymbol(c: Char?) = c != null && !c.isDigit() && c != '.'

private fun getNumber(input: List<String>, lineIndex: Int, charIndex: Int): Int? {
    val c = getChar(input, lineIndex, charIndex)
    if (c == null || !c.isDigit()) {
        return null
    }
    var number = c.digitToInt()
    var pos = charIndex - 1
    var multiply = 10
    var x = getChar(input, lineIndex, pos)
    while (x != null && x.isDigit()) {
        number += x.digitToInt() * multiply
        pos--
        multiply *= 10
        x = getChar(input, lineIndex, pos)
    }
    pos = charIndex + 1
    x = getChar(input, lineIndex, pos)
    while (x != null && x.isDigit()) {
        number *= 10
        number += x.digitToInt()
        pos++
        x = getChar(input, lineIndex, pos)
    }
    return number
}

private fun <T> MutableList<T>.addOrSkip(v: T?) {
    if (v != null) this.add(v)
}
