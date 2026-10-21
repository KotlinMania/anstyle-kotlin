package consumer

import ai.solace.tui.anstyle.AnsiColor
import ai.solace.tui.anstyle.Ansi256Color
import ai.solace.tui.anstyle.Color
import ai.solace.tui.anstyle.Effects
import ai.solace.tui.anstyle.RgbColor
import ai.solace.tui.anstyle.Style

internal object AnstyleConsumer {
    val ansi: AnsiColor.Companion = AnsiColor.Companion
    val ansi256: Ansi256Color.Companion = Ansi256Color.Companion
    val color: Color.Companion = Color.Companion
    val rgb: RgbColor.Companion = RgbColor.Companion
    val style: Style.Companion = Style.Companion
    val effects: Effects.Companion = Effects.Companion
}
