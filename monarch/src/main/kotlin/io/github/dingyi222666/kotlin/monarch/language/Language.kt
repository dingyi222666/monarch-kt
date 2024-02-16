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
 */

package io.github.dingyi222666.kotlin.monarch.language

import io.github.dingyi222666.kotlin.monarch.types.IMonarchLanguage

data class Language(
    val languageName: String,
    val monarchLanguage: IMonarchLanguage,
    val languageId: String = languageName,
    val fileExtensions: Array<String>? = null,
    val embeddedLanguages: Map<String, String>? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Language

        if (languageName != other.languageName) return false
        if (monarchLanguage != other.monarchLanguage) return false
        if (languageId != other.languageId) return false
        if (fileExtensions != null) {
            if (other.fileExtensions == null) return false
            if (!fileExtensions.contentEquals(other.fileExtensions)) return false
        } else if (other.fileExtensions != null) return false
        if (embeddedLanguages != other.embeddedLanguages) return false

        return true
    }

    override fun hashCode(): Int {
        var result = languageName.hashCode()
        result = 31 * result + monarchLanguage.hashCode()
        result = 31 * result + languageId.hashCode()
        result = 31 * result + (fileExtensions?.contentHashCode() ?: 0)
        result = 31 * result + (embeddedLanguages?.hashCode() ?: 0)
        return result
    }
}