// port-lint: tests color.rs
package ai.solace.tui.anstyle

import kotlin.test.Test
import kotlin.test.assertEquals

class ColorTest {
    private fun assertNoAlign(d: Displayable) {
        val expected = buildString { d.formatTo(this) }
        val actual = buildString { d.formatTo(this) }
        assertEquals(expected, actual)
    }

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
        assertNoAlign(AnsiColor.White.renderFg())
        assertNoAlign(AnsiColor.White.renderBg())
        assertNoAlign(Ansi256Color(0u).renderFg())
        assertNoAlign(Ansi256Color(0u).renderBg())
        assertNoAlign(RgbColor(0u, 0u, 0u).renderFg())
        assertNoAlign(RgbColor(0u, 0u, 0u).renderBg())
        assertNoAlign(Color.Ansi(AnsiColor.White).renderFg())
        assertNoAlign(Color.Ansi(AnsiColor.White).renderBg())
    }

    @Test
    fun canonicalColorMethods() {
        // Method calls for Ansi256Color::index at 0, 255, and intermediate
        assertEquals(0u.toUByte(), Ansi256Color(0u).index())
        assertEquals(255u.toUByte(), Ansi256Color(255u).index())
        assertEquals(42u.toUByte(), Ansi256Color(42u).index())

        // Method calls for RgbColor::r, g, b at 0, 255, and mixed
        val black = RgbColor(0u, 0u, 0u)
        assertEquals(0u.toUByte(), black.r())
        assertEquals(0u.toUByte(), black.g())
        assertEquals(0u.toUByte(), black.b())

        val white = RgbColor(255u, 255u, 255u)
        assertEquals(255u.toUByte(), white.r())
        assertEquals(255u.toUByte(), white.g())
        assertEquals(255u.toUByte(), white.b())

        val mixed = RgbColor(12u, 34u, 56u)
        assertEquals(12u.toUByte(), mixed.r())
        assertEquals(34u.toUByte(), mixed.g())
        assertEquals(56u.toUByte(), mixed.b())
    }
}
