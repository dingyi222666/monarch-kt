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

import io.github.dingyi222666.monarch.language.LanguageRegistry
import io.github.dingyi222666.monarch.types.*
import java.util.Collections.addAll

/**
 * Modern tokens collector for monarch.
 *
 * Source from
 * [here](https://github.com/microsoft/vscode/blob/d30f7018d2ba0b4fe35816989363e6f5b84f7361/src/vs/editor/standalone/common/monarch/monarchLexer.ts#L295)
 */
class MonarchModernTokensCollector(
    private val languageRegistry: LanguageRegistry = LanguageRegistry.instance,
    private val tokenTheme: ITokenTheme,
) : IMonarchTokensCollector {

    private var languageId: String? = null
    private var lastTokenType: String? = null
    private var lastTokenLanguage: String? = null

    private var prependTokens = emptyList<Int>()
    private var tokens = mutableListOf<Int>()

    private var lastTokenMetadata: Int = 0

    override fun enterLanguage(languageId: String) {
        this.languageId = languageId
    }

    override fun emit(startOffset: Int, type: String) {
        val metadata = tokenTheme.match(LanguageId.Null, type) or MetadataConsts.BALANCED_BRACKETS_MASK
        if (lastTokenMetadata == metadata) {
            return;
        }
        lastTokenMetadata = metadata
        tokens.add(startOffset)
        tokens.add(metadata)
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

        val nestedResult =
            nestedLanguageTokenizationSupport.tokenizeEncoded(embeddedLanguageLine, hasEOL, embeddedModeState)
        if (offsetDelta != 0) {
            /*for (let i = 0, len = nestedResult.tokens.length; i < len; i += 2) {
                nestedResult.tokens[i] += offsetDelta;
            }*/
            for (i in 0 until nestedResult.tokens.size step 2) {
                nestedResult.tokens[i] += offsetDelta
            }

        }

        this.prependTokens = prependTokens + tokens + nestedResult.tokens
        tokens.clear()
        this.lastTokenMetadata = 0;
        this.languageId = null
        return nestedResult.endState
    }

    fun finalize(endState: MonarchLineState): EncodedTokenizationResult {
        return EncodedTokenizationResult((prependTokens + tokens).toMutableList(), endState)
    }
}
