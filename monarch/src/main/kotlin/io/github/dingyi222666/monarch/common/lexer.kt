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


package io.github.dingyi222666.monarch.common

import io.github.dingyi222666.regex.GlobalRegexLib
import io.github.dingyi222666.regex.RegexLib
import io.github.dingyi222666.regex.RegexOption
import io.github.dingyi222666.monarch.extension.*
import io.github.dingyi222666.monarch.types.*

// https://github.com/microsoft/vscode/blob/7215958b3c57945b49d3b70afdba7fb47319ca85/src/vs/editor/standalone/common/monarch/monarchCompile.ts

// Lexer helpers

val regex1 = "@@".toRegex()
val wordsRegex = "@(\\w+)".toRegex()


/**
 * Compiles a regular expression string, adding the 'i' flag if 'ignoreCase' is set, and the 'u' flag if 'unicode' is set.
 * Also replaces @\w+ sequences with the content of the specified attribute. Escaping `@` signs with another `@` sign avoids replacement.
 *
 * @example /@attr/ will be replaced with the value of lexer.attr
 * @example /@@text/ will not be replaced and will become /@text/.
 */
fun IMonarchLexerMin.compileRegExp(str: String): io.github.dingyi222666.regex.Regex {

    // @@ must be interpreted as a literal @, so we replace all occurrences of @@ with a placeholder character
    var str = str.replace(regex1, "\u0001")

    var hadExpansion: Boolean
    var n = 0
    do {
        hadExpansion = false
        str = str.replace(wordsRegex) { matchResult ->
            val attr = matchResult.groupValues[1]
            hadExpansion = true
            val sub = when (val value = this[attr]) {
                is String -> value
                is Regex -> value.pattern
                else -> {
                    if (value == null) {
                        throw MonarchException(
                            "language definition does not contain attribute '$attr', used at: $str"
                        )
                    } else {
                        throw MonarchException(
                            "attribute reference '$attr' must be a string or RegExp, used at: $str"
                        )
                    }
                }
            }
            if (sub.isEmpty()) "" else "(?:$sub)"
        }
        n++
    } while (hadExpansion && n < 5)

    // handle escaped @@

    str = str.replace("\u0001", "@")

    val options = mutableSetOf<RegexOption>()

    if (ignoreCase) {
        options.add(RegexOption.IGNORE_CASE)
    }

    if (unicode) {
        options.add(RegexOption.UNICODE_CASE)
    }

    return regexLib.compile(str, options)

}

// Compiles guard functions for case matches.
// This compiles 'cases' attributes into efficient match functions.
fun selectScrutinee(id: String, matches: List<String>, state: String, num: Int): String? {
    if (num < 0) {
        return id
    }
    if (num < matches.size) {
        return matches[num]
    }

    if (num >= 100) {
        val currentNum = num - 100
        val parts = state.split('.').toMutableList()
        parts.add(0, state)
        if (currentNum < parts.size) {
            return parts[currentNum]
        }
    }
    return null
}

internal val tKeyRegex = """^\$(([sS]?)(\d\d?)|#)(.*)$""".toRegex()
internal val wordRegex = """^\w*$""".toRegex()
internal val opMatchRegex = "^(@|!@|~|!~|==|!=)(.*)$".toRegex()
internal val wordRegex2 = Regex("""^(\w|\|)*$""")
internal val nextRegex = Regex("^(@pop|@push|@popall)$")

