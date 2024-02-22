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

package io.github.dingyi222666.kotlin.regex

import io.github.dingyi222666.kotlin.regex.standard.StandardRegexLib

interface RegexLib {
    fun createRegexScanner(patterns: Array<CharSequence>): RegexScanner
    fun compile(str: CharSequence): Regex
}

interface RegexScanner {
    fun findNext(source: CharSequence, startPosition: Int): CaptureIndex?
    fun dispose() {
        // no-op
    }
}

abstract class Regex {
    abstract val pattern: String

    abstract fun containsMatchIn(input: CharSequence): Boolean

    abstract fun search(input: CharSequence, startPosition: Int, cached: Boolean = true): MatchResult?

    fun search(input: CharSequence, cached: Boolean = false) = search(input, 0, cached)

    abstract fun replace(source: String, transform: (result: MatchGroup) -> String): String

    fun replace(source: String, target: String) = replace(source) { target }

}

data class CaptureIndex(
    val start: Int,
    val range: IntRange
)

data class MatchResult(
    val value: CharSequence,
    val range: IntRange,
    val groups: Array<MatchGroup>
) {
    val count: Int
        get() = groups.size

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

    override fun compile(str: CharSequence): Regex =
        defaultRegexLib.compile(str)
}