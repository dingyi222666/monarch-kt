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

import com.squareup.kotlinpoet.*
import com.squareup.moshi.JsonClass
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


import kotlin.test.assertEquals


typealias TestKotlin = kotlin.test.Test

class GenerateLanguages {

    private fun scanLanguages(): MutableList<RawLanguage> {
        applyOnigRegexLibToGlobal()

        val dir = File("src/test/resources/language_packs/")

        val result = mutableListOf<RawLanguage>()

        dir.walk()
            .filter { file -> file.isFile && file.name.endsWith(".test.json") }
            .map { it.name.replace(".test.json", "") }
            .forEach { name ->
                val registry = LanguageRegistry()
                getLanguage(registry, mutableListOf(name))?.let { result.add(it) }
            }

        return result

    }

    @TestKotlin
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

    @TestKotlin
    fun generateLanguageTests() {

        val rawLanguages = scanLanguages()

        val fileSpec = FileSpec.builder("", "LanguageTests")
            .addImport("io.github.dingyi222666.monarch.common", "LanguageScope")
            .addKotlinDefaultImports(includeJvm = true)

        fun getSubLanguage(rawLanguage: RawLanguage): List<RawLanguage> {
            return  rawLanguages.filter {
                rawLanguage.dependencies.contains(it.instance.languageName.lowercase())
            }
        }

        val classSpec = TypeSpec.classBuilder("LanguagesTest")
        for (rawLanguage in rawLanguages) {
            val languageName = rawLanguage.languageName
            val upperCaseLanguageName = languageName.replaceFirstChar { it.uppercaseChar() }
            val func = FunSpec.builder("testLanguage${upperCaseLanguageName}")
                .addAnnotation(ClassName("", "TestKotlin"))

            val block = CodeBlock.builder()


            val subLanguages =
                rawLanguages.filter {
                    rawLanguage.dependencies.contains(it.instance.languageName.lowercase())
                }.flatMap {
                    getSubLanguage(rawLanguage)
                }.distinct()

            addLanguages(rawLanguage, upperCaseLanguageName, subLanguages, block)

            func.addCode(block.build())
            func.addStatement("runTests(%N,%N,%N)", "mainLanguage", "subLanguages", "tests")
            // println(func.build())

            classSpec.addFunction(func.build())
        }

        fileSpec.addType(classSpec.build())

        val file = fileSpec.build()

        val filePath =
            File("../monarch-language-pack/src/test/kotlin/LanguageTests.kt")

        if (!filePath.exists()) {
            filePath.createNewFile()
        }

        filePath.writeText(
            file.toString().replace(
                "io.github.dingyi222666.monarch.common.LanguageScope",
                "io.github.dingyi222666.monarch.types.*\n" +
                        "import io.github.dingyi222666.monarch.languages.*\n" +
                        "import io.github.dingyi222666.monarch.language.*"
            )
        )
    }

    private fun addLanguages(
        rawLanguage: RawLanguage,
        upperCaseLanguageName: String,
        subLanguages: List<RawLanguage>,
        block: CodeBlock.Builder
    ) {
        block.apply {
            addStatement("val mainLanguage = ")
            add(rawLanguageAsLanguage(rawLanguage, upperCaseLanguageName))
            if (subLanguages.isNotEmpty()) {

                addStatement("val subLanguages = listOf(")
                withIndent {
                    for (i in subLanguages.indices) {
                        if (i > 0) {
                            add(",")
                        }
                        val subLanguage = subLanguages[i]
                        add(
                            rawLanguageAsLanguage(
                                subLanguage,
                                subLanguage.languageName.replaceFirstChar { it.uppercaseChar() })
                        )

                    }
                }
                addStatement(");")
            } else {
                addStatement("val subLanguages = emptyList<Language>()")
            }
            addStatement("val tests = ")
            add(generateTests(rawLanguage.tests))
        }
    }

