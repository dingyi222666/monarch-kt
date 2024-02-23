import com.squareup.moshi.JsonClass
import com.squareup.moshi.adapter
import io.github.dingyi222666.kotlin.regex.oniguruma.applyOnigRegexLibToGlobal
import io.github.dingyi222666.kotlin.regex.re2j.applyRe2JRegexLibToGlobal
import io.github.dingyi222666.monarch.language.Language
import io.github.dingyi222666.monarch.language.LanguageRegistry
import io.github.dingyi222666.monarch.loader.json.MoshiRoot
import io.github.dingyi222666.monarch.loader.json.loadMonarchJson
import io.github.dingyi222666.monarch.loader.json.toMonarchJson
import java.io.File
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


fun runTests(language: String) {
    // If both of the following lines are commented out,
    // then a default regular expression is used for implementation.

    //applyRe2JRegexLibToGlobal()
    // default: oniguruma
    applyOnigRegexLibToGlobal()

    val languageRegistry = LanguageRegistry()
    val (languageId, tests) = registerLanguage(languageRegistry, mutableListOf(language))

    if (languageId.isEmpty()) {
        System.err.println("[WARN]: Language $language not found")
        return
    }

    val tokenizer = languageRegistry.getTokenizer(languageId) ?: throw Exception("Language $language not found")

    for ((index, test1) in tests.withIndex()) {
        var state = tokenizer.getInitialState();

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

@OptIn(ExperimentalStdlibApi::class)
private tailrec fun registerLanguage(
    languageRegistry: LanguageRegistry,
    registryLanguages: MutableList<String>,
    pair: Pair<String, List<List<Test>>>? = null
): Pair<String, List<List<Test>>> {
    val language = registryLanguages.removeLastOrNull() ?: return pair ?: ("" to emptyList())
    val languageJson = File("src/test/resources/language_packs/$language/$language.json").absoluteFile

    if (!languageJson.exists()) {
        System.err.println("Language $language not found")
        return pair ?: ("" to emptyList())
    }
    val testLanguageJson = File("src/test/resources/language_packs/$language/$language.test.json")

    val testAdapter = MoshiRoot.adapter<Tests>()

    val tests = testAdapter.fromJson(testLanguageJson.readText()) ?: error("Test file not found")


    val willRegisteredLanguages = (tests.languages.filter { it != language } + registryLanguages)
        .distinct()
        .toMutableList()

    if (language == "mdx") {
        willRegisteredLanguages.add("javascript")
    } else if (language == "handlebars") {
        willRegisteredLanguages.addAll(listOf("html"))
    }

    val monarchLanguage = loadMonarchJson(languageJson.readText()) ?: error("Language $language not found")

    val generatedFile = File(languageJson.absolutePath + ".generated.json")

    generatedFile.writeText(monarchLanguage.toMonarchJson())

    val tokenPostfix = monarchLanguage.tokenPostfix ?: ""
    val languageName =
        if (tokenPostfix.isNotEmpty()) {
            tokenPostfix.substring(tokenPostfix.indexOf(".") + 1)
        } else language


    val baseEmbeddingLanguages = mapOf(
        "text/javascript" to "js",
        "javascript" to "js",
        "text/x-handlebars-template" to "handlebars"
    )

    if (languageRegistry.isRegisteredLanguage(languageName)) {
        return pair ?: (languageName to tests.tests)
    }

    languageRegistry.registerLanguage(
        Language(
            languageName = languageName,
            monarchLanguage,
            languageId = languageName,
            embeddedLanguages = willRegisteredLanguages.associateWith { it } + baseEmbeddingLanguages
        )
    )

    return registerLanguage(languageRegistry, willRegisteredLanguages, pair ?: (languageName to tests.tests))
}


@JsonClass(generateAdapter = true)
data class Tests(
    val languages: List<String>,
    val tests: List<List<Test>>
)

@JsonClass(generateAdapter = true)
data class Test(
    val line: String,
    val tokens: List<Token>
)

@JsonClass(generateAdapter = true)
data class Token(
    val startIndex: Int,
    val type: String
)