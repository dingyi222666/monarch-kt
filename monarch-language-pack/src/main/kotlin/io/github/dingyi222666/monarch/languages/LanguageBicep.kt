package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val BicepLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".bicep"
    defaultToken = ""
    brackets {
      bracket("{","}","delimiter.curly")
      bracket("[","]","delimiter.square")
      bracket("(",")","delimiter.parenthesis")
    }
    symbols("[=><!~?:&|+\\-*/^%]+")
    keywords("targetScope", "resource", "module", "param", "var", "output", "for", "in", "if",
        "existing")
    "namedLiterals" and listOf("true", "false", "null")
    escapes("\\\\(u{[0-9A-Fa-f]+}|n|r|t|\\\\|'|\\${'$'}{)")
    tokenizer {
      root {
        include("@expression")
        include("@whitespace")
      }
      "stringVerbatim" rules {
        "(|'|'')[^']".action {
          token = "string"
        }
        "'''".action {
          token = "string.quote"
          next = "@pop"
        }
      }
      "stringLiteral" rules {
        "\\${'$'}{".action {
          token = "delimiter.bracket"
          next = "@bracketCounting"
        }
        "[^\\\\'${'$'}]+".action {
          token = "string"
        }
        "@escapes".action {
          token = "string.escape"
        }
        "\\\\.".action {
          token = "string.escape.invalid"
        }
        "'".action {
          token = "string"
          next = "@pop"
        }
      }
      "bracketCounting" rules {
        "{".action {
          token = "delimiter.bracket"
          next = "@bracketCounting"
        }
        "}".action {
          token = "delimiter.bracket"
          next = "@pop"
        }
        include("expression")
      }
      comment {
        "[^\\*]+".action {
          token = "comment"
        }
        "\\*\\/".action {
          token = "comment"
          next = "@pop"
        }
        "[\\/*]".action {
          token = "comment"
        }
      }
      whitespace {
        "\\/\\*".action {
          token = "comment"
          next = "@comment"
        }
        "\\/\\/.*${'$'}".action {
          token = "comment"
        }
      }
      "expression" rules {
        "'''".action {
          token = "string.quote"
          next = "@stringVerbatim"
        }
        "'".action {
          token = "string.quote"
          next = "@stringLiteral"
        }
        "[0-9]+".action {
          token = "number"
        }
        "\\b[_a-zA-Z][_a-zA-Z0-9]*\\b".action {
          cases {
            "@keywords" and {
              token = "keyword"
            }
            "@namedLiterals" and {
              token = "keyword"
            }
            "@default" and {
              token = "identifier"
            }
          }
        }
      }
    }
  }
}

