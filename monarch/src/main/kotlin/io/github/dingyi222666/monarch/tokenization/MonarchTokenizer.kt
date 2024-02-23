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

import io.github.dingyi222666.kotlin.regex.GlobalRegexLib
import io.github.dingyi222666.kotlin.regex.RegexLib
import io.github.dingyi222666.monarch.extension.*
import io.github.dingyi222666.monarch.language.LanguageRegistry
import io.github.dingyi222666.monarch.types.*

/**
 * Monarch main tokenizer.
 *
 * Source from [here](https://github.com/microsoft/vscode/blob/233fd797c0878a551994e55f1e87c23f5a200969/src/vs/editor/standalone/common/monarch/monarchLexer.ts#L390C14-L390C31)
 */
class MonarchTokenizer(
    val languageId: String,
    private val lexer: IMonarchLexer,
    private val languageRegistry: LanguageRegistry = LanguageRegistry.instance,
    private val themeService: IThemeService? = null,
    private val regexLib: RegexLib = GlobalRegexLib,
    var maxTokenizationLineLength: Int = 5000
) : ITokenizationSupport {

    private val embeddedLanguages by lazy {
        languageRegistry.getLanguage(languageId)?.embeddedLanguages ?: emptyMap()
    }

    override fun getInitialState(): TokenizeState {
        val rootState = MonarchStackElementFactory.create(null, lexer.start!!)
        return MonarchLineStateFactory.create(rootState, null)
    }

    override fun tokenize(line: String, hasEOL: Boolean, lineState: TokenizeState): TokenizationResult {
        if (line.length >= maxTokenizationLineLength) {
            return nullTokenize(languageId, lineState)
        }
        val tokensCollector = MonarchClassicTokensCollector(languageRegistry)
        val endLineState = tokenizeImpl(line, hasEOL, lineState as MonarchLineState, tokensCollector)
        return tokensCollector.finalize(endLineState)
    }

    override fun tokenizeEncoded(line: String, hasEOL: Boolean, lineState: TokenizeState): EncodedTokenizationResult {
        if (line.length >= maxTokenizationLineLength) {
            return nullTokenizeEncoded(LanguageId.Null, lineState)
        }
        val tokenTheme = themeService?.currentColorTheme() ?: throw IllegalStateException("No theme service")
        val tokensCollector = MonarchModernTokensCollector(languageRegistry, tokenTheme);
        val endLineState = tokenizeImpl(line, hasEOL, lineState as MonarchLineState, tokensCollector)
        return tokensCollector.finalize(endLineState);
    }

    private fun tokenizeImpl(
        line: String,
        hasEOL: Boolean,
        lineState: MonarchLineState,
        collector: IMonarchTokensCollector
    ): MonarchLineState {
        return if (lineState.embeddedLanguageData != null) {
            nestedTokenize(line, hasEOL, lineState, 0, collector)
        } else {
            defaultTokenizeImpl(line, hasEOL, lineState, 0, collector)
        }
    }

    private fun findLeavingNestedLanguageOffset(line: String, state: MonarchLineState): Int {
        val rules = lexer.tokenizer[state.stack.state]
        /* do parent matching */
            ?: lexer.findRules(state.stack.state)
            ?: throw lexer.createError(
                "tokenizer state is not defined: " + state.stack.state
            )

        var popOffset = -1
        var hasEmbeddedPopRule = false

        for (rule in rules) {
            val action = rule.action

            if (action !is MonarchFuzzyAction.ActionBase || action.nextEmbedded != "@pop") {
                continue
            }

            hasEmbeddedPopRule = true

            var regex = rule.regex
            val regexSource = rule.regex.pattern
            if (regexSource.substring(0, 4) == "^(?:" && regexSource.last() == ')') {
                regex = regexLib.compile(regexSource.substring(4, regexSource.length - 1), regex.options)
            }

            val result = regex.search(line)?.range?.first ?: -1

            if (result == -1 || (result != 0 && rule.matchOnlyAtLineStart)) {
                continue
            }

            if (popOffset == -1 || result < popOffset) {
                popOffset = result
            }
        }

        if (!hasEmbeddedPopRule) {
            throw this.lexer.createError(
                "no rule containing nextEmbedded: \"@pop\" in tokenizer embedded state: " + state.stack.state
            )
        }

        return popOffset
    }

    private fun nestedTokenize(
        line: String,
        hasEOL: Boolean,
        lineState: MonarchLineState,
        offsetDelta: Int,
        tokensCollector: IMonarchTokensCollector
    ): MonarchLineState {

        val popOffset = findLeavingNestedLanguageOffset(line, lineState)
        val embeddedLanguageData =
            lineState.embeddedLanguageData ?: throw lexer.createError("no embedded language data of type: $lineState")

        if (popOffset == -1) {
            // tokenization will not leave nested language
            val nestedEndState =
                tokensCollector.nestedLanguageTokenize(line, hasEOL, embeddedLanguageData, offsetDelta)
            return MonarchLineStateFactory.create(
                lineState.stack,
                EmbeddedLanguageData(embeddedLanguageData.languageId, nestedEndState)
            )
        }

        val nestedLanguageLine = line.substring(0, popOffset)
        if (nestedLanguageLine.isNotEmpty()) {
            // tokenize with the nested language
            tokensCollector.nestedLanguageTokenize(
                nestedLanguageLine,
                false,
                embeddedLanguageData,
                offsetDelta
            )
        }

        val restOfTheLine = line.substring(popOffset)
        return defaultTokenizeImpl(restOfTheLine, hasEOL, lineState, offsetDelta + popOffset, tokensCollector)
    }

    private fun defaultTokenizeImpl(
        lineWithoutLF: String,
        hasEOL: Boolean,
        lineState: MonarchLineState,
        offsetDelta: Int,
        tokensCollector: IMonarchTokensCollector
    ): MonarchLineState {
        tokensCollector.enterLanguage(languageId)

        val lineWithoutLFLength = lineWithoutLF.length
        val line = if (hasEOL && lexer.includeLF) lineWithoutLF + '\n' else lineWithoutLF
        val lineLength = line.length

        var embeddedLanguageData = lineState.embeddedLanguageData
        var stack = lineState.stack
        var pos = 0

        var groupMatching: GroupMatching? = null

        // See https://github.com/microsoft/monaco-editor/issues/1235
        // Evaluate rules at least once for an empty line
        var forceEvaluation = true

        while (forceEvaluation || pos < lineLength) {
            val pos0 = pos
            val stackLen0 = stack.depth
            val groupLen0 = groupMatching?.groups?.size ?: 0
            val state = stack.state

            var matches: List<String>? = null
            var matched: String? = null
            var action: MonarchFuzzyAction? = null
            var rule: MonarchRule? = null

            var enteringEmbeddedLanguage: String? = null

            // check if we need to process group matches first
            if (groupMatching != null) {
                matches = groupMatching.matches
                val groupEntry = groupMatching.groups.removeFirst()
                matched = groupEntry.matched
                action = groupEntry.action
                rule = groupMatching.rule

                // cleanup if necessary
                if (groupMatching.groups.isEmpty()) {
                    groupMatching = null
                }
            } else {
                // otherwise we match on the token stream

                if (!forceEvaluation && pos >= lineLength) {
                    // nothing to do
                    break
                }

                forceEvaluation = false

                // get the rules for this state
                val rules = lexer.tokenizer[state]
                // do parent matching
                    ?: lexer.findRules(state) ?: throw lexer.createError("tokenizer state is not defined: $state")


                // try each rule until we match
                val restOfLine = line.substring(pos)
                for (rule in rules) {
                    if (pos == 0 || !rule.matchOnlyAtLineStart) {
                        val currentMatches = rule.regex.search(restOfLine)?.groupValues
                        if (currentMatches != null) {
                            matches = currentMatches
                            matched = currentMatches[0]
                            action = rule.action
                            break
                        }
                    }
                }
            }

            // We matched 'rule' with 'matches' and 'action'
            if (matches == null) {
                matches = listOf("")
                matched = ""
            }

            if (action == null) {
                // bad: we didn't match anything, and there is no action to take
                // we need to advance the stream or we get progress trouble
                if (pos < lineLength) {
                    matches = listOf(line[pos].toString())
                    matched = matches[0]
                }
                action = MonarchFuzzyAction.ActionString(this.lexer.defaultToken)
            }

            if (matched == null) {
                // should never happen, needed for strict null checking
                break
            }

            // advance stream
            pos += matched.length


            // maybe call action function (used for 'cases')
            while (action is MonarchFuzzyAction.ActionBase && action.test != null) {
                action = action.test?.invoke(matched, matches, state, pos == lineLength) ?: break
            }

            var result: MonarchFuzzyAction? = null
            // set the result: either a string or an array of actions
            if (action is MonarchFuzzyAction.ActionString || action is MonarchFuzzyAction.ActionArray) {
                result = action
            } /*else if (action is MonarchFuzzyAction.ActionBase && action.group != null) {
                result = MonarchFuzzyAction.ActionArray(action.group!!)
            } */ else if (action is MonarchFuzzyAction.ActionBase && action.token != null) {
                val token = action.token ?: throw lexer.createError("invalid token action: ${action.token}")
                // do $n replacements?
                result = if (action.tokenSubst == true) {
                    MonarchFuzzyAction.ActionString(lexer.substituteMatches(token, matched, matches, state))
                } else {
                    MonarchFuzzyAction.ActionString(token)
                }

                // enter embedded language?
                val nextEmbedded = action.nextEmbedded
                if (nextEmbedded != null) {
                    if (nextEmbedded == "@pop") {
                        if (embeddedLanguageData == null) {
                            throw lexer.createError("cannot pop embedded language if not inside one")
                        }
                        embeddedLanguageData = null
                    } else if (embeddedLanguageData != null) {
                        throw lexer.createError("cannot enter embedded language from within an embedded language")
                    } else {
                        enteringEmbeddedLanguage =
                            lexer.substituteMatches(nextEmbedded, matched, matches, state)
                    }
                }

                val goBack = action.goBack

                // state transformations
                if (goBack != null) { // back up the stream...
                    pos = 0.coerceAtLeast(pos - goBack)
                }

                val switchTo = action.switchTo
                val transform = action.transform
                val next = action.next
                val log = action.log

                if (switchTo != null) {
                    var nextState =
                        lexer.substituteMatches(switchTo, matched, matches, state)  // switch state without a push...
                    if (nextState[0] == '@') {
                        nextState = nextState.substring(1) // peel off starting '@'
                    }
                    if (lexer.findRules(nextState) == null) {
                        throw lexer.createError(
                            "trying to switch to a state '$nextState' that is undefined in rule: ' + ${
                                this.safeRuleName(
                                    rule
                                )
                            }"
                        )
                    } else {
                        stack = stack.switchTo(nextState)
                    }

                } else if (transform != null) {
                    throw lexer.createError("action.transform not supported")
                } else if (next != null) {
                    if (next == "@push") {
                        if (stack.depth >= lexer.maxStack) {
                            throw lexer.createError(
                                "maximum tokenizer stack size reached: [" +
                                        stack.state + "," + stack.parent!!.state + ",...]"
                            )
                        } else {
                            stack = stack.push(state)
                        }
                    } else if (action.next == "@pop") {
                        if (stack.depth <= 1) {
                            throw lexer.createError(
                                "trying to pop an empty stack in rule: " + safeRuleName(rule)
                            )
                        } else {
                            stack = requireNotNull(stack.pop())
                        }
                    } else if (action.next == "@popall") {
                        stack = stack.popall()
                    } else {
                        var nextState = lexer.substituteMatches(next, matched, matches, state)

                        if (nextState[0] == '@') {
                            nextState = nextState.substring(1) // peel off starting '@'
                        }

                        if (lexer.findRules(nextState) == null) {
                            throw lexer.createError(
                                "trying to set a next state '$nextState' that is undefined in rule: '${
                                    this.safeRuleName(
                                        rule
                                    )
                                }"
                            )
                        } else {
                            stack = stack.push(nextState)
                        }
                    }
                }


                if (log != null) {
                    println(
                        lexer.languageId + ": " + lexer.substituteMatches(
                            log,
                            matched,
                            matches,
                            state
                        )
                    )
                }
            }

            // check result
            if (result == null) {
                throw lexer.createError("lexer rule has no well-defined action in rule: " + this.safeRuleName(rule))
            }

            // is the result a group match?
            if (result is MonarchFuzzyAction.ActionArray) {
                if (groupMatching != null && groupMatching.groups.size > 0) {
                    throw lexer.createError("groups cannot be nested: " + this.safeRuleName(rule))
                }

                if (matches.size != result.actions.size + 1) {
                    throw lexer.createError(
                        "matched number of groups does not match the number of actions in rule: " + this.safeRuleName(
                            rule
                        )
                    )
                }

                var totalLen = 0

                for (i in 1 until matches.size) {
                    totalLen += matches[i].length
                }

                if (totalLen != matched.length) {
                    throw lexer.createError(
                        "with groups, all characters should be matched in consecutive groups in rule: " + this.safeRuleName(
                            rule
                        )
                    )
                }

                groupMatching = GroupMatching(
                    rule = rule, matches = matches, groups = mutableListOf()
                )

                result.actions.forEachIndexed { index, action ->
                    groupMatching.groups.add(
                        GroupMatching.Group(
                            action = action,
                            matched = matches!![index + 1],
                        )
                    )
                }

                pos -= matched.length

                // call recursively to initiate first result match
                continue
            } else {
                // regular result

                // check for '@rematch'
                if (result is MonarchFuzzyAction.ActionString && result.token == "@rematch") {
                    pos -= matched.length
                    matched = ""  // better set the next state too...
                    matches = null
                    result = MonarchFuzzyAction.ActionString("")

                    // Even though `@rematch` was specified, if `nextEmbedded` also specified,
                    // a state transition should occur.
                    if (enteringEmbeddedLanguage !== null) {
                        return computeNewStateForEmbeddedLanguage(
                            enteringEmbeddedLanguage,
                            pos, lineLength, lineWithoutLF, hasEOL, offsetDelta, tokensCollector, stack
                        )
                    }
                }


                // check progress
                if (matched.isEmpty()) {
                    if (lineLength == 0 || stack.depth != stackLen0 ||
                        state != stack.state ||
                        (groupMatching?.groups?.size ?: 0) != groupLen0
                    ) {
                        continue
                    } else {
                        throw lexer.createError(
                            "no progress in tokenizer in rule: " + safeRuleName(rule)
                        )
                    }
                }

                // return the result (and check for brace matching)
                // todo: for efficiency we could pre-sanitize tokenPostfix and substitutions
                val tokenType =
                    if (
                        (result is MonarchFuzzyAction.ActionString) && result.token.indexOf("@brackets") == 0) {
                        val rest = result.token.substring("@brackets".length)
                        val bracket = lexer.findBracket(matched)
                            ?: throw lexer.createError(
                                "@brackets token returned but no bracket defined as: $matched"
                            )
                        (bracket.token + rest).sanitize()
                    } else {
                        val rawToken = (result as MonarchFuzzyAction.ActionString).token
                        val token = if (rawToken.isEmpty()) "" else rawToken + lexer.tokenPostfix
                        token.sanitize()
                    }

                if (pos0 < lineWithoutLFLength) {
                    tokensCollector.emit(pos0 + offsetDelta, tokenType)
                }
            }

            if (enteringEmbeddedLanguage !== null) {
                return computeNewStateForEmbeddedLanguage(
                    enteringEmbeddedLanguage,
                    pos, lineLength, lineWithoutLF, hasEOL, offsetDelta, tokensCollector, stack
                )
            }
        }

        return MonarchLineStateFactory.create(stack, embeddedLanguageData)
    }

    private fun computeNewStateForEmbeddedLanguage(
        enteringEmbeddedLanguage: String,
        pos: Int,
        lineLength: Int,
        lineWithoutLF: String,
        hasEOL: Boolean,
        offsetDelta: Int,
        tokensCollector: IMonarchTokensCollector,
        stack: MonarchStackElement
    ): MonarchLineState {
        var languageId = embeddedLanguages[enteringEmbeddedLanguage] ?: enteringEmbeddedLanguage

        var embeddedLanguageData = getNestedEmbeddedLanguageData(languageId)

        if (embeddedLanguageData.state === NullState && languageId.contains("/")) {
            languageId = languageId.substringAfterLast("/")
            embeddedLanguageData = getNestedEmbeddedLanguageData(languageId)
        }

        if (pos < lineLength) {
            // there is content from the embedded language on this line
            val restOfLine = lineWithoutLF.substring(pos)
            return this.nestedTokenize(
                restOfLine,
                hasEOL,
                MonarchLineStateFactory.create(stack, embeddedLanguageData),
                offsetDelta + pos,
                tokensCollector
            )
        } else {
            return MonarchLineStateFactory.create(stack, embeddedLanguageData)
        }
    }

    private fun safeRuleName(rule: IMonarchRule?): String {
        return rule?.name ?: "(unknown)"
    }

    private fun getNestedEmbeddedLanguageData(languageId: String): EmbeddedLanguageData {
        if (!languageRegistry.isRegisteredLanguage(languageId)) {
            return EmbeddedLanguageData(languageId, NullState)
        }

        /* if (languageId !== this.languageId) {
             // Fire language loading event
             this._languageService.requestBasicLanguageFeatures(languageId);
             languages.TokenizationRegistry.getOrCreate(languageId);
             this._embeddedLanguages[languageId] = true;
         }*/

        val tokenizationSupport = languageRegistry.getTokenizer(languageId)
        if (tokenizationSupport != null) {
            return EmbeddedLanguageData(languageId, tokenizationSupport.getInitialState())
        }

        return EmbeddedLanguageData(languageId, NullState)
    }


    // regular expression group matching
