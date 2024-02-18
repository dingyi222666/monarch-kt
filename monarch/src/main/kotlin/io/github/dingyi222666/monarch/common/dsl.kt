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

package io.github.dingyi222666.monarch.common

import io.github.dingyi222666.monarch.extension.UnionType
import io.github.dingyi222666.monarch.extension.splitSpaceToList
import io.github.dingyi222666.monarch.language.Language
import io.github.dingyi222666.monarch.types.*
import io.github.dingyi222666.monarch.types.MonarchLanguage

@DslMarker
annotation class MonarchDSL

@MonarchDSL
class LanguageScope : MonarchLanguageScope() {

    var languageId = "any"

    var languageName = "any"
        set(value) {
            this.languageId = value
            field = value
        }

    val embeddedLanguages = mutableMapOf<String, String>()

    val fileExtensions = mutableListOf<String>()


    @MonarchDSL
    fun fileExtensions(vararg extensions: String) {
        this.fileExtensions.addAll(extensions)
    }

    @MonarchDSL
    fun fileExtension(extension: String) {
        this.fileExtensions.add(extension)
    }

    @MonarchDSL
    fun embeddedLanguage(languageName: String, languageId: String) {
        this.embeddedLanguages[languageName] = languageId
    }

    @MonarchDSL
    inline fun embeddedLanguages(block: LanguageEmbeddingLanguageScope.() -> Unit) {
        val languageScope = LanguageEmbeddingLanguageScope()
        languageScope.block()
    }


    fun buildLanguage(): Language {
        val monarchLanguage = MonarchLanguage().apply {
            brackets = this@LanguageScope.brackets
            attrMap = this@LanguageScope.attrMap
            defaultToken = this@LanguageScope.defaultToken
            ignoreCase = this@LanguageScope.ignoreCase
            unicode = this@LanguageScope.unicode
            includeLF = this@LanguageScope.includeLF
            tokenPostfix = this@LanguageScope.tokenPostfix
            start = this@LanguageScope.start
            tokenizer = this@LanguageScope.tokenizer
        }

        return Language(
            languageName,
            monarchLanguage,
            languageId,
            fileExtensions,
            embeddedLanguages
        )
    }


    @MonarchDSL
    inner class LanguageEmbeddingLanguageScope {

        @MonarchDSL
        infix fun String.to(languageId: String) {
            this@LanguageScope.embeddedLanguages[this] = languageId
        }

        fun embeddedLanguage(languageName: String, languageId: String) {
            this@LanguageScope.embeddedLanguages[languageName] = languageId
        }
    }
}

@MonarchDSL
open class MonarchLanguageScope {

    var defaultToken = "source"

    var ignoreCase = false

    val brackets = mutableListOf<MonarchLanguageBracket>()

    val attrMap = mutableMapOf<String, Any>()

    var unicode = true

    var includeLF = false

    var tokenPostfix: String? = null

    var start: String? = null

    val tokenizer = mutableMapOf<String, MutableList<MonarchLanguageRule>>()


    @MonarchDSL
    inline fun brackets(block: MonarchLanguageBracketScope.() -> Unit) {
        buildMonarchLanguageBrackets(brackets, block)
    }

    @MonarchDSL
    inline fun tokenizer(block: MonarchLanguageRuleScope.() -> Unit) {
        buildMonarchLanguageRule(tokenizer, block)
    }


    fun build(): IMonarchLanguage {
        return MonarchLanguage().apply {
            brackets = this@MonarchLanguageScope.brackets
            attrMap = this@MonarchLanguageScope.attrMap
            defaultToken = this@MonarchLanguageScope.defaultToken
            ignoreCase = this@MonarchLanguageScope.ignoreCase
            unicode = this@MonarchLanguageScope.unicode
            includeLF = this@MonarchLanguageScope.includeLF
            tokenPostfix = this@MonarchLanguageScope.tokenPostfix
            start = this@MonarchLanguageScope.start
            tokenizer = this@MonarchLanguageScope.tokenizer
        }
    }

    fun clear() {
        attrMap.clear()
        brackets.clear()
    }

}

