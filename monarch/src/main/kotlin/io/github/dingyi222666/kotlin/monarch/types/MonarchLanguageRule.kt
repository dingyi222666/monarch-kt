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

import io.github.dingyi222666.kotlin.monarch.extension.UnionType


sealed interface MonarchLanguageRule {
    data class ShortRule1(
        override val regex: UnionType<String, Regex>,
        override val action: MonarchLanguageAction
    ) : MonarchLanguageRule, ShortRule

    data class ShortRule2(
        override val regex: UnionType<String, Regex>,
        override val action: MonarchLanguageAction,
        val nextState: String
    ) : MonarchLanguageRule, ShortRule

    data class ExpandedLanguageRule(
        val include: String
    ) : MonarchLanguageRule

    interface ShortRule {
        val regex: UnionType<String, Regex>
        val action: MonarchLanguageAction
    }
}


