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

package io.github.dingyi222666.monarch.loader

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.Import
import com.squareup.kotlinpoet.PropertySpec
import io.github.dingyi222666.monarch.common.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage
import io.github.dingyi222666.monarch.types.MonarchLanguageAction
import io.github.dingyi222666.monarch.types.MonarchLanguageBracket
import io.github.dingyi222666.monarch.types.MonarchLanguageRule
import java.util.*


fun IMonarchLanguage.toMonarchJson(): String {
    return MoshiRoot.adapter(IMonarchLanguage::class.java).toJson(this)
}

/**
 * Compile to Kotlin DSL
 *
 * You need to implementation the kotlin poet library by your self.
 */
fun IMonarchLanguage.toKotlinDSL(
    packageName: String = "io.github.dingyi222666.monarch.languages",
    fileName: String = "MonarchLanguage",
    languageName: String = "Monarch"
): String {
    val codeBlock = compileMonarchLanguageToKotlinDSL(this, CodeBlock.builder())

    val file = FileSpec.builder(packageName, fileName)
        .addImport("io.github.dingyi222666.monarch.common","LanguageScope")
        .addKotlinDefaultImports(includeJvm = true)
        .addProperty(PropertySpec.builder(
            // val Monarch
            "${
                languageName.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                    else it.toString()
                }
            }Language",
            IMonarchLanguage::class,
        ).initializer(
            // =
            codeBlock
        ).build())
        .build()

    val builder = StringBuilder()

    file.writeTo(builder)

    return builder.toString().replace("io.github.dingyi222666.monarch.common.LanguageScope", "io.github.dingyi222666.monarch.common.*")
}

private fun compileMonarchLanguageToKotlinDSL(language: IMonarchLanguage, codeBlock: CodeBlock.Builder): CodeBlock {
    // add main function

    // buildMonarchLanguage {
    codeBlock.beginControlFlow("%N", "buildMonarchLanguage")

    // includeLF = ...
    if (language.includeLF != null) {
        codeBlock.addStatement("%N = %L", "includeLF", language.includeLF)
    }

    // tokenPostfix = ...
    if (language.tokenPostfix != null) {
        codeBlock.addStatement("%N = %S", "tokenPostfix", language.tokenPostfix)
    }

    // start = ...
    if (language.start != null) {
        codeBlock.addStatement("%N = %S", "start", language.start)
    }

    // ignoreCase = ...
    if (language.ignoreCase != null) {
        codeBlock.addStatement("%N = %L", "ignoreCase", language.ignoreCase)
    }

    // unicode = ...
    if (language.unicode != null) {
        codeBlock.addStatement("%N = %L", "unicode", language.unicode)
    }

    // defaultToken = ...
    if (language.defaultToken != null) {
        codeBlock.addStatement("%N = %S", "defaultToken", language.defaultToken)
    }

    val brackets = language.brackets
    if (!brackets.isNullOrEmpty()) {
        // brackets { ... }
        compileMonarchLanguageBracketsToKotlinDSL(brackets, codeBlock)
    }

    val builtinStringMethod = arrayOf(
        "binarydigits",
        "octaldigits",
        "hexdigits",
        "digits",
        "control",
        "comments",
        "identifiers",
        "escapes",
        "symbols"
    )
    val builtinArrayMethod = arrayOf(
        "keywords",
        "operators",
        "typeKeywords"
    )

    for ((key, value) in language.attrMap) {

        when (value) {
            is String -> if (builtinStringMethod.contains(key)) {
                // symbols("")
                codeBlock.addStatement("%N(%P)", key, value)
            } else {
                // "symbols" and "xxx"
                codeBlock.addStatement("%S and %P", key, value)
            }

            is Regex -> if (builtinStringMethod.contains(key)) {
                // symbols("")
                codeBlock.addStatement("%N(%P)", key, value.pattern)
            } else {
                // "symbols" and "xxx"
                codeBlock.addStatement("%S and %P", key, value.pattern)
            }

            is List<*> -> if (builtinArrayMethod.contains(key)) {
                // symbols("")
                codeBlock.addStatement("%N(%L)", key, value.formatStringArgs())
            } else {
                // "symbols" and "xxx"
                codeBlock.addStatement("%S and listOf(%L)", key, value.formatStringArgs())
            }

            else -> {
                throw Exception("Unsupported type of $value in attrMap")
            }
        }
    }

    val tokenizer = language.tokenizer

    if (!tokenizer.isNullOrEmpty()) {
        // tokenizer { ... }
        compileMonarchLanguageRulesToKotlinDSL(tokenizer, codeBlock)
    }

    codeBlock.endControlFlow()

    return codeBlock.build()
}

