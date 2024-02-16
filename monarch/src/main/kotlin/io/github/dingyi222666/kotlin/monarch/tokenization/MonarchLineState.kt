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

import io.github.dingyi222666.kotlin.monarch.types.TokenizeState

/**
 * The state of the tokenizer on a line.
 *
 * Source from [here](https://github.com/microsoft/vscode/blob/7215958b3c57945b49d3b70afdba7fb47319ca85/src/vs/editor/standalone/common/monarch/monarchLexer.ts#L193)
 */
class MonarchLineState(
    val stack: MonarchStackElement,
    val embeddedLanguageData: EmbeddedLanguageData?
) : TokenizeState {

    override fun clone(): MonarchLineState {
        val embeddedLanguageDataClone = this.embeddedLanguageData?.clone()
        // save an object
        if (embeddedLanguageDataClone === this.embeddedLanguageData) {
            return this
        }
        return MonarchLineStateFactory.create(this.stack, this.embeddedLanguageData)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is MonarchLineState) {
            return false
        }
        if (this.stack != other.stack) {
            return false
        }
        if (this.embeddedLanguageData === null && other.embeddedLanguageData === null) {
            return true
        }
        if (this.embeddedLanguageData === null || other.embeddedLanguageData === null) {
            return false
        }
        return this.embeddedLanguageData == other.embeddedLanguageData
    }

    override fun hashCode(): Int {
        var result = stack.hashCode()
        result = 31 * result + (embeddedLanguageData?.hashCode() ?: 0)
        return result
    }
}