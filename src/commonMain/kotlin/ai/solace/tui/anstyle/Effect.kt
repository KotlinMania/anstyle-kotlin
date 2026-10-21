// port-lint: source effect.rs
package ai.solace.tui.anstyle

/**
 * A set of text effects
 *
 * Example:
 * ```kotlin
 * val effects = Effects.BOLD or Effects.UNDERLINE
 * ```
 */
data class Effects(
    var bits: UShort,
) : Comparable<Effects> {
    override fun compareTo(other: Effects): Int = bits.compareTo(other.bits)

    companion object {
        /** No [Effects] applied */
        val PLAIN: Effects get() = Effects(0u)

        /**
         * No effects enabled
         *
         * Example:
         * ```kotlin
         * val effects = Effects.new()
         * ```
         */
        fun new(): Effects = PLAIN

        val BOLD: Effects get() = Effects((1 shl 0).toUShort())
        val DIMMED: Effects get() = Effects((1 shl 1).toUShort())

        /** Not widely supported. Sometimes treated as inverse or blink */
        val ITALIC: Effects get() = Effects((1 shl 2).toUShort())

        /** Style extensions exist for Kitty, VTE, mintty and iTerm2. */
        val UNDERLINE: Effects get() = Effects((1 shl 3).toUShort())
        val DOUBLE_UNDERLINE: Effects get() = Effects((1 shl 4).toUShort())
        val CURLY_UNDERLINE: Effects get() = Effects((1 shl 5).toUShort())
        val DOTTED_UNDERLINE: Effects get() = Effects((1 shl 6).toUShort())
        val DASHED_UNDERLINE: Effects get() = Effects((1 shl 7).toUShort())
        val BLINK: Effects get() = Effects((1 shl 8).toUShort())

        /** Swap foreground and background colors; inconsistent emulation */
        val INVERT: Effects get() = Effects((1 shl 9).toUShort())
        val HIDDEN: Effects get() = Effects((1 shl 10).toUShort())

        /** Characters legible but marked as if for deletion. Not supported in Terminal.app */
        val STRIKETHROUGH: Effects get() = Effects((1 shl 11).toUShort())

        /** Convert effects to effects (identity) */
        fun from(effects: Effects): Effects = effects
    }

    /**
     * Operator + to combine effects
     */
    operator fun plus(other: Effects): Effects = insert(other)

    /**
     * Operator iterator to iterate through enabled effects
     */
    operator fun iterator(): Iterator<Effects> = iter()

    /**
     * Bitwise and of effects
     */
    infix fun and(other: Effects): Effects = Effects((bits.toInt() and other.bits.toInt()).toUShort())

    /**
     * Check if no effects are enabled
     *
     * Example:
     * ```kotlin
     * val effects = Effects.PLAIN
     * assert(effects.isPlain())
     *
     * val effects = Effects.BOLD or Effects.UNDERLINE
     * assert(!effects.isPlain())
     * ```
     */
    fun isPlain(): Boolean = bits == PLAIN.bits

    /**
     * Returns `true` if all of the effects in `other` are contained within `this`.
     *
     * Example:
     * ```kotlin
     * val effects = Effects.BOLD or Effects.UNDERLINE
     * assert(effects.contains(Effects.BOLD))
     *
     * val effects = Effects.PLAIN
     * assert(!effects.contains(Effects.BOLD))
     * ```
     */
    fun contains(other: Effects): Boolean = (other.bits and bits) == other.bits

    /**
     * Inserts the specified effects.
     *
     * Example:
     * ```kotlin
     * val effects = Effects.PLAIN.insert(Effects.PLAIN)
     * assert(effects.isPlain())
     *
     * val effects = Effects.PLAIN.insert(Effects.BOLD)
     * assert(effects.contains(Effects.BOLD))
     * ```
     */
    fun insert(other: Effects): Effects = Effects((bits.toInt() or other.bits.toInt()).toUShort())

    fun bitor(other: Effects): Effects = insert(other)

    fun bitorAssign(other: Effects): Effects {
        this.bits = (this.bits.toInt() or other.bits.toInt()).toUShort()
        return this
    }

    /**
     * Removes the specified effects.
     *
     * Example:
     * ```kotlin
     * val effects = (Effects.BOLD or Effects.UNDERLINE).remove(Effects.BOLD)
     * assert(!effects.contains(Effects.BOLD))
     * assert(effects.contains(Effects.UNDERLINE))
     * ```
     */
    fun remove(other: Effects): Effects = Effects((bits.toInt() and other.bits.toInt().inv()).toUShort())

    fun sub(other: Effects): Effects = remove(other)

    fun subAssign(other: Effects): Effects {
        this.bits = (this.bits.toInt() and other.bits.toInt().inv()).toUShort()
        return this
    }

    /**
     * Reset all effects
     * ```kotlin
     * val effects = (Effects.BOLD or Effects.UNDERLINE).clear()
     * assert(!effects.contains(Effects.BOLD))
     * assert(!effects.contains(Effects.UNDERLINE))
     * ```
     */
    fun clear(): Effects = PLAIN

    /**
     * Enable or disable the specified effects depending on the passed value.
     *
     * Example:
     * ```kotlin
     * val effects = Effects.PLAIN.set(Effects.BOLD, true)
     * assert(effects.contains(Effects.BOLD))
     * ```
     */
    fun set(other: Effects, enable: Boolean): Effects =
        if (enable) insert(other) else remove(other)

    /**
     * Iterate over enabled effects
     */
    fun iter(): EffectIter = EffectIter(index = 0, effects = this)

    /**
     * Iterate over enabled effect indices
     */
    internal fun indexIter(): EffectIndexIter = EffectIndexIter(index = 0, effects = this)

    /**
     * Render the ANSI code
     */
    fun render(): Displayable = EffectsDisplay(this)

    /**
     * Debug format for [Effects], emitting `Effects(EFFECT1 | EFFECT2)` or `Effects()`
     */
    fun fmt(appendable: Appendable): Appendable {
        appendable.append("Effects(")
        var first = true
        for (index in indexIter()) {
            if (!first) appendable.append(" | ")
            first = false
            appendable.append(METADATA[index].name)
        }
        appendable.append(")")
        return appendable
    }

    internal fun writeTo(appendable: Appendable): Appendable {
        for (index in indexIter()) {
            appendable.append(METADATA[index].escape)
        }
        return appendable
    }

    override fun toString(): String = buildString { fmt(this) }
}

