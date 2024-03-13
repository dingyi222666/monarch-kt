package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val CssLanguage: IMonarchLanguage = buildMonarchLanguage {
  tokenPostfix = ".css"
  defaultToken = ""
  brackets {
    bracket("{","}","delimiter.bracket")
    bracket("[","]","delimiter.bracket")
    bracket("(",")","delimiter.parenthesis")
    bracket("<",">","delimiter.angle")
  }
  "ws" and """
      |[ 	
      |]*
      """.trimMargin()
  "identifier" and
      "-?-?([a-zA-Z]|(\\\\(([0-9a-fA-F]{1,6}\\s?)|[^[0-9a-fA-F])))([\\w\\-]|(\\\\(([0-9a-fA-F]{1,6}\\s?)|[^[0-9a-fA-F])))*"
  tokenizer {
    root {
      include("@selector")
    }
    "selector" rules {
      include("@comments")
      include("@import")
      include("@strings")
      "[@](keyframes|-webkit-keyframes|-moz-keyframes|-o-keyframes)".action {
        token = "keyword"
        next = "@keyframedeclaration"
      }
      "[@](page|content|font-face|-moz-document)".action {
        token = "keyword"
      }
      "[@](charset|namespace)".action {
        token = "keyword"
        next = "@declarationbody"
      }
      "(url-prefix)(\\()".actionArray {
        token("attribute.value")
        action("delimiter.parenthesis") {
          next = "@urldeclaration"
        }
      }
      "(url)(\\()".actionArray {
        token("attribute.value")
        action("delimiter.parenthesis") {
          next = "@urldeclaration"
        }
      }
      include("@selectorname")
      "[\\*]".token("tag")
      "[>\\+,]".token("delimiter")
      "\\[".action {
        token = "delimiter.bracket"
        next = "@selectorattribute"
      }
      "{".action {
        token = "delimiter.bracket"
        next = "@selectorbody"
      }
    }
    "selectorbody" rules {
      include("@comments")
      "[*_]?@identifier@ws:(?=(\\s|\\d|[^{;}]*[;}]))".action("attribute.name").state("@rulevalue")
      "}".action {
        token = "delimiter.bracket"
        next = "@pop"
      }
    }
    "selectorname" rules {
      "(\\.|#(?=[^{])|%|(@identifier)|:)+".token("tag")
    }
    "selectorattribute" rules {
      include("@term")
      "]".action {
        token = "delimiter.bracket"
        next = "@pop"
      }
    }
    "term" rules {
      include("@comments")
      "(url-prefix)(\\()".actionArray {
        token("attribute.value")
        action("delimiter.parenthesis") {
          next = "@urldeclaration"
        }
      }
      "(url)(\\()".actionArray {
        token("attribute.value")
        action("delimiter.parenthesis") {
          next = "@urldeclaration"
        }
      }
      include("@functioninvocation")
      include("@numbers")
      include("@name")
      include("@strings")
      "([<>=\\+\\-\\*\\/\\^\\|\\~,])".token("delimiter")
      ",".token("delimiter")
    }
    "rulevalue" rules {
      include("@comments")
      include("@strings")
      include("@term")
      "!important".token("keyword")
      ";".action("delimiter").state("@pop")
      "(?=})".action {
        token = ""
        next = "@pop"
      }
    }
    "warndebug" rules {
      "[@](warn|debug)".action {
        token = "keyword"
        next = "@declarationbody"
      }
    }
    "import" rules {
      "[@](import)".action {
        token = "keyword"
        next = "@declarationbody"
      }
    }
    "urldeclaration" rules {
      include("@strings")
      """
          |[^)
          |]+
          """.trimMargin().token("string")
      "\\)".action {
        token = "delimiter.parenthesis"
        next = "@pop"
      }
    }
    "parenthizedterm" rules {
      include("@term")
      "\\)".action {
        token = "delimiter.parenthesis"
        next = "@pop"
      }
    }
    "declarationbody" rules {
      include("@term")
      ";".action("delimiter").state("@pop")
      "(?=})".action {
        token = ""
        next = "@pop"
      }
    }
    comments {
      "\\/\\*".action("comment").state("@comment")
      "\\/\\/+.*".token("comment")
    }
    comment {
      "\\*\\/".action("comment").state("@pop")
      "[^*/]+".token("comment")
      ".".token("comment")
    }
    "name" rules {
      "@identifier".token("attribute.value")
    }
    "numbers" rules {
      "-?(\\d*\\.)?\\d+([eE][\\-+]?\\d+)?".action {
        token = "attribute.value.number"
        next = "@units"
      }
      "#[0-9a-fA-F_]+(?!\\w)".token("attribute.value.hex")
    }
    "units" rules {
      "(em|ex|ch|rem|fr|vmin|vmax|vw|vh|vm|cm|mm|in|px|pt|pc|deg|grad|rad|turn|s|ms|Hz|kHz|%)?".action("attribute.value.unit").state("@pop")
    }
    "keyframedeclaration" rules {
      "@identifier".token("attribute.value")
      "{".action {
        token = "delimiter.bracket"
        switchTo = "@keyframebody"
      }
    }
    "keyframebody" rules {
      include("@term")
      "{".action {
        token = "delimiter.bracket"
        next = "@selectorbody"
      }
      "}".action {
        token = "delimiter.bracket"
        next = "@pop"
      }
    }
    "functioninvocation" rules {
      "@identifier\\(".action {
        token = "attribute.value"
        next = "@functionarguments"
      }
    }
    "functionarguments" rules {
      "\\${'$'}@identifier@ws:".token("attribute.name")
      "[,]".token("delimiter")
      include("@term")
      "\\)".action {
        token = "attribute.value"
        next = "@pop"
      }
    }
    "strings" rules {
      "~?\"".action {
        token = "string"
        next = "@stringenddoublequote"
      }
      "~?'".action {
        token = "string"
        next = "@stringendquote"
      }
    }
    "stringenddoublequote" rules {
      "\\\\.".token("string")
      "\"".action {
        token = "string"
        next = "@pop"
      }
      "[^\\\\\"]+".token("string")
      ".".token("string")
    }
    "stringendquote" rules {
      "\\\\.".token("string")
      "'".action {
        token = "string"
        next = "@pop"
      }
      "[^\\\\']+".token("string")
      ".".token("string")
    }
  }
}

