package ai.solace.tui.anstyle

import kotlin.test.Test
import kotlin.test.assertEquals

class EffectsTest {
    @Test
    fun printSizeOf() {
        // Effects is a value class wrapping UShort.
        println("Effects: value class wrapping UShort (2 bytes)")
        println("EffectsDisplay: class wrapping Effects")
    }

    @Test
    fun noAlign() {
        fun assertNoAlign(d: Displayable) {
            val expected = buildString { d.formatTo(this) }
            val actual = buildString { d.formatTo(this) }
            assertEquals(expected, actual)
        }

        assertNoAlign(Effects.BOLD.render())
    }

    @Test
    fun debugFormat() {
        val effects = Effects.PLAIN
        assertEquals("Effects()", effects.toString())

        val effects2 = Effects.BOLD or Effects.UNDERLINE
        assertEquals("Effects(BOLD | UNDERLINE)", effects2.toString())
    }
}