// get the scrutinee and pattern
fun IMonarchLexerMin.createGuard(ruleName: String, tkey: String, value: MonarchFuzzyAction): MonarchBranch {
    // get the scrutinee and pattern
    var scrut = -1 // -1: $!, 0-99: $n, 100+n: $Sn
    var oppat = tkey
    val matches = tKeyRegex.matchEntire(tkey)?.groupValues
    if (matches != null) {
        if (matches[3].isNotEmpty()) { // if digits
            scrut = matches[3].toInt()
            if (matches[2].isNotEmpty()) {
                scrut += 100 // if [sS] present
            }
        }
        oppat = matches[4]
    }
    // get operator
    var op = "~"
    var pat = oppat
    if (oppat.isEmpty()) {
        op = "!="
        pat = ""
    } else if (wordRegex.matches(pat)) {  // just a word
        op = "=="
    } else {
        val opMatches = opMatchRegex.matchEntire(oppat)?.groupValues
        if (opMatches != null) {
            op = opMatches[1]
            pat = opMatches[2]
        }
    }
    // set the tester function
    val tester: (String, String, List<String>, String, Boolean) -> Boolean
    // special case a regexp that matches just words

    if ((op == "~" || op == "!~") && wordRegex2.matches(pat)) {
        val inWords = createKeywordMatcher(pat.split('|'), ignoreCase)
        tester = { s, _, _, _, _ -> (op == "~") == inWords(s) }
    } else if (op == "@" || op == "!@") {

        val words = this[pat] as? List<*>
            ?: throw createError("the @ match target '$pat' is not defined, in rule: $ruleName")
        if (words.any { it !is String }) {
            throw createError("the @ match target '$pat' must be an array of strings, in rule: $ruleName")
        }

        val inWords = createKeywordMatcher(words as List<String>, ignoreCase)
        tester = { s, _, _, _, _ -> (op == "@") == inWords(s) }
    } else if (op == "~" || op == "!~") {

        if (!pat.contains('$')) {
            // precompile regular expression
            val re = compileRegExp("^$pat$")
            tester = { s, _, _, _, _ -> (op == "~") == re.matches(s) }
        } else {
            tester = { s, id, matches, state, _ ->
                val re = compileRegExp("^" + substituteMatches(pat, id, matches, state) + "$")
                re.matches(s)
            }
        }

    } else { // if (op == "==" || op == "!=") {

        if (!pat.contains('$')) {
            val patx = fixCase(pat)
            tester = { s, _, _, _, _ -> (op == "==") == (s == patx) }
        } else {
            val patx = fixCase(pat)
            tester = { s, id, matches, state, _ ->
                val patexp = substituteMatches(patx, id, matches, state)
                (op == "==") == (s == patexp)
            }
        }
    }

    // return the branch object
    return if (scrut == -1) {
        MonarchBranch(
            name = tkey,
            value = value,
            test = { id, matches, state, eos ->
                tester(id, id, matches, state, eos)
            }
        )
    } else {
        MonarchBranch(
            name = tkey,
            value = value,
            test = { id, matches, state, eos ->
                val scrutinee = selectScrutinee(id, matches, state, scrut)
                tester(scrutinee ?: "", id, matches, state, eos)
            }
        )
    }
}


/**
 * Compiles an action: i.e. optimize regular expressions and case matches
 * and do many sanity checks.
 *
 * This is called only during compilation but if the lexer definition
 * contains user functions as actions (which is usually not allowed), then this
 * may be called during lexing. It is important therefore to compile common cases efficiently
 */
fun IMonarchLexerMin.compileAction(ruleName: String, action: Any?): MonarchFuzzyAction {
    if (action == null) {
        return MonarchFuzzyAction.ActionString("")
    }

    if (action is String) {
        return MonarchFuzzyAction.ActionString(action)  // { token: action };
    }

    if (action is List<*>) {
        val results = mutableListOf<MonarchFuzzyAction>()

        for (a in action) {
            results.add(compileAction(ruleName, a))
        }

        return MonarchFuzzyAction.ActionArray(results)
    }

    if (action !is MonarchLanguageAction) {
        throw createError(
            "an action must be a string, an object with a \'token\' or \'cases\' attribute, or an array of actions; in rule: $ruleName"
        )
    }

    return when (action) {
        is MonarchLanguageAction.ShortLanguageAction -> MonarchFuzzyAction.ActionString(action.token)
        is MonarchLanguageAction.ActionArray -> compileAction(ruleName, action.actions)
        is MonarchLanguageAction.ExpandedLanguageAction -> compileExpandedLanguageAction(ruleName, action)
    }
}


