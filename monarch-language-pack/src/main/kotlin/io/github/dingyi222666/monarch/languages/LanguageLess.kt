package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val LessLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".less"
    defaultToken = ""
    brackets {
      bracket("{","}","delimiter.curly")
      bracket("[","]","delimiter.bracket")
      bracket("(",")","delimiter.parenthesis")
      bracket("<",">","delimiter.angle")
    }
    "identifier" and
        "-?-?([a-zA-Z]|(\\\\(([0-9a-fA-F]{1,6}\\s?)|[^[0-9a-fA-F])))([\\w\\-]|(\\\\(([0-9a-fA-F]{1,6}\\s?)|[^[0-9a-fA-F])))*"
    "identifierPlus" and
        "-?-?([a-zA-Z:.]|(\\\\(([0-9a-fA-F]{1,6}\\s?)|[^[0-9a-fA-F])))([\\w\\-:.]|(\\\\(([0-9a-fA-F]{1,6}\\s?)|[^[0-9a-fA-F])))*"
    tokenizer {
      root {
        include("@nestedJSBegin")
        "[ \\t\\r\\n]+".token("")
        include("@comments")
        include("@keyword")
        include("@strings")
        include("@numbers")
        "[*_]?[a-zA-Z\\-\\s]+(?=:.*(;|(\\\\${'$'})))".action("attribute.name").state("@attribute")
        "url(\\-prefix)?\\(".action {
          token = "tag"
          next = "@urldeclaration"
        }
        "[{}()\\[\\]]".token("@brackets")
        "[,:;]".token("delimiter")
        "#@identifierPlus".token("tag.id")
        "&".token("tag")
        "\\.@identifierPlus(?=\\()".action("tag.class").state("@attribute")
        "\\.@identifierPlus".token("tag.class")
        "@identifierPlus".token("tag")
        include("@operators")
        "@(@identifier(?=[:,\\)]))".action("variable").state("@attribute")
        "@(@identifier)".token("variable")
        "@".action("key").state("@atRules")
      }
      "nestedJSBegin" rules {
        "``".token("delimiter.backtick")
        "`".action {
          token = "delimiter.backtick"
          next = "@nestedJSEnd"
          nextEmbedded = "text/javascript"
          nextEmbedded = "text/javascript"
        }
      }
      "nestedJSEnd" rules {
        "`".action {
          token = "delimiter.backtick"
          next = "@pop"
          nextEmbedded = "@pop"
          nextEmbedded = "@pop"
        }
      }
      "operators" rules {
        "[<>=\\+\\-\\*\\/\\^\\|\\~]".token("operator")
      }
      "keyword" rules {
        "(@[\\s]*import|![\\s]*important|true|false|when|iscolor|isnumber|isstring|iskeyword|isurl|ispixel|ispercentage|isem|hue|saturation|lightness|alpha|lighten|darken|saturate|desaturate|fadein|fadeout|fade|spin|mix|round|ceil|floor|percentage)\\b".token("keyword")
      }
      "urldeclaration" rules {
        include("@strings")
        """
            |[^)
            |]+
            """.trimMargin().token("string")
        "\\)".action {
          token = "tag"
          next = "@pop"
        }
      }
      "attribute" rules {
        include("@nestedJSBegin")
        include("@comments")
        include("@strings")
        include("@numbers")
        include("@keyword")
        "[a-zA-Z\\-]+(?=\\()".action("attribute.value").state("@attribute")
        ">".action("operator").state("@pop")
        "@identifier".token("attribute.value")
        include("@operators")
        "@(@identifier)".token("variable")
        "[)\\}]".action("@brackets").state("@pop")
        "[{}()\\[\\]>]".token("@brackets")
        "[;]".action("delimiter").state("@pop")
        "[,=:]".token("delimiter")
        "\\s".token("")
        ".".token("attribute.value")
      }
      comments {
        "\\/\\*".action("comment").state("@comment")
        "\\/\\/+.*".token("comment")
      }
      comment {
        "\\*\\/".action("comment").state("@pop")
        ".".token("comment")
      }
      "numbers" rules {
        "(\\d*\\.)?\\d+([eE][\\-+]?\\d+)?".action {
          token = "attribute.value.number"
          next = "@units"
        }
        "#[0-9a-fA-F_]+(?!\\w)".token("attribute.value.hex")
      }
      "units" rules {
        "(em|ex|ch|rem|fr|vmin|vmax|vw|vh|vm|cm|mm|in|px|pt|pc|deg|grad|rad|turn|s|ms|Hz|kHz|%)?".action("attribute.value.unit").state("@pop")
      }
      "strings" rules {
        "~?\"".action {
          token = "string.delimiter"
          next = "@stringsEndDoubleQuote"
        }
        "~?'".action {
          token = "string.delimiter"
          next = "@stringsEndQuote"
        }
      }
      "stringsEndDoubleQuote" rules {
        "\\\\\"".token("string")
        "\"".action {
          token = "string.delimiter"
          next = "@popall"
        }
        ".".token("string")
      }
      "stringsEndQuote" rules {
        "\\\\'".token("string")
        "'".action {
          token = "string.delimiter"
          next = "@popall"
        }
        ".".token("string")
      }
      "atRules" rules {
        include("@comments")
        include("@strings")
        "[()]".token("delimiter")
        "[\\{;]".action("delimiter").state("@pop")
        ".".token("key")
      }
    }
  }
}

