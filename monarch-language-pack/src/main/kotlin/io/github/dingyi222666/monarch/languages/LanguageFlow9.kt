package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val FlowLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".flow"
    defaultToken = ""
    keywords("import", "require", "export", "forbid", "native", "if", "else", "cast", "unsafe",
        "switch", "default")
    "types" and listOf("io", "mutable", "bool", "int", "double", "string", "flow", "void", "ref",
        "true", "false", "with")
    operators("=", ">", "<", "<=", ">=", "==", "!", "!=", ":=", "::=", "&&", "||", "+", "-", "*",
        "/", "@", "&", "%", ":", "->", "\\", "\$", "??", "^")
    symbols("[@${'$'}=><!~?:&|+\\-*\\\\\\/\\^%]+")
    escapes("\\\\(?:[abfnrtv\\\\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
    tokenizer {
      root {
        "[a-zA-Z_]\\w*".action {
          cases {
            "@keywords" and "keyword"
            "@types" and "type"
            "@default" and "identifier"
          }
        }
        include("@whitespace")
        "[{}()\\[\\]]".token("delimiter")
        "[<>](?!@symbols)".token("delimiter")
        "@symbols".action {
          cases {
            "@operators" and "delimiter"
            "@default" and ""
          }
        }
        "((0(x|X)[0-9a-fA-F]*)|(([0-9]+\\.?[0-9]*)|(\\.[0-9]+))((e|E)(\\+|-)?[0-9]+)?)".token("number")
        "[;,.]".token("delimiter")
        "\"([^\"\\\\]|\\\\.)*${'$'}".token("string.invalid")
        "\"".action("string").state("@string")
      }
      whitespace {
        "[ \\t\\r\\n]+".token("")
        "\\/\\*".action("comment").state("@comment")
        "\\/\\/.*${'$'}".token("comment")
      }
      comment {
        "[^\\/*]+".token("comment")
        "\\*\\/".action("comment").state("@pop")
        "[\\/*]".token("comment")
      }
      string {
        "[^\\\\\"]+".token("string")
        "@escapes".token("string.escape")
        "\\\\.".token("string.escape.invalid")
        "\"".action("string").state("@pop")
      }
    }
  }
}

