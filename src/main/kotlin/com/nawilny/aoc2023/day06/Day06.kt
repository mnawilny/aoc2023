package com.nawilny.aoc2023.day06

import com.nawilny.aoc2023.commons.Input
import com.nawilny.aoc2023.commons.Input.println

fun main() {
    val input = Input.readFileLinesNormalized("day06", "input.txt")

    // part 1
    val times = parseLine(input[0])
    val distances = parseLine(input[1])
    val races = times.zip(distances).toMap()
    races.map { getWinningHoldTimesCount(it.key, it.value) }.multiply().println()

    // part 2
    val largeRaceTime = parseLineAsSingleRace(input[0])
    val largeRaceDistance = parseLineAsSingleRace(input[1])
    getWinningHoldTimesCount(largeRaceTime, largeRaceDistance).println()
}

private fun getWinningHoldTimesCount(time: Long, recordDistance: Long): Int {
    return (1..time).map { getDist(time, it) }.count { it > recordDistance }
}

private fun getDist(time: Long, holdTime: Long) = holdTime * (time - holdTime)

private fun parseLine(l: String) = l.split(" ").filter { it.isNotBlank() }.drop(1).map { it.toLong() }

private fun parseLineAsSingleRace(l: String) = l.replace(" ", "").split(":")[1].toLong()

private fun List<Int>.multiply() = this.fold(1L) { acc, i -> acc * i }
