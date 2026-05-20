// port-lint: source crates/colorchoice/src/lib.rs
package ai.solace.tui.anstyle.colorchoice

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorChoiceTest {
    @AfterTest
    fun restoreDefault() {
        ColorChoice.Auto.writeGlobal()
    }

    @Test
    fun choiceSerialization() {
        val expected = listOf(
            ColorChoice.Auto,
            ColorChoice.AlwaysAnsi,
            ColorChoice.Always,
            ColorChoice.Never,
        )
        val values = expected.map(AtomicChoice.Companion::fromChoice)
        val actual = values.mapNotNull(AtomicChoice.Companion::toChoice)

        assertEquals(expected, actual)
    }

    @Test
    fun globalDefaultsToAuto() {
        ColorChoice.Auto.writeGlobal()

        assertEquals(ColorChoice.Auto, ColorChoice.global())
    }

    @Test
    fun writeGlobalOverridesChoice() {
        ColorChoice.Never.writeGlobal()
        assertEquals(ColorChoice.Never, ColorChoice.global())

        ColorChoice.AlwaysAnsi.writeGlobal()
        assertEquals(ColorChoice.AlwaysAnsi, ColorChoice.global())

        ColorChoice.Always.writeGlobal()
        assertEquals(ColorChoice.Always, ColorChoice.global())
    }
}