// these never need cloning or equality since they are only used within a line match
    private data class GroupMatching(
        var matches: List<String>,
        var rule: MonarchRule?,
        val groups: MutableList<Group>,
    ) {
        data class Group(
            val action: MonarchFuzzyAction,
            val matched: String
        )

    }
}

fun nullTokenize(languageId: String, state: TokenizeState): TokenizationResult {
    return TokenizationResult(listOf(Token(0, "", languageId)), state)
}

fun nullTokenizeEncoded(languageId: Int, state: TokenizeState?): EncodedTokenizationResult {
    val tokens = mutableListOf(0, 0)
    tokens[0] = 0
    tokens[1] = (
            (languageId shl MetadataConsts.LANGUAGEID_OFFSET).toUInt()
                    or ((StandardTokenType.Other shl MetadataConsts.TOKEN_TYPE_OFFSET).toUInt())
                    or ((FontStyle.None shl MetadataConsts.FONT_STYLE_OFFSET).toUInt())
                    or ((ColorId.DefaultForeground shl MetadataConsts.FOREGROUND_OFFSET).toUInt())
                    or ((ColorId.DefaultBackground shl MetadataConsts.BACKGROUND_OFFSET).toUInt())
            ).toInt()

    return EncodedTokenizationResult(tokens, if (state === null) NullState else state)
}