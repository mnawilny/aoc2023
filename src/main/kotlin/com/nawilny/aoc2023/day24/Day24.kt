package com.nawilny.aoc2023.day24

import com.nawilny.aoc2023.commons.Input

fun main() {

    val input = Input.readFileLinesNormalized("day24", "input.txt")
//    val testArea = 7L..27L
    val testArea = 200000000000000L..400000000000000L
    val hailstones = input.map { parseHailstone(it) }

    val pairs = hailstones.withIndex().flatMap { h1 ->
        hailstones.drop(h1.index + 1).map { Pair(h1.value, it) }
    }
    println(pairs.count { areCrossing(it.first, it.second, testArea) })

    // for part 2 I used https://matheclipse.org/
    // used 3 first points from input (needed points with different vx, vy and vz) and created query:
    // Solve({39*t1+233210433951170==ax*t1+bx, -98*t1+272655040388795==ay*t1+by, 166*t1+179982504986147==az*t1+bz, -71*t2+385274025881243==ax*t2+bx, -36*t2+351578921558552==ay*t2+by, -9*t2+375160114124378==az*t2+bz, 36*t3+298962016918939==ax*t3+bx, 8*t3+322446494312107==ay*t3+by, 96*t3+293073189215975==az*t3+bz}, {ax,ay,az,bx,by,bz,t1,t2,t3})
    // result was
    // {{𝖺𝗑−>𝟣𝟦𝟪,𝖺𝗒−>𝟣𝟧𝟫,𝖺𝗓−>𝟤𝟦𝟫,𝖻𝗑−>𝟣𝟫𝟦𝟩𝟤𝟥𝟧𝟣𝟪𝟥𝟨𝟩𝟥𝟥𝟫,𝖻𝗒−>𝟣𝟪𝟣𝟫𝟣𝟢𝟨𝟨𝟣𝟦𝟦𝟥𝟦𝟥𝟤,𝖻𝗓−>𝟣𝟧𝟢𝟨𝟩𝟧𝟫𝟧𝟦𝟧𝟪𝟩𝟦𝟧𝟢,𝗍𝟣−>𝟥𝟧𝟥𝟢𝟫𝟢𝟫𝟨𝟪𝟨𝟧𝟫,𝗍𝟤−>𝟪𝟩𝟢𝟢𝟫𝟥𝟨𝟦𝟣𝟨𝟣𝟨,𝗍𝟥−>𝟫𝟥𝟢𝟩𝟢𝟢𝟪𝟩𝟫𝟫𝟤𝟧}}
    // so solution was
    // 𝟣𝟫𝟦𝟩𝟤𝟥𝟧𝟣𝟪𝟥𝟨𝟩𝟥𝟥𝟫+𝟣𝟪𝟣𝟫𝟣𝟢𝟨𝟨𝟣𝟦𝟦𝟥𝟦𝟥𝟤+𝟣𝟧𝟢𝟨𝟩𝟧𝟫𝟧𝟦𝟧𝟪𝟩𝟦𝟧𝟢
}


private fun areCrossing(h1: Hailstone, h2: Hailstone, testArea: LongRange): Boolean {
    val p = getCrossing(h1, h2) ?: return false
    return p.x >= testArea.first && p.y >= testArea.first
            && p.x <= testArea.last && p.y <= testArea.last
            && h1.isInFuture(p) && h2.isInFuture(p)
}

private fun getCrossing(h1: Hailstone, h2: Hailstone): Point? {
    val f1 = h1.getFunction()
    val f2 = h2.getFunction()
    if (f1.a == f2.a) {
        return null
    }
    val x = (f2.b - f1.b) / (f1.a - f2.a)
    val y = (f1.a * x) + f1.b
    return Point(x, y)
}

private data class Point(val x: Double, val y: Double)

private data class Hailstone(val p: Point, val v: Point) {
    fun getFunction(): LinearFunction {
        val a = v.y / v.x
        val b = p.y - (a * p.x)
        return LinearFunction(a, b)
    }

    fun isInFuture(a: Point): Boolean {
        val xOK = if (v.x > 0) a.x >= p.x else a.x <= p.x
        val yOK = if (v.y > 0) a.y >= p.y else a.y <= p.y
        return xOK && yOK
    }
}

private data class LinearFunction(val a: Double, val b: Double)

private fun parsePoint(s: String): Point {
    val parts = s.split(", ").map { it.trim().toDouble() }
    return Point(parts[0], parts[1])
}

private fun parseHailstone(s: String): Hailstone {
    val parts = s.split(" @ ").map { parsePoint(it) }
    return Hailstone(parts[0], parts[1])
}