internal fun IMonarchLexerMin.compileExpandedLanguageAction(
    ruleName: String,
    action: MonarchLanguageAction.ExpandedLanguageAction
): MonarchFuzzyAction {
    if (action.cases != null) {
        // build an array of test cases
        val cases = mutableListOf<MonarchBranch>()

        // for each case, push a test function and result value
        for ((caseKey, value) in action.cases) {
            val compiledValue = compileAction(ruleName, value)

            // what kind of case
            val branch = if (caseKey == "@default" || caseKey == "@" || caseKey.isEmpty()) {
                MonarchBranch(
                    name = caseKey,
                    value = compiledValue,
                    test = null,
                )
            } else if (caseKey == "@eos") {
                MonarchBranch(
                    name = caseKey,
                    value = compiledValue,
                    test = { _, _, _, eos ->
                        eos
                    }
                )
            } else {
                createGuard(
                    ruleName,
                    caseKey, compiledValue
                )  // call separate function to avoid local variable capture
            }

            cases.add(branch)
        }

        val defaultAction = MonarchFuzzyAction.ActionString(defaultToken)

        return MonarchFuzzyAction.ActionBase(
            test = { id, matches, state, eos ->
                for (caseValue in cases) {
                    val didMatch = caseValue.test == null || caseValue.test.invoke(id, matches, state, eos)
                    if (didMatch) {
                        return@ActionBase caseValue.value
                    }
                }
                return@ActionBase defaultAction
            },
        )
    }

    if (action.token != null) {
        // only copy specific typed fields (only happens once during compile Lexer)
        val newAction = MonarchFuzzyAction.ActionBase(
            token = action.token
        )

        if (action.token.indexOf('$') >= 0) {
            newAction.tokenSubst = true
        }

        val bracket = action.bracket

        if (bracket != null) {
            when (bracket) {
                "@open" -> {
                    newAction.bracket = MonarchBracketType.Open
                }

                "@close" -> {
                    newAction.bracket = MonarchBracketType.Close
                }

                else -> {
                    throw createError(
                        "a 'bracket' attribute must be either '@open' or '@close', in rule: $ruleName"
                    )
                }
            }
        }

        val next = action.next

        if (next != null) {
            var newNext = next
            if (!nextRegex.matches(next)) {
                if (next[0] == '@') {
                    newNext = next.substring(1) // peel off starting @ sign
                }

                if (newNext.indexOf('$') < 0) {  // no dollar substitution, we can check if the state exists
                    if (!stateExists(substituteMatches(newNext, "", emptyList(), ""))) {
                        throw createError("the next state ${action.next} is not defined in rule: $ruleName")
                    }
                }
            }
            newAction.next = newNext
        }


        newAction.goBack = action.goBack
        newAction.switchTo = action.switchTo
        newAction.log = action.log
        newAction.nextEmbedded = action.nextEmbedded

        usesEmbedded = true

        return newAction
    }

    throw createError(
        "an action must be a string, an object with a \'token\' or \'cases\' attribute, or an array of actions; in rule: $ruleName"
    )
}

/**
 * Compiles a language description function into json where all regular expressions
 */
