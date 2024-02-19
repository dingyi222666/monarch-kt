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
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import io.github.dingyi222666.monarch.extension.UnionType
import io.github.dingyi222666.monarch.types.MonarchLanguageAction
import io.github.dingyi222666.monarch.types.MonarchLanguageRule


class MonarchLanguageRuleAdapter : JsonAdapter<Map<String, List<MonarchLanguageRule>>>() {


    override fun fromJson(reader: JsonReader): Map<String, List<MonarchLanguageRule>> {
        reader.isLenient = true

        val result = mutableMapOf<String, List<MonarchLanguageRule>>()
        // an object
        reader.beginObject()

        // { rule1: [], rule2: [] }
        while (reader.peek() != JsonReader.Token.END_OBJECT) {
            // get key
            val key = reader.nextName()

            result[key] = parseRules(reader)
        }

        reader.endObject()

        return result
    }

    private fun parseRules(reader: JsonReader): List<MonarchLanguageRule> {
        val result = mutableListOf<MonarchLanguageRule>()

        reader.beginArray()

        while (reader.peek() != JsonReader.Token.END_ARRAY) {
            when (val nextToken = reader.peek()) {
                JsonReader.Token.BEGIN_ARRAY -> {
                    result.add(parseArrayRule(reader))
                }

                JsonReader.Token.BEGIN_OBJECT -> {
                    result.add(parseObjectRule(reader))
                }

                else -> {
                    throw JsonDataException("Unexpected token: $nextToken")
                }
            }
        }

        reader.endArray()

        return result
    }

    /* parse [regex, action]
     [regex, action, next] */
    private fun parseArrayRule(reader: JsonReader): MonarchLanguageRule {
        reader.beginArray()
        // regex
        val regex = reader.nextString()

        // action can be a string or an object, ...

        val action = parseAction(reader)

        val nextToken = reader.peek()
        // [regex, action, next]
        val result = if (nextToken == JsonReader.Token.STRING) {
            val nextState = reader.nextString()
            MonarchLanguageRule.ShortRule2(UnionType(regex), action, nextState)
        } else {
            MonarchLanguageRule.ShortRule1(UnionType(regex), action)
        }

        reader.endArray()

        return result
    }

    // {regex: regex, action: action }
    // { include: }
    private fun parseObjectRule(reader: JsonReader): MonarchLanguageRule {
        reader.beginObject()

        var regex: String? = null
        var action: MonarchLanguageAction? = null
        var include: String? = null

        while (reader.peek() != JsonReader.Token.END_OBJECT) {
            when (val nextName = reader.nextName()) {
                "regex" -> regex = reader.nextString()
                "action" -> action = parseAction(reader)
                "include" -> include = reader.nextString()
                else -> throw JsonDataException("Unexpected name: $nextName in ${reader.path}")
            }
        }

        reader.endObject()

        if (include != null) {
            return MonarchLanguageRule.ExpandedLanguageRule(include)
        }

        if (regex == null || action == null) {
            throw JsonDataException("Missing regex or action in ${reader.path}")
        }

        return MonarchLanguageRule.ShortRule1(UnionType(regex), action)
    }

    // string
    // [action1,...,actionN]
    // { token: tokenclass }
    // { cases: { guard1: action1, ..., guardN: actionN } }
    private fun parseAction(reader: JsonReader): MonarchLanguageAction {
        return when (val nextToken = reader.peek()) {
            // string
            JsonReader.Token.STRING -> {
                MonarchLanguageAction.ShortLanguageAction(reader.nextString())
            }
            // [action1,...,actionN]
            JsonReader.Token.BEGIN_ARRAY -> {
                reader.beginArray()
                val result = mutableListOf<MonarchLanguageAction>()

                while (reader.peek() != JsonReader.Token.END_ARRAY) {
                    result.add(parseAction(reader))
                }

                reader.endArray()

                MonarchLanguageAction.ActionArray(result)
            }

            // { token: tokenclass }
            // { cases: { guard1: action1, ..., guardN: actionN } }
            JsonReader.Token.BEGIN_OBJECT -> {
                reader.beginObject()

                val action = MonarchLanguageAction.MutableExpandedLanguageAction()

                while (reader.peek() != JsonReader.Token.END_OBJECT) {
                    when (val nextName = reader.nextName()) {
                        "cases" -> {
                            action.cases = parseActionCases(reader)
                        }

                        "token" -> {
                            action.token = reader.nextString()
                        }

                        "next" -> {
                            action.next = reader.nextString()
                        }

                        "switchTo" -> {
                            action.switchTo = reader.nextString()
                        }

                        "goBack" -> {
                            action.goBack = reader.nextInt()
                        }

                        "bracket" -> {
                            action.bracket = reader.nextString()
                        }

                        "nextEmbedded" -> {
                            action.nextEmbedded = reader.nextString()
                        }

                        "log" -> {
                            action.log = reader.nextString()
                        }

                        else -> throw JsonDataException("Unexpected name: $nextName in ${reader.path}")
                    }
                }

                reader.endObject()

                action.toExpandedLanguageAction()
            }

            else -> throw JsonDataException("Unexpected token: $nextToken in path ${reader.path}")
        }
    }

    // { cases: { guard1: action1, ..., guardN: actionN } }
    private fun parseActionCases(reader: JsonReader): Map<String, MonarchLanguageAction> {
        reader.beginObject()

        val result = mutableMapOf<String, MonarchLanguageAction>()

        while (reader.peek() != JsonReader.Token.END_OBJECT) {
            // get key
            val guard = reader.nextName()

            result[guard] = parseAction(reader)

        }

        reader.endObject()

        return result
    }

    override fun toJson(writer: JsonWriter, value: Map<String, List<MonarchLanguageRule>>?) {
        if (value == null) {
            writer.nullValue()
            return
        }
    }
}