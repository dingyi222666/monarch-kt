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


import org.jcodings.specific.UTF8Encoding
import java.nio.charset.StandardCharsets
import java.util.*

abstract class OnigString(
    open val content: String, open val bytesUTF8: ByteArray
) {
    val bytesCount by lazy {
        bytesUTF8.size
    }

    companion object {
        fun create(string: String): OnigString {
            string.toByteArray(StandardCharsets.UTF_8).let {
                if (it.size == string.length) {
                    return SingleByteString(string, it)
                }
                return MultiByteString(string, it)
            }
        }
    }

    abstract fun getByteIndexOfChar(charIndex: Int): Int

    abstract fun getCharIndexOfByte(byteIndex: Int): Int

    abstract fun dispose()
}

class MultiByteString(
    override val content: String, override val bytesUTF8: ByteArray
) : OnigString(content, bytesUTF8) {

    private val lastCharIndex = content.length - 1
    private var byteToCharOffsets: IntArray? = null

    override fun getByteIndexOfChar(charIndex: Int): Int {
        if (charIndex == lastCharIndex + 1) {
            return lastCharIndex
        }

        if (charIndex < 0 || charIndex > lastCharIndex) {
            throw IndexOutOfBoundsException("$charIndex, $lastCharIndex")
        }
        if (charIndex == 0) {
            return 0
        }

        val byteToCharOffsets = getByteToCharOffsets()
        var byteIndex = Arrays.binarySearch(byteToCharOffsets, charIndex)
        while (byteIndex > 0 && byteToCharOffsets[byteIndex - 1] == charIndex) {
            byteIndex--
        }
        return byteIndex
    }

    private fun getByteToCharOffsets(): IntArray {
        var offsets: IntArray? = byteToCharOffsets
        if (offsets == null) {
            offsets = IntArray(bytesCount)
            var charIndex = 0
            var byteIndex = 0
            val maxByteIndex: Int = bytesCount - 1
            while (byteIndex <= maxByteIndex) {
                val charLenInBytes = UTF8Encoding.INSTANCE.length(bytesUTF8, byteIndex, bytesCount)
                var charCount = 1
                if (charLenInBytes > 1) {
                    val chars = ByteArray(charLenInBytes) {
                        bytesUTF8[byteIndex + it]
                    }
                    charCount = String(chars, Charsets.UTF_8).length
                }

                val l = byteIndex + charLenInBytes
                while (byteIndex < l) {
                    offsets[byteIndex] = charIndex
                    byteIndex++
                }


                charIndex += charCount
            }
            byteToCharOffsets = offsets
        }
        return offsets
    }

    override fun getCharIndexOfByte(byteIndex: Int): Int {
        if (byteIndex == bytesCount) {
            return lastCharIndex + 1
        }

        if (byteIndex < 0 || byteIndex >= bytesCount) {
            throw IndexOutOfBoundsException("$byteIndex, $bytesCount")
        }
        return if (byteIndex == 0) {
            0
        } else getByteToCharOffsets()[byteIndex]

    }

    override fun dispose() {
    }

}

class SingleByteString(
    override val content: String, override val bytesUTF8: ByteArray
) : OnigString(content, bytesUTF8) {

    override fun getByteIndexOfChar(charIndex: Int): Int {
        if (charIndex == bytesCount) {
            return charIndex
        }

        if (charIndex < 0 || charIndex >= bytesCount) {
            throw IndexOutOfBoundsException("$charIndex, $bytesCount")
        }
        return charIndex
    }

    override fun getCharIndexOfByte(byteIndex: Int): Int {
        if (byteIndex == bytesCount) {
            return byteIndex
        }

        if (byteIndex < 0 || byteIndex >= bytesCount) {
            throw IndexOutOfBoundsException("$byteIndex, $bytesCount")
        }
        return byteIndex
    }

    override fun dispose() {

    }

}