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
 *
 * Initial code from https://github.com/microsoft/vscode
 * Initial copyright Copyright (C) Microsoft Corporation. All rights reserved.
 * Initial license: MIT
 *
 * Contributors:
 * - Microsoft Corporation: Initial code, written in TypeScript, licensed under MIT license
 * - dingyi222666 <dingyi222666@foxmail.com> - translation and adaptation to Kotlin
 */

package io.github.dingyi222666.monarch.types

// https://github.com/microsoft/vscode/blob/main/src/vs/editor/common/encodedTokenAttributes.ts#L9

/**
 * Open ended enum at runtime
 */

object LanguageId {
    const val Null = 0
    const val PlainText = 1
}

/**
 * A font style. Values are 2^x such that a bit mask can be used.
 */
/*export const enum FontStyle {
    NotSet = -1,
    None = 0,
    Italic = 1,
    Bold = 2,
    Underline = 4,
    Strikethrough = 8,
}*/
object FontStyle {
    const val NotSet = -1
    const val None = 0
    const val Italic = 1
    const val Bold = 2
    const val Underline = 4
    const val Strikethrough = 8
}

/**
 * Open ended enum at runtime
 */

object ColorId {
    const val None = 0
    const val DefaultForeground = 1
    const val DefaultBackground = 2
}

/**
 * A standard token type.
 */
object StandardTokenType {
    const val Other = 0
    const val Comment = 1
    const val String = 2
    const val RegEx = 3
}

/**
 * Helpers to manage the "collapsed" metadata of an entire StackElement stack.
 * The following assumptions have been made:
 *  - languageId < 256 => needs 8 bits
 *  - unique color count < 512 => needs 9 bits
 *
 * The binary format is:
 * - -------------------------------------------
 *     3322 2222 2222 1111 1111 1100 0000 0000
 *     1098 7654 3210 9876 5432 1098 7654 3210
 * - -------------------------------------------
 *     xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx
 *     bbbb bbbb ffff ffff fFFF FBTT LLLL LLLL
 * - -------------------------------------------
 *  - L = LanguageId (8 bits)
 *  - T = StandardTokenType (2 bits)
 *  - B = Balanced bracket (1 bit)
 *  - F = FontStyle (4 bits)
 *  - f = foreground color (9 bits)
 *  - b = background color (9 bits)
 *
 */
object MetadataConsts {
    const val LANGUAGEID_MASK = 0b00000000000000000000000011111111
    const val TOKEN_TYPE_MASK = 0b00000000000000000000001100000000
    const val BALANCED_BRACKETS_MASK = 0b00000000000000000000010000000000
    const val FONT_STYLE_MASK = 0b00000000000000000111100000000000
    const val FOREGROUND_MASK = 0b00000000111111111000000000000000
    const val BACKGROUND_MASK = (0b11111111000000000000000000000000).toInt()

    const val ITALIC_MASK = 0b00000000000000000000100000000000
    const val BOLD_MASK = 0b00000000000000000001000000000000
    const val UNDERLINE_MASK = 0b00000000000000000010000000000000
    const val STRIKETHROUGH_MASK = 0b00000000000000000100000000000000

    // Semantic tokens cannot set the language id, so we can
    // use the first 8 bits for control purposes
    const val SEMANTIC_USE_ITALIC = 0b00000000000000000000000000000001
    const val SEMANTIC_USE_BOLD = 0b00000000000000000000000000000010
    const val SEMANTIC_USE_UNDERLINE = 0b00000000000000000000000000000100
    const val SEMANTIC_USE_STRIKETHROUGH = 0b00000000000000000000000000001000
    const val SEMANTIC_USE_FOREGROUND = 0b00000000000000000000000000010000
    const val SEMANTIC_USE_BACKGROUND = 0b00000000000000000000000000100000

    const val LANGUAGEID_OFFSET = 0
    const val TOKEN_TYPE_OFFSET = 8
    const val BALANCED_BRACKETS_OFFSET = 10
    const val FONT_STYLE_OFFSET = 11
    const val FOREGROUND_OFFSET = 15
    const val BACKGROUND_OFFSET = 24
}

