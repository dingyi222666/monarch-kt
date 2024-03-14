package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val SbLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".sb"
    ignoreCase = true
    defaultToken = ""
    brackets {
      bracket("[","]","delimiter.array")
      bracket("(",")","delimiter.parenthesis")
      bracket("If","EndIf","keyword.tag-if")
      bracket("While","EndWhile","keyword.tag-while")
      bracket("For","EndFor","keyword.tag-for")
      bracket("Sub","EndSub","keyword.tag-sub")
    }
    keywords("Else", "ElseIf", "EndFor", "EndIf", "EndSub", "EndWhile", "For", "Goto", "If", "Step",
        "Sub", "Then", "To", "While")
    "tagwords" and listOf("If", "Sub", "While", "For")
    operators(">", "<", "<>", "<=", ">=", "And", "Or", "+", "-", "*", "/", "=")
    "identifier" and "[a-zA-Z_][\\w]*"
    symbols("[=><:+\\-*\\/%\\.,]+")
    escapes("\\\\(?:[abfnrtv\\\\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
    tokenizer {
      root {
        include("@whitespace")
        "(@identifier)(?=[.])".token("type")
        "@identifier".action {
          cases {
            "@keywords" and {
              token = "keyword.${'$'}0"
            }
            "@operators" and "operator"
            "@default" and "variable.name"
          }
        }
        "([.])(@identifier)".action {
          cases {
            "${'$'}2" actionArray {
              token("delimiter")
              token("type.member")
            }
            "@default" and ""
          }
        }
        "\\d*\\.\\d+".token("number.float")
        "\\d+".token("number")
        "[()\\[\\]]".token("@brackets")
        "@symbols".action {
          cases {
            "@operators" and "operator"
            "@default" and "delimiter"
          }
        }
        "\"([^\"\\\\]|\\\\.)*${'$'}".token("string.invalid")
        "\"".action("string").state("@string")
      }
      whitespace {
        "[ \\t\\r\\n]+".token("")
        "(\\').*${'$'}".token("comment")
      }
      string {
        "[^\\\\\"]+".token("string")
        "@escapes".token("string.escape")
        "\\\\.".token("string.escape.invalid")
        "\"C?".action("string").state("@pop")
      }
    }
  }
}

