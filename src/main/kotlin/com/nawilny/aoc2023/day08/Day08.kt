package com.nawilny.aoc2023.day08

import com.nawilny.aoc2023.commons.Input
import com.nawilny.aoc2023.commons.Input.println

fun main() {
    val input = Input.readFileLinesNormalized("day08", "input.txt")
    val instructions = Instructions(input[0])
    val nodes = input.drop(1).map { parseToNode(it) }.associateBy { it.id }
    nodes.values.forEach { it.updateReferences(nodes) }

    // val part 1
    countStepsTill(nodes["AAA"]!!, instructions) { it.id == "ZZZ" }.first.println()

    // val part 2
    val startNodes = nodes.values.filter { it.id.endsWith("A") }

    // in the input file all start nodes have only one end node and start -> end and end -> end journey takes
    // exactly the same amount of steps ...
    startNodes.forEach { node ->
        instructions.reset()
        val firstEndNode = countStepsTill(node, instructions) { it.id.endsWith("Z") }
        val next = firstEndNode.second.move(instructions.next())
        val secondEndNode = countStepsTill(next, instructions) { it.id.endsWith("Z") }
        check(firstEndNode.first == secondEndNode.first + 1)
        check(firstEndNode.second == secondEndNode.second)
    }

    // ... so we can solve this part by calculating LCM
    val endReachTimes = startNodes.map { node ->
        instructions.reset()
        countStepsTill(node, instructions) { it.id.endsWith("Z") }.first
    }
    println(endReachTimes.leastCommonMultiple())
}

private class Instructions(val dirs: String) {
    private var i = 0
    fun next(): Char {
        val c = dirs[i++]
        if (i >= dirs.length) {
            i = 0
        }
        return c
    }

    fun reset() {
        i = 0
    }
}

private fun countStepsTill(node: Node, instructions: Instructions, endPredicate: (Node) -> Boolean): Pair<Long, Node> {
    var steps = 0L
    var currentNode = node
    while (!endPredicate(currentNode)) {
        currentNode = currentNode.move(instructions.next())
        steps++
    }
    return Pair(steps, currentNode)
}

private data class Node(val id: String, val leftId: String, val rightId: String) {
    private var left: Node? = null
    private var right: Node? = null

    fun move(c: Char): Node {
        return when (c) {
            'L' -> left!!
            'R' -> right!!
            else -> error("Unknown direction")
        }
    }

    fun updateReferences(nodes: Map<String, Node>) {
        left = nodes[leftId]!!
        right = nodes[rightId]!!
    }
}

private fun parseToNode(s: String): Node {
    val id = s.substring(0, 3)
    val left = s.substring(7, 10)
    val right = s.substring(12, 15)
    return Node(id, left, right)
}

private fun findLeastCommonMultiple(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}

private fun List<Long>.leastCommonMultiple(): Long {
    return this.fold(1) { acc, i ->
        findLeastCommonMultiple(acc, i)
    }
}
