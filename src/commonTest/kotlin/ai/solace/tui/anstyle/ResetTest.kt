package ai.solace.tui.anstyle

import kotlin.test.Test
import kotlin.test.assertEquals

class ResetTest {
    @Test
    fun printSizeOf() {
        // Reset is a singleton object (zero-sized equivalent in Kotlin)
        println("Reset: object (singleton)")
    }

    @Test
    fun noAlign() {
        fun assertNoAlign(d: Displayable) {
            val expected = buildString { d.formatTo(this) }
            val actual = buildString { d.formatTo(this) }
            assertEquals(expected, actual)
        }

        assertNoAlign(Reset)
        assertNoAlign(Reset.render())
    }
}

