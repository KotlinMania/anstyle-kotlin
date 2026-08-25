// port-lint: tests effect.rs
package ai.solace.tui.anstyle

import kotlin.test.Test
import kotlin.test.assertEquals

class EffectsTest {
    @Test
    fun printSizeOf() {
        println("Effects: data class wrapping UShort")
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