fun IMonarchLanguage.compile(languageId: String, regexLib: RegexLib = GlobalRegexLib): IMonarchLexer {

    // Create our lexer
    val lexer = MonarchLexer(languageId, regexLib, this.attrMap)

    lexer.includeLF = includeLF ?: false
    lexer.noThrow = false // raise exceptions during compilation
    lexer.maxStack = 100

    // Set standard fields: be defensive about types
    lexer.start = start
    lexer.ignoreCase = ignoreCase ?: false
    lexer.unicode = unicode ?: false

    lexer.tokenPostfix = tokenPostfix ?: ".${lexer.languageId}"
    lexer.defaultToken = defaultToken ?: "source"

    lexer.usesEmbedded = false // becomes true if we find a nextEmbedded action

    // compile the tokenizer rules
    val tokenizer = this.tokenizer
        ?: throw lexer.createError("a language definition must define the 'tokenizer' attribute as an object")


    val lexerMin = MonarchLexer(languageId, regexLib, this.attrMap)

    lexerMin.includeLF = lexer.includeLF
    lexerMin.ignoreCase = lexer.ignoreCase
    lexerMin.unicode = lexer.unicode
    lexerMin.noThrow = lexer.noThrow
    lexerMin.usesEmbedded = lexer.usesEmbedded
    lexerMin.stateNames = tokenizer
    lexerMin.defaultToken = lexer.defaultToken

    // Compile an array of rules into newrules where RegExp objects are created.
    fun addRules(state: String, newRules: MutableList<MonarchRule>, rules: List<MonarchLanguageRule>) {
        for (rule in rules) {
            // let include = rule.include;
            // if (include) {
            if (rule is MonarchLanguageRule.ExpandedLanguageRule) {
                var include =
                    rule.include
                if (include[0] == '@') {
                    include = include.substring(1) // peel off starting @
                }

                val subInclude =
                    tokenizer[include] ?: throw lexer.createError("include target '$include' is not defined at: $state")

                addRules("$state.$include", newRules, subInclude)

                continue
            }

            val newRule = MonarchRule(state)

            if (rule !is MonarchLanguageRule.ShortRule) {
                throw lexer.createError("a rule must be a string or an object, in rule: $state")
            }


            val action = rule.action

            /*if (typeof (rule[1]) === 'string') {
                newrule.setAction(lexerMin, { token: rule[1], next: rule[2] });
            }*/
            if (action is MonarchLanguageAction.ShortLanguageAction && rule is
                        MonarchLanguageRule.ShortRule2
            ) {
                newRule.setAction(
                    lexerMin, MonarchLanguageAction.ExpandedLanguageAction(
                        token = action.token,
                        next = rule.nextState
                    )
                )
            } else if (action is MonarchLanguageAction.ExpandedLanguageAction && rule
                        is MonarchLanguageRule.ShortRule2
            ) {
                /*else if (typeof (rule[1]) === 'object') {
                    const rule1 = rule[1];
                    rule1.next = rule[2];
                    newrule.setAction(lexerMin, rule1);
                }*/
                val copyOfAction = action.copy(next = rule.nextState)
                newRule.setAction(lexerMin, copyOfAction)
            } else {
                action?.let { newRule.setAction(lexerMin, it) }
            }

            try {
                newRule.setRegex(lexerMin, rule.regex)
            } catch (e: Throwable) {
                System.err.println("compileRegexError: ${rule.regex}")
                throw e
            }

            newRules.add(newRule)
        }
    }

    val lexerTokenizer = mutableMapOf<String, MutableList<MonarchRule>>()
    lexer.tokenizer = lexerTokenizer
    for ((key, value) in tokenizer) {
        if (lexer.start == null) {
            lexer.start = key
        }

        val prepareAddList = lexerTokenizer.getOrPut(key) {
            mutableListOf()
        }
        addRules("tokenizer.$key", prepareAddList, value)
        lexerTokenizer[key] = prepareAddList

    }
    lexer.usesEmbedded = lexer.usesEmbedded  // can be set during compileAction

    val brackets = if (brackets != null && brackets?.isNotEmpty() == true) requireNotNull(brackets) else listOf(
        MonarchLanguageBracket("{", "}", "delimiter.curly"),
        MonarchLanguageBracket("[", "]", "delimiter.square"),
        MonarchLanguageBracket("(", ")", "delimiter.parenthesis"),
        MonarchLanguageBracket("<", ">", "delimiter.angle"),
    )

    val newBrackets = mutableListOf<MonarchLanguageBracket>()

    for (bracket in brackets) {
        if (bracket.open == bracket.close) {
            throw lexer.createError("open and close brackets in a 'brackets' attribute must be different: ${bracket.open} \n hint: use the 'bracket' attribute if matching on equal brackets is required.")
        }


        newBrackets.add(
            MonarchLanguageBracket(
                lexer.fixCase(bracket.open),
                lexer.fixCase(bracket.close),
                bracket.token + lexer.tokenPostfix,
            )
        )


    }


    lexer.brackets = newBrackets

    // Disable throw so the syntax highlighter goes, no matter what
    lexer.noThrow = true
    return lexer
}