@MonarchDSL
class MonarchLanguageRuleScope(
    private val tokenizer: MutableMap<String, MutableList<MonarchLanguageRule>>,
) {

    class MonarchLanguageRuleArrayScope {
        private val ruleList = mutableListOf<MonarchLanguageRule>()

        fun include(ruleName: String): MonarchLanguageRule {
            val result = MonarchLanguageRule.ExpandedLanguageRule(ruleName)
            ruleList.add(result)
            return result
        }

        fun build(): MutableList<MonarchLanguageRule> {
            return ruleList
        }

        @MonarchDSL
        infix fun String.action(action: MonarchLanguageAction): MonarchLanguageRule {
            val result = MonarchLanguageRule.ShortRule1(UnionType(this), action)
            ruleList.add(result)
            return result
        }

        @MonarchDSL
        infix fun String.token(token: String): MonarchLanguageRule {
            return action(MonarchLanguageAction.ShortLanguageAction(token))
        }

        @MonarchDSL
        inline infix fun String.actionArray(block: MonarchLanguageActionArrayScope.() -> Unit): MonarchLanguageRule {
            val action = buildMonarchLanguageActionArray(block)
            return action(action)
        }

        @MonarchDSL
        inline infix fun String.action(block: MonarchLanguageActionScope.() -> Unit): MonarchLanguageRule {
            val action = buildMonarchLanguageAction(block)
            return action(action)
        }

        @MonarchDSL
        inline infix fun String.cases(block: MonarchLanguageCaseActionScope.() -> Unit): MonarchLanguageRule {
            val action = buildMonarchLanguageAction {
                cases(block)
            }
            return action(action)
        }

        @MonarchDSL
        infix fun String.action(token: String): MonarchLanguageActionWithNextStateScope {
            val action = buildMonarchLanguageAction {
                this.token = token
            }
            action(action)
            return MonarchLanguageActionWithNextStateScope(action)
        }

        @MonarchDSL
        infix fun Regex.action(action: MonarchLanguageAction): MonarchLanguageRule {
            val result = MonarchLanguageRule.ShortRule1(UnionType(this), action)
            ruleList.add(result)
            return result
        }

        @MonarchDSL
        infix fun Regex.token(token: String): MonarchLanguageRule {
            return action(MonarchLanguageAction.ShortLanguageAction(token))
        }

        @MonarchDSL
        inline infix fun Regex.cases(block: MonarchLanguageCaseActionScope.() -> Unit): MonarchLanguageRule {
            val action = buildMonarchLanguageAction {
                cases(block)
            }
            return action(action)
        }

        @MonarchDSL
        inline infix fun Regex.action(block: MonarchLanguageActionScope.() -> Unit): MonarchLanguageRule {
            val action = buildMonarchLanguageAction(block)
            return action(action)
        }

        @MonarchDSL
        inline infix fun Regex.actionArray(block: MonarchLanguageActionArrayScope.() -> Unit): MonarchLanguageRule {
            val action = buildMonarchLanguageActionArray(block)
            return action(action)
        }

        @MonarchDSL
        infix fun Regex.action(token: String): MonarchLanguageActionWithNextStateScope {
            val action = buildMonarchLanguageAction {
                this.token = token
            }
            action(action)
            return MonarchLanguageActionWithNextStateScope(action)
        }
    }

    @MonarchDSL
    infix fun String.rules(block: MonarchLanguageRuleArrayScope.() -> Unit): MutableList<MonarchLanguageRule> {
        val rules = buildMonarchLanguageRuleArray(block)
        this@MonarchLanguageRuleScope.tokenizer[this] = rules
        return rules
    }

    @MonarchDSL
    infix fun String.rules(rules: MutableList<MonarchLanguageRule>): MutableList<MonarchLanguageRule> {
        this@MonarchLanguageRuleScope.tokenizer[this] = rules
        return rules
    }

    fun build(): MutableMap<String, MutableList<MonarchLanguageRule>> {
        return tokenizer
    }

}

@MonarchDSL
class MonarchLanguageActionScope {
    // map from string to ILanguageAction
    var cases: MutableMap<String, MonarchLanguageAction>? = null

    // token class (i.e. css class) (or "@brackets" or "@rematch")
    var token: String? = null

    // the next state to push, or "@push", "@pop", "@popall"
    var next: String? = null

    // switch to this state
    var switchTo: String? = null

    // go back n characters in the stream
    var goBack: Int? = null

    // @open or @close
    var bracket: String? = null

    // switch to embedded language (using the mimetype) or get out using "@pop"
    var nextEmbedded: String? = null

    // log a message to the browser console window
    var log: String? = null

    @MonarchDSL
    inline fun cases(block: MonarchLanguageCaseActionScope.() -> Unit): MutableMap<String, MonarchLanguageAction> {
        val cases = this.cases ?: mutableMapOf()
        cases.putAll(buildMonarchLanguageCaseAction(block))
        this.cases = cases
        return cases
    }

