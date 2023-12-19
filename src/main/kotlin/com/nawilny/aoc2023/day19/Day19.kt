package com.nawilny.aoc2023.day19

import com.nawilny.aoc2023.commons.Input
import com.nawilny.aoc2023.commons.Input.println

fun main() {
    val input = Input.divideByNewLines(Input.readFileLines("day19", "input.txt"))

    val workflows = input[0].map { parseWorkflow(it) }.associateBy { it.name }
    val parts = input[1].map { parsePart(it) }

    parts.filter { isAccepted(it, workflows) }.sumOf { it.ratingsSum() }.println()

    val fieldRanges = getFieldRanges(workflows)
    var counter = 0L
    for (x in fieldRanges[Field.X]!!) {
        println(x)
        for (m in fieldRanges[Field.M]!!) {
            for (a in fieldRanges[Field.A]!!) {
                for (s in fieldRanges[Field.S]!!) {
                    if (isAccepted(Part(x.first, m.first, a.first, s.first), workflows)) {
                        counter += x.count().toLong() * m.count().toLong() * a.count().toLong() * s.count().toLong()
                    }
                }
            }
        }
    }
    println(counter)
}

private fun getFieldRanges(workflows: Map<String, Workflow>): Map<Field, List<IntRange>> {
    val fieldRanges = Field.values().associateWith { mutableSetOf(4001) }
    val conditions = workflows.flatMap { it.value.rules }.mapNotNull { it.condition }
    conditions.forEach {
        val ranges = fieldRanges[it.field]!!
        when (it.operator) {
            Rule.Condition.Operator.G -> ranges.add(it.value + 1)
            Rule.Condition.Operator.L -> ranges.add(it.value)
        }
    }
    return fieldRanges.mapValues { v ->
        var prev = 1
        val ranges = mutableListOf<IntRange>()
        v.value.sorted().forEach {
            ranges.add(IntRange(prev, it - 1))
            prev = it
        }
        ranges
    }
}

private fun isAccepted(part: Part, workflows: Map<String, Workflow>): Boolean {
    var workflow = workflows["in"]!!
    while (true) {
        when (val destination = workflow.execute(part)) {
            "A" -> return true
            "R" -> return false
            else -> workflow = workflows[destination]!!
        }
    }
}

private enum class Field(val get: (Part) -> Int) {
    X({ it.x }), M({ it.m }), A({ it.a }), S({ it.s })
}

private data class Rule(val condition: Condition?, val destination: String) {

    data class Condition(val field: Field, val operator: Operator, val value: Int) {

        enum class Operator(val compare: (a: Int, b: Int) -> Boolean) {
            L({ a, b -> a < b }), G({ a, b -> a > b })
        }

        fun execute(p: Part) = operator.compare(field.get(p), value)

    }

    fun execute(p: Part): Boolean {
        return condition?.execute(p) ?: true
    }
}

private data class Workflow(val name: String, val rules: List<Rule>) {
    fun execute(p: Part) = rules.first { it.execute(p) }.destination
}

private data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {
    fun ratingsSum() = x + m + a + s
}

private fun parseWorkflow(l: String): Workflow {
    val parts = l.split("{")
    val name = parts[0]
    val rules = parts[1].dropLast(1).split(",").map { r ->
        val ruleParts = r.split(":")
        if (ruleParts.size == 1) {
            Rule(null, ruleParts[0])
        } else {
            val field = Field.valueOf(ruleParts[0][0].uppercase())
            val operator = when (ruleParts[0][1]) {
                '>' -> Rule.Condition.Operator.G
                '<' -> Rule.Condition.Operator.L
                else -> error("Invalid operator ${ruleParts[0][1]}")
            }
            val value = ruleParts[0].substring(2 until ruleParts[0].length).toInt()

            Rule(Rule.Condition(field, operator, value), ruleParts[1])
        }
    }
    return Workflow(name, rules)
}

private fun parsePart(l: String): Part {
    val values = l.substring(1..l.length - 2).split(",").map { it.drop(2).toInt() }
    return Part(values[0], values[1], values[2], values[3])
}
