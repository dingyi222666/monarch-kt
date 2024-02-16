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
 */

import io.github.dingyi222666.kotlin.monarch.common.*
import kotlin.test.Test


class TokenizerTest {

    @Test
    fun test() {
        println(testLanguage)
    }
}


val testLanguage = buildMonarchLanguage {
    defaultToken = "source"

    keywords(
        "abstract", "continue", "for", "new", "switch", "assert", "goto", "do",
        "if", "private", "this", "break", "protected", "throw", "else", "public",
        "enum", "return", "catch", "try", "interface", "static", "class",
        "finally", "const", "super", "while", "true", "false"
    )

    typeKeywords(
        "boolean", "double", "byte", "int", "short", "char", "void", "long", "float"
    )

    operators(
        "=", ">", "<", "!", "~", "?", ":", "==", "<=", ">=", "!=",
        "&&", "||", "++", "--", "+", "-", "*", "/", "&", "|", "^", "%",
        "<<", ">>", ">>>", "+=", "-=", "*=", "/=", "&=", "|=", "^=",
        "%=", "<<=", ">>=", ">>>="
    )

    // we include these common regular expressions
    symbols("[=><!~ ?: &|+\\-*\\/\\^%]+".r)

    // C# style strings
    escapes("\\( ?: [abfnrtv\\\"\"]|x[0-9A-Fa-f]{ 1, 4 }|u[0-9A-Fa-f]{ 4 }|U[0-9A-Fa-f]{ 8 })".r)


    tokenizer {
        root {
            // identifiers and keywords
            "[a-z_\$][\\w\$]*" cases {
                "@typeKeywords" and "keyword"
                "@keywords" and "keyword"
                "@default" and "identifier"
            }

            "[A-Z][\\w\$]*" token "type.identifier" // to show class names nicely

            // whitespace
            include("whitespace")

            // delimiters and operators

            "[{}()\\[\\]]" token "@brackets"
            "[<>](?!@symbols)" token "@brackets"
            "@symbols" cases {
                "@operators" and "operator"
                "@default" and ""
            }

            // @ annotations.
            // As an example, we emit a debugging log message on these tokens.
            // Note: message are supressed during the first load -- change some lines to see them.
            "@\\s*[a-zA-Z_\\\$][\\w\\\$]*" action {
                token = "annotation"
                log = "annotation token: $0"
            }

            // numbers
            "\\d*\\.\\d+([eE][\\-+]?\\d+)?" token "number.float"
            "0[xX][0-9a-fA-F]+" token "number.hex"
            "\\d+" token "number"

            // delimiter: after number because of .\d floats
            "[;,.]" token "delimiter"

            // strings

            "\"([^\"\\\\]|\\\\.)*\$" token "string.invalid"
            "\"" action {
                token = "string.quote"
                bracket = "@open"
                next = "@string"
            }

            // characters
            "'[^\\\\']'" token "string"
            "(')(@escapes)(')" actionArray {
                shortActions("string", "string.escape", "string")
            }
        }

        comment {
            "[^\\/*]+" token "comment"
            "\\/\\*" actionAndNext "comment" state "@push"
            "\\*/" actionAndNext "comment" state "@pop"
            "[\\/*]" token "comment"
        }

        string {
            "[^\\\\\"]+" token "string"
            "@escapes" token "string.escape"
            "\\\\." token "string.escape.invalid"
            "\"" action {
                token = "string.quote"
                bracket = "@close"
                next = "@pop"
            }
        }

        whitespace {
            "[ \t\r\n]+" token "whitespace"
            "\\/\\*" actionAndNext "comment" state "@comment"
            "\\/\\/.*\$" token "comment"
        }
    }
}