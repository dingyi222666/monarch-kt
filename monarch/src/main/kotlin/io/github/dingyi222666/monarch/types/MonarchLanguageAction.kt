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

/**
 * See [here](https://github.com/microsoft/vscode/blob/7215958b3c57945b49d3b70afdba7fb47319ca85/src/vs/editor/standalone/common/monarch/monarchTypes.ts#L55-L130)
 */
sealed interface MonarchLanguageAction {
    @JvmInline
    value class ShortLanguageAction(val token: String) : MonarchLanguageAction

    data class ExpandedLanguageAction(
        // array of actions for each parenthesized match group
        val group: List<MonarchLanguageAction>? = null,

        // map from string to ILanguageAction
        val cases: Map<String, MonarchLanguageAction>? = null,

        // token class (ie. css class) (or "@brackets" or "@rematch")
        val token: String? = null,

        // the next state to push, or "@push", "@pop", "@popall"
        var next: String? = null,

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
    ) : MonarchLanguageAction


    @JvmInline
    value class ActionArray(val actions: List<MonarchLanguageAction>) : MonarchLanguageAction

    data class MutableExpandedLanguageAction(
        // array of actions for each parenthesized match group
        var group: List<MonarchLanguageAction>? = null,

        // map from string to ILanguageAction
        var cases: Map<String, MonarchLanguageAction>? = null,

        // token class (ie. css class) (or "@brackets" or "@rematch")
        var token: String? = null,

        // the next state to push, or "@push", "@pop", "@popall"
        var next: String? = null,

        // switch to this state
        var switchTo: String? = null,

        // go back n characters in the stream
        var goBack: Int? = null,

        // @open or @close
        var bracket: String? = null,

        // switch to embedded language (using the mimetype) or get out using "@pop"
        var nextEmbedded: String? = null,

        // log a message to the browser console window
        var log: String? = null
    ) {
        fun toExpandedLanguageAction(): ExpandedLanguageAction = ExpandedLanguageAction(
            group,
            cases,
            token,
            next,
            switchTo,
            goBack,
            bracket,
            nextEmbedded,
            log
        )
    }
}






