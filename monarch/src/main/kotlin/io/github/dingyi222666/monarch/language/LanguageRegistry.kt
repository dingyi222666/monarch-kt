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

package io.github.dingyi222666.monarch.language

import io.github.dingyi222666.monarch.common.compile
import io.github.dingyi222666.monarch.tokenization.MonarchTokenizer
import io.github.dingyi222666.monarch.types.IThemeService
import io.github.dingyi222666.monarch.types.ITokenizationSupport

/**
 * The language registry.
 *
 */
class LanguageRegistry(
    val parent: LanguageRegistry? = null
) {

    private val languageIdToLanguages = mutableMapOf<String, Language>()
    private val tokenizationSupports = mutableMapOf<String, ITokenizationSupport>()

    fun registerTokenizer(languageId: String, support: ITokenizationSupport) {
        this.tokenizationSupports[languageId] = support
    }

    fun getTokenizer(languageId: String): ITokenizationSupport? {
        return this.tokenizationSupports[languageId] ?: parent?.getTokenizer(languageId)
    }

    inline fun <reified T : ITokenizationSupport> getTokenizerCast(languageId: String): T? {
        return getTokenizer(languageId) as? T
    }

    fun unregisterTokenizer(languageId: String) {
        this.tokenizationSupports.remove(languageId)
    }

    fun registerLanguage(
        language: Language,
        compileToTokenizer: Boolean = true,
        themeService: IThemeService? = null,
        maxTokenizationLineLength: Int = 5000
    ) {
        this.languageIdToLanguages[language.languageId] = language

        if (compileToTokenizer) {
            val compiledLexer = language.monarchLanguage.compile(language.languageId)
            val tokenizer =
                MonarchTokenizer(language.languageId, compiledLexer, this, themeService, maxTokenizationLineLength)
            this.registerTokenizer(language.languageId, tokenizer)
        }
    }

    fun getLanguage(languageId: String): Language? {
        return this.languageIdToLanguages[languageId] ?: parent?.getLanguage(languageId)
    }

    fun isRegisteredLanguage(languageId: String): Boolean {
        return this.languageIdToLanguages.containsKey(languageId)
    }

    fun getLanguageByName(languageName: String): Language? {
        return this.languageIdToLanguages.values.firstOrNull { it.languageName == languageName }
            ?: parent?.getLanguageByName(languageName)
    }

    fun getRegisteredLanguages(): List<Language> {
        return this.languageIdToLanguages.values.toList().plus(
            parent?.getRegisteredLanguages() ?: emptyList()
        )
    }

    fun getRegisteredTokenizers(): List<ITokenizationSupport> {
        return this.tokenizationSupports.values.toList().plus(
            parent?.getRegisteredTokenizers() ?: emptyList()
        )
    }

    fun getLanguagesByExt(ext: String): List<Language> {
        return this.languageIdToLanguages.values.filter { it.fileExtensions?.contains(ext) ?: false }.plus(
            parent?.getLanguagesByExt(ext) ?: emptyList()
        )
    }

    fun unregisterLanguage(languageId: String) {
        this.languageIdToLanguages.remove(languageId)
        this.unregisterTokenizer(languageId)
    }

    fun clear() {
        this.languageIdToLanguages.clear()
        this.tokenizationSupports.clear()
    }

    companion object {
        val instance by lazy {
            LanguageRegistry()
        }
    }
}