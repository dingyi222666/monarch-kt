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
 *
 * Contributors:
 * - Microsoft Corporation: Initial code, written in TypeScript, licensed under MIT license
 * - dingyi222666 <dingyi222666@foxmail.com> - translation and adaptation to Kotlin
 */


package io.github.dingyi222666.monarch.types

// A Monarch language definition
interface IMonarchLanguage {
    // map from string to ILanguageRule[]
    val tokenizer: MutableMap<String, MutableList<MonarchLanguageRule>>?

    // is the language case-insensitive?
    var ignoreCase: Boolean?

    // is the language unicode-aware? (i.e., /\u{1D306}/). Defaults to true.
    var unicode: Boolean?

    // if no match in the tokenizer assign this token class (default 'source')
    val defaultToken: String?

    // for example [['{','}','delimiter.curly']]
    val brackets: MutableList<MonarchLanguageBracket>?

    // start symbol in the tokenizer (by default the first entry is used)
    val start: String?

    // attach this to every token class (by default '.' + name)
    val tokenPostfix: String?

    // include line feeds (in the form of a \n character) at the end of lines
    // Defaults to false
    var includeLF: Boolean?

    // Other keys that can be referred to by the tokenizer.
    @Suppress("UNUSED")
    operator fun get(key: String): Any?
}