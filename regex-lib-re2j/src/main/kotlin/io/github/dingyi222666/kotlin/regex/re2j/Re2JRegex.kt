/*
 * monarch-kt - Kotlin port of Monarch library.
 * https://github.com/dingyi222666/monarch-kt
 * Copyright (C) 2024-2024  dingyi
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
 *
 * Initial code from https://github.com/microsoft/vscode
 * Initial copyright Copyright (C) Microsoft Corporation. All rights reserved.
 * Initial license: MIT
 */

package io.github.dingyi222666.kotlin.regex.re2j

import com.google.re2j.Pattern
import io.github.dingyi222666.kotlin.regex.*


class Re2JRegexLib(
    cacheSize: Int = 20
) : RegexLib {
    private val cache = LRUCache<CharSequence, Re2JRegex>(cacheSize)

    override fun createRegexScanner(patterns: Array<CharSequence>): RegexScanner {
        return StandardRegexScanner(patterns)
    }


    override fun compile(str: CharSequence, regexOption: Set<RegexOption>?): Re2JRegex {
        val cached = cache.get(str)
        return cached ?: Re2JRegex(str, regexOption).also { cache.put(str, it) }
    }
}

class StandardRegexScanner(
    patterns: Array<CharSequence>
) : RegexScanner {

    private val regexes = patterns.map { Re2JRegex(it.toString()) }

    override fun findNext(source: CharSequence, startPosition: Int): CaptureIndex? {
        var bestLocation = 0
        var bestResult: MatchResult? = null
        var indexInScanner = 0

        for ((idx, regex) in regexes.withIndex()) {
            val result = regex.search(source, startPosition)
            if (result != null && result.count > 0) {
                val location = result.range.first
                if (bestResult == null || location < bestLocation) {
                    bestLocation = location
                    bestResult = result
                    indexInScanner = idx
                }
                if (location == startPosition) {
                    break
                }
            }
        }

        return if (bestResult == null) {
            null
        } else CaptureIndex(
            start = indexInScanner,
            range = bestResult.range
        )
    }

    override fun dispose() {
        // no-op
    }
}

class Re2JRegex(
    pattern: CharSequence,
    regexOption: Set<RegexOption>? = null
) : Regex() {

    override val options = regexOption ?: setOf(RegexOption.NONE)

    private val nativeRegex =
        Pattern.compile(pattern.toString(), regexOption?.map { it.toRe2JRegexOption() }?.toInt() ?: 0)

    override val pattern: String
        get() = nativeRegex.pattern()

    private var lastSearchString: CharSequence? = null

    private var lastSearchPosition = -1

    private var lastSearchResult: MatchResult? = null

    override fun containsMatchIn(input: CharSequence): Boolean {
        return nativeRegex.matcher(input.toString()).find()
    }

    override fun search(
        input: CharSequence,
        startPosition: Int,
        cached: Boolean
    ): MatchResult? {
        if (cached) {
            synchronized(this) {
                val lastSearchResult0 = this.lastSearchResult
                if (lastSearchString == input
                    && lastSearchPosition <= startPosition
                    && (lastSearchResult0 == null || lastSearchResult0.range.first >= startPosition)
                ) {
                    return lastSearchResult0;
                }
            }
        }

        val result = searchInternal(input, startPosition)
        synchronized(this) {
            lastSearchString = input
            lastSearchPosition = startPosition
            lastSearchResult = result
        }
        return result
    }

    private fun searchInternal(
        input: CharSequence,
        startPosition: Int
    ): MatchResult? {
        val matcher = nativeRegex.matcher(input)

        if (!matcher.find(startPosition)) {
            return null
        }

        val groups = Array(matcher.groupCount() + 1) { i ->
            val group = matcher.group(i) ?: ""
            MatchGroup(
                value = group,
                range = IntRange(matcher.start(i), matcher.end(i))
            )
        }
        return MatchResult(
            value = matcher.group(),
            range = matcher.start()..matcher.end(),
            groups = groups
        )

    }

    override fun replace(source: String, transform: (result: MatchGroup) -> String): String {
        val matchResult = searchInternal(source, 0) ?: return source

        val groups = matchResult.groups

        val sb = StringBuilder()

        for (element in groups) {
            sb.append(source, matchResult.range.first, element.range.first)
            sb.append(transform(element))
        }

        return sb.toString()
    }
}


/**
 * Provides enumeration values to use to set regular expression options.
 */
enum class Re2JRegexOption(override val value: Int, override val mask: Int = value) : FlagEnum {
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


    /** Enables the mode, when the expression `.` matches any character, including a line terminator. */
    DOT_MATCHES_ALL(Pattern.DOTALL),

}

fun RegexOption.toRe2JRegexOption() = when (this) {
    RegexOption.IGNORE_CASE -> Re2JRegexOption.IGNORE_CASE
    RegexOption.MULTILINE -> Re2JRegexOption.MULTILINE
    else -> throw IllegalArgumentException("Unsupported regex option: $this")
}

fun applyRe2JRegexLibToGlobal() {
    GlobalRegexLib.defaultRegexLib = Re2JRegexLib()
}