    private fun generateTests(tests: Tests): CodeBlock {
        fun CodeBlock.Builder.addToken(token: Token) {
            addStatement("Token(")
            withIndent {
                addStatement("startIndex = %L,", token.startIndex)
                addStatement("type = %S", token.type)
            }
            add(")")
        }

        fun CodeBlock.Builder.addTest(test: Test) {
            addStatement("Test(")
            withIndent {
                addStatement("line = %S,", test.line)
                addStatement("tokens = listOf(")
                test.tokens.forEachIndexed { index, token ->
                    if (index > 0) {
                        add(",")
                    }
                    withIndent {
                        addToken(token)
                    }
                }

                addStatement(")")
            }
            addStatement(")")
        }
        return CodeBlock.builder().apply {

            addStatement("Tests(")
            withIndent {
                addStatement("languages = listOf(")
                withIndent {
                    for (i in tests.languages.indices) {
                        val language = tests.languages[i]
                        if (i > 0) {
                            add(",")
                        }
                        addStatement("\"$language\"")
                    }
                }
                addStatement("),")

                addStatement("tests = listOf(")
                withIndent {
                    tests.tests.forEachIndexed { index, tests ->
                        if (index > 0) {
                            add(",")
                        }
                        addStatement("listOf(")
                        withIndent {
                            tests.forEachIndexed { index, test ->
                                if (index > 0) {
                                    add(",")
                                }
                                addTest(test)
                            }
                        }
                        addStatement(")")
                    }
                }
                addStatement(")")
            }
            addStatement(")")
        }.build()
    }

    private fun rawLanguageAsLanguage(rawLanguage: RawLanguage, languageName: String): CodeBlock {
        return CodeBlock.builder().apply {

            addStatement("""Language(""")
            withIndent {
                addStatement("languageName = %S,", rawLanguage.instance.languageName)
                addStatement("languageId = %S,", rawLanguage.instance.languageId)
                if (rawLanguage.instance.fileExtensions != null) {
                    addStatement("fileExtensions = listOf(%N)", rawLanguage.instance.fileExtensions?.joinToString(
                        separator = ","
                    ) {
                        "\"$it\""
                    })
                }

                if (rawLanguage.instance.embeddedLanguages != null) {
                    addStatement(
                        "embeddedLanguages = mapOf("
                    )
                    withIndent {
                        var status = false
                        rawLanguage.instance.embeddedLanguages?.forEach { (key, value) ->
                            if (status) {
                                add(",")
                            }
                            addStatement("%S to %S", key, value)
                            status = true
                        }
                    }
                    addStatement("),")

                }
                addStatement("monarchLanguage = %N", "${languageName}Language")
                /*     languageName = % S,
                 monarchLanguage = % N,
                 languageId = % S,
                 fileExtensions = % N,
                 embeddedLanguages = % S,*/
            }
            addStatement(")")
        }.build()

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
    } else if (currentLanguage == "handlebars" || currentLanguage == "liquid" || currentLanguage == "razor") {
        willRegisteredLanguages.add("html")
    }

    val monarchLanguage = loadMonarchJson(languageJson.readText()) ?: error("Language $currentLanguage not found")

    val generatedFile = File(languageJson.absolutePath + ".generated.json")

    generatedFile.writeText(monarchLanguage.toMonarchJson())

    val tokenPostfix = monarchLanguage.tokenPostfix ?: ""
    val languageId =
           if (tokenPostfix.isNotEmpty()) {
               tokenPostfix.substring(tokenPostfix.indexOf(".") + 1)
           } else currentLanguage



    val baseEmbeddingLanguages = mapOf(
        "text/javascript" to "js",
        "javascript" to "js",
        "text/x-handlebars-template" to "handlebars"
    )

    if (languageRegistry.isRegisteredLanguage(currentLanguage)) {
        return rawLanguage
    }

    val language = Language(
        languageName = currentLanguage,
        monarchLanguage,
        languageId = languageId,
        embeddedLanguages = willRegisteredLanguages.associateWith { it } + baseEmbeddingLanguages
    )
    languageRegistry.registerLanguage(
        language
    )

    val rawLanguage = rawLanguage ?: RawLanguage(
        currentLanguage,
        languageJson.path,
        testLanguageJson.path, willRegisteredLanguages,
        language,
        tests
    )

    return getLanguage(
        languageRegistry, willRegisteredLanguages.toMutableList(),
        rawLanguage
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