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

package io.github.dingyi222666.monarch.extension

import io.github.dingyi222666.monarch.types.*
import java.util.*


// https://github.com/microsoft/vscode/blob/7215958b3c57945b49d3b70afdba7fb47319ca85/src/vs/editor/standalone/common/monarch/monarchCompile.ts

/** Puts a string to lower case if 'ignoreCase' is set. */
fun IMonarchLexerMin.fixCase(str: String): String =
    if (ignoreCase) str.lowercase(Locale.getDefault()) else str

private val sanitizeRegex = Regex("[&<>'\"_]")

fun StringBuilder.sanitize(): String {
    var result = this
    sanitizeRegex.findAll(this).forEach { matchResult ->
        result.setCharAt(matchResult.range.first, '-')
    }
    return result.toString().intern()
}
// Helper functions for rule finding and substitution

val substituteRegex = Regex("\\$((\\$)|(#)|(\\d\\d?)|[sS](\\d\\d?)|@(\\w+))")


/**
 * substituteMatches is used on lexer strings and can substitutes predefined patterns: $$ => $ $# =>
 * id $n => matched entry n
 * @attr => contents of lexer.attr
 *
 * See documentation for more info
 */
fun IMonarchLexerMin.substituteMatches(
    str: String, id: String, matches: List<String>, state: String
): String {

    var stateMatches: MutableList<String>? = null
    return str.replace(substituteRegex) { matchResult ->
        val (_, dollar, hash, n, s, attr) = matchResult.destructured
        if (dollar.isNotEmpty()) {
            return@replace "$" // $$
        }
        if (hash.isNotEmpty()) {
            return@replace fixCase(id) // default $#
        }
        if (n.isNotEmpty() && n.toInt() < matches.size) {
            return@replace fixCase(matches[n.toInt()]) // $n
        }
        if (attr.isNotEmpty() && this[attr] is String) {
            return@replace this[attr] as String // @attribute
        }
        if (stateMatches == null) { // split state on demand
            val matchesList = state.split('.').toMutableList()
            matchesList.add(0, state)
            stateMatches = matchesList
        }
        val stateMatches = stateMatches ?: throw IllegalStateException("stateMatches is null")
        val sNumber = s.toInt()
        if (s.isNotEmpty() && sNumber < stateMatches.size) {
            return@replace fixCase(stateMatches[sNumber]) // $Sn
        }
        return@replace ""
    }
}


/** Find the tokenizer rules for a specific state (i.e. next action) */
fun IMonarchLexer.findRules(inState: String): List<MonarchRule>? {
    var state: String? = inState
    while (!state.isNullOrEmpty()) {
        val rules = this.tokenizer[state]

        if (rules != null) {
            return rules
        }

        val idx = state.lastIndexOf('.')
        state = if (idx < 0) {
            null // no further parent
        } else {
            state.substring(0, idx)
        }
    }
    return null
}

/**
 * Is a certain state defined? In contrast to 'findRules' this works on a ILexerMin. This is used
 * during compilation where we may know the defined states but not yet whether the corresponding
 * rules are correct.
 */
fun IMonarchLexerMin.stateExists(inState: String): Boolean {
    var state: String? = inState
    while (!state.isNullOrEmpty()) {
        val rules = this.stateNames[state]

        if (rules != null) {
            return true
        }

        val idx = state.lastIndexOf('.')
        state = if (idx < 0) {
            null // no further parent
        } else {
            state.substring(0, idx)
        }
    }
    return false
}

fun IMonarchLexerMin.createError(msg: String): MonarchException {
    return MonarchException("${languageId}: $msg")
}

/**
 * Searches for a bracket in the 'brackets' attribute that matches the input.
 */
fun IMonarchLexer.findBracket(matched: String?): MonarchBracket? {
    if (matched == null) {
        return null
    }

    val fixCaseMatched = fixCase(matched)
    val brackets = brackets

    for (bracket in brackets) {
        if (bracket.open == fixCaseMatched) {
            return MonarchBracket(token = bracket.token, bracketType = MonarchBracketType.Open)
        } else if (bracket.close == fixCaseMatched) {
            return MonarchBracket(token = bracket.token, bracketType = MonarchBracketType.Close)
        }
    }
    return null
}

