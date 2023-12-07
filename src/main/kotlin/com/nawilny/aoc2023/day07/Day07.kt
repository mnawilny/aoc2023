package com.nawilny.aoc2023.day07

import com.nawilny.aoc2023.commons.Input
import com.nawilny.aoc2023.commons.Input.println

fun main() {
    val input = Input.readFileLinesNormalized("day07", "input.txt")

    getWinnings(input.map { parseHand(it) }).println()
    getWinnings(input.map { parseHandWithJokers(it) }).println()
}

private fun getWinnings(hands: List<Hand>) = hands.sorted().withIndex().sumOf { it.value.bid * (it.index + 1) }

private data class Hand(val cards: List<Int>, val bid: Int) : Comparable<Hand> {

    val type: Int

    init {
        val jokers = cards.count { it == 1 }
        val nonJokers = cards.filter { it != 1 }
        val uniqueCards = nonJokers.distinct()
        val maxReps = uniqueCards.maxOfOrNull { c -> nonJokers.count { it == c } } ?: 0
        type = when (uniqueCards.size) {
            0 -> 7 // All jokers
            1 -> 7 // Five of a kind
            2 -> { // Four of a kind or Full house
                if (maxReps + jokers == 4) 6 else 5
            }

            3 -> { // Three of a kind or Two pair
                if (maxReps + jokers == 3) 4 else 3
            }

            4 -> 2 // One pair
            5 -> 1 // High card
            else -> error("Too many cards ${uniqueCards.size}")
        }
    }

    override fun compareTo(other: Hand): Int {
        if (this.type == other.type) {
            for (i in cards.indices) {
                if (this.cards[i] != other.cards[i]) {
                    return this.cards[i].compareTo(other.cards[i])
                }
            }
            error("Cards ${this.cards} and ${other.cards} are the same")
        } else {
            return this.type.compareTo(other.type)
        }
    }
}

private fun parseHand(s: String, jValue: Int = 11): Hand {
    val parts = s.split(" ")
    val cards = parts[0].map {
        when (it) {
            'T' -> 10
            'J' -> jValue
            'Q' -> 12
            'K' -> 13
            'A' -> 14
            else -> it.digitToInt()
        }
    }
    return Hand(cards, parts[1].toInt())
}

private fun parseHandWithJokers(s: String): Hand {
    return parseHand(s, 1)
}

