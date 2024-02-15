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

import io.github.dingyi222666.kotlin.monarch.common.compileAction
import io.github.dingyi222666.kotlin.monarch.common.compileRegExp
import io.github.dingyi222666.kotlin.monarch.extension.createError

interface IMonarchRule {
    var regex: Regex
    var action: MonarchFuzzyAction
    var matchOnlyAtLineStart: Boolean
    val name: String
}

class MonarchRule(
    name: String
) : IMonarchRule {
    override var regex: Regex = Regex("")
    override var action: MonarchFuzzyAction = MonarchFuzzyAction.ActionString("")
    override var matchOnlyAtLineStart: Boolean = false
    private var innerName: String = name

    override val name: String
        get() = innerName

    fun setRegex(lexer: IMonarchLexerMin, regexArg: Any) {
        var sregex: String
        if (regexArg is String) {
            sregex = regexArg
        } else if (regexArg is Regex) {
            sregex = regexArg.pattern
        } else {
            throw lexer.createError("rules must start with a match string or regular expression: ${this.name}");
        }

        matchOnlyAtLineStart = (sregex.isNotEmpty() && sregex[0] == '^');
        innerName = "$name: $sregex"
        val lexerRegexString = if (matchOnlyAtLineStart) {
            sregex.substring(1)
        } else {
            sregex
        }

        regex = lexer.compileRegExp("^(?:$lexerRegexString)");
    }

    fun setAction(lexer: IMonarchLexerMin, act: Any) {
        this.action = lexer.compileAction(this.name, act);
    }
}