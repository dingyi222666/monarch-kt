import com.squareup.moshi.JsonClass
import io.github.dingyi222666.kotlin.regex.oniguruma.applyOnigRegexLibToGlobal
import io.github.dingyi222666.monarch.language.Language
import io.github.dingyi222666.monarch.language.LanguageRegistry
import kotlin.test.assertEquals

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
 */


fun runTests(mainLanguage: Language, subLanguage: List<Language>, tests: Tests) {
    // If both of the following lines are commented out,
    // then a default regular expression is used for implementation.

    //applyRe2JRegexLibToGlobal()
    // default: oniguruma
    applyOnigRegexLibToGlobal()

    val languageRegistry = LanguageRegistry()


    registerLanguages(languageRegistry, subLanguage + mainLanguage)

    val tokenizer = languageRegistry.getTokenizer(mainLanguage.languageId)
        ?: throw Exception("Language ${mainLanguage.languageName} not found")

    for ((index, test1) in tests.tests.withIndex()) {
        var state = tokenizer.getInitialState()

        for ((subIndex, subTest) in test1.withIndex()) {
            val result = tokenizer.tokenize(subTest.line, true, state)
            state = result.endState
            assertEquals(subTest.tokens, result.tokens.map {
                Token(it.offset, it.type)
            }, "The tokens are not equal in line root[$index][$subIndex]: ${subTest.line}")
        }
    }

    languageRegistry.clear()
}

fun registerLanguages(languageRegistry: LanguageRegistry, languages: List<Language>) {
    for (language in languages) {
        languageRegistry.registerLanguage(language, true)
    }
}


@JsonClass(generateAdapter = true)
data class Tests(
    val languages: List<String>,
    val tests: List<List<Test>>
)

@JsonClass(generateAdapter = true)
class Test(
    val line: String,
    val tokens: List<Token>
)

@JsonClass(generateAdapter = true)
data class Token(
    val startIndex: Int,
    val type: String
)

typealias TestKotlin = kotlin.test.Test