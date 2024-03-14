package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val LuaLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".lua"
    defaultToken = ""
    brackets {
      bracket("{","}","delimiter.bracket")
      bracket("[","]","delimiter.array")
      bracket("(",")","delimiter.parenthesis")
    }
    keywords("and", "break", "do", "else", "elseif", "end", "false", "for", "function", "goto",
        "if", "in", "local", "nil", "not", "or", "repeat", "return", "then", "true", "until",
        "while")
    operators("+", "-", "*", "/", "%", "^", "#", "==", "~=", "<=", ">=", "<", ">", "=", ";", ":",
        ",", ".", "..", "...")
    symbols("[=><!~?:&|+\\-*\\/\\^%]+")
    escapes("\\\\(?:[abfnrtv\\\\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
    tokenizer {
      root {
        "[a-zA-Z_]\\w*".action {
          cases {
            "@keywords" and {
              token = "keyword.${'$'}0"
            }
            "@default" and "identifier"
          }
        }
        include("@whitespace")
        "(,)(\\s*)([a-zA-Z_]\\w*)(\\s*)(:)(?!:)".actionArray {
          token("delimiter")
          token("")
          token("key")
          token("")
          token("delimiter")
        }
        "({)(\\s*)([a-zA-Z_]\\w*)(\\s*)(:)(?!:)".actionArray {
          token("@brackets")
          token("")
          token("key")
          token("")
          token("delimiter")
        }
        "[{}()\\[\\]]".token("@brackets")
        "@symbols".action {
          cases {
            "@operators" and "delimiter"
            "@default" and ""
          }
        }
        "\\d*\\.\\d+([eE][\\-+]?\\d+)?".token("number.float")
        "0[xX][0-9a-fA-F_]*[0-9a-fA-F]".token("number.hex")
        "\\d+?".token("number")
        "[;,.]".token("delimiter")
        "\"([^\"\\\\]|\\\\.)*${'$'}".token("string.invalid")
        "'([^'\\\\]|\\\\.)*${'$'}".token("string.invalid")
        "\"".action("string").state("@string.\"")
        "'".action("string").state("@string.'")
      }
      whitespace {
        "[ \\t\\r\\n]+".token("")
        "--\\[([=]*)\\[".action("comment").state("@comment.${'$'}1")
        "--.*${'$'}".token("comment")
      }
      comment {
        "[^\\]]+".token("comment")
        "\\]([=]*)\\]".action {
          cases {
            "${'$'}1==${'$'}S2" and {
              token = "comment"
              next = "@pop"
            }
            "@default" and "comment"
          }
        }
        ".".token("comment")
      }
      string {
        "[^\\\\\"']+".token("string")
        "@escapes".token("string.escape")
        "\\\\.".token("string.escape.invalid")
        "[\"']".action {
          cases {
            "${'$'}#==${'$'}S2" and {
              token = "string"
              next = "@pop"
            }
            "@default" and "string"
          }
        }
      }
    }
  }
}

