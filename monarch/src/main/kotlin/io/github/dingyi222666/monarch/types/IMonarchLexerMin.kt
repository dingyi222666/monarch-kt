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

import io.github.dingyi222666.kotlin.regex.RegexLib

/**
 * Minimal interface for a Monarch lexer.
 *
 * Source from [here](https://github.com/microsoft/vscode/blob/7215958b3c57945b49d3b70afdba7fb47319ca85/src/vs/editor/standalone/common/monarch/monarchCommon.ts#L23)
 */
interface IMonarchLexerMin {
    val languageId: String
    var includeLF: Boolean
    var noThrow: Boolean
    var ignoreCase: Boolean
    var unicode: Boolean
    var usesEmbedded: Boolean
    var defaultToken: String
    var stateNames: Map<String, Any>
    val regexLib: RegexLib

    // Other keys that can be referred to by the tokenizer.
    @Suppress("UNUSED")
    operator fun get(attr: String): Any?
}
