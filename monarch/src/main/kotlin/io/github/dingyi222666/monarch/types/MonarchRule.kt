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
 *
 * Contributors:
 * - Microsoft Corporation: Initial code, written in TypeScript, licensed under MIT license
 * - dingyi222666 <dingyi222666@foxmail.com> - translation and adaptation to Kotlin
 */


package io.github.dingyi222666.monarch.types

import io.github.dingyi222666.kotlin.regex.GlobalRegexLib
import io.github.dingyi222666.monarch.common.compileAction
import io.github.dingyi222666.monarch.common.compileRegExp
import io.github.dingyi222666.monarch.extension.*
import io.github.dingyi222666.kotlin.regex.Regex

/**
 * See [here](https://github.com/microsoft/vscode/blob/7215958b3c57945b49d3b70afdba7fb47319ca85/src/vs/editor/standalone/common/monarch/monarchCommon.ts#L70)
 */
interface IMonarchRule {
    var regex: io.github.dingyi222666.kotlin.regex.Regex
    var action: MonarchFuzzyAction
    var matchOnlyAtLineStart: Boolean
    val name: String
}

class MonarchRule(
    name: String
) : IMonarchRule {
    override var regex: io.github.dingyi222666.kotlin.regex.Regex = GlobalRegexLib.compile("")
    override var action: MonarchFuzzyAction = MonarchFuzzyAction.ActionString("")
    override var matchOnlyAtLineStart = false
    private var innerName: String = name

    override val name: String
        get() = innerName

    fun setRegex(lexer: IMonarchLexerMin, regexArg: Any) {
        val currentRegex = when(regexArg) {
            is String -> regexArg
            is Regex -> regexArg.pattern
            else -> throw lexer.createError("rules must start with a match string or regular expression: ${this.name}")
        }

        matchOnlyAtLineStart = (currentRegex.isNotEmpty() && currentRegex[0] == '^')
        innerName = "$name: $currentRegex"

        val lexerRegexString = if (matchOnlyAtLineStart) {
            currentRegex.substring(1)
        } else {
            currentRegex
        }

        regex = lexer.compileRegExp("^(?:$lexerRegexString)")
    }

    fun setAction(lexer: IMonarchLexerMin, act: Any) {
        this.action = lexer.compileAction(innerName, act)
    }
}