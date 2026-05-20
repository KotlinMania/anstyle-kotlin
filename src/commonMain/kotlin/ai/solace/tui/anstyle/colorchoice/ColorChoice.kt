// port-lint: source crates/colorchoice/src/lib.rs
package ai.solace.tui.anstyle.colorchoice

import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * Selection for overriding color output.
 */
enum class ColorChoice {
    /**
     * Use colors if the output device appears to support them.
     */
    Auto,

    /**
     * Like [Always], except it never tries to use anything other than emitting
     * ANSI color codes.
     */
    AlwaysAnsi,

    /**
     * Try very hard to emit colors.
     *
     * This includes emitting ANSI colors on Windows if the console API is
     * unavailable.
     */
    Always,

    /**
     * Never emit colors.
     */
    Never;

    /**
     * Override the detected [ColorChoice].
     */
    fun writeGlobal() {
        USER.set(this)
    }

    companion object {
        /**
         * Get the current [ColorChoice] state.
         */
        fun global(): ColorChoice = USER.get()
    }
}

private val USER = AtomicChoice()

@OptIn(ExperimentalAtomicApi::class)
internal class AtomicChoice {
    private val choice = AtomicInt(fromChoice(ColorChoice.Auto))

    fun get(): ColorChoice {
        val raw = choice.load()
        return toChoice(raw) ?: error("Only ColorChoice values can be set")
    }

    fun set(choice: ColorChoice) {
        this.choice.store(fromChoice(choice))
    }

    companion object {
        fun fromChoice(choice: ColorChoice): Int = when (choice) {
            ColorChoice.Auto -> 0
            ColorChoice.AlwaysAnsi -> 1
            ColorChoice.Always -> 2
            ColorChoice.Never -> 3
        }

        fun toChoice(choice: Int): ColorChoice? = when (choice) {
            0 -> ColorChoice.Auto
            1 -> ColorChoice.AlwaysAnsi
            2 -> ColorChoice.Always
            3 -> ColorChoice.Never
            else -> null
        }
    }
}
