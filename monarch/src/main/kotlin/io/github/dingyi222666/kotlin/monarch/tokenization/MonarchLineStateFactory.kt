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
 */

package io.github.dingyi222666.kotlin.monarch.tokenization

/**
 * The factory for creating [MonarchLineState] instances.
 *
 * Source from
 * [here](https://github.com/microsoft/vscode/blob/7215958b3c57945b49d3b70afdba7fb47319ca85/src/vs/editor/standalone/common/monarch/monarchLexer.ts#L157)
 */
object MonarchLineStateFactory {
    private val entries = mutableMapOf<String, MonarchLineState>()

    fun create(
        stack: MonarchStackElement, embeddedLanguageData: EmbeddedLanguageData?
    ): MonarchLineState {
        if (embeddedLanguageData != null) {
            // no caching when embedding
            return MonarchLineState(stack, embeddedLanguageData)
        }
        if (stack.depth >= CACHE_STACK_DEPTH) {
            // no caching above a certain depth
            return MonarchLineState(stack, null)
        }

        val stackElementId = MonarchStackElement.getStackElementId(stack)

        val result = this.entries[stackElementId]

        return if (result != null) {
            result
        } else {
            val newElement = MonarchLineState(stack, null)
            this.entries[stackElementId] = newElement
            newElement
        }
    }
}
