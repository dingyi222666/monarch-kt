package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val GoLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".go"
    defaultToken = ""
    keywords("break", "case", "chan", "const", "continue", "default", "defer", "else",
        "fallthrough", "for", "func", "go", "goto", "if", "import", "interface", "map", "package",
        "range", "return", "select", "struct", "switch", "type", "var", "bool", "true", "false",
        "uint8", "uint16", "uint32", "uint64", "int8", "int16", "int32", "int64", "float32",
        "float64", "complex64", "complex128", "byte", "rune", "uint", "int", "uintptr", "string",
        "nil")
    operators("+", "-", "*", "/", "%", "&", "|", "^", "<<", ">>", "&^", "+=", "-=", "*=", "/=",
        "%=", "&=", "|=", "^=", "<<=", ">>=", "&^=", "&&", "||", "<-", "++", "--", "==", "<", ">",
        "=", "!", "!=", "<=", ">=", ":=", "...", "(", ")", "", "]", "{", "}", ",", ";", ".", ":")
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
        "\\[\\[.*\\]\\]".token("annotation")
        "^\\s*#\\w+".token("keyword")
        "[{}()\\[\\]]".token("@brackets")
        "[<>](?!@symbols)".token("@brackets")
        "@symbols".action {
          cases {
            "@operators" and "delimiter"
            "@default" and ""
          }
        }
        "\\d*\\d+[eE]([\\-+]?\\d+)?".token("number.float")
        "\\d*\\.\\d+([eE][\\-+]?\\d+)?".token("number.float")
        "0[xX][0-9a-fA-F']*[0-9a-fA-F]".token("number.hex")
        "0[0-7']*[0-7]".token("number.octal")
        "0[bB][0-1']*[0-1]".token("number.binary")
        "\\d[\\d']*".token("number")
        "\\d".token("number")
        "[;,.]".token("delimiter")
        "\"([^\"\\\\]|\\\\.)*${'$'}".token("string.invalid")
        "\"".action("string").state("@string")
        "`".action("string").state("@rawstring")
        "'[^\\\\']'".token("string")
        "(')(@escapes)(')".actionArray {
          token("string")
          token("string.escape")
          token("string")
        }
        "'".token("string.invalid")
      }
      whitespace {
        "[ \\t\\r\\n]+".token("")
        "\\/\\*\\*(?!\\/)".action("comment.doc").state("@doccomment")
        "\\/\\*".action("comment").state("@comment")
        "\\/\\/.*${'$'}".token("comment")
      }
      comment {
        "[^\\/*]+".token("comment")
        "\\*\\/".action("comment").state("@pop")
        "[\\/*]".token("comment")
      }
      "doccomment" rules {
        "[^\\/*]+".token("comment.doc")
        "\\/\\*".token("comment.doc.invalid")
        "\\*\\/".action("comment.doc").state("@pop")
        "[\\/*]".token("comment.doc")
      }
      string {
        "[^\\\\\"]+".token("string")
        "@escapes".token("string.escape")
        "\\\\.".token("string.escape.invalid")
        "\"".action("string").state("@pop")
      }
      "rawstring" rules {
        "[^\\`]".token("string")
        "`".action("string").state("@pop")
      }
    }
  }
}

