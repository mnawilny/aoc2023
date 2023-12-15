package com.nawilny.aoc2023.day15

import com.nawilny.aoc2023.commons.Input
import com.nawilny.aoc2023.commons.Input.println

fun main() {
    val input = Input.readFileLinesNormalized("day15", "input.txt")
    val sequence = input[0].split(",")

    sequence.sumOf { hash(it) }.println()

    val boxes = (0..255).map { Box(it) }
    sequence.forEach { executeStep(it, boxes) }
    boxes.sumOf { it.getFocusingPower() }.println()
}

private fun executeStep(s: String, boxes: List<Box>) {
    if (s.contains("=")) {
        val parts = s.split("=")
        val label = parts[0]
        val focalLength = parts[1].toInt()
        boxes[hash(label)].putLens(label, focalLength)
    } else {
        val label = s.dropLast(1)
        boxes[hash(label)].removeLens(label)
    }
}

private data class Box(val boxNumber: Int) {
    val lensesList = mutableListOf<String>()
    val lensesMap = mutableMapOf<String, Int>()

    fun removeLens(label: String) {
        val value = lensesMap.remove(label)
        if (value != null) {
            lensesList.remove(label)
        }
    }

    fun putLens(label: String, focalLength: Int) {
        if (!lensesMap.contains(label)) {
            lensesList.add(label)
        }
        lensesMap[label] = focalLength
    }

    fun getFocusingPower() = lensesList.withIndex().sumOf { (boxNumber + 1) * (it.index + 1) * lensesMap[it.value]!! }
}

private fun hash(s: String): Int {
    return s.fold(0) { acc, c ->
        var h = acc + c.code
        h *= 17
        h %= 256
        h
    }
}
