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

import io.github.dingyi222666.regex.CaptureIndex
import io.github.dingyi222666.regex.RegexScanner


class OnigRegexScanner(
    patterns: Array<CharSequence>,
    regexLib: OnigRegexLib
) : RegexScanner {

    private val regexps = patterns.map { regexLib.compile(it.toString()) }

    private fun createOnigString(source: CharSequence): OnigString {
        return OnigStringFactory.create(source.toString())
    }

    override fun findNext(source: CharSequence, startPosition: Int): CaptureIndex? {
        val onigString = createOnigString(source)
        val byteOffset = onigString.getByteIndexOfChar(startPosition)

        var bestLocation = 0
        var bestResult: OnigResult? = null
        for ((index, regExp) in this.regexps.withIndex()) {
            val result = regExp.fastSearch(onigString, byteOffset)
            if (result != null && result.count > 0) {
                val location = result.locationAt(0)
                if (bestResult == null || location < bestLocation) {
                    bestLocation = location
                    bestResult = result
                    bestResult.indexInScanner = index
                }
                if (location == byteOffset) {
                    break
                }
            }
        }

        return if (bestResult == null) {
            null
        } else createCaptureIndex(onigString, bestResult)
    }

    private fun createCaptureIndex(source: OnigString, result: OnigResult): CaptureIndex {
        val resultCount = result.count

        val captures = Array(resultCount) { index ->
            val loc = result.locationAt(index)
            val captureStart = source.getCharIndexOfByte(loc)
            val captureEnd = source.getCharIndexOfByte(loc + result.lengthAt(index))
            IntRange(captureStart, captureEnd - 1)
        }

        return CaptureIndex(result.indexInScanner, captures)
    }

    override fun dispose() {
        // no-op
    }
}
