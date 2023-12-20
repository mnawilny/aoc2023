package com.nawilny.aoc2023.day20

import com.nawilny.aoc2023.commons.Input
import java.util.*

fun main() {
    val input = Input.readFileLinesNormalized("day20", "example3.txt")
    val modules = input.map { parseModule(it) }.associateBy { it.name }
    modules.values.forEach { m -> m.destinations.forEach { modules[it]?.onConnect(m) } }

    val total = (1..1000).fold(PressResult(0, 0, false)) { acc, _ -> acc.add(pressButton(modules)) }
    println(total.lowCounter * total.highCounter)

    modules.values.forEach { it.onReset() }

    // Part 2 solved on paper :)
    // the result was the least common denominator of 3769, 4001, 4027, 3929
    var rxReceivedLow = false
    var presses = 0L
    while (!rxReceivedLow) {
        presses++
        rxReceivedLow = pressButton(modules).rxReceivedLow
    }
    println(presses)
}

private fun pressButton(modules: Map<String, Module>): PressResult {
    val pulsesQueue: Queue<Pulse> = LinkedList()
    pulsesQueue.add(Pulse(PulseType.LOW, "button", "broadcaster"))
    var lowCounter = 0L
    var highCounter = 0L
    var rxReceivedLow = false
    while (pulsesQueue.isNotEmpty()) {
        val p = pulsesQueue.remove()
        when (p.type) {
            PulseType.LOW -> lowCounter++
            PulseType.HIGH -> highCounter++
        }
        if (p.destination == "rx" && p.type == PulseType.LOW) {
            rxReceivedLow = true
        }
        if (modules.contains(p.destination)) {
            pulsesQueue.addAll(modules[p.destination]!!.onPulse(p))
        }
    }
    return PressResult(lowCounter, highCounter, rxReceivedLow)
}

private data class PressResult(val lowCounter: Long, val highCounter: Long, val rxReceivedLow: Boolean) {
    fun add(p: PressResult) = PressResult(lowCounter + p.lowCounter, highCounter + p.highCounter, rxReceivedLow)
}

private enum class PulseType { LOW, HIGH }

private data class Pulse(val type: PulseType, val source: String, val destination: String)

private sealed class Module(val name: String, val destinations: List<String>) {
    abstract fun onPulse(pulse: Pulse): List<Pulse>
    open fun onConnect(module: Module) {}
    open fun onReset() {}

    abstract fun getState(): String

    protected fun send(type: PulseType) = destinations.map { Pulse(type, name, it) }
}

private class BroadcastModule(name: String, destinations: List<String>) : Module(name, destinations) {
    override fun onPulse(pulse: Pulse) = send(pulse.type)
    override fun getState() = ""
}

private class FlipFlopModule(name: String, destinations: List<String>) : Module(name, destinations) {
    var on = false

    override fun onPulse(pulse: Pulse): List<Pulse> {
        if (pulse.type == PulseType.HIGH) {
            return listOf()
        }
        val type = when (on) {
            true -> PulseType.LOW
            false -> PulseType.HIGH
        }
        on = !on
        return send(type)
    }

    override fun onReset() {
        on = false
    }

    override fun getState() = on.toString()
}

private class ConjunctionModule(name: String, destinations: List<String>) : Module(name, destinations) {
    val lastPulses = mutableMapOf<String, PulseType>()

    override fun onPulse(pulse: Pulse): List<Pulse> {
        lastPulses[pulse.source] = pulse.type
        return send(if (lastPulses.all { it.value == PulseType.HIGH }) PulseType.LOW else PulseType.HIGH)
    }

    override fun onConnect(module: Module) {
        lastPulses[module.name] = PulseType.LOW
    }

    override fun onReset() {
        lastPulses.keys.forEach { lastPulses[it] = PulseType.LOW }
    }

    override fun getState() = lastPulses.toString()
}

private fun parseModule(line: String): Module {
    val parts = line.split(" -> ")
    val name = parts[0]
    val destinations = parts[1].split(", ")
    return when {
        name == "broadcaster" -> BroadcastModule(name, destinations)
        name.startsWith("%") -> FlipFlopModule(name.drop(1), destinations)
        name.startsWith("&") -> ConjunctionModule(name.drop(1), destinations)
        else -> error("Invalid module name '$name'")
    }
}

