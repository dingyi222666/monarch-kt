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

package io.github.dingyi222666.monarch.tokenization

import io.github.dingyi222666.monarch.language.LanguageRegistry
import io.github.dingyi222666.monarch.types.IMonarchTokensCollector
import io.github.dingyi222666.monarch.types.Token
import io.github.dingyi222666.monarch.types.TokenizationResult
import io.github.dingyi222666.monarch.types.TokenizeState

/**
 * Classic tokens collector for monarch.
 *
 * Source from
 * [here](https://github.com/microsoft/vscode/blob/d30f7018d2ba0b4fe35816989363e6f5b84f7361/src/vs/editor/standalone/common/monarch/monarchLexer.ts#L238C7-L238C36)
 */
class MonarchClassicTokensCollector(
    private val languageRegistry: LanguageRegistry = LanguageRegistry.instance
) : IMonarchTokensCollector {
    private val tokens = mutableListOf<Token>()
    private var languageId: String? = null
    private var lastTokenType: String? = null
    private var lastTokenLanguage: String? = null

    override fun enterLanguage(languageId: String) {
        this.languageId = languageId
    }

    override fun emit(startOffset: Int, type: String) {
        if (lastTokenType == type && lastTokenLanguage == languageId) {
            return
        }
        lastTokenType = type
        lastTokenLanguage = languageId
        tokens.add(Token(startOffset, type, this.languageId))
    }

    override fun nestedLanguageTokenize(
        embeddedLanguageLine: String,
        hasEOL: Boolean,
        embeddedLanguageData: EmbeddedLanguageData,
        offsetDelta: Int
    ): TokenizeState {
        val nestedLanguageId = embeddedLanguageData.languageId
        val embeddedModeState = embeddedLanguageData.state

        val nestedLanguageTokenizationSupport = languageRegistry.getTokenizer(nestedLanguageId)
        if (nestedLanguageTokenizationSupport == null) {
            enterLanguage(nestedLanguageId)
            emit(offsetDelta, "")
            return embeddedModeState
        }

        val nestedResult = nestedLanguageTokenizationSupport.tokenize(embeddedLanguageLine, hasEOL, embeddedModeState)
        if (offsetDelta != 0) {
            for (token in nestedResult.tokens) {
                tokens.add(Token(token.offset + offsetDelta, token.type, token.language))
            }
        } else {
            // this._tokens = this._tokens.concat(nestedResult.tokens);
            this.tokens.addAll(nestedResult.tokens)
        }

        lastTokenType = null
        lastTokenLanguage = null
        languageId = null
        return nestedResult.endState
    }

    fun finalize(endState: MonarchLineState): TokenizationResult {
        return TokenizationResult(tokens, endState)
    }
}