// Operator extensions for Effects
infix fun Effects.or(other: Effects): Effects = this.insert(other)

operator fun Effects.minus(other: Effects): Effects = this.remove(other)

internal data class Metadata(
    val name: String,
    val escape: String,
)

internal val METADATA: Array<Metadata> =
    arrayOf(
        Metadata(name = "BOLD", escape = escape("1")),
        Metadata(name = "DIMMED", escape = escape("2")),
        Metadata(name = "ITALIC", escape = escape("3")),
        Metadata(name = "UNDERLINE", escape = escape("4")),
        Metadata(name = "DOUBLE_UNDERLINE", escape = escape("21")),
        Metadata(name = "CURLY_UNDERLINE", escape = escape("4:3")),
        Metadata(name = "DOTTED_UNDERLINE", escape = escape("4:4")),
        Metadata(name = "DASHED_UNDERLINE", escape = escape("4:5")),
        Metadata(name = "BLINK", escape = escape("5")),
        Metadata(name = "INVERT", escape = escape("7")),
        Metadata(name = "HIDDEN", escape = escape("8")),
        Metadata(name = "STRIKETHROUGH", escape = escape("9")),
    )

internal class EffectsDisplay(
    private val effects: Effects,
) : Displayable {
    fun fmt(appendable: Appendable): Appendable = formatTo(appendable)

    override fun formatTo(appendable: Appendable): Appendable {
        for (index in effects.indexIter()) {
            appendable.append(METADATA[index].escape)
        }
        return appendable
    }

    override fun toString(): String = buildString { formatTo(this) }
}

/**
 * Enumerate each enabled value in [Effects]
 */
class EffectIter(
    private var index: Int,
    private val effects: Effects,
) : Iterator<Effects> {
    override fun hasNext(): Boolean {
        while (index < METADATA.size) {
            val effect = Effects((1 shl index).toUShort())
            if (effects.contains(effect)) {
                return true
            }
            index++
        }
        return false
    }

    override fun next(): Effects {
        while (index < METADATA.size) {
            val currentIndex = index
            index++
            val effect = Effects((1 shl currentIndex).toUShort())
            if (effects.contains(effect)) {
                return effect
            }
        }
        throw NoSuchElementException()
    }
}

internal class EffectIndexIter(
    private var index: Int,
    private val effects: Effects,
) : Iterator<Int> {
    override fun hasNext(): Boolean {
        while (index < METADATA.size) {
            val effect = Effects((1 shl index).toUShort())
            if (effects.contains(effect)) {
                return true
            }
            index++
        }
        return false
    }

    override fun next(): Int {
        while (index < METADATA.size) {
            val currentIndex = index
            index++
            val effect = Effects((1 shl currentIndex).toUShort())
            if (effects.contains(effect)) {
                return currentIndex
            }
        }
        throw NoSuchElementException()
    }
}