private fun compileMonarchLanguageRulesToKotlinDSL(
    rules: Map<String, List<MonarchLanguageRule>>, codeBlock: CodeBlock.Builder
) {

    // tokenizer {
    codeBlock.beginControlFlow("%N", "tokenizer")


    val builtInRules = arrayOf(
        "root", "comment", "comments", "string", "whitespace", "javadoc", "style", "script"
    )

    rules.forEach { (key, value) ->
        // root { ...
        if (builtInRules.contains(key)) {
            codeBlock.beginControlFlow("%N", key)
        } else {
            // root rules { ...
            codeBlock.beginControlFlow("%N rules", key)
        }

        for (rule in value) {
            compileMonarchLanguageRuleToKotlinDSL(rule, codeBlock)
        }

        // }
        codeBlock.endControlFlow()
    }

    // }
    codeBlock.endControlFlow()
}

private fun regexToString(rawRegex: Any): String {
    val rawString = when (rawRegex) {
        is String -> rawRegex
        is Regex -> rawRegex.pattern
        else -> throw Exception("Unsupported type of $rawRegex in rules")
    }

    // escape current "\n" -> "\\n", and more

    return rawString
        /*.replace("\\", "\\\\")
        .replace("\"", "\\\"")*/
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")


}

private fun compileMonarchLanguageRuleToKotlinDSL(
    rule: MonarchLanguageRule, codeBlock: CodeBlock.Builder
) {
    when (rule) {
        is MonarchLanguageRule.ShortRule1 -> {
            when (val action = rule.action) {
                is MonarchLanguageAction.ShortLanguageAction -> {
                    //  "[A-Z][\\w\$]*" token "type.identifier"
                    codeBlock.addStatement("%P token %S", regexToString(rule.regex), action.token)
                }

                // "(')(@escapes)(')" actionArray {
                //   ....
                //  }
                is MonarchLanguageAction.ActionArray -> {
                    codeBlock.beginControlFlow("%P actionArray", regexToString(rule.regex))
                    compileMonarchLanguageArrayActionToKotlinDSL(action, codeBlock)
                    codeBlock.endControlFlow()
                }

                // "xxx" action {
                //   token = 111
                // }
                is MonarchLanguageAction.ExpandedLanguageAction -> {
                    codeBlock.beginControlFlow("%P action", regexToString(rule.regex))
                    compileMonarchExpandedLanguageActionToKotlinDSL(action, codeBlock)
                    codeBlock.endControlFlow()
                }
            }

        }

        // "xx" action "ss" state "xx"
        is MonarchLanguageRule.ShortRule2 -> {
            when (val action = rule.action) {
                is MonarchLanguageAction.ShortLanguageAction -> {
                    //  "[A-Z][\\w\$]*" token "type.identifier"
                    codeBlock.addStatement(
                        "%P action %S state %S",
                        regexToString(rule.regex),
                        action.token,
                        rule.nextState
                    )
                }

                // "(')(@escapes)(')" actionArray {
                //   ....
                //  }
                is MonarchLanguageAction.ActionArray -> {
                    throw  Exception("Unsupported type of $action in short rules 2")

                }

                // "xxx" action {
                //   token = 111
                // } state "xx"
                is MonarchLanguageAction.ExpandedLanguageAction -> {

                    if (action.token == null ||
                        action.goBack != null ||
                        action.switchTo != null ||
                        action.cases != null ||
                        action.nextEmbedded != null  ||
                        action.group != null
                    ) {
                        codeBlock.beginControlFlow("%P action", regexToString(rule.regex))
                        action.next = rule.nextState
                        compileMonarchExpandedLanguageActionToKotlinDSL(action, codeBlock)
                        codeBlock.endControlFlow()
                        return
                    }


                    codeBlock.addStatement(
                        "%P action %S state %S",
                        regexToString(rule.regex),
                        action.token,
                        rule.nextState
                    )

                }
            }

        }

        // include("xxxx")
        is MonarchLanguageRule.ExpandedLanguageRule -> {
            codeBlock.addStatement("include(%S)", rule.include)
        }
    }

}

