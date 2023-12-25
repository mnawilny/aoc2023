package com.nawilny.aoc2023.day25

import com.nawilny.aoc2023.commons.Input
import kotlin.math.max

fun main() {
    val input = Input.readFileLinesNormalized("day25", "input.txt")

    val nodes = parseNodes(input)

    val minCut = findMinCutStoerWagnerAlgorithm(nodes)
    println(minCut)
    val solution = minCut.second.toLong() * (nodes.size - minCut.second)
    println(solution)
}

private fun findMinCutStoerWagnerAlgorithm(nodes: Map<String, Node>): Pair<Int, Int> {
    val nodesWithWeightMap = copy(nodes)

    var minCut = Pair(Int.MAX_VALUE, 0)

    while (nodesWithWeightMap.size > 1) {
//        println(nodesWithWeightMap.size)
        val cut = findACut(nodesWithWeightMap)
//        println(cut)
        if (cut.cutSize < minCut.first) {
            val n1 = nodesWithWeightMap[cut.n1]!!
            val n2 = nodesWithWeightMap[cut.n2]!!
            minCut = Pair(cut.cutSize, max(n1.originalNodesCount, n2.originalNodesCount))
        }
        mergeNodes(cut.n1, cut.n2, nodesWithWeightMap)
    }
    return minCut
}

private fun findACut(nodesWithWeightMap: Map<String, Node>): Cut {
    val newNodes = copy(nodesWithWeightMap)
    var currentNode = newNodes.values.first()
    var previousNode = currentNode
    var previousPreviousNode = currentNode
    var latestCutSize = 0
    while (newNodes.size > 1) {
        val next = currentNode.connections.maxBy { it.value }
        latestCutSize = next.value
        val mergedNode = mergeNodes(currentNode.id, next.key.id, newNodes)
        previousPreviousNode = previousNode
        previousNode = next.key
        currentNode = mergedNode
    }
    return Cut(previousNode.id, previousPreviousNode.id, latestCutSize)
}

private fun copy(map: Map<String, Node>): MutableMap<String, Node> {
    val newMap = mutableMapOf<String, Node>()
    map.forEach { newMap[it.key] = Node(it.key, it.value.originalNodesCount) }
    map.forEach { n ->
        n.value.connections.forEach {
            newMap[n.key]!!.connections[newMap[it.key.id]!!] = it.value
        }
    }
    return newMap
}

private data class Cut(val n1: String, val n2: String, val cutSize: Int)

private fun mergeNodes(id1: String, id2: String, nodes: MutableMap<String, Node>): Node {
    val n1 = nodes[id1]!!
    val n2 = nodes[id2]!!
    val mergedNode = Node("$id1-$id2", n1.originalNodesCount + n2.originalNodesCount)
    mergedNode.connections.putAll(n1.connections)
    n2.connections.forEach { c ->
        val existing = mergedNode.connections[c.key]
        if (existing != null) {
            mergedNode.connections[c.key] = existing + c.value
        } else {
            mergedNode.connections[c.key] = c.value
        }
    }
    mergedNode.connections.remove(n1)
    mergedNode.connections.remove(n2)
    mergedNode.connections.forEach { c ->
        c.key.connections.remove(n1)
        c.key.connections.remove(n2)
        c.key.connections[mergedNode] = c.value
    }
    nodes.remove(n1.id)
    nodes.remove(n2.id)
    nodes[mergedNode.id] = mergedNode
    return mergedNode
}

private data class Node(val id: String, val originalNodesCount: Int) {
    val connections: MutableMap<Node, Int> = mutableMapOf()
}

private fun parseNodes(input: List<String>): Map<String, Node> {
    val nodes = input.map { parseLine(it) }.flatMap {
        it.second.plus(it.first)
    }.toSet().map { Node(it, 1) }.associateBy { it.id }

    input.map { parseLine(it) }.forEach { l ->
        val leftId = l.first
        val left = nodes[leftId]!!
        l.second.forEach {
            val right = nodes[it]!!
            left.connections[right] = 1
            right.connections[left] = 1
        }
    }
    return nodes
}

private fun parseLine(s: String): Pair<String, List<String>> {
    val parts = s.split(": ")
    return Pair(parts[0], parts[1].split(" "))
}
