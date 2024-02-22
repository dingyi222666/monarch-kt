import io.github.dingyi222666.monarch.common.*
import io.github.dingyi222666.monarch.language.LanguageRegistry
import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.tokenization.MonarchTokenizer
import io.github.dingyi222666.monarch.types.Token
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
 * Initial copyright (C) Microsoft Corporation. All rights reserved.
 * Initial license: MIT
 */

// current sync version: https://github.com/microsoft/vscode/tree/a8f73340be02966c3816a2f23cb7e446a3a7cb9b/src/vs/editor/standalone/common

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
            brackets {

            }
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
    @Test
    fun testGrammar2() {
        val languageRegistry = LanguageRegistry()

        val tokenizer = buildLanguage("test") {
            /*tokenizer: {
            root: [
            { includeand@comments' },
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

        languageRegistry.clear()
    }

    // microsoft/monaco-editor#2265: Exit a state at end of line
    @Test
    fun testGrammar3() {
        val languageRegistry = LanguageRegistry()

        val tokenizer = buildLanguage("test") {
            includeLF = true

            tokenizer {
                root {
                    """^\*""" action "" state "@inner"
                    """\:\*""" action "" state "@inner"
                    "[^*:]+" token "string"
                    "[*:]" token "string"
                }

                "inner" rules {
                    "\n" action "" state "@pop"
                    """\d+""" token "number"
                    """[^\d]+""" token ""
                }

            }
        }.let {
            languageRegistry.registerLanguage(it, true)
            languageRegistry.getTokenizerCast<MonarchTokenizer>(it.languageId)
                ?: throw IllegalStateException("No tokenizer found")
        }

        val lines = listOf(
            "PRINT 10 * 20",
            "*FX200, 3",
            "PRINT 2*3:*FX200, 3"
        )

        val actualTokens = getTokens(tokenizer, lines);

        assertEquals(
            listOf(
                listOf(Token(0, "string.test", "test")),
                listOf(
                    Token(0, "", "test"),
                    Token(3, "number.test", "test"),
                    Token(6, "", "test"),
                    Token(8, "number.test", "test"),
                ),
                listOf(
                    Token(0, "string.test", "test"),
                    Token(9, "", "test"),
                    Token(13, "number.test", "test"),
                    Token(16, "", "test"),
                    Token(18, "number.test", "test")
                )
            ),
            actualTokens
        )

        languageRegistry.clear()
    }

    // microsoft/vscode #115662: monarchCompile function need an extra option which can control replacement
    @Test
    fun testGrammar4() {

        val tokenizer1 = buildMonarchLanguage {
            ignoreCase = false
            "uselessReplaceKey1" and "@uselessReplaceKey2"
            "uselessReplaceKey2" and "@uselessReplaceKey3"
            "uselessReplaceKey3" and "@uselessReplaceKey4"
            "uselessReplaceKey4" and "@uselessReplaceKey5"
            "uselessReplaceKey5" and "@ham"

            tokenizer {
                root {
                    // /@\w+/
                    (if (Regex("@\\w+").matches("@ham")) {
                        // ^${'@uselessReplaceKey1'}$ -> ^@uselessReplaceKey1$
                        "^@uselessReplaceKey1$"
                    } else {
                        // ^${'@ham'}$ -> ^@ham$
                        "^@ham$"
                    }) action {
                        token = "ham"
                    }
                }
            }
        }.let {
            val lexer = it.compile("test")
            MonarchTokenizer("test", lexer, LanguageRegistry.instance, null, 5000)
        }

        val tokenizer2 = buildMonarchLanguage {
            ignoreCase = false
            tokenizer {
                root {
                    "@@ham" token "ham"
                }
            }
        }.let {
            val lexer = it.compile("test")
            MonarchTokenizer("test", lexer, LanguageRegistry.instance, null, 5000)
        }

        val lines = listOf(
            "@ham"
        )

        val actualTokens1 = getTokens(tokenizer1, lines)

        assertEquals(
            listOf(
                listOf(Token(0, "ham.test", "test"))
            ),
            actualTokens1
        )

        val actualTokens2 = getTokens(tokenizer2, lines)

        assertEquals(
            listOf(
                listOf(Token(0, "ham.test", "test"))
            ),
            actualTokens2
        )
    }

    // microsoft/monaco-editor#2424: Allow to target @@
    @Test
    fun testGrammar5() {
        val languageRegistry = LanguageRegistry()

        val tokenizer = buildLanguage("test") {
            /* ignoreCase: false,
             tokenizer: {
             root: [
             {
                 regex: /@@@@/,
                 action: { token: 'ham' }
             },
             ],
         },*/
            ignoreCase = false
            tokenizer {
                root {
                    "@@" action { token = "ham" }
                }
            }
        }.let {
            languageRegistry.registerLanguage(it, true)
            languageRegistry.getTokenizerCast<MonarchTokenizer>(it.languageId)
                ?: throw IllegalStateException("No tokenizer found")
        }

        /* const lines = [
             `@@`
         ];

         const actualTokens = getTokens(tokenizer, lines);
         assert.deepStrictEqual(actualTokens, [
             [
                 new Token(0, 'ham.test', 'test'),
         ]
         ]);

         disposables.dispose();*/

        val lines = listOf(
            "@@"
        )

        val actualTokens = getTokens(tokenizer, lines)

        assertEquals(
            listOf(
                listOf(Token(0, "ham.test", "test"))
            ),
            actualTokens
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