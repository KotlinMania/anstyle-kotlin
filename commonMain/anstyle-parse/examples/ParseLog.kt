package anstyle.parse.examples

// Parse input from stdin and log actions on stdout

import anstyle.parse.AsciiParser
import anstyle.parse.Params
import anstyle.parse.Parser
import anstyle.parse.Perform

// Rust original:
// use std::io::{self, Read};
// use anstyle_parse::{DefaultCharAccumulator, Params, Parser, Perform};

/**
 * A type implementing Perform that just logs actions
 */
class Log : Perform {
    override fun print(c: Char) {
        println("[print] '$c'")
    }

    override fun execute(byte: UByte) {
        println("[execute] ${byte.toString(16).padStart(2, '0')}")
    }

    override fun hook(params: Params, intermediates: UByteArray, ignore: Boolean, action: UByte) {
        val paramsStr = params.toList().map { it.toList() }
        val intermediatesStr = intermediates.toList()
        println("[hook] params=$paramsStr, intermediates=$intermediatesStr, ignore=$ignore, char=$action")
    }

    override fun put(byte: UByte) {
        println("[put] ${byte.toString(16).padStart(2, '0')}")
    }

    override fun unhook() {
        println("[unhook]")
    }

    override fun oscDispatch(params: Array<ByteArray>, bellTerminated: Boolean) {
        val paramsStr = params.map { it.toList() }
        println("[osc_dispatch] params=$paramsStr bell_terminated=$bellTerminated")
    }

    override fun csiDispatch(params: Params, intermediates: UByteArray, ignore: Boolean, action: UByte) {
        val paramsStr = params.toList().map { it.toList() }
        val intermediatesStr = intermediates.toList()
        println("[csi_dispatch] params=$paramsStr, intermediates=$intermediatesStr, ignore=$ignore, char=$action")
    }

    override fun escDispatch(intermediates: UByteArray, ignore: Boolean, byte: UByte) {
        val intermediatesStr = intermediates.toList()
        println("[esc_dispatch] intermediates=$intermediatesStr, ignore=$ignore, byte=${byte.toString(16).padStart(2, '0')}")
    }
}

/**
 * Parse the given byte array and log all ANSI sequences found
 */
fun parseAndLog(input: ByteArray) {
    val parser = Parser<AsciiParser>()
    val performer = Log()

    for (byte in input) {
        parser.advance(performer, byte.toUByte())
    }
}

/**
 * Parse a string and log all ANSI sequences found
 */
fun parseAndLog(input: String) {
    parseAndLog(input.encodeToByteArray())
}

// Note: In Kotlin/Native, main function with stdin reading would be:
// fun main() {
//     // Read from stdin and process
//     val input = generateSequence(::readLine).joinToString("\n")
//     parseAndLog(input)
// }