    fun build(): MonarchLanguageAction.ExpandedLanguageAction {
        return MonarchLanguageAction.ExpandedLanguageAction(
            cases = cases,
            token = token,
            next = next,
            switchTo = switchTo,
            goBack = goBack,
            bracket = bracket,
            nextEmbedded = nextEmbedded,
            log = log
        )
    }


}

@MonarchDSL
class MonarchLanguageActionArrayScope {
    val group = mutableListOf<MonarchLanguageAction>()

    fun shortActions(vararg shortAction: String) {
        group.addAll(shortAction.map { MonarchLanguageAction.ShortLanguageAction(it) })
    }

    fun token(token: String) {
        group.add(MonarchLanguageAction.ShortLanguageAction(token))
    }

    inline fun action(block: MonarchLanguageActionScope.() -> Unit) {
        group.add(buildMonarchLanguageAction(block))
    }

    inline fun action(token: String, block: MonarchLanguageActionScope.() -> Unit) {
        group.add(buildMonarchLanguageAction {
            this.token = token
            block.invoke(this)
        })
    }

    inline fun actionArray(block: MonarchLanguageActionArrayScope.() -> Unit) {
        group.add(buildMonarchLanguageActionArray(block))
    }

    fun build(): MonarchLanguageAction.ActionArray {
        return MonarchLanguageAction.ActionArray(group)
    }
}


@JvmInline
value class MonarchLanguageActionWithNextStateScope(private val action: MonarchLanguageAction.ExpandedLanguageAction) {
    @MonarchDSL
    infix fun state(state: String) {
        action.next = state
    }
}

@MonarchDSL
class MonarchLanguageCaseActionScope {

    val caseMaps = mutableMapOf<String, MonarchLanguageAction>()

    @MonarchDSL
    inline infix fun String.and(block: MonarchLanguageActionScope.() -> Unit) {
        caseMaps[this] = buildMonarchLanguageAction(block)
    }

    @MonarchDSL
    infix fun String.and(shortAction: String) {
        caseMaps[this] = MonarchLanguageAction.ShortLanguageAction(shortAction)
    }

    fun build(): MutableMap<String, MonarchLanguageAction> {
        return caseMaps
    }

}


@MonarchDSL
class MonarchStringArrayBuildScope {
    val values = mutableListOf<String>()

    fun splitToRegex(value: String) {
        values.addAll(value.splitSpaceToList())
    }

    fun string(value: String) {
        values.add(value)
    }

    fun array(vararg value: String) {
        values.addAll(value)
    }

    fun list(value: List<String>) {
        values.addAll(value)
    }

    fun build(): List<String> {
        return values
    }
}


@MonarchDSL
class MonarchLanguageBracketScope(
    val brackets: MutableList<MonarchLanguageBracket>
) {

    fun bracket(open: String, close: String, token: String) {
        brackets.add(MonarchLanguageBracket(open, close, token))
    }

    operator fun plus(list: List<MonarchLanguageBracket>) {
        this.brackets.addAll(list)
    }

    fun build(): List<MonarchLanguageBracket> {
        return brackets
    }

    @MonarchDSL
    infix fun String.with(close: String): MonarchLanguageBracketWithTokenScope {
        return MonarchLanguageBracketWithTokenScope(this, close)
    }

    @MonarchDSL
    inner class MonarchLanguageBracketWithTokenScope(
        private val open: String,
        private val close: String
    ) {

        @MonarchDSL
        infix fun token(token: String): MonarchLanguageBracket {
            val result = MonarchLanguageBracket(
                this@MonarchLanguageBracketWithTokenScope.open,
                this@MonarchLanguageBracketWithTokenScope.close,
                token
            )
            this@MonarchLanguageBracketScope.brackets.add(result)
            return result
        }
    }
}

// Language scope extension functions

context(MonarchLanguageScope)
@MonarchDSL
infix fun String.and(array: List<String>) {
    attrMap[this] = array
}

context(MonarchLanguageScope)
@MonarchDSL
infix fun String.and(regex: Regex) {
    attrMap[this] = UnionType<String, Regex>(regex)
}

context(MonarchLanguageScope)
@MonarchDSL
val String.r: Regex
    get() = this.toRegex()

context(MonarchLanguageScope)
@MonarchDSL
infix fun String.and(regexRaw: String) {
    attrMap[this] = UnionType<String, Regex>(regexRaw)
}

context(MonarchLanguageScope)
@MonarchDSL
inline infix fun String.and(block: MonarchStringArrayBuildScope.() -> Unit) {
    attrMap[this] = buildStringArray(block)
}

