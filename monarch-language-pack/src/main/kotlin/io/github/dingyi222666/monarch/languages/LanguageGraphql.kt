package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val GqlLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".gql"
    defaultToken = "invalid"
    keywords("null", "true", "false", "query", "mutation", "subscription", "extend", "schema",
        "directive", "scalar", "type", "interface", "union", "enum", "input", "implements",
        "fragment", "on")
    typeKeywords("Int", "Float", "String", "Boolean", "ID")
    "directiveLocations" and listOf("SCHEMA", "SCALAR", "OBJECT", "FIELD_DEFINITION",
        "ARGUMENT_DEFINITION", "INTERFACE", "UNION", "ENUM", "ENUM_VALUE", "INPUT_OBJECT",
        "INPUT_FIELD_DEFINITION", "QUERY", "MUTATION", "SUBSCRIPTION", "FIELD",
        "FRAGMENT_DEFINITION", "FRAGMENT_SPREAD", "INLINE_FRAGMENT", "VARIABLE_DEFINITION")
    operators("=", "!", "?", ":", "&", "|")
    symbols("[=!?:&|]+")
    escapes("\\\\(?:[\"\\\\\\/bfnrt]|u[0-9A-Fa-f]{4})")
    tokenizer {
      root {
        "[a-z_][\\w${'$'}]*".action {
          cases {
            "@keywords" and "keyword"
            "@default" and "key.identifier"
          }
        }
        "[${'$'}][\\w${'$'}]*".action {
          cases {
            "@keywords" and "keyword"
            "@default" and "argument.identifier"
          }
        }
        "[A-Z][\\w\\${'$'}]*".action {
          cases {
            "@typeKeywords" and "keyword"
            "@default" and "type.identifier"
          }
        }
        include("@whitespace")
        "[{}()\\[\\]]".token("@brackets")
        "@symbols".action {
          cases {
            "@operators" and "operator"
            "@default" and ""
          }
        }
        "@\\s*[a-zA-Z_\\${'$'}][\\w\\${'$'}]*".action {
          token = "annotation"
          log = "annotation token: ${'$'}0"
        }
        "\\d*\\.\\d+([eE][\\-+]?\\d+)?".token("number.float")
        "0[xX][0-9a-fA-F]+".token("number.hex")
        "\\d+".token("number")
        "[;,.]".token("delimiter")
        "\"\"\"".action {
          token = "string"
          next = "@mlstring"
          nextEmbedded = "markdown"
          nextEmbedded = "markdown"
        }
        "\"([^\"\\\\]|\\\\.)*${'$'}".token("string.invalid")
        "\"".action {
          token = "string.quote"
          next = "@string"
          bracket = "@open"
        }
      }
      "mlstring" rules {
        "[^\"]+".token("string")
        "\"\"\"".action {
          token = "string"
          next = "@pop"
          nextEmbedded = "@pop"
          nextEmbedded = "@pop"
        }
      }
      string {
        "[^\\\\\"]+".token("string")
        "@escapes".token("string.escape")
        "\\\\.".token("string.escape.invalid")
        "\"".action {
          token = "string.quote"
          next = "@pop"
          bracket = "@close"
        }
      }
      whitespace {
        "[ \\t\\r\\n]+".token("")
        "#.*${'$'}".token("comment")
      }
    }
  }
}

