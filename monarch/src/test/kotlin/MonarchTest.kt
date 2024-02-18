import io.github.dingyi222666.monarch.common.*
import io.github.dingyi222666.monarch.language.LanguageRegistry
import io.github.dingyi222666.monarch.tokenization.MonarchTokenizer
import io.github.dingyi222666.monarch.types.IMonarchLanguage
import io.github.dingyi222666.monarch.types.Token
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

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
 * Initial copyright (C) Microsoft Corporation. All rights reserved.
 * Initial license: MIT
 */

// https://github.com/microsoft/vscode/blob/main/src/vs/editor/standalone/test/browser/monarch.test.ts
class MonarchTest {


    // Ensure @rematch and nextEmbedded can be used together in Monarch grammar
    @Test
    fun testGrammar1() {
        val languageRegistry = LanguageRegistry()

        buildLanguage("sql") {
            tokenizer {
                root {
                    "." token "token"
                }
            }
        }.apply {
            languageRegistry.registerLanguage(this)
        }

        val SQL_QUERY_START = "(SELECT|INSERT|UPDATE|DELETE|CREATE|REPLACE|ALTER|WITH)"

        val tokenizer = buildLanguage("test1") {
            tokenizer {
                root {
                    "(\"\"\")${SQL_QUERY_START}" actionArray {
                        token("string.quote")
                        action("@rematch") {
                            next = "@endStringWithSQL"
                            nextEmbedded = "sql"
                        }
                    }
                    "(\"\"\")$" action "string.quote" state "@maybeStringIsSQL"
                }

                "maybeStringIsSQL" rules {
                    "(.*)" cases {
                        "${SQL_QUERY_START}\\b.*" and {
                            token = "@rematch"
                            next = "@endStringWithSQL"
                            nextEmbedded = "sql"
                        }

                        "@default" and {
                            token = "@rematch"
                            switchTo = "@endDblDocString"
                        }
                    }
                }

                "endDblDocString" rules {
                    "[^\']+" token "string"
                    "\\\\\'" token "string"
                    "'\'\'\'" action "string" state "@popall"
                    "\'" token "string"
                }

                "endStringWithSQL" rules {
                    "\"\"\"" action {
                        token = "string.quote"
                        next = "@popall"
                        nextEmbedded = "@pop"
                    }
                }
            }
        }.let {
            languageRegistry.registerLanguage(it, true)
            languageRegistry.getTokenizerCast<MonarchTokenizer>(it.languageId)
                ?: throw IllegalStateException("No tokenizer found")
        }

        val lines = listOf(
            "mysql_query(\"\"\"SELECT * FROM table_name WHERE ds = '<DATEID>'\"\"\")",
            "mysql_query(\"\"\"",
            "SELECT *",
            "FROM table_name",
            "WHERE ds = '<DATEID>'",
            "\"\"\")"
        )

        val actualTokens = getTokens(tokenizer, lines)

        assertEquals(
            listOf(
                listOf(
                    Token(0, "source.test1", "test1"),
                    Token(12, "string.quote.test1", "test1"),
                    Token(15, "token.sql", "sql"),
                    Token(61, "string.quote.test1", "test1"),
                    Token(64, "source.test1", "test1")
                ),
                listOf(
                    Token(0, "source.test1", "test1"),
                    Token(12, "string.quote.test1", "test1")
                ),
                listOf(
                    Token(0, "token.sql", "sql")
                ),
                listOf(
                    Token(0, "token.sql", "sql")
                ),
                listOf(
                    Token(0, "token.sql", "sql")
                ),
                listOf(
                    Token(0, "string.quote.test1", "test1"),
                    Token(3, "source.test1", "test1")
                )
            ), actualTokens
        )

        languageRegistry.clear()
    }

    // microsoft/monaco-editor#1235: Empty Line Handling
    @OptIn(ExperimentalEncodingApi::class)
    @Test
    fun testGrammar2() {
        val languageRegistry = LanguageRegistry()

        val tokenizer = buildLanguage("test") {
            /*tokenizer: {
            root: [
            { include: '@comments' },
            ],

            comments: [
            [/\/\/$/, 'comment'], // empty single-line comment
            [/\/\//, 'comment', '@comment_cpp'],
            ],

            comment_cpp: [
            [/(?:[^\\]|(?:\\.))+$/, 'comment', '@pop'],
            [/.+$/, 'comment'],
            [/$/, 'comment', '@pop']
            // No possible rule to detect an empty line and @pop?
            ],*/

            tokenizer {
                root {
                    include("@comments")
                }

                comments {
                    "\\/\\/\$" token "comment" // empty single-line comment
                    "\\/\\/" action "comment" state "@comment_cpp"
                }

                "comment_cpp" rules {
                    // in javascript: (?:[^\\]|(?:\\.))+$
                    // why we need six backslashes?
                    Regex("""(?:[^\\\\\\]|(?:\\\\\\.))+$""") action "comment" state "@pop"
                    Regex(".+$") action "comment"
                    "$" action "comment" state "@pop"
                    // No possible rule to detect an empty line and @pop?
                }


            }
        }.let {
            languageRegistry.registerLanguage(it, true)
            languageRegistry.getTokenizerCast<MonarchTokenizer>(it.languageId)
                ?: throw IllegalStateException("No tokenizer found")
        }


        val lines = listOf(
            """// This comment \\""",
            "   continues on the following line",
            "",
            """// This comment does NOT continue \\\\""",
            "   because the escape char was itself escaped",
            "",
            """// This comment DOES continue because \\\\\\""",
            """   the 1st '\\' escapes the 2nd; the 3rd escapes EOL""",
            "",
            """// This comment continues to the following line \\""",
            "",
            "But the line was empty. This line should not be commented."
        )

        println(lines)

        val actualTokens = getTokens(tokenizer, lines);

        assertEquals(
            listOf(
                listOf(Token(0, "comment.test", "test")),
                listOf(Token(0, "comment.test", "test")),
                emptyList(),
                listOf(Token(0, "comment.test", "test")),
                listOf(Token(0, "source.test", "test")),
                emptyList(),
                listOf(Token(0, "comment.test", "test")),
                listOf(Token(0, "comment.test", "test")),
                emptyList(),
                listOf(Token(0, "comment.test", "test")),
                emptyList(),
                listOf(Token(0, "source.test", "test"))
            ), actualTokens
        )
    }
}

fun getTokens(tokenizer: MonarchTokenizer, lines: List<String>): List<List<Token>> {
    val actualTokens = mutableListOf<List<Token>>()
    var state = tokenizer.getInitialState();
    for (line in lines) {
        val result = tokenizer.tokenize(line, true, state);
        actualTokens.add(result.tokens);
        state = result.endState;
    }
    return actualTokens;
}