context(MonarchLanguageScope)
@MonarchDSL
infix fun String.splitToRegex(content: String) {
    attrMap[this] = content.splitSpaceToList()
}

@MonarchDSL
fun MonarchLanguageScope.keywords(vararg keywords: String) {
    "keywords" and keywords.toList()
}

@MonarchDSL
inline fun MonarchLanguageScope.keywords(block: MonarchStringArrayBuildScope.() -> Unit) {
    "keywords" and block
}

@MonarchDSL
fun MonarchLanguageScope.keywordsToRegex(keywords: String) {
    "keywords" and keywords.splitSpaceToList()
}

@MonarchDSL
fun MonarchLanguageScope.typeKeywords(vararg keywords: String) {
    "typeKeywords" and keywords.toList()
}

@MonarchDSL
fun MonarchLanguageScope.typeKeywordsToRegex(keywords: String) {
    "typeKeywords" and keywords.splitSpaceToList()
}

@MonarchDSL
fun MonarchLanguageScope.operatorsToRegex(keywords: String) {
    "operators" and keywords.splitSpaceToList()
}

@MonarchDSL
inline fun MonarchLanguageScope.operators(block: MonarchStringArrayBuildScope.() -> Unit) {
    "operators" and block
}

@MonarchDSL
fun MonarchLanguageScope.operators(vararg keywords: String) {
    "operators" and keywords.toList()
}

@MonarchDSL
fun MonarchLanguageScope.symbols(regexRaw: String) {
    "symbols" and regexRaw
}

@MonarchDSL
fun MonarchLanguageScope.symbols(regex: Regex) {
    "symbols" and regex
}

@MonarchDSL
fun MonarchLanguageScope.escapes(regexRaw: String) {
    "escapes" and regexRaw
}

@MonarchDSL
fun MonarchLanguageScope.escapes(regex: Regex) {
    "escapes" and regex
}

@MonarchDSL
fun MonarchLanguageScope.identifiers(regexRaw: String) {
    "identifiers" and regexRaw
}

@MonarchDSL
fun MonarchLanguageScope.identifiers(regex: Regex) {
    "identifiers" and regex
}

@MonarchDSL
fun MonarchLanguageScope.comments(regexRaw: String) {
    "comments" and regexRaw
}

@MonarchDSL
fun MonarchLanguageScope.comments(regex: Regex) {
    "comments" and regex
}

@MonarchDSL
fun MonarchLanguageScope.control(regexRaw: String) {
    "control" and regexRaw
}

@MonarchDSL
fun MonarchLanguageScope.control(regex: Regex) {
    "control" and regex
}

@MonarchDSL
fun MonarchLanguageScope.digits(regexRaw: String) {
    "digits" and regexRaw
}

@MonarchDSL
fun MonarchLanguageScope.digits(regex: Regex) {
    "digits" and regex
}

@MonarchDSL
fun MonarchLanguageScope.octaldigits(regexRaw: String) {
    "octaldigits" and regexRaw
}

@MonarchDSL
fun MonarchLanguageScope.octaldigits(regex: Regex) {
    "octaldigits" and regex
}

@MonarchDSL
fun MonarchLanguageScope.hexdigits(regexRaw: String) {
    "hexdigits" and regexRaw
}

@MonarchDSL
fun MonarchLanguageScope.hexdigits(regex: Regex) {
    "hexdigits" and regex
}

@MonarchDSL
fun MonarchLanguageScope.binarydigits(regexRaw: String) {
    "binarydigits" and regexRaw
}

@MonarchDSL
fun MonarchLanguageScope.binarydigits(regex: Regex) {
    "binarydigits" and regex
}

@MonarchDSL
inline fun buildMonarchLanguage(block: MonarchLanguageScope.() -> Unit): IMonarchLanguage {
    val scope = MonarchLanguageScope()
    scope.block()
    return scope.build()
}

// LanguageRuleScope extension functions

@MonarchDSL
inline fun MonarchLanguageRuleScope.root(block: MonarchLanguageRuleScope.MonarchLanguageRuleArrayScope.() -> Unit): MutableList<MonarchLanguageRule> {
    return "root" rules buildMonarchLanguageRuleArray(block)
}

@MonarchDSL
inline fun MonarchLanguageRuleScope.comment(block: MonarchLanguageRuleScope.MonarchLanguageRuleArrayScope.() -> Unit): MutableList<MonarchLanguageRule> {
    return "comment" rules buildMonarchLanguageRuleArray(block)
}

