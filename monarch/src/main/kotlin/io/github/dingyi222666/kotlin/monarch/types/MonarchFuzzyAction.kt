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
 *
 * Contributors:
 * - Microsoft Corporation: Initial code, written in TypeScript, licensed under MIT license
 * - dingyi222666 <dingyi222666@foxmail.com> - translation and adaptation to Kotlin
 */


package io.github.dingyi222666.kotlin.monarch.types

sealed interface MonarchFuzzyAction {
    class ActionString(val action: String) : MonarchFuzzyAction

    data class ActionBase(
        val group: List<MonarchFuzzyAction>? = null,
        val test: ((id: String, matches: List<String>, state: String, eos: Boolean) -> MonarchFuzzyAction)? = null,
        val token: String? = null,
        val tokenSubst: Boolean? = null,
        val next: String? = null,
        val switchTo: String? = null,
        val goBack: Int? = null,
        val bracket: MonarchBracket? = null,
        val nextEmbedded: String? = null,
        val log: String? = null,
        val transform: ((states: List<String>) -> List<String>)? = null
    ) : MonarchFuzzyAction
}