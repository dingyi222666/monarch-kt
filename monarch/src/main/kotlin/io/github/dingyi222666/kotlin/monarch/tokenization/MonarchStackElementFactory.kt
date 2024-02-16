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

internal const val CACHE_STACK_DEPTH = 5


/**
 * Reuse the same stack elements up to a certain depth.
 *
 * Source from [here](https://github.com/microsoft/vscode/blob/7215958b3c57945b49d3b70afdba7fb47319ca85/src/vs/editor/standalone/common/monarch/monarchLexer.ts#L157)
 */
object MonarchStackElementFactory {

    private val entries = mutableMapOf<String, MonarchStackElement>()

    fun create(parent: MonarchStackElement?, state: String): MonarchStackElement {
        if (parent != null && parent.depth >= CACHE_STACK_DEPTH) {
            // no caching above a certain depth
            return MonarchStackElement(parent, state)
        }
        val stackElementId = MonarchStackElement.getStackElementId(parent!!)
        val fullId = when {
            stackElementId.isNotEmpty() -> "$stackElementId|"
            else -> ""
        } + state

        val result = entries[fullId]

        return if (result != null) {
            result
        } else {
            val newElement = MonarchStackElement(parent, state)
            entries[fullId] = newElement
            newElement
        }
    }
}
