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

package io.github.dingyi222666.kotlin.regex.oniguruma

import io.github.dingyi222666.kotlin.regex.*
import org.joni.Option
import org.joni.Syntax
import java.util.regex.Pattern

class OnigRegexLib(
    cacheSize: Int = 100
) : RegexLib {
    private val cache = LRUCache<Int, OnigRegex>(cacheSize)


    override fun createRegexScanner(patterns: Array<CharSequence>): RegexScanner {
        return OnigRegexScanner(patterns, this)
    }

    override fun compile(str: CharSequence, regexOption: Set<RegexOption>?): OnigRegex {
        val key = str.hashCode() + (regexOption?.toInt() ?: 0)
        val cached = cache.get(key)
        return cached ?: OnigRegex(str, regexOption).also { cache.put(key, it) }
    }

    override fun compile(str: CharSequence, vararg regexOption: RegexOption): OnigRegex {
        return compile(str, regexOption.toSet())
    }
}

fun applyOnigRegexLibToGlobal() {
    GlobalRegexLib.defaultRegexLib = OnigRegexLib()
}


/**
 * Provides enumeration values to use to set regular expression options.
 */
enum class OnigRegexOption(override val value: Int, override val mask: Int = value) : FlagEnum {
    // common

    NONE(0),

    /** Enables case-insensitive matching. Case comparison is Unicode-aware. */
    IGNORE_CASE(Option.IGNORECASE),

    /** Enables multiline mode.
     *
     * In multiline mode the expressions `^` and `$` match just after or just before,
     * respectively, a line terminator or the end of the input sequence. */
    MULTILINE(Option.MULTILINE),


    UNICODE_CASE(Option.POSIX_REGION),

}

fun RegexOption.toOnigRegexOption() = when (this) {
    RegexOption.IGNORE_CASE -> OnigRegexOption.IGNORE_CASE
    RegexOption.MULTILINE -> OnigRegexOption.MULTILINE
    RegexOption.UNICODE_CASE -> OnigRegexOption.UNICODE_CASE
    else -> throw IllegalArgumentException("Unsupported regex option: $this")
}