// port-lint: tests style.rs
package ai.solace.tui.anstyle

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
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
        assertFalse(style.isPlain())

        val plain = Style.PLAIN
        assertTrue(plain.isPlain())
        assertEquals("", plain.renderReset().toString())
    }

    @Test
    fun styleCombinations() {
        val style1 = Style.new() or Effects.BOLD
        assertTrue(style1.getEffects().contains(Effects.BOLD))

        val style2 = Style.new().bold().underline() - Effects.BOLD
        assertFalse(style2.getEffects().contains(Effects.BOLD))
        assertTrue(style2.getEffects().contains(Effects.UNDERLINE))

        val style3 = Style.new() + Effects.ITALIC
        assertTrue(style3.getEffects().contains(Effects.ITALIC))

        val style4 = Style.new() + AnsiColor.Red.toColor()
        assertEquals(AnsiColor.Red.toColor(), style4.getFgColor())
    }

    @Test
    fun styleEquality() {
        val effects = Effects.BOLD
        assertEquals(Style.new().effects(effects), effects.toStyle())
        assertTrue(Style.new().effects(effects).matches(effects))
        assertNotEquals(Effects.UNDERLINE or effects, effects)
        assertNotEquals(RgbColor(0u, 0u, 0u).onDefault() or effects, effects.toStyle())
    }

    @Test
    fun styleRenderAndReset() {
        val style = Style().bold()
        val rendered = style.render().toString()
        assertTrue(rendered.isNotEmpty())
        val reset = style.renderReset().toString()
        assertEquals(RESET, reset)

        val plain = Style.new()
        assertEquals("", plain.renderReset().toString())
    }

    @Test
    fun styleColors() {
        val style =
            Style
                .new()
                .fgColor(AnsiColor.Red.toColor())
                .bgColor(AnsiColor.Blue.toColor())
                .underlineColor(AnsiColor.Green.toColor())

        assertEquals(AnsiColor.Red.toColor(), style.getFgColor())
        assertEquals(AnsiColor.Blue.toColor(), style.getBgColor())
        assertEquals(AnsiColor.Green.toColor(), style.getUnderlineColor())
    }

    @Test
    fun styleBitorAssignMutatesReceiverAndRetainedAlias() {
        val style = Style.new()
        val alias = style
        val returned = style.bitorAssign(Effects.BOLD)

        kotlin.test.assertSame(style, alias)
        kotlin.test.assertSame(style, returned)
        assertEquals(Effects.BOLD, style.getEffects())
        assertEquals(Effects.BOLD, alias.getEffects())
    }

    @Test
    fun styleSubAssignMutatesReceiverAndRetainedAlias() {
        val style = Style.new().bold().underline()
        val alias = style
        val returned = style.subAssign(Effects.BOLD)

        kotlin.test.assertSame(style, alias)
        kotlin.test.assertSame(style, returned)
        assertEquals(Effects.UNDERLINE, style.getEffects())
        assertEquals(Effects.UNDERLINE, alias.getEffects())
    }

    @Test
    fun styleFromAndInputBoundarySnapshot() {
        val effects = Effects.BOLD
        val style = Style(effects = effects)
        effects.bitorAssign(Effects.UNDERLINE)

        assertEquals(Effects.BOLD, style.getEffects())

        val effects2 = Effects.BOLD
        val styleFrom = Style.from(effects2)
        effects2.bitorAssign(Effects.UNDERLINE)

        assertEquals(Effects.BOLD, styleFrom.getEffects())
    }

    @Test
    fun styleGetEffectsOutputSnapshot() {
        val style = Style().bold()
        val retrieved = style.getEffects()
        retrieved.bitorAssign(Effects.UNDERLINE)

        assertEquals(Effects.BOLD, style.getEffects())
    }

    @Test
    fun styleCopyDeepCopy() {
        val style = Style().bold()
        val copy = style.copy()
        style.bitorAssign(Effects.UNDERLINE)

        assertEquals(Effects.BOLD, copy.getEffects())
        assertEquals(Effects.BOLD or Effects.UNDERLINE, style.getEffects())
    }

    @Test
    fun savedStyleDisplaySnapshot() {
        val style = Style().bold()
        val display = style.render()
        val before = display.toString()
        style.bitorAssign(Effects.UNDERLINE)
        val after = display.toString()

        assertEquals(before, after)
        assertEquals("\u001b[1m", after)
    }

    @Test
    fun retainedAliasStillMutates() {
        val s1 = Style.new().bold()
        val s2 = s1
        s1.bitorAssign(Effects.UNDERLINE)

        assertEquals(Effects.BOLD or Effects.UNDERLINE, s2.getEffects())
        kotlin.test.assertSame(s1, s2)
    }
}
