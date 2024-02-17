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

package io.github.dingyi222666.monarch.tokenization

/**
 * A stack element.
 *
 * Source from [here](https://github.com/microsoft/vscode/blob/7215958b3c57945b49d3b70afdba7fb47319ca85/src/vs/editor/standalone/common/monarch/monarchLexer.ts#L62)
 */
class MonarchStackElement(
    val parent: MonarchStackElement?,
    val state: String
) {
    val depth: Int = (parent?.depth ?: 0) + 1

    companion object {
        fun getStackElementId(element: MonarchStackElement?): String {
            var result = ""
            var current = element
            while (current != null) {
                if (result.isNotEmpty()) {
                    result += "|"
                }
                result += current.state
                current = current.parent
            }
            return result
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is MonarchStackElement) {
            return false
        }

        var a: MonarchStackElement? = this
        var b: MonarchStackElement? = other

        while (a != null && b != null) {
            if (a === b) {
                return true
            }
            if (a.state != b.state) {
                return false
            }
            a = a.parent
            b = b.parent
        }
        return a == null && b == null
    }

    fun push(state: String): MonarchStackElement {
        return MonarchStackElementFactory.create(this, state)
    }

    fun pop(): MonarchStackElement? {
        return parent
    }

    fun popall(): MonarchStackElement {
        var result: MonarchStackElement = this
        while (result.parent != null) {
            result = result.parent ?: break
        }
        return result
    }

    fun switchTo(state: String): MonarchStackElement {
        return MonarchStackElementFactory.create(parent, state)
    }

    override fun hashCode(): Int {
        var result = parent?.hashCode() ?: 0
        result = 31 * result + state.hashCode()
        result = 31 * result + depth
        return result
    }
}
