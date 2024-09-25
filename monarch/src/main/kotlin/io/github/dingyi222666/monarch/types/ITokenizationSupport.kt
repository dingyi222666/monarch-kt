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

/**
 * The tokenization support.
 *
 * Source from [here](https://github.com/microsoft/vscode/blob/233fd797c0878a551994e55f1e87c23f5a200969/src/vs/editor/common/languages.ts#L87)
 */
interface ITokenizationSupport {

    fun getInitialState(): TokenizeState

    fun tokenize(line: CharSequence, hasEOL: Boolean, lineState: TokenizeState): TokenizationResult

    fun tokenizeEncoded(line: CharSequence, hasEOL: Boolean, lineState: TokenizeState): EncodedTokenizationResult;
}