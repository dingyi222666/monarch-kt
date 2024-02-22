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

package io.github.dingyi222666.monarch.tokenization

import io.github.dingyi222666.monarch.types.MetadataConsts


object TokenMetadata {
    fun getLanguageId(metadata: Int): Int {
        return (metadata and MetadataConsts.LANGUAGEID_MASK) ushr MetadataConsts.LANGUAGEID_OFFSET
    }

    fun getTokenType(metadata: Int): Int {
        return (metadata and MetadataConsts.TOKEN_TYPE_MASK) ushr MetadataConsts.TOKEN_TYPE_OFFSET
    }

    fun containsBalancedBrackets(metadata: Int): Boolean {
        return (metadata and MetadataConsts.BALANCED_BRACKETS_MASK) != 0
    }

    fun getFontStyle(metadata: Int): Int {
        return (metadata and MetadataConsts.FONT_STYLE_MASK) ushr MetadataConsts.FONT_STYLE_OFFSET
    }

    fun getForeground(metadata: Int): Int {
        return (metadata and MetadataConsts.FOREGROUND_MASK) ushr MetadataConsts.FOREGROUND_OFFSET
    }

    fun getBackground(metadata: Int): Int {
        return (metadata and MetadataConsts.BACKGROUND_MASK) ushr MetadataConsts.BACKGROUND_OFFSET
    }

}
