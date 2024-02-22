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

package io.github.dingyi222666.monarch.loader.json

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import io.github.dingyi222666.monarch.types.MonarchLanguageBracket

class MonarchLanguageBracketAdapter : JsonAdapter<List<MonarchLanguageBracket>>() {
    override fun fromJson(reader: JsonReader): List<MonarchLanguageBracket> {
        reader.isLenient = true
        val result = mutableListOf<MonarchLanguageBracket>()

        // array
        reader.beginArray()

        var nextToken = reader.peek()

        while (nextToken != JsonReader.Token.END_ARRAY) {
            when (nextToken) {
                JsonReader.Token.BEGIN_ARRAY -> {
                    parseArray(reader, result)
                }

                JsonReader.Token.BEGIN_OBJECT -> {
                    parseObject(reader, result)
                }

                else -> {
                    throw IllegalStateException("Expected BEGIN_ARRAY but was $nextToken in ${reader.path}")
                }
            }
            nextToken = reader.peek()
        }

        reader.endArray()

        return result
    }

    private fun parseObject(reader: JsonReader, result: MutableList<MonarchLanguageBracket>) {
        // object
        reader.beginObject()

        var open = ""
        var close = ""
        var token = ""

        // parse until end object
        while (reader.peek() != JsonReader.Token.END_OBJECT) {
            when (val name = reader.nextName()) {
                "open" -> open = reader.nextString()
                "close" -> close = reader.nextString()
                "token" -> token = reader.nextString()
                else -> throw IllegalStateException("Unknown name: $name in ${reader.path}")
            }
        }

        result.add(MonarchLanguageBracket(open, close, token))

        reader.endObject()
    }

    private fun parseArray(reader: JsonReader, result: MutableList<MonarchLanguageBracket>) {
        // parse until end array
        while (reader.peek() != JsonReader.Token.END_ARRAY) {
            reader.beginArray()
            val open = reader.nextString()
            val close = reader.nextString()
            val token = reader.nextString()
            result.add(MonarchLanguageBracket(open, close, token))
            reader.endArray()
        }
    }

    override fun toJson(writer: JsonWriter, value: List<MonarchLanguageBracket>?) {
        if (value == null) {
            writer.nullValue()
            return
        }
        writer.beginArray()

        for (i in value) {
            writer.beginArray()
            writer.value(i.open)
            writer.value(i.close)
            writer.value(i.token)
            writer.endArray()
        }

        writer.endArray()
    }
}