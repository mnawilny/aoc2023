package com.nawilny.aoc2023.day21

import com.nawilny.aoc2023.commons.Input
import com.nawilny.aoc2023.commons.Input.println
import kotlin.math.abs

fun main() {
    val input = Input.readFileLinesNormalized("day21", "input.txt")

    val gardenMap = GardenMap.parse(input)
    val start = GardenMap.getStart(input)

    solvePart1(64, start, gardenMap).println()

    // Assumptions for part 2
    // - map is square
    // - start is in the middle of a map
    // - edge of the map is all '.'
    // - lines crossing the middle of a map is '.' (example does not fulfill this condition)
    // - map side is odd (parity fluctuates)

    val steps = 26501365
    val stepsToReachEdge = ((gardenMap.side / 2) + 1).toLong()
    println("stepsToReachEdge - $stepsToReachEdge")

    val mapsThatCanBeReached = (((steps - stepsToReachEdge) / gardenMap.side) + 1).toLong()
    println("mapsThatCanBeReached - $mapsThatCanBeReached")

    if (mapsThatCanBeReached % 2 != 0L) {
        error("Not supported - I'm too lazy")
    }

    println("--- full even ")
    val x = (mapsThatCanBeReached / 2) - 1
    val fullEvenMaps = ((4 + (4 * ((2 * x) - 1))) * x) / 2
    println("fullEvenMaps - $fullEvenMaps")
    val resultForEvenInstance = getAllPointsThatCanBeReached(gardenMap, 0)
    println("resultForEvenInstance - $resultForEvenInstance")
    val totalFullEvenInstancesResult = fullEvenMaps * resultForEvenInstance
    println("totalFullEvenInstancesResult - $totalFullEvenInstancesResult")

    println("--- full odd ")
    val x2 = (mapsThatCanBeReached / 2) - 1
    val fullOddMaps = (((8 + (8 * x2)) * x2) / 2) + 1
    println("fullOddMaps - $fullOddMaps")
    val resultForOddInstance = getAllPointsThatCanBeReached(gardenMap, 1)
    println("resultForOddInstance - $resultForOddInstance")
    val totalFullOddInstancesResult = fullOddMaps * resultForOddInstance
    println("totalFullOddInstancesResult - $totalFullOddInstancesResult")

    val sidePoints = listOf(
        Point(start.x, 0),
        Point(start.x, gardenMap.side - 1),
        Point(0, start.y),
        Point(gardenMap.side - 1, start.y)
    )
    println("--- inner side ")
    val stepsToReachInnerSide1 = stepsToReachEdge + (mapsThatCanBeReached - 2) * gardenMap.side
    println("stepsToReachInnerSide1 - $stepsToReachInnerSide1")
    val stepsInInnerSide1 = steps - stepsToReachInnerSide1
    println("stepsInInnerSide1 - $stepsInInnerSide1")
    val innerSide1Value = sidePoints.sumOf { getPointsThatCanBeReached(stepsInInnerSide1, it, gardenMap, 0) }
    println("innerSide1Value - $innerSide1Value")

    println("--- outer side ")
    val stepsToReachOuterSide1 = stepsToReachEdge + (mapsThatCanBeReached - 1) * gardenMap.side
    println("stepsToReachOuterSide1 - $stepsToReachOuterSide1")
    val stepsInOuterSide1 = steps - stepsToReachOuterSide1
    println("stepsInOuterSide1 - $stepsInOuterSide1")
    val outerSide1Value = sidePoints.sumOf { getPointsThatCanBeReached(stepsInOuterSide1, it, gardenMap, 1) }
    println("outerSide1Value - $outerSide1Value")

    val cornerPoints = listOf(
        Point(0, 0),
        Point(0, gardenMap.side - 1),
        Point(gardenMap.side - 1, 0),
        Point(gardenMap.side - 1, gardenMap.side - 1)
    )
    println("--- inner edge ")
    val innerEdgePerSideCount = mapsThatCanBeReached - 2
    println("innerEdgePerSideCount - $innerEdgePerSideCount")
    val stepsToReachInnerEdge =
        stepsToReachEdge + (gardenMap.side / 2) + 1 + (mapsThatCanBeReached - 3) * gardenMap.side
    println("stepsToReachInnerEdge - $stepsToReachInnerEdge")
    val remainingStepsInInnerEdge = steps - stepsToReachInnerEdge
    println("remainingStepsInInnerEdge - $remainingStepsInInnerEdge")
    val innerEdgeValue = innerEdgePerSideCount * cornerPoints.sumOf {
        getPointsThatCanBeReached(remainingStepsInInnerEdge, it, gardenMap, 0)
    }
    println("innerEdgeValue - $innerEdgeValue")

    println("--- middle edge ")
    val middleEdgePerSideCount = mapsThatCanBeReached - 1
    println("middleEdgePerSideCount - $middleEdgePerSideCount")
    val stepsToReachMiddleEdge =
        stepsToReachEdge + (gardenMap.side / 2) + 1 + (mapsThatCanBeReached - 2) * gardenMap.side
    println("stepsToReachMiddleEdge - $stepsToReachMiddleEdge")
    val remainingStepsInMiddleEdge = steps - stepsToReachMiddleEdge
    println("remainingStepsInMiddleEdge - $remainingStepsInMiddleEdge")
    val middleEdgeValue = middleEdgePerSideCount * cornerPoints.sumOf {
        getPointsThatCanBeReached(remainingStepsInMiddleEdge, it, gardenMap, 1)
    }
    println("middleEdgeValue - $middleEdgeValue")

    println("--- outer edge ")
    val outerEdgePerSideCount = mapsThatCanBeReached
    println("outerEdgePerSideCount - $outerEdgePerSideCount")
    val stepsToReachOuterEdge =
        stepsToReachEdge + (gardenMap.side / 2) + 1 + (mapsThatCanBeReached - 1) * gardenMap.side
    println("stepsToReachOuterEdge - $stepsToReachOuterEdge")
    val remainingStepsInOuterEdge = steps - stepsToReachOuterEdge
    println("remainingStepsInOuterEdge - $remainingStepsInOuterEdge")
    val outerEdgeValue = if (remainingStepsInOuterEdge >= 0) {
        outerEdgePerSideCount * cornerPoints.sumOf {
            getPointsThatCanBeReached(remainingStepsInOuterEdge, it, gardenMap, 0)
        }
    } else {
        0
    }
    println("outerEdgeValue - $outerEdgeValue")

    println("------------------------------------")
    val result =
        totalFullEvenInstancesResult + totalFullOddInstancesResult + innerSide1Value + outerSide1Value + innerEdgeValue + middleEdgeValue + outerEdgeValue
    println("result - $result")

//    solvePart2NaiveApproach(steps, start, gardenMap, false).println()
}

