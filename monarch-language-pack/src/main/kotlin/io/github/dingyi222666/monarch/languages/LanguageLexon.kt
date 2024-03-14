package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val LexonLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".lexon"
    ignoreCase = true
    keywords("lexon", "lex", "clause", "terms", "contracts", "may", "pay", "pays", "appoints",
        "into", "to")
    typeKeywords("amount", "person", "key", "time", "date", "asset", "text")
    operators("less", "greater", "equal", "le", "gt", "or", "and", "add", "added", "subtract",
        "subtracted", "multiply", "multiplied", "times", "divide", "divided", "is", "be",
        "certified")
    symbols("[=><!~?:&|+\\-*\\/\\^%]+")
    tokenizer {
      root {
        "^(\\s*)(comment:?(?:\\s.*|))${'$'}".actionArray {
          token("")
          token("comment")
        }
        "\"".action {
          token = "identifier.quote"
          next = "@quoted_identifier"
          bracket = "@open"
        }
        "LEX${'$'}".action {
          token = "keyword"
          next = "@identifier_until_period"
          bracket = "@open"
        }
        "LEXON".action {
          token = "keyword"
          next = "@semver"
          bracket = "@open"
        }
        ":".action {
          token = "delimiter"
          next = "@identifier_until_period"
          bracket = "@open"
        }
        "[a-z_${'$'}][\\w${'$'}]*".action {
          cases {
            "@operators" and "operator"
            "@typeKeywords" and "keyword.type"
            "@keywords" and "keyword"
            "@default" and "identifier"
          }
        }
        include("@whitespace")
        "[{}()\\[\\]]".token("@brackets")
        "[<>](?!@symbols)".token("@brackets")
        "@symbols".token("delimiter")
        "\\d*\\.\\d*\\.\\d*".token("number.semver")
        "\\d*\\.\\d+([eE][\\-+]?\\d+)?".token("number.float")
        "0[xX][0-9a-fA-F]+".token("number.hex")
        "\\d+".token("number")
        "[;,.]".token("delimiter")
      }
      "quoted_identifier" rules {
        "[^\\\\\"]+".token("identifier")
        "\"".action {
          token = "identifier.quote"
          next = "@pop"
          bracket = "@close"
        }
      }
      "space_identifier_until_period" rules {
        ":".token("delimiter")
        " ".action {
          token = "white"
          next = "@identifier_rest"
        }
      }
      "identifier_until_period" rules {
        include("@whitespace")
        ":".action {
          token = "delimiter"
          next = "@identifier_rest"
        }
        "[^\\\\.]+".token("identifier")
        "\\.".action {
          token = "delimiter"
          next = "@pop"
          bracket = "@close"
        }
      }
      "identifier_rest" rules {
        "[^\\\\.]+".token("identifier")
        "\\.".action {
          token = "delimiter"
          next = "@pop"
          bracket = "@close"
        }
      }
      "semver" rules {
        include("@whitespace")
        ":".token("delimiter")
        "\\d*\\.\\d*\\.\\d*".action {
          token = "number.semver"
          next = "@pop"
          bracket = "@close"
        }
      }
      whitespace {
        "[ \\t\\r\\n]+".token("white")
      }
    }
  }
}

