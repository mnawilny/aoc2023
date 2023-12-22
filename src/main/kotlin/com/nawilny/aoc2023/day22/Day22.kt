package com.nawilny.aoc2023.day22

import com.nawilny.aoc2023.commons.Input
import kotlin.math.max
import kotlin.math.min

fun main() {
    val input = Input.readFileLinesNormalized("day22", "input.txt")
    val bricks = input.map { Brick.parse(it) }
    val fallenBricks = fallAllBricks(bricks).first
    val brickDependencies = getBrickDependencies(fallenBricks)

    val bricksThatCanBeRemoved = fallenBricks.count { b ->
        brickDependencies.filter { it.restsOn.contains(b) }.all { it.restOnMoreThanOneBrick() }
    }
    println(bricksThatCanBeRemoved)

    var bricksThatWouldFallCount = 0
    fallenBricks.forEach { b ->
        val bricksWithoutABrick = fallenBricks.minus(b)
        bricksThatWouldFallCount += fallAllBricks(bricksWithoutABrick).second
    }
    println(bricksThatWouldFallCount)
}

private fun fallAllBricks(bricks: List<Brick>): Pair<List<Brick>, Int> {
    val fallenBricks = mutableListOf<Brick>()
    val fallenParts = mutableSetOf<Point>()
    var fallenBricksCount = 0
    bricks.sortedBy { it.bottom }.forEach { brick ->
        val points = brick.getBase()
        val z = points.first().z
        var fallDistance = 0

        while (z - fallDistance > 0 && points.none { fallenParts.contains(it.moveDown(fallDistance)) }) {
            fallDistance++
        }

        val fallenBrick = brick.moveDown(fallDistance - 1)
        fallenBricks.add(fallenBrick)
        fallenParts.addAll(fallenBrick.allPoints)
        if (fallDistance > 1) {
            fallenBricksCount++
        }
    }
    return Pair(fallenBricks, fallenBricksCount)
}

private fun getBrickDependencies(bricks: List<Brick>): List<BrickDependency> {
    return bricks.map { brick ->
        val points = brick.getBase()
        val restsOn = bricks.filter { b -> points.any { b.contains(it.moveDown(1)) } }
        BrickDependency(brick, restsOn.toSet())
    }
}

private data class BrickDependency(val brick: Brick, val restsOn: Set<Brick>) {
    fun restOnMoreThanOneBrick() = restsOn.count() > 1
}

private data class Brick(val p1: Point, val p2: Point) {

    val allPoints = (min(p1.x, p2.x)..max(p1.x, p2.x)).flatMap { x ->
        (min(p1.y, p2.y)..max(p1.y, p2.y)).flatMap { y ->
            (min(p1.z, p2.z)..max(p1.z, p2.z)).map { z ->
                Point(x, y, z)
            }
        }
    }.toSet()

    val bottom = min(p1.z, p2.z)

    fun getBase(): Set<Point> {
        return (min(p1.x, p2.x)..max(p1.x, p2.x)).flatMap { x ->
            (min(p1.y, p2.y)..max(p1.y, p2.y)).map { Point(x, it, bottom) }
        }.toSet()
    }

    fun moveDown(dist: Int) = Brick(p1.moveDown(dist), p2.moveDown(dist))

    fun contains(p: Point) = allPoints.contains(p)

    companion object {
        fun parse(s: String): Brick {
            val parts = s.split("~")
            return Brick(Point.parse(parts[0]), Point.parse(parts[1]))
        }
    }
}

private data class Point(val x: Int, val y: Int, val z: Int) {

    fun moveDown(dist: Int) = Point(x, y, z - dist)

    companion object {
        fun parse(s: String): Point {
            val parts = s.split(",").map { it.toInt() }
            return Point(parts[0], parts[1], parts[2])
        }
    }
}
