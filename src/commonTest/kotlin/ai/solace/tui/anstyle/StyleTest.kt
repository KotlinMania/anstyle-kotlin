package ai.solace.tui.anstyle

import kotlin.test.Test
import kotlin.test.assertTrue

class StyleTest {
    @Test
    fun printSizeOf() {
        // In Kotlin, data class size depends on target runtime implementation
        println("Style: data class with 4 fields (3 nullable Color + Effects)")
        println("StyleDisplay: class wrapping Style")
    }

    @Test
    fun basicUsage() {
        val style = Style().bold()
        assertTrue(style.getEffects().contains(Effects.BOLD))
    }
}