private fun solvePart1(steps: Int, start: Point, gardenMap: GardenMap): Long {
    val requiredEndParity = (start.parity() + (steps % 2)) % 2
    return getPointsThatCanBeReached(steps.toLong(), start, gardenMap, requiredEndParity)
}

private fun solvePart2NaiveApproach(steps: Int, start: Point, gardenMap: GardenMap, print: Boolean): Long {
    val requiredEndParity = (start.parity() + (steps % 2)) % 2
    val visitedPoints = mutableSetOf(start)
    var lastPoints = setOf(start)
    repeat(steps) {
        val newLastPoints = lastPoints.flatMap { point ->
            gardenMap.getMovesForInfiniteBoard(point).filter { !visitedPoints.contains(it) }
        }.toSet()
        visitedPoints.addAll(newLastPoints)
        lastPoints = newLastPoints
    }

    if (print) {
        val resultPoints = visitedPoints.filter { it.parity() == requiredEndParity }.toSet()
        for (y in (visitedPoints.minOf { it.y } - 1)..(visitedPoints.maxOf { it.y } + 1)) {
            for (x in (visitedPoints.minOf { it.x } - 1)..(visitedPoints.maxOf { it.x } + 1)) {
                val p = Point(x, y)
                var absx = x % gardenMap.side
                if (absx < 0) {
                    absx += gardenMap.side
                }
                var absy = y % gardenMap.side
                if (absy < 0) {
                    absy += gardenMap.side
                }
                val absp = Point(absx, absy)
                if (p == start) {
                    print("S")
                } else if (resultPoints.contains(p)) {
                    print("0")
                } else if (gardenMap.rocks.contains(absp)) {
                    print("#")
                } else {
                    print(".")
                }
            }
            println("")
        }
    }

    return visitedPoints.count { it.parity() == requiredEndParity }.toLong()
}

private fun getAllPointsThatCanBeReached(gardenMap: GardenMap, parity: Int): Long {
    val visitedPoints = mutableSetOf(Point(0, 0))
    var lastPoints = setOf(Point(0, 0))
    while (lastPoints.isNotEmpty()) {
        val newLastPoints = lastPoints.flatMap { point ->
            gardenMap.getMoves(point).filter { !visitedPoints.contains(it) }
        }.toSet()
        visitedPoints.addAll(newLastPoints)
        lastPoints = newLastPoints
    }

    return visitedPoints.count { it.parity() == parity }.toLong()
}

private fun getPointsThatCanBeReached(steps: Long, start: Point, gardenMap: GardenMap, parity: Int): Long {
    val visitedPoints = getPointsThatCanBeVisited(steps.toInt(), start, gardenMap)
    return visitedPoints.count { it.parity() == parity }.toLong()
}

private fun getPointsThatCanBeVisited(steps: Int, start: Point, gardenMap: GardenMap): Set<Point> {
    val visitedPoints = mutableSetOf(start)
    var lastPoints = setOf(start)
    repeat(steps) {
        val newLastPoints = lastPoints.flatMap { point ->
            gardenMap.getMoves(point).filter { !visitedPoints.contains(it) }
        }.toSet()
        visitedPoints.addAll(newLastPoints)
        lastPoints = newLastPoints
    }
    return visitedPoints
}

private data class Point(val x: Int, val y: Int) {
    fun parity() = abs(x + y) % 2
}

private data class GardenMap(val side: Int, val rocks: Set<Point>) {

    fun getMoves(p: Point): List<Point> {
        return listOf(
            Point(p.x - 1, p.y),
            Point(p.x + 1, p.y),
            Point(p.x, p.y - 1),
            Point(p.x, p.y + 1)
        ).filter { it.x >= 0 && it.y >= 0 && it.x < side && it.y < side && !rocks.contains(Point(it.x, it.y)) }
    }

    fun getMovesForInfiniteBoard(p: Point): List<Point> {
        return listOf(
            Point(p.x - 1, p.y),
            Point(p.x + 1, p.y),
            Point(p.x, p.y - 1),
            Point(p.x, p.y + 1)
        ).filter {
            !rocks.contains(Point(modSide(it.x), modSide(it.y)))
        }
    }

    private fun modSide(a: Int): Int {
        val m = a % side
        return if (m < 0) m + side else m
    }

    companion object {
        fun parse(input: List<String>): GardenMap {
            val rocks = input.withIndex().flatMap { line ->
                line.value.withIndex().filter { it.value == '#' }.map { Point(it.index, line.index) }
            }.toSet()
            return GardenMap(input.size, rocks)
        }

        fun getStart(input: List<String>): Point {
            val line = input.withIndex().first { it.value.contains('S') }
            return Point(line.value.indexOf('S'), line.index)
        }
    }
}
