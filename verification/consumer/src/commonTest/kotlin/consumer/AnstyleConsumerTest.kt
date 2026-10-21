package consumer

import ai.solace.tui.anstyle.AnsiColor
import ai.solace.tui.anstyle.parse.Parser
import ai.solace.tui.anstyle.parse.Perform
import ai.solace.tui.anstyle.parse.Utf8Parser
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
    @Test fun parserPreservesUnicodeScalars() {
        val output = mutableListOf<Int>()
        val performer = object : Perform {
            override fun print(c: Int) { output.add(c) }
        }
        val parser = Parser(Utf8Parser())
        for (byte in "😀\u001B[31mZ".encodeToByteArray()) {
            parser.advance(performer, byte.toUByte())
        }
        assertEquals(listOf(0x1F600, 0x5A), output)
        val decoder = Utf8Parser()
        val decoded = listOf(0xED, 0xA0, 0x80, 0x41).mapNotNull { decoder.add(it.toUByte()) }
        assertEquals(listOf(0xFFFD, 0xFFFD, 0x41), decoded)
    }
}
