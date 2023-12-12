package com.nawilny.aoc2023.day12

import com.nawilny.aoc2023.commons.Input
import com.nawilny.aoc2023.commons.Input.println
import kotlin.math.min

fun main() {
    val input = Input.readFileLinesNormalized("day12", "input.txt")
    val records = input.map { parseRecordRow(it) }

    records.sumOf { countValidArrangements(it.springs, it.groups) }.println()
    records.map { unfoldRecordRow(it) }.sumOf { countValidArrangements(it.springs, it.groups) }.println()
}

private data class RecordRow(val springs: String, val groups: List<Int>)

private val cache = mutableMapOf<Pair<String, List<Int>>, Long>()

private fun countValidArrangements(springs: String, groups: List<Int>): Long {
    val cacheKey = Pair(springs, groups)
    if (cache.contains(cacheKey)) {
        return cache[cacheKey]!!
    }
    val minLength = groups.sum() + groups.size - 1
    val limit = if (springs.contains("#")) {
        min(springs.indexOf('#'), springs.length - minLength)
    } else {
        springs.length - minLength
    }
    val firstGroup = groups.first()
    var combinations = 0L

    for (i in 0..limit) {
        var canPut = true
        for (g in 0 until firstGroup) {
            if (springs[i + g] == '.') {
                canPut = false
            }
        }
        if (springs.getOrNull(i + firstGroup) == '#') {
            canPut = false
        }
        if (canPut) {
            val g = groups.drop(1)
            if (g.isEmpty()) {
                if (!springs.substring(i + firstGroup).contains('#')) {
                    combinations += 1
                }
            } else {
                val s = springs.substring(i + firstGroup + 1)
                combinations += countValidArrangements(s, g)
            }
        }
    }
    cache[cacheKey] = combinations
    return combinations
}

private fun unfoldRecordRow(r: RecordRow): RecordRow {
    var newSprings = r.springs
    var newGroups = r.groups
    repeat(4) {
        newSprings = newSprings.plus("?").plus(r.springs)
        newGroups = newGroups.plus(r.groups)
    }
    return RecordRow(newSprings, newGroups)
}

private fun parseRecordRow(line: String): RecordRow {
    val parts = line.split(" ")
    return RecordRow(parts[0], parts[1].split(",").map { it.toInt() })
}
