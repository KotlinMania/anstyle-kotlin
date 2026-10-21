import Testing
import Anstyle

@Suite("Anstyle exported behavior")
struct AnstyleExportTests {
    @Test func ansiPaletteConversion() {
        let color: ai.solace.tui.anstyle.AnsiColor? = ai.solace.tui.anstyle.AnsiColor.Companion.shared.from(index: 1)
        #expect(color == .Red)
        #expect(ai.solace.tui.anstyle.AnsiColor.Companion.shared.from(index: 16) == nil)
    }

    @Test func rgbComponents() {
        let rgb: ai.solace.tui.anstyle.RgbColor = ai.solace.tui.anstyle.RgbColor.Companion.shared.from(r: 12, g: 34, b: 56)
        #expect(rgb.r == 12)
        #expect(rgb.g == 34)
        #expect(rgb.b == 56)
        let color: ai.solace.tui.anstyle.Color = ai.solace.tui.anstyle.Color.Companion.shared.from(rgb: rgb)
        #expect(color.onDefault().toString() == "\u{1B}[38;2;12;34;56m")
    }

    @Test func styleEffects() {
        let plain: ai.solace.tui.anstyle.Style = ai.solace.tui.anstyle.Style.Companion.shared.new()
        #expect(plain.isPlain())
        let bold: ai.solace.tui.anstyle.Style = plain.bold()
        #expect(!bold.isPlain())
        #expect(!bold.getEffects().isPlain())
        #expect(bold.toString() == "\u{1B}[1m")
        #expect(plain.isPlain())
    }

    @Test func effectsIdentityConversion() {
        let effects: ai.solace.tui.anstyle.Effects = ai.solace.tui.anstyle.Effects.Companion.shared.new()
        let style: ai.solace.tui.anstyle.Style = ai.solace.tui.anstyle.Style.Companion.shared.from(effects: effects)
        #expect(style.isPlain())
        #expect(style.toString().isEmpty)
    }
}
