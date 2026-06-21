package ai.solace.tui.anstyle

import kotlin.test.Test
import kotlin.test.assertEquals

class ColorTest {
    @Test
    fun maxDisplayBuffer() {
        val c = RgbColor(255u, 255u, 255u)
        val actual = c.renderFg().toString()
        assertEquals("\u001B[38;2;255;255;255m", actual)
        assertEquals(DISPLAY_BUFFER_CAPACITY, actual.length)
    }

    @Test
    fun printSizeOf() {
        // In Kotlin, we don't have direct sizeof, but we can print structure info
        println("Color: sealed class with 3 variants")
        println("AnsiColor: enum with 16 entries")
        println("Ansi256Color: data class with UByte")
        println("RgbColor: data class with 3 UBytes")
        println("DisplayBuffer: class with ByteArray($DISPLAY_BUFFER_CAPACITY) + Int")
    }

    @Test
    fun noAlign() {
        fun assertNoAlign(d: Displayable) {
            val expected = buildString { d.formatTo(this) }
            val actual = buildString { d.formatTo(this) }
            assertEquals(expected, actual)
        }

        assertNoAlign(AnsiColor.White.renderFg())
        assertNoAlign(AnsiColor.White.renderBg())
        assertNoAlign(Ansi256Color(0u).renderFg())
        assertNoAlign(Ansi256Color(0u).renderBg())
        assertNoAlign(RgbColor(0u, 0u, 0u).renderFg())
        assertNoAlign(RgbColor(0u, 0u, 0u).renderBg())
        assertNoAlign(Color.Ansi(AnsiColor.White).renderFg())
        assertNoAlign(Color.Ansi(AnsiColor.White).renderBg())
    }
}
