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
 */

package io.github.dingyi222666.monarch.types

import io.github.dingyi222666.monarch.tokenization.EmbeddedLanguageData

/**
 * Interface for collecting tokens.
 *
 * Source from
 * [here](https://github.com/microsoft/vscode/blob/d30f7018d2ba0b4fe35816989363e6f5b84f7361/src/vs/editor/standalone/common/monarch/monarchLexer.ts#L232C11-L232C35)
 */
interface IMonarchTokensCollector {
    fun enterLanguage(languageId: String)
    fun emit(startOffset: Int, type: String)
    fun nestedLanguageTokenize(
            embeddedLanguageLine: CharSequence,
            hasEOL: Boolean,
            embeddedLanguageData: EmbeddedLanguageData,
            offsetDelta: Int
    ): TokenizeState
}
