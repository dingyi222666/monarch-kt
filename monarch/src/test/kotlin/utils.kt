import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import io.github.dingyi222666.monarch.language.Language
import io.github.dingyi222666.monarch.language.LanguageRegistry
import io.github.dingyi222666.monarch.loader.MoshiRoot
import io.github.dingyi222666.monarch.loader.loadMonarchJson
import io.github.dingyi222666.monarch.loader.toMonarchJson
import java.io.File
import kotlin.test.assertEquals

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
 */


fun runTests(language: String) {
    val languageRegistry = LanguageRegistry()
    val (languageId, tests) = registerLanguage(languageRegistry, mutableListOf(language))

    val tokenizer = languageRegistry.getTokenizer(languageId) ?: throw Exception("Language $language not found")

    for (test1 in tests) {
        var state = tokenizer.getInitialState();

        for (subTest in test1) {
            val result = tokenizer.tokenize(subTest.line, true, state)
            state = result.endState;
            assertEquals(subTest.tokens, result.tokens.map {
                Token(it.offset, it.type)
            })
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
private fun registerLanguage(
    languageRegistry: LanguageRegistry,
    registryLanguages: MutableList<String>
): Pair<String, List<List<Test>>> {
    val language = registryLanguages.removeLastOrNull() ?: return "" to emptyList()
    val languageJson = File("src/test/resources/language_packs/$language/$language.json").absoluteFile

    if (!languageJson.exists()) {
        throw Exception("Language $language not found")
    }
    val testLanguageJson = File("src/test/resources/language_packs/$language/$language.test.json")

    val testAdapter = MoshiRoot.adapter<Tests>()

    val tests = testAdapter.fromJson(testLanguageJson.readText()) ?: error("Test file not found")

    val willRegisteredLanguages = (tests.languages.filter { it != language } + registryLanguages)
        .distinct()
        .toMutableList()

    val monarchLanguage = loadMonarchJson(languageJson.readText()) ?: error("Language $language not found")

    val generatedFile = File(languageJson.absolutePath + ".generated.json")

    generatedFile.writeText(monarchLanguage.toMonarchJson())

    val tokenPostfix = monarchLanguage.tokenPostfix ?: ""
    val languageName =
        if (tokenPostfix.isNotEmpty()) {
            tokenPostfix.substring(tokenPostfix.indexOf("."))
        } else language


    languageRegistry.registerLanguage(
        Language(
            languageName = languageName,
            monarchLanguage,
            languageId = languageName,
            embeddedLanguages = willRegisteredLanguages.associateWith { it }
        )
    )

    registerLanguage(languageRegistry, willRegisteredLanguages)

    return languageName to tests.tests
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