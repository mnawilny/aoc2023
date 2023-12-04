package com.nawilny.aoc2023.day04

import com.nawilny.aoc2022.common.Input

fun main() {
    val input = Input.readFileLinesNormalized("day04", "input.txt")
    val cards = input.map { Card.parse(it) }

    // part 1
    println(cards.map { it.getMatchingCards() }.sumOf { calculateScore(it) })

    // part 2
    val cardsCounter = cards.associate { it.id to 1 }.toMutableMap()
    cards.forEach { card ->
        val matching = card.getMatchingCards()
        val amount = cardsCounter[card.id]!!
        for (i in (card.id + 1)..(card.id + matching)) {
            cardsCounter[i] = cardsCounter[i]!! + amount
        }
    }
    println(cardsCounter.values.sum())
}

private fun calculateScore(matchingCards: Int): Int {
    return if (matchingCards == 0) {
        0
    } else {
        var score = 1
        for (i in 2..matchingCards) {
            score *= 2
        }
        score
    }
}

private data class Card(val id: Int, val winningNumbers: Set<Int>, val currentNumbers: Set<Int>) {

    fun getMatchingCards() = winningNumbers.count { currentNumbers.contains(it) }

    companion object {
        fun parse(line: String): Card {
            val parts1 = line.split(": ")
            val id = parts1[0].split(" ").last { it.isNotBlank() }.toInt()
            val parts2 = parts1[1].split("| ")
            val winningNumbers = toNumbersSet(parts2[0])
            val currentNumbers = toNumbersSet(parts2[1])
            return Card(id, winningNumbers, currentNumbers)
        }

        private fun toNumbersSet(s: String) = s.split(" ").filter { it.isNotBlank() }.map { it.toInt() }.toSet()
    }
}
