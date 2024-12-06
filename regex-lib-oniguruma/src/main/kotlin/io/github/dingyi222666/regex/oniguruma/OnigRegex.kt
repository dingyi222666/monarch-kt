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

package io.github.dingyi222666.regex.oniguruma


import io.github.dingyi222666.regex.MatchResult
import io.github.dingyi222666.regex.Regex
import io.github.dingyi222666.regex.RegexOption
import io.github.dingyi222666.regex.toInt
import org.jcodings.specific.UTF8Encoding
import org.joni.Matcher
import org.joni.Option
import org.joni.Syntax
import org.joni.WarnCallback
import java.nio.charset.StandardCharsets
import kotlin.math.max

typealias NativeRegex = org.joni.Regex

class OnigRegex(
    pattern: CharSequence,
    regexOption: Set<RegexOption>? = null
) : Regex() {
    override val options = regexOption ?: setOf(RegexOption.NONE)
    override val pattern by lazy(LazyThreadSafetyMode.NONE) { pattern.toString() }

    private val nativeRegex = run {
        val removeGlobalPattern = this.pattern
            .removePrefix("\\G")
            .toByteArray(StandardCharsets.UTF_8)

        val defaultPattern = "\$"
            .toByteArray(StandardCharsets.UTF_8)

        val option = Option.CAPTURE_GROUP or options.map { it.toOnigRegexOption() }.toInt()
        kotlin.runCatching {
            NativeRegex(
                removeGlobalPattern,
                0,
                removeGlobalPattern.size,
                option,
                UTF8Encoding.INSTANCE,
                Syntax.ECMAScript,
                WarnCallback.DEFAULT
            )
        }.getOrElse {
            runCatching {
                NativeRegex(
                    removeGlobalPattern,
                    0,
                    removeGlobalPattern.size,
                    option,
                    UTF8Encoding.INSTANCE,
                    Syntax.Grep,
                    WarnCallback.DEFAULT
                )
            }.getOrElse {
                // from https://github.com/JetBrains/intellij-community/blob/881c9bc397b850bad1d393a67bcbc82861d55d79/plugins/textmate/core/src/org/jetbrains/plugins/textmate/regex/joni/JoniRegexFactory.kt#L32
                NativeRegex(
                    defaultPattern,
                    0,
                    defaultPattern.size,
                    option,
                    UTF8Encoding.INSTANCE,
                    Syntax.Grep,
                    WarnCallback.DEFAULT
                )
            }
        }
    }

    private var lastSearchString: CharSequence? = null

    private var lastSearchPosition = -1

    private var lastSearchResult: MatchResult? = null

    private var lastFastSearchString: OnigString? = null

    private var lastFastSearchPosition = -1

    private var lastFastSearchResult: OnigResult? = null

    override fun containsMatchIn(input: CharSequence): Boolean {
        createOnigString(input.toString()).let {
            return nativeRegex.matcher(it.bytesUTF8)?.search(0, it.bytesCount, 0) != Matcher.FAILED
        }
    }

    fun fastSearch(input: OnigString, startPosition: Int): OnigResult? {
        synchronized(this) {
            val lastSearchResult0 = this.lastFastSearchResult
            if (lastFastSearchString == input
                && lastSearchPosition <= startPosition
                && (lastSearchResult0 == null || lastSearchResult0.locationAt(
                    0
                ) >= startPosition)
            ) {
                return lastSearchResult0;
            }
        }

        val result = searchForRegion(input.bytesUTF8, startPosition, input.bytesCount)

        synchronized(this) {
            lastFastSearchString = input
            lastFastSearchPosition = startPosition
            lastFastSearchResult = result
        }

        return result
    }

    override fun search(input: CharSequence, startPosition: Int, cached: Boolean): MatchResult? {
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

    private fun searchForRegion(data: ByteArray, position: Int, end: Int): OnigResult? {
        val matcher = nativeRegex.matcher(data) ?: return null
        val status = matcher.search(position, end, 0)
        if (status != Matcher.FAILED) {
            val region = matcher.eagerRegion
            return OnigResult(region, -1)
        }
        return null
    }


    private fun searchInternal(input: CharSequence, startPosition: Int): MatchResult? {
        val onigStr = createOnigString(input.toString())

        val nativeMatchResult = searchForRegion(onigStr.bytesUTF8, startPosition, onigStr.bytesCount) ?: return null

        val region = nativeMatchResult.region
        val groups = Array(nativeMatchResult.count) {
            val byteContent = ByteArray(region.getEnd(it) - region.getBeg(it)) { i ->
                onigStr.bytesUTF8[region.getBeg(it) + i]
            }
            MatchGroup(
                String(byteContent, Charsets.UTF_8), IntRange(
                    onigStr.getCharIndexOfByte(max(0, region.getBeg(it))),
                    onigStr.getCharIndexOfByte(max(0, region.getEnd(it))) - 1
                )
            )
        }

        val resultRange =
            IntRange(
                onigStr.getCharIndexOfByte(max(0, region.getBeg(0))),
                onigStr.getCharIndexOfByte(max(0, region.getEnd(0))) - 1
            )
        return MatchResult(
            onigStr.content.substring(resultRange),
            resultRange,
            groups
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

    private fun createOnigString(input: String): OnigString {
        return OnigStringFactory.create(input)
    }
}

