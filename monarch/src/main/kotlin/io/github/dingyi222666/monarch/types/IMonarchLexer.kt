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

import io.github.dingyi222666.regex.RegexLib

interface IMonarchLexer : IMonarchLexerMin {
    var maxStack: Int
    var start: String?
    var tokenPostfix: String

    var tokenizer: MutableMap<String, MutableList<MonarchRule>>
    var brackets: List<MonarchLanguageBracket>

    override var ignoreCase: Boolean
    override var unicode: Boolean
}

class MonarchLexer(
    override val languageId: String,
    override val regexLib: RegexLib,
    private val attrMap: Map<String,Any>
) : IMonarchLexer {
    override var maxStack: Int = 0

    override var start: String? = null

    override var tokenPostfix: String = ""

    override var tokenizer: MutableMap<String, MutableList<MonarchRule>> = mutableMapOf()

    override var brackets: List<MonarchLanguageBracket> = listOf()

    override var ignoreCase: Boolean = false

    override var unicode: Boolean = false

    override var includeLF: Boolean = true
    override var noThrow: Boolean = false
    override var usesEmbedded: Boolean = false
    override var defaultToken: String = ""
    override var stateNames: Map<String, Any> = mapOf()

    override fun get(attr: String): Any? = attrMap[attr]

    override fun toString(): String {
        return "MonarchLexer(languageId='$languageId', attrMap=$attrMap, maxStack=$maxStack, start=$start, tokenPostfix='$tokenPostfix', tokenizer=$tokenizer, brackets=$brackets, ignoreCase=$ignoreCase, unicode=$unicode, includeLF=$includeLF, noThrow=$noThrow, usesEmbedded=$usesEmbedded, defaultToken='$defaultToken', stateNames=$stateNames)"
    }

}