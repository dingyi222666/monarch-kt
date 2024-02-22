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
 * See [here](https://github.com/microsoft/vscode/blob/7215958b3c57945b49d3b70afdba7fb47319ca85/src/vs/editor/standalone/common/monarch/monarchCommon.ts#L52-L93)
 */
sealed interface MonarchFuzzyAction {
    @JvmInline
    value class ActionString(val token: String) : MonarchFuzzyAction

    data class ActionBase(
        var token: String? = null,
        var tokenSubst: Boolean? = null,
        var next: String? = null,
        var switchTo: String? = null,
        var goBack: Int? = null,
        var bracket: MonarchBracketType? = null,
        var nextEmbedded: String? = null,
        var log: String? = null,
        var test: ((id: String, matches: List<String>, state: String, eos: Boolean) -> MonarchFuzzyAction)? = null,
        var transform: ((states: List<String>) -> List<String>)? = null
    ) : MonarchFuzzyAction

    @JvmInline
    value class ActionArray(val actions: List<MonarchFuzzyAction>) : MonarchFuzzyAction

}