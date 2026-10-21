// port-lint: tests effect.rs
package ai.solace.tui.anstyle

import kotlin.test.Test
import kotlin.test.assertEquals

class EffectsTest {
    private fun assertNoAlign(d: Displayable) {
        val expected = buildString { d.formatTo(this) }
        val actual = buildString { d.formatTo(this) }
        assertEquals(expected, actual)
    }

    @Test
    fun printSizeOf() {
        println("Effects: data class wrapping UShort")
        println("EffectsDisplay: class wrapping Effects")
    }

    @Test
    fun noAlign() {
        assertNoAlign(Effects.BOLD.render())
    }

    @Test
    fun debugFormat() {
        val effects = Effects.PLAIN
        assertEquals("Effects()", effects.toString())

        val effects2 = Effects.BOLD or Effects.UNDERLINE
        assertEquals("Effects(BOLD | UNDERLINE)", effects2.toString())
    }

    @Test
    fun effectsBitorAssignMutatesReceiverAndRetainedAlias() {
        val effects = Effects.BOLD
        val alias = effects
        val returned = effects.bitorAssign(Effects.UNDERLINE)

        kotlin.test.assertSame(effects, alias)
        kotlin.test.assertSame(effects, returned)
        assertEquals(Effects.BOLD or Effects.UNDERLINE, effects)
        assertEquals(Effects.BOLD or Effects.UNDERLINE, alias)
    }

    @Test
    fun effectsSubAssignMutatesReceiverAndRetainedAlias() {
        val effects = Effects.BOLD or Effects.UNDERLINE
        val alias = effects
        val returned = effects.subAssign(Effects.BOLD)

        kotlin.test.assertSame(effects, alias)
        kotlin.test.assertSame(effects, returned)
        assertEquals(Effects.UNDERLINE, effects)
        assertEquals(Effects.UNDERLINE, alias)
    }

    @Test
    fun effectsDebugFmtVersusRenderEmpty() {
        val effects = Effects.PLAIN
        val debugStr = buildString { effects.fmt(this) }
        val renderStr = buildString { effects.render().formatTo(this) }
        val displayFmtStr = buildString { EffectsDisplay(effects).fmt(this) }

        assertEquals("Effects()", debugStr)
        assertEquals("", renderStr)
        assertEquals("", displayFmtStr)
        assertEquals(debugStr, effects.toString())
    }

    @Test
    fun effectsDebugFmtVersusRenderNonEmpty() {
        val effects = Effects.BOLD or Effects.UNDERLINE
        val debugStr = buildString { effects.fmt(this) }
        val renderStr = buildString { effects.render().formatTo(this) }
        val displayFmtStr = buildString { EffectsDisplay(effects).fmt(this) }

        assertEquals("Effects(BOLD | UNDERLINE)", debugStr)
        assertEquals("\u001b[1m\u001b[4m", renderStr)
        assertEquals("\u001b[1m\u001b[4m", displayFmtStr)
        assertEquals(debugStr, effects.toString())
    }
}
