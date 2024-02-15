/*
 * monarch-kt - Kotlin port of Monaco monarch library.
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
 *
 * Contributors:
 * - Microsoft Corporation: Initial code, written in TypeScript, licensed under MIT license
 * - dingyi222666 <dingyi222666@foxmail.com> - translation and adaptation to Kotlin
 */

package io.github.dingyi222666.kotlin.monarch

// See https://github.com/microsoft/vscode/blob/633992ca1cc89097c60d4a4e0eba8acb050edabb/src/vs/editor/standalone/common/monarch/monarchTypes.ts

// A Monarch language definition
interface IMonarchLanguage {
    // map from string to ILanguageRule[]
    val tokenizer: Map<String, List<IMonarchLanguageRule>>

    // is the language case-insensitive?
    val ignoreCase: Boolean?

    // is the language unicode-aware? (i.e., /\u{1D306}/)
    val unicode: Boolean?

    // if no match in the tokenizer assign this token class (default 'source')
    val defaultToken: String?

    // for example [['{','}','delimiter.curly']]
    val brackets: List<IMonarchLanguageBracket>?

    // start symbol in the tokenizer (by default the first entry is used)
    val start: String?

    // attach this to every token class (by default '.' + name)
    val tokenPostfix: String?

    // include line feeds (in the form of a \n character) at the end of lines
    // Defaults to false
    val includeLF: Boolean?

    // Other keys that can be referred to by the tokenizer.
    @Suppress("UNUSED")
    operator fun get(key: String): Any?
}

sealed class MonarchLanguageRule {
    data class ShortMonarchLanguageRule1(
        val regex: String,
        val action: IMonarchLanguageAction
    ) : MonarchLanguageRule()

    data class ShortMonarchLanguageRule2(
        val regex: String,
        val action: IMonarchLanguageAction,
        val nextState: String
    ) : MonarchLanguageRule()

    data class ExpandedMonarchLanguageRule(
        val regex: String?,
        val action: IMonarchLanguageAction?,
        val include: String?
    ) : MonarchLanguageRule()
}

typealias IMonarchLanguageRule = MonarchLanguageRule

sealed class IMonarchLanguageAction {
    data class ShortMonarchLanguageAction(val action: String) : IMonarchLanguageAction()
    data class ExpandedMonarchLanguageAction(
        // array of actions for each parenthesized match group
        val group: List<IMonarchLanguageAction>? = null,

        // map from string to ILanguageAction
        val cases: Map<String, IMonarchLanguageAction>? = null,

        // token class (ie. css class) (or "@brackets" or "@rematch")
        val token: String? = null,

        // the next state to push, or "@push", "@pop", "@popall"
        val next: String? = null,

        // switch to this state
        val switchTo: String? = null,

        // go back n characters in the stream
        val goBack: Int? = null,

        // @open or @close
        val bracket: String? = null,

        // switch to embedded language (using the mimetype) or get out using "@pop"
        val nextEmbedded: String? = null,

        // log a message to the browser console window
        val log: String? = null
    ) : IMonarchLanguageAction()


    data class ActionArray(val actions: Array<out IMonarchLanguageAction>) : IMonarchLanguageAction() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ActionArray

            return actions.contentEquals(other.actions)
        }

        override fun hashCode(): Int {
            return actions.contentHashCode()
        }
    }
}


// This interface can be shortened as an array, ie. ['{','}','delimiter.curly']
data class IMonarchLanguageBracket(
    // open bracket
    val open: String,

    // closing bracket
    val close: String,

    // token class
    val token: String
)