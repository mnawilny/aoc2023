package com.nawilny.aoc2023.day05

import com.nawilny.aoc2023.commons.Input
import com.nawilny.aoc2023.commons.Input.println

fun main() {
    val input = Input.divideByNewLines(Input.readFileLines("day05", "input.txt"))
    val seeds = parseSeeds(input[0][0])
    val almanacMaps = input.drop(1).map { parseAlmanacMap(it) }.associateBy { it.from }

    var from = "seed"
    var values1 = seeds
    var values2 = parseSeedRanges(seeds)
    while (from != "location") {
        val map = almanacMaps[from]!!
        from = map.to
        values1 = values1.map { map.mapValue(it) }
        values2 = values2.flatMap { map.mapValueRanges(it) }
    }
    values1.min().println()
    values2.minBy { it.first }.first.println()
}

private data class AlmanacMap(val from: String, val to: String, val ranges: List<AlmanacMapRange>) {
    fun mapValue(v: Long): Long {
        ranges.forEach {
            val result = it.mapValue(v)
            if (result != null) {
                return result
            }
        }
        return v
    }

    fun mapValueRanges(sourceRange: LongRange): List<LongRange> {
        var source = listOf(sourceRange)
        var target = mutableListOf<LongRange>()
        ranges.forEach { range ->
            val newSource = mutableListOf<LongRange>()
            source.forEach { s ->
                val result = range.mapValueRange(s)
                if (result.target != null) {
                    target.add(result.target)
                }
                newSource.addAll(result.unmappedSourceRanges)
            }
            source = newSource
        }
        target.addAll(source)
        return target
    }
}

private data class AlmanacMapRange(val destinationStart: Long, val sourceStart: Long, val length: Long) {
    fun mapValue(v: Long): Long? {
        val dist = v - sourceStart
        if (dist in 0..length) {
            return destinationStart + dist
        }
        return null
    }

    data class RangingResult(val target: LongRange?, val unmappedSourceRanges: List<LongRange>)

    fun mapValueRange(v: LongRange): RangingResult {
        // range: ---|=========|---
        // value: -----|======|---
        return if (v.first >= sourceStart && v.last < sourceStart + length) {
            RangingResult(
                LongRange(mapValue(v.first)!!, mapValue(v.last)!!),
                listOf()
            )
            // range: ---|=========|---
            // value: -|=============|-
        } else if (v.first < sourceStart && v.last >= sourceStart + length) {
            RangingResult(
                LongRange(destinationStart, destinationStart + length - 1),
                listOf(
                    LongRange(v.first, sourceStart - 1),
                    LongRange(sourceStart + length, v.last)
                )
            )
            // range: ---|=========|---
            // value: -|========|------
        } else if (v.first < sourceStart && v.last >= sourceStart) {
            RangingResult(
                LongRange(mapValue(sourceStart)!!, mapValue(v.last)!!),
                listOf(LongRange(v.first, sourceStart - 1))
            )
            // range: ---|=========|---
            // value: ------|========|-
        } else if (v.first < sourceStart + length && v.last >= sourceStart + length) {
            RangingResult(
                LongRange(mapValue(v.first)!!, mapValue(sourceStart + length - 1)!!),
                listOf(LongRange(sourceStart + length, v.last))
            )
            // range: -|====|-------
            // value: --------|===|-
        } else {
            RangingResult(null, listOf(v))
        }
    }
}

private fun parseSeeds(l: String) = l.split(" ").drop(1).map { it.toLong() }

private fun parseAlmanacMap(input: List<String>): AlmanacMap {
    val description = input[0].split(" ")[0].split("-")
    val from = description[0]
    val to = description[2]
    val ranges =
        input.drop(1).map { l -> l.split(" ").map { it.toLong() } }.map { AlmanacMapRange(it[0], it[1], it[2]) }
    return AlmanacMap(from, to, ranges)
}

private fun parseSeedRanges(l: List<Long>) = l.chunked(2).map { LongRange(it[0], it[0] + it[1] - 1) }