@MonarchDSL
inline fun MonarchLanguageRuleScope.comments(block: MonarchLanguageRuleScope.MonarchLanguageRuleArrayScope.() -> Unit): MutableList<MonarchLanguageRule> {
    return "comments" rules buildMonarchLanguageRuleArray(block)
}

@MonarchDSL
inline fun MonarchLanguageRuleScope.string(block: MonarchLanguageRuleScope.MonarchLanguageRuleArrayScope.() -> Unit): MutableList<MonarchLanguageRule> {
    return "string" rules buildMonarchLanguageRuleArray(block)
}

@MonarchDSL
inline fun MonarchLanguageRuleScope.whitespace(block: MonarchLanguageRuleScope.MonarchLanguageRuleArrayScope.() -> Unit): MutableList<MonarchLanguageRule> {
    return "whitespace" rules buildMonarchLanguageRuleArray(block)
}

@MonarchDSL
inline fun MonarchLanguageRuleScope.javadoc(block: MonarchLanguageRuleScope.MonarchLanguageRuleArrayScope.() -> Unit): MutableList<MonarchLanguageRule> {
    return "javadoc" rules buildMonarchLanguageRuleArray(block)
}

@MonarchDSL
inline fun MonarchLanguageRuleScope.style(block: MonarchLanguageRuleScope.MonarchLanguageRuleArrayScope.() -> Unit): MutableList<MonarchLanguageRule> {
    return "style" rules buildMonarchLanguageRuleArray(block)
}

@MonarchDSL
inline fun MonarchLanguageRuleScope.script(block: MonarchLanguageRuleScope.MonarchLanguageRuleArrayScope.() -> Unit): MutableList<MonarchLanguageRule> {
    return "script" rules buildMonarchLanguageRuleArray(block)
}

@MonarchDSL
inline fun buildMonarchLanguageRuleArray(block: MonarchLanguageRuleScope.MonarchLanguageRuleArrayScope.() -> Unit): MutableList<MonarchLanguageRule> {
    val scope = MonarchLanguageRuleScope.MonarchLanguageRuleArrayScope()
    scope.block()
    return scope.build()
}

// LanguageActionScope extension functions

@MonarchDSL
inline fun buildMonarchLanguageAction(block: MonarchLanguageActionScope.() -> Unit): MonarchLanguageAction.ExpandedLanguageAction {
    val scope = MonarchLanguageActionScope()
    scope.block()
    return scope.build()
}

@MonarchDSL
inline fun buildMonarchLanguageCaseAction(block: MonarchLanguageCaseActionScope.() -> Unit): MutableMap<String, MonarchLanguageAction> {
    val scope = MonarchLanguageCaseActionScope()
    scope.block()
    return scope.build()
}


@MonarchDSL
inline fun buildMonarchLanguageRule(
    value: MutableMap<String, MutableList<MonarchLanguageRule>>,
    block: MonarchLanguageRuleScope.() -> Unit
): MutableMap<String, MutableList<MonarchLanguageRule>> {
    val scope = MonarchLanguageRuleScope(value)
    scope.block()
    return scope.build()
}

@MonarchDSL
inline fun buildMonarchLanguageActionArray(
    block: MonarchLanguageActionArrayScope.() -> Unit
): MonarchLanguageAction.ActionArray {
    val scope = MonarchLanguageActionArrayScope()
    scope.block()
    return scope.build()
}


// StringArrayBuildScope extension functions

context(MonarchStringArrayBuildScope)
@MonarchDSL
operator fun String.unaryPlus() {
    values.add(this)
}

@MonarchDSL
inline fun buildStringArray(block: MonarchStringArrayBuildScope.() -> Unit): List<String> {
    val scope = MonarchStringArrayBuildScope()
    block.invoke(scope)
    return scope.build()
}

// LanguageBracketScope extension functions

context(MonarchLanguageBracketScope)
@MonarchDSL
operator fun List<MonarchLanguageBracket>.unaryPlus() {
    brackets.addAll(this)
}

@MonarchDSL
inline fun buildMonarchLanguageBrackets(
    value: MutableList<MonarchLanguageBracket>,
    block: MonarchLanguageBracketScope.() -> Unit
): List<MonarchLanguageBracket> {
    val result = MonarchLanguageBracketScope(value)
    result.block()
    return result.build()
}

@MonarchDSL
inline fun buildLanguage(languageId: String, block: LanguageScope.() -> Unit): Language {
    return buildLanguage {
        this.languageId = languageId
        block.invoke(this)
    }
}

@MonarchDSL
inline fun buildLanguage(
    block: LanguageScope.() -> Unit
): Language {
    val result = LanguageScope()
    result.block()
    return result.buildLanguage()
}