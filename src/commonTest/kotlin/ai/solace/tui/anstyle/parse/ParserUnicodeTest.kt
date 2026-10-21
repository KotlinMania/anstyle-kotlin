// port-lint: tests crates/anstyle-parse/tests/testsuite.rs
package ai.solace.tui.anstyle.parse

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ParserUnicodeTest {
    @Test
    fun advanceUtf8() {
        val input = "Aé€😀\uDBFF\uDFFFZ".encodeToByteArray()
        assertEquals(listOf(0x41, 0xE9, 0x20AC, 0x1F600, 0x10FFFF, 0x5A), print(input))
    }

    @Test
    fun scalarBoundaries() {
        val cases =
            listOf(
                listOf(0x00) to 0x00,
                listOf(0x7F) to 0x7F,
                listOf(0xC2, 0x80) to 0x80,
                listOf(0xDF, 0xBF) to 0x7FF,
                listOf(0xE0, 0xA0, 0x80) to 0x800,
                listOf(0xED, 0x9F, 0xBF) to 0xD7FF,
                listOf(0xEE, 0x80, 0x80) to 0xE000,
                listOf(0xEF, 0xBF, 0xBF) to 0xFFFF,
                listOf(0xF0, 0x90, 0x80, 0x80) to 0x10000,
                listOf(0xF0, 0x9F, 0x98, 0x80) to 0x1F600,
                listOf(0xF4, 0x8F, 0xBF, 0xBF) to 0x10FFFF,
            )
        for ((bytes, expected) in cases) {
            assertEquals(listOf(expected), add(bytes), bytes.toString())
        }
    }

    @Test
    fun invalidSequencesAndRecovery() {
        val cases =
            listOf(
                listOf(0xC0, 0x80) to listOf(0xFFFD, 0xFFFD),
                listOf(0xE0, 0x80, 0x80) to listOf(0xFFFD, 0xFFFD),
                listOf(0xED, 0xA0, 0x80) to listOf(0xFFFD, 0xFFFD),
                listOf(0xF0, 0x80, 0x80, 0x80) to listOf(0xFFFD, 0xFFFD, 0xFFFD),
                listOf(0xF4, 0x90, 0x80, 0x80) to listOf(0xFFFD, 0xFFFD, 0xFFFD),
                listOf(0xF5, 0x80, 0x80, 0x80) to listOf(0xFFFD, 0xFFFD, 0xFFFD, 0xFFFD),
                listOf(0xC2, 0x41, 0x42) to listOf(0xFFFD, 0x42),
                listOf(0xF0, 0x9F, 0x41, 0x42) to listOf(0xFFFD, 0x42),
            )
        for ((bytes, expected) in cases) {
            assertEquals(expected, add(bytes), bytes.toString())
        }
        val parser = Utf8Parser()
        assertNull(parser.add(0xF0u))
        assertNull(parser.add(0x9Fu))
    }

    @Test
    fun supplementaryScalarFollowedByEscape() {
        val dispatcher = Dispatcher()
        val parser = Parser(Utf8Parser())
        for (byte in "😀\u001B[31mZ".encodeToByteArray()) {
            parser.advance(dispatcher, byte.toUByte())
        }
        assertEquals(3, dispatcher.dispatched.size)
        assertEquals(0x1F600, (dispatcher.dispatched[0] as Sequence.Print).c)
        assertEquals(Sequence.Csi(listOf(listOf(31u.toUShort())), emptyList(), false, 'm'.code.toUByte()), dispatcher.dispatched[1])
        assertEquals(0x5A, (dispatcher.dispatched[2] as Sequence.Print).c)
    }

    private fun print(bytes: ByteArray): List<Int> {
        val output = mutableListOf<Int>()
        val performer =
            object : Perform {
                override fun print(c: Int) {
                    output.add(c)
                }
            }
        val parser = Parser(Utf8Parser())
        for (byte in bytes) parser.advance(performer, byte.toUByte())
        return output
    }

    private fun add(bytes: List<Int>): List<Int> {
        val parser = Utf8Parser()
        return bytes.mapNotNull { parser.add(it.toUByte()) }
    }
}
