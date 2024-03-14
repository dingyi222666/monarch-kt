package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val JavaLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".java"
    defaultToken = ""
    keywords("abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package",
        "synchronized", "boolean", "do", "if", "private", "this", "break", "double", "implements",
        "protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum",
        "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char",
        "final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile",
        "const", "float", "native", "super", "while", "true", "false", "yield", "record", "sealed",
        "non-sealed", "permits")
    operators("=", ">", "<", "!", "~", "?", ":", "==", "<=", ">=", "!=", "&&", "||", "++", "--",
        "+", "-", "*", "/", "&", "|", "^", "%", "<<", ">>", ">>>", "+=", "-=", "*=", "/=", "&=",
        "|=", "^=", "%=", "<<=", ">>=", ">>>=")
    symbols("[=><!~?:&|+\\-*\\/\\^%]+")
    escapes("\\\\(?:[abfnrtv\\\\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
    digits("\\d+(_+\\d+)*")
    octaldigits("[0-7]+(_+[0-7]+)*")
    binarydigits("[0-1]+(_+[0-1]+)*")
    hexdigits("[[0-9a-fA-F]+(_+[0-9a-fA-F]+)*")
    tokenizer {
      root {
        "non-sealed".token("keyword.non-sealed")
        "[a-zA-Z_${'$'}][\\w${'$'}]*".action {
          cases {
            "@keywords" and {
              token = "keyword.${'$'}0"
            }
            "@default" and "identifier"
          }
        }
        include("@whitespace")
        "[{}()\\[\\]]".token("@brackets")
        "[<>](?!@symbols)".token("@brackets")
        "@symbols".action {
          cases {
            "@operators" and "delimiter"
            "@default" and ""
          }
        }
        "@\\s*[a-zA-Z_\\${'$'}][\\w\\${'$'}]*".token("annotation")
        "(@digits)[eE]([\\-+]?(@digits))?[fFdD]?".token("number.float")
        "(@digits)\\.(@digits)([eE][\\-+]?(@digits))?[fFdD]?".token("number.float")
        "0[xX](@hexdigits)[Ll]?".token("number.hex")
        "0(@octaldigits)[Ll]?".token("number.octal")
        "0[bB](@binarydigits)[Ll]?".token("number.binary")
        "(@digits)[fFdD]".token("number.float")
        "(@digits)[lL]?".token("number")
        "[;,.]".token("delimiter")
        "\"([^\"\\\\]|\\\\.)*${'$'}".token("string.invalid")
        "\"\"\"".action("string").state("@multistring")
        "\"".action("string").state("@string")
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
        "\\/\\*\\*(?!\\/)".action("comment.doc").state("@javadoc")
        "\\/\\*".action("comment").state("@comment")
        "\\/\\/.*${'$'}".token("comment")
      }
      comment {
        "[^\\/*]+".token("comment")
        "\\*\\/".action("comment").state("@pop")
        "[\\/*]".token("comment")
      }
      javadoc {
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
      "multistring" rules {
        "[^\\\\\"]+".token("string")
        "@escapes".token("string.escape")
        "\\\\.".token("string.escape.invalid")
        "\"\"\"".action("string").state("@pop")
        ".".token("string")
      }
    }
  }
}

