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


package io.github.dingyi222666.kotlin.monarch.extension

import io.github.dingyi222666.kotlin.monarch.types.IMonarchLexer
import io.github.dingyi222666.kotlin.monarch.types.IMonarchLexerMin
import io.github.dingyi222666.kotlin.monarch.types.MonarchRule
import java.util.*

/**
 * Puts a string to lower case if 'ignoreCase' is set.
 */
fun IMonarchLexerMin.fixCase(str: String): String =
    if (ignoreCase) str.lowercase(Locale.getDefault()) else str

private val sanitizeRegex = Regex("[&<>'\"_]")

internal fun String.sanitize(): String {
    return replace(sanitizeRegex, "-"); // used on all output token CSS classes
}


// Helper functions for rule finding and substitution

/**
 * substituteMatches is used on lexer strings and can substitutes predefined patterns:
 * 		$$  => $
 * 		$#  => id
 * 		$n  => matched entry n
 * 		@attr => contents of lexer[attr]
 *
 * See documentation for more info
 */
fun IMonarchLexerMin.substituteMatches(str: String, id: String, matches: Array<String>, state: String): String {
    val re = Regex("\\$((\\$)|(#)|(\\d\\d?)|[sS](\\d\\d?)|@(\\w+))")

    var stateMatches: MutableList<String>? = null
    return str.replace(re) { matchResult ->
        val (sub, dollar, hash, n, s, attr) = matchResult.destructured
        if (dollar.isNotEmpty()) {
            return@replace "$" // $$
        }
        if (hash.isNotEmpty()) {
            return@replace fixCase(id)   // default $#
        }
        if (n.isNotEmpty() && n.toInt() < matches.size) {
            return@replace fixCase(matches[n.toInt()]) // $n
        }
        if (attr.isNotEmpty() && this[attr] is String) {
            return@replace this[attr] as String //@attribute
        }
        if (stateMatches == null) { // split state on demand
            val matchesList = state.split(".").toMutableList()
            matchesList.add(0, state)
            stateMatches = matchesList
        }
        val sNumber = s.toInt()
        if (s.isNotEmpty() && sNumber < stateMatches!!.size) {
            return@replace fixCase(stateMatches!![sNumber]) //$Sn
        }
        return@replace ""
    }
}

/**
 * Find the tokenizer rules for a specific state (i.e. next action)
 */
fun IMonarchLexer.findRules(inState: String): List<MonarchRule>? {
    var state: String? = inState
    while (!state.isNullOrEmpty()) {
        val rules = this.tokenizer[state]

        if (rules != null) {
            return rules;
        }

        val idx = state.lastIndexOf('.');
        state = if (idx < 0) {
            null; // no further parent
        } else {
            state.substring(0, idx);
        }
    }
    return null
}

/**
 * Is a certain state defined? In contrast to 'findRules' this works on a ILexerMin.
 * This is used during compilation where we may know the defined states
 * but not yet whether the corresponding rules are correct.
 */
fun IMonarchLexerMin.stateExists(inState: String): Boolean {
    var state: String? = inState
    while (!state.isNullOrEmpty()) {
        val rules = this.stateNames[state]

        if (rules != null) {
            return true
        }

        val idx = state.lastIndexOf('.');
        state = if (idx < 0) {
            null; // no further parent
        } else {
            state.substring(0, idx);
        }
    }
    return false
}