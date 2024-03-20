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

package io.github.dingyi222666.regex.standard

import io.github.dingyi222666.regex.*
import io.github.dingyi222666.regex.MatchResult
import io.github.dingyi222666.regex.RegexOption
import io.github.dingyi222666.regex.regex.*
import java.util.regex.Pattern


class StandardRegexLib(
    cacheSize: Int = 200
) : RegexLib {

    private val cache = LRUCache<Int, StandardRegex>(cacheSize)

    override fun createRegexScanner(patterns: Array<CharSequence>): RegexScanner {
        return StandardRegexScanner(patterns, this)
    }


    override fun compile(str: CharSequence, regexOption: Set<RegexOption>?): StandardRegex {
        val key = str.hashCode() + (regexOption?.toInt() ?: 0)
        val cached = cache.get(key)
        return cached ?: StandardRegex(str, regexOption).also { cache.put(key, it) }
    }
}

class StandardRegexScanner(
    patterns: Array<CharSequence>,
    regexLib: StandardRegexLib
) : RegexScanner {

    private val regexps = patterns.map { regexLib.compile(it) }

    override fun findNext(source: CharSequence, startPosition: Int): CaptureIndex? {
        var bestLocation = 0
        var bestResult: MatchResult? = null
        var indexInScanner = 0

        for ((idx, regex) in regexps.withIndex()) {
            val result = regex.search(source, startPosition, true)
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
        } else {
            val ranges = Array(bestResult.count) { index ->
                bestResult.groups[index].range
            }
            CaptureIndex(
                start = indexInScanner,
                range = ranges
            )
        }
    }


    override fun dispose() {
        // no-op
    }
}

class StandardRegex(
    pattern: CharSequence,
    regexOption: Set<RegexOption>? = null
) : Regex() {

    override val options = regexOption ?: setOf(RegexOption.NONE)

    private val nativeRegex =
        kotlin.runCatching {
            Pattern.compile(pattern.toString(), regexOption?.toInt() ?: 0)
        }.getOrElse {
            val rawPatternString = pattern.toString()

            // fuck ecma regex...
            var exception: Exception? = null
            for (pattern in patterns) {
                try {
                    return@getOrElse Pattern.compile(pattern.invoke(rawPatternString), regexOption?.toInt() ?: 0)
                } catch (e: Exception) {
                    exception = e
                    continue
                }
            }
            throw Exception("Can't compile regex $pattern $exception")
        }

    override val pattern: String by lazy(LazyThreadSafetyMode.NONE) { nativeRegex.pattern() }

    private var lastSearchString: CharSequence? = null

    private var lastSearchPosition = -1

    private var lastSearchResult: MatchResult? = null

    override fun containsMatchIn(input: CharSequence): Boolean {
        return nativeRegex.matcher(input).find()
    }

    override fun search(input: CharSequence, startPosition: Int, cached: Boolean): MatchResult? {
        if (cached) {
            synchronized(this) {
                val lastSearchResult0 = this.lastSearchResult
                if (lastSearchString == input
                    && lastSearchPosition <= startPosition
                    && (lastSearchResult0 == null || lastSearchResult0.range.first >= startPosition)
                ) {
                    return lastSearchResult0
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

    private fun searchInternal(input: CharSequence, startPosition: Int): MatchResult? {
        val matcher = nativeRegex.matcher(input)

        if (!matcher.find(startPosition)) {
            return null
        }

        val groupCount = matcher.groupCount()
        val groups = Array(groupCount + 1) { i ->
            val group = matcher.group(i) ?: ""
            val start = matcher.start(i)
            val end = matcher.end(i)
            MatchGroup(
                value = group,
                range = IntRange(start, end)
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

    companion object {
        private val patterns = arrayOf(
            { rawPattern: String -> rawPattern.replace("[[", "[") },
            { rawPattern: String -> rawPattern.replace("{", "\\{") },
            { rawPattern: String -> rawPattern.replace("{", "\\{").replace("^", "\\^") },
            { rawPattern: String -> rawPattern.replace("([", "(\\[") },

            { rawPattern: String -> rawPattern
                .replace("\\","\\\\")

            },
           // { rawPattern: String -> rawPattern.replace("\\p{", "").replace("}", "") }
        )
    }

}

