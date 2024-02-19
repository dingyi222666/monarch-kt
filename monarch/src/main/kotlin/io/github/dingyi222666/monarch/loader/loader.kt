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

package io.github.dingyi222666.monarch.loader

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.github.dingyi222666.monarch.types.IMonarchLanguage
import io.github.dingyi222666.monarch.types.MonarchLanguageBracket
import io.github.dingyi222666.monarch.types.MonarchLanguageRule

internal val MoshiRoot: Moshi = Moshi.Builder()
    .add<IMonarchLanguage>(MonarchLanguageAdapter())
    .add<List<MonarchLanguageBracket>>(MonarchLanguageBracketAdapter())
    .add<Map<String, List<MonarchLanguageRule>>>(MonarchLanguageRuleAdapter())
    .build()

inline fun <reified T> Moshi.Builder.add(adapter: JsonAdapter<T>): Moshi.Builder {
    add(T::class.java, adapter)
    return this
}

fun loadMonarchJson(json: String): IMonarchLanguage? {
    return MoshiRoot.adapter(IMonarchLanguage::class.java).fromJson(json)
}