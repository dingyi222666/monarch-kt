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

class OnigRegexLib(
    cacheSize: Int = 100
) : RegexLib {
    private val cache = LRUCache<CharSequence, OnigRegex>(cacheSize)

    override fun createRegexScanner(patterns: Array<CharSequence>): RegexScanner {
        return OnigRegexScanner(patterns, this)
    }


    override fun compile(str: CharSequence, regexOption: Set<RegexOption>?): OnigRegex {
        val cached = cache.get(str)
        return cached ?: OnigRegex(str, regexOption).also { cache.put(str, it) }
    }

    override fun compile(str: CharSequence, vararg regexOption: RegexOption): OnigRegex {
       return compile(str, regexOption.toSet())
    }
}

fun applyOnigRegexLibToGlobal() {
    GlobalRegexLib.defaultRegexLib = OnigRegexLib()
}