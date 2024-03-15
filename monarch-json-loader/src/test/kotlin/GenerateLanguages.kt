import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.moshi.adapter
import io.github.dingyi222666.kotlin.regex.match
import io.github.dingyi222666.kotlin.regex.oniguruma.applyOnigRegexLibToGlobal
import io.github.dingyi222666.monarch.language.Language
import io.github.dingyi222666.monarch.language.LanguageRegistry
import io.github.dingyi222666.monarch.loader.dsl.toKotlinDSL
import io.github.dingyi222666.monarch.loader.json.MoshiRoot
import io.github.dingyi222666.monarch.loader.json.loadMonarchJson
import io.github.dingyi222666.monarch.loader.json.toMonarchJson
import io.github.dingyi222666.monarch.types.IMonarchLanguage
import java.io.File
import java.util.*

import kotlin.test.Test
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
 * Initial code from https://github.com/microsoft/vscode
 * Initial copyright Copyright (C) Microsoft Corporation. All rights reserved.
 * Initial license: MIT
 */


class GenerateLanguages {

    private fun scanLanguages(): MutableList<RawLanguage> {
        applyOnigRegexLibToGlobal()

        val dir = File("src/test/resources/language_packs/")

        val result = mutableListOf<RawLanguage>()
        val registry = LanguageRegistry.instance
        dir.walk()
            .filter { file -> file.isFile && file.name.endsWith(".test.json") }
            .map { it.name.replace(".test.json", "") }
            .forEach { name ->
                getLanguage(registry, mutableListOf(name))?.let { result.add(it) }
            }

        return result

    }

    @Test
    fun generateLanguages() {
        val rawLanguages = scanLanguages()

        for (rawLanguage in rawLanguages) {
            kotlin.runCatching {
                val instance = rawLanguage.instance
                val jsonPath = File(rawLanguage.languageJsonPath)
                val fileName = "Language${jsonPath.nameWithoutExtension.replaceFirstChar { it.uppercaseChar() }}"

                val fileContent = instance.monarchLanguage.toKotlinDSL(
                    "io.github.dingyi222666.monarch.languages",
                    fileName, instance.languageName
                )
                val filePath =
                    File("../monarch-language-pack/src/main/kotlin/io/github/dingyi222666/monarch/languages/${fileName}.kt")
                filePath.parentFile?.mkdirs()
                if (!filePath.exists()) {
                    filePath.createNewFile()
                }

                filePath.writeText(fileContent)
            }.onFailure {
                println(rawLanguage.languageJsonPath)
                throw it
            }
        }
    }

    @Test
    fun generateLanguageTests() {
        val rawLanguages = scanLanguages()

        val fileSpec = FileSpec.builder("", "LanguageTests")
            .addImport("io.github.dingyi222666.monarch.common", "LanguageScope")
            .addKotlinDefaultImports(includeJvm = true)



        for (rawLanguage in rawLanguages) {
            val languageName = rawLanguage.languageName
            val upperCaseLanguageName = languageName.replaceFirstChar { it.uppercaseChar() }
            val func = FunSpec.builder("testLanguage${languageName}")
                .addAnnotation(Test::class)
                .build()
            val block = CodeBlock.builder()

            println(rawLanguage.dependencies)
            val subLanguages =
                rawLanguages.filter { rawLanguage.dependencies.contains(it.instance.languageName) }

            println(subLanguages)

            fileSpec.addFunction(func)
        }


        val file = fileSpec.build()

        println(file.writeTo(System.out))
    }
}


@OptIn(ExperimentalStdlibApi::class)
private tailrec fun getLanguage(
    languageRegistry: LanguageRegistry,
    registryLanguages: MutableList<String>,
    rawLanguage: RawLanguage? = null
): RawLanguage? {
    val currentLanguage = registryLanguages.removeLastOrNull() ?: return rawLanguage
    val languageJson = File("src/test/resources/language_packs/$currentLanguage/$currentLanguage.json").absoluteFile

    if (!languageJson.exists()) {
        System.err.println("Language $currentLanguage not found")
        return rawLanguage
    }
    val testLanguageJson = File("src/test/resources/language_packs/$currentLanguage/$currentLanguage.test.json")


    val testAdapter = MoshiRoot.adapter<Tests>()

    val tests = testAdapter.fromJson(testLanguageJson.readText()) ?: error("Test file not found")


    val willRegisteredLanguages = (tests.languages.filter { it != currentLanguage } + registryLanguages)
        .distinct()
        .toMutableList()

    if (currentLanguage == "mdx") {
        willRegisteredLanguages.add("javascript")
    } else if (currentLanguage == "handlebars") {
        willRegisteredLanguages.add("html")
    }

    val monarchLanguage = loadMonarchJson(languageJson.readText()) ?: error("Language $currentLanguage not found")

    val generatedFile = File(languageJson.absolutePath + ".generated.json")

    generatedFile.writeText(monarchLanguage.toMonarchJson())

    val tokenPostfix = monarchLanguage.tokenPostfix ?: ""
    val languageName =
        if (tokenPostfix.isNotEmpty()) {
            tokenPostfix.substring(tokenPostfix.indexOf(".") + 1)
        } else currentLanguage


    val baseEmbeddingLanguages = mapOf(
        "text/javascript" to "js",
        "javascript" to "js",
        "text/x-handlebars-template" to "handlebars"
    )

    if (languageRegistry.isRegisteredLanguage(languageName)) {
        return rawLanguage
    }

    val language = Language(
        languageName = languageName,
        monarchLanguage,
        languageId = languageName,
        embeddedLanguages = willRegisteredLanguages.associateWith { it } + baseEmbeddingLanguages
    )
    languageRegistry.registerLanguage(
        language
    )

    return getLanguage(
        languageRegistry, willRegisteredLanguages,
        rawLanguage ?: RawLanguage(
            languageName,
            languageJson.path,
            testLanguageJson.path, willRegisteredLanguages,
            language,
            tests
        )
    )
}


data class RawLanguage(
    val languageName: String,
    val languageJsonPath: String,
    val languageTestPath: String,
    val dependencies: List<String>,
    val instance: Language,
    val tests: Tests
)