private fun compileMonarchLanguageArrayActionToKotlinDSL(
    actions: MonarchLanguageAction.ActionArray, codeBlock: CodeBlock.Builder
) {
    actions.actions.forEach { action ->
        when (action) {
            is MonarchLanguageAction.ActionArray -> {
                val value = action.actions

                val filterInstance = value.filterIsInstance<MonarchLanguageAction.ShortLanguageAction>()

                if (filterInstance.size == value.size && filterInstance.isNotEmpty()) {
                    // shortActions("xx", "xx", "xx")
                    codeBlock.addStatement(
                        "shortActions(%N)", filterInstance.map { it.token }.formatStringArgs()
                    )
                    return@forEach
                }

                // actionArray { ... }
                codeBlock.beginControlFlow("actionArray")
                compileMonarchLanguageArrayActionToKotlinDSL(action, codeBlock)
                codeBlock.endControlFlow()
            }

            is MonarchLanguageAction.ExpandedLanguageAction -> {
                if (action.token != null) {
                    // expanded action("xx") { ... }
                    codeBlock.beginControlFlow("action(%S)", action.token)
                    compileMonarchExpandedLanguageActionToKotlinDSL(action, codeBlock, true)
                    codeBlock.endControlFlow()
                } else {
                    // expanded action { ... }
                    compileMonarchExpandedLanguageActionToKotlinDSL(action, codeBlock)
                }
            }

            is MonarchLanguageAction.ShortLanguageAction -> {
                // simple token("xx")
                codeBlock.addStatement("token(%S)", action.token)
            }
        }
    }
}

private fun compileMonarchExpandedLanguageActionToKotlinDSL(
    action: MonarchLanguageAction.ExpandedLanguageAction,
    codeBlock: CodeBlock.Builder,
    withoutToken: Boolean = false
) {
    // token = "xx"
    if (action.token != null && !withoutToken) {
        codeBlock.addStatement("token = %S", action.token)
    }

    // next = "xx"
    if (action.next != null) {
        codeBlock.addStatement("next = %S", action.next)
    }
    // nextEmbedded = "xx"
    if (action.nextEmbedded != null) {
        codeBlock.addStatement("nextEmbedded = %S", action.nextEmbedded)
    }
    // switchTo = "xx"
    if (action.switchTo != null) {
        codeBlock.addStatement("switchTo = %S", action.switchTo)
    }
    // nextEmbedded = "xx"
    if (action.nextEmbedded != null) {
        codeBlock.addStatement("nextEmbedded = %S", action.nextEmbedded)
    }
    // log = "xx"
    if (action.log != null) {
        codeBlock.addStatement("log = %S", action.log)
    }
    // bracket = "xx"
    if (action.bracket != null) {
        codeBlock.addStatement("bracket = %S", action.bracket)
    }
    // goBack = 1
    if (action.goBack != null) {
        codeBlock.addStatement("goBack = %L", action.goBack)
    }
    // group = "xx"
    if (action.group != null) {
        throw UnsupportedOperationException("The group key of value ${action.group} are not supported")
    }

    val cases = action.cases
    if (cases != null) {
        // cases {
        codeBlock.beginControlFlow("cases")

        cases.forEach { (key, value) ->
            when (value) {
                is MonarchLanguageAction.ExpandedLanguageAction -> {
                    // 'xx" and { ... }
                    codeBlock.beginControlFlow("%S and", key)
                    compileMonarchExpandedLanguageActionToKotlinDSL(value, codeBlock)
                    codeBlock.endControlFlow()
                }

                // 'xx' and "xx"
                is MonarchLanguageAction.ShortLanguageAction -> {
                    codeBlock.addStatement("%S and %S", key, value.token)
                }

                else -> throw Exception("Unsupported type of cases $value")
            }
        }

        codeBlock.endControlFlow()
    }
}


private fun compileMonarchLanguageBracketsToKotlinDSL(
    list: List<MonarchLanguageBracket>, codeBlock: CodeBlock.Builder
) {

    // brackets {
    codeBlock.beginControlFlow("%N", "brackets")

    list.forEach { bracket ->
        // bracket(open,close,token)
        codeBlock.addStatement("bracket(%S,%S,%S)", bracket.open, bracket.close, bracket.token)
    }

    // }
    codeBlock.endControlFlow()

}

// ['1','2','3'] -> "1","2","3"
private fun List<*>.formatStringArgs(): String {
    return joinToString(separator = ", ") { "\"$it\"" }
}