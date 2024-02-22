/*
 * monarch-kt - Kotlin port of Monarch library.
 * https://github.com/dingyi222666/monarch-kt
 * Copyright (C) 2024  dingyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.dingyi222666.kotlin.regex

import io.github.dingyi222666.kotlin.regex.standard.StandardRegexLib
import java.util.*
import java.util.regex.Pattern

interface RegexLib {
    fun createRegexScanner(patterns: Array<CharSequence>): RegexScanner

    fun compile(str: CharSequence, regexOption: Set<RegexOption>?): Regex

    fun compile(str: CharSequence, vararg regexOption: RegexOption): Regex {
        return compile(str, regexOption.toSet())
    }
}

interface RegexScanner {
    fun findNext(source: CharSequence, startPosition: Int): CaptureIndex?
    fun dispose() {
        // no-op
    }
}

abstract class Regex {
    abstract val options: Set<RegexOption>
    abstract val pattern: String

    abstract fun containsMatchIn(input: CharSequence): Boolean

    abstract fun search(input: CharSequence, startPosition: Int, cached: Boolean = false): MatchResult?

    fun search(input: CharSequence, cached: Boolean = false) = search(input, 0, cached)

    abstract fun replace(source: String, transform: (result: MatchGroup) -> String): String

    fun replace(source: String, target: String) = replace(source) { target }

    fun matches(input: CharSequence): Boolean {
        return search(input, 0) != null
    }

}

data class CaptureIndex(
    val start: Int,
    val range: Array<IntRange>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CaptureIndex

        if (start != other.start) return false
        if (!range.contentEquals(other.range)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = start
        result = 31 * result + range.contentHashCode()
        return result
    }
}

data class MatchResult(
    val value: CharSequence,
    val range: IntRange,
    val groups: Array<MatchGroup>
) {
    val count: Int
        get() = groups.size

    val groupValues by lazy(LazyThreadSafetyMode.NONE) { groups.map { it.value } }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MatchResult

        if (value != other.value) return false
        if (range != other.range) return false
        if (!groups.contentEquals(other.groups)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + range.hashCode()
        result = 31 * result + groups.contentHashCode()
        return result
    }
}

object FindOptions {
    const val None: Byte = 0
    const val NotBeginString: Byte = 1
    const val NotEndString: Byte = 2
    const val NotBeginPosition: Byte = 4
    const val DebugCall: Byte = 8
}


fun String.replace(regex: Regex, replacement: (result: MatchGroup) -> String): String {
    return regex.replace(this, replacement)
}

fun String.replace(regex: Regex, replacement: String): String {
    return regex.replace(this) { replacement }
}

fun String.match(regex: Regex): MatchResult? {
    return regex.search(this)
}

fun String.containsMatch(regex: Regex): Boolean {
    return regex.containsMatchIn(this)
}

fun String.match(regex: Regex, startPosition: Int): MatchResult? {
    return regex.search(this, startPosition)
}

object GlobalRegexLib : RegexLib {
    @get:Synchronized
    @set:Synchronized
    var defaultRegexLib: RegexLib = StandardRegexLib()

    override fun createRegexScanner(patterns: Array<CharSequence>): RegexScanner =
        defaultRegexLib.createRegexScanner(patterns)

    override fun compile(str: CharSequence, regexOption: Set<RegexOption>?) = defaultRegexLib.compile(str, regexOption)
}

interface FlagEnum {
    val value: Int
    val mask: Int
}

fun Iterable<FlagEnum>.toInt(): Int =
    this.fold(0) { value, option -> value or option.value }

internal inline fun <reified T> fromInt(value: Int): Set<T> where T : FlagEnum, T : Enum<T> =
    Collections.unmodifiableSet(EnumSet.allOf(T::class.java).apply {
        retainAll { value and it.mask == it.value }
    })

/**
 * Provides enumeration values to use to set regular expression options.
 */
enum class RegexOption(override val value: Int, override val mask: Int = value) : FlagEnum {
    // common

    NONE(0),

    /** Enables case-insensitive matching. Case comparison is Unicode-aware. */
    IGNORE_CASE(Pattern.CASE_INSENSITIVE),

    /** Enables multiline mode.
     *
     * In multiline mode the expressions `^` and `$` match just after or just before,
     * respectively, a line terminator or the end of the input sequence. */
    MULTILINE(Pattern.MULTILINE),

    //jvm-specific

    /** Enables literal parsing of the pattern.
     *
     * Metacharacters or escape sequences in the input sequence will be given no special meaning.
     */
    LITERAL(Pattern.LITERAL),

    UNICODE_CASE(Pattern.UNICODE_CASE),

    /** Enables Unix lines mode. In this mode, only the `'\n'` is recognized as a line terminator. */
    UNIX_LINES(Pattern.UNIX_LINES),

    /** Permits whitespace and comments in pattern. */
    COMMENTS(Pattern.COMMENTS),

    /** Enables the mode, when the expression `.` matches any character, including a line terminator. */
    DOT_MATCHES_ALL(Pattern.DOTALL),

    /** Enables equivalence by canonical decomposition. */
    CANON_EQ(Pattern.CANON_EQ)
}