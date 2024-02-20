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

import com.squareup.moshi.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage
import io.github.dingyi222666.monarch.types.MonarchLanguage
import io.github.dingyi222666.monarch.types.MonarchLanguageBracket

@OptIn(ExperimentalStdlibApi::class)
class MonarchLanguageAdapter : JsonAdapter<IMonarchLanguage>() {

    private val stringArrayAdapter = Moshi.Builder().build().adapter<List<String>>()
    private val bracketAdapter = MonarchLanguageBracketAdapter()
    private val ruleAdapter = MonarchLanguageRuleAdapter()

    override fun fromJson(reader: JsonReader): IMonarchLanguage {
        reader.isLenient = true
        val language = MonarchLanguage()

        reader.beginObject()
        while (reader.hasNext()) {
            when (val name = reader.nextName()) {
                "defaultToken" -> language.defaultToken = reader.nextString()
                "includeLF" -> language.includeLF = reader.nextBoolean()
                "unicode" -> language.unicode = reader.nextBoolean()
                "ignoreCase" -> language.ignoreCase = reader.nextBoolean()
                "start" -> language.start = reader.nextString()
                "brackets" -> {
                    language.brackets = bracketAdapter.fromJson(reader)
                }

                "tokenizer" -> {
                    language.tokenizer = ruleAdapter.fromJson(reader)
                }

                else -> {
                    val value = reader.peek()

                    language.attrMap[name] = when (value) {
                        JsonReader.Token.BEGIN_ARRAY -> {
                            // parse string array
                            stringArrayAdapter.fromJson(reader) ?: emptyList<String>() // or throw?
                        }

                        JsonReader.Token.STRING -> {
                            reader.nextString()
                        }

                        else -> {
                            throw JsonDataException("Unexpected token $value in path ${reader.path}")
                        }
                    }
                }
            }
        }
        reader.endObject()
        return language
    }

    override fun toJson(writer: JsonWriter, value: IMonarchLanguage?) {
        if (value == null) {
            writer.nullValue()
            return
        }

        writer.indent = "  "
        writer.isLenient = true

        writer.beginObject()

        if (value.defaultToken != null) {
            writer.name("defaultToken")
            writer.value(value.defaultToken)
        }

        if (value.brackets != null && value.brackets?.isNotEmpty() == true) {
            writer.name("brackets")
            bracketAdapter.toJson(writer, value.brackets)
        }


        if (value.start != null) {
            writer.name("start")
            writer.value(value.start)
        }

        if (value.includeLF != null) {
            writer.name("includeLF")
            writer.value(value.includeLF)
        }

        if (value.unicode != null) {
            writer.name("unicode")
            writer.value(value.unicode)
        }

        if (value.ignoreCase != null) {
            writer.name("ignoreCase")
            writer.value(value.ignoreCase)
        }

        for ((key, value) in value.attrMap) {
            writer.name(key)
            when (value) {
                is List<*> -> {
                    stringArrayAdapter.toJson(
                        writer,
                        value as List<String>
                    )
                }

                is String -> {
                    writer.value(value)
                }

                is Regex -> {
                    writer.value(value.pattern)
                }

                else -> {
                    throw JsonDataException("Unexpected value $value in path ${writer.path}")
                }
            }
        }


        if (value.tokenizer != null) {
            writer.name("tokenizer")
            ruleAdapter.toJson(writer, value.tokenizer)
        }


        writer.endObject()
    }
}