package consumer

import ai.solace.tui.anstyle.AnsiColor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AnstyleConsumerTest {
    @Test fun paletteConversion() {
        assertEquals(AnsiColor.Red, AnstyleConsumer.ansi.from(1u))
        assertNull(AnstyleConsumer.ansi.from(16u))
        assertEquals(AnsiColor.BrightWhite, AnstyleConsumer.ansi256.fromAnsi(AnsiColor.BrightWhite).intoAnsi())
    }

    @Test fun rgbRendering() {
        val rgb = AnstyleConsumer.rgb.from(12u, 34u, 56u)
        assertEquals(12.toUByte(), rgb.r)
        assertEquals(34.toUByte(), rgb.g)
        assertEquals(56.toUByte(), rgb.b)
        val color = AnstyleConsumer.color.from(rgb)
        assertEquals("\u001B[38;2;12;34;56m", color.renderFg().toString())
    }

    @Test fun stylesDoNotMutateAliases() {
        val plain = AnstyleConsumer.style.new()
        val alias = plain
        val bold = plain.bold()
        assertTrue(alias.isPlain())
        assertFalse(bold.isPlain())
        assertEquals("\u001B[1m", bold.toString())
    }

    @Test fun effectsConversion() {
        val effects = AnstyleConsumer.effects.from(AnstyleConsumer.effects.BOLD)
        val style = AnstyleConsumer.style.from(effects)
        assertFalse(style.getEffects().isPlain())
        assertEquals("\u001B[1m", style.toString())
        assertEquals("\u001B[38;5;42m", AnstyleConsumer.color.from(AnstyleConsumer.ansi256.from(42u)).renderFg().toString())
    }
}
