package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val ScssLanguage: IMonarchLanguage = buildMonarchLanguage {
  tokenPostfix = ".scss"
  defaultToken = ""
  brackets {
    bracket("{","}","delimiter.curly")
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
      include("@variabledeclaration")
      include("@warndebug")
      "[@](include)".action {
        token = "keyword"
        next = "@includedeclaration"
      }
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
      "[@](function)".action {
        token = "keyword"
        next = "@functiondeclaration"
      }
      "[@](mixin)".action {
        token = "keyword"
        next = "@mixindeclaration"
      }
      "url(\\-prefix)?\\(".action {
        token = "meta"
        next = "@urldeclaration"
      }
      include("@controlstatement")
      include("@selectorname")
      "[&\\*]".token("tag")
      "[>\\+,]".token("delimiter")
      "\\[".action {
        token = "delimiter.bracket"
        next = "@selectorattribute"
      }
      "{".action {
        token = "delimiter.curly"
        next = "@selectorbody"
      }
    }
    "selectorbody" rules {
      "[*_]?@identifier@ws:(?=(\\s|\\d|[^{;}]*[;}]))".action("attribute.name").state("@rulevalue")
      include("@selector")
      "[@](extend)".action {
        token = "keyword"
        next = "@extendbody"
      }
      "[@](return)".action {
        token = "keyword"
        next = "@declarationbody"
      }
      "}".action {
        token = "delimiter.curly"
        next = "@pop"
      }
    }
    "selectorname" rules {
      "#{".action {
        token = "meta"
        next = "@variableinterpolation"
      }
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
      "url(\\-prefix)?\\(".action {
        token = "meta"
        next = "@urldeclaration"
      }
      include("@functioninvocation")
      include("@numbers")
      include("@strings")
      include("@variablereference")
      "(and\\b|or\\b|not\\b)".token("operator")
      include("@name")
      "([<>=\\+\\-\\*\\/\\^\\|\\~,])".token("operator")
      ",".token("delimiter")
      "!default".token("literal")
      "\\(".action {
        token = "delimiter.parenthesis"
        next = "@parenthizedterm"
      }
    }
    "rulevalue" rules {
      include("@term")
      "!important".token("literal")
      ";".action("delimiter").state("@pop")
      "{".action {
        token = "delimiter.curly"
        switchTo = "@nestedproperty"
      }
      "(?=})".action {
        token = ""
        next = "@pop"
      }
    }
    "nestedproperty" rules {
      "[*_]?@identifier@ws:".action("attribute.name").state("@rulevalue")
      include("@comments")
      "}".action {
        token = "delimiter.curly"
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
    "variabledeclaration" rules {
      "\\${'$'}@identifier@ws:".action("variable.decl").state("@declarationbody")
    }
    "urldeclaration" rules {
      include("@strings")
      """
          |[^)
          |]+
          """.trimMargin().token("string")
      "\\)".action {
        token = "meta"
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
    "extendbody" rules {
      include("@selectorname")
      "!optional".token("literal")
      ";".action("delimiter").state("@pop")
      "(?=})".action {
        token = ""
        next = "@pop"
      }
    }
    "variablereference" rules {
      "\\${'$'}@identifier".token("variable.ref")
      "\\.\\.\\.".token("operator")
      "#{".action {
        token = "meta"
        next = "@variableinterpolation"
      }
    }
    "variableinterpolation" rules {
      include("@variablereference")
      "}".action {
        token = "meta"
        next = "@pop"
      }
    }
    comments {
      "\\/\\*".action("comment").state("@comment")
      "\\/\\/+.*".token("comment")
    }
    comment {
      "\\*\\/".action("comment").state("@pop")
      ".".token("comment")
    }
    "name" rules {
      "@identifier".token("attribute.value")
    }
    "numbers" rules {
      "(\\d*\\.)?\\d+([eE][\\-+]?\\d+)?".action {
        token = "number"
        next = "@units"
      }
      "#[0-9a-fA-F_]+(?!\\w)".token("number.hex")
    }
    "units" rules {
      "(em|ex|ch|rem|fr|vmin|vmax|vw|vh|vm|cm|mm|in|px|pt|pc|deg|grad|rad|turn|s|ms|Hz|kHz|%)?".action("number").state("@pop")
    }
    "functiondeclaration" rules {
      "@identifier@ws\\(".action {
        token = "meta"
        next = "@parameterdeclaration"
      }
      "{".action {
        token = "delimiter.curly"
        switchTo = "@functionbody"
      }
    }
    "mixindeclaration" rules {
      "@identifier@ws\\(".action {
        token = "meta"
        next = "@parameterdeclaration"
      }
      "@identifier".token("meta")
      "{".action {
        token = "delimiter.curly"
        switchTo = "@selectorbody"
      }
    }
    "parameterdeclaration" rules {
      "\\${'$'}@identifier@ws:".token("variable.decl")
      "\\.\\.\\.".token("operator")
      ",".token("delimiter")
      include("@term")
      "\\)".action {
        token = "meta"
        next = "@pop"
      }
    }
    "includedeclaration" rules {
      include("@functioninvocation")
      "@identifier".token("meta")
      ";".action("delimiter").state("@pop")
      "(?=})".action {
        token = ""
        next = "@pop"
      }
      "{".action {
        token = "delimiter.curly"
        switchTo = "@selectorbody"
      }
    }
    "keyframedeclaration" rules {
      "@identifier".token("meta")
      "{".action {
        token = "delimiter.curly"
        switchTo = "@keyframebody"
      }
    }
    "keyframebody" rules {
      include("@term")
      "{".action {
        token = "delimiter.curly"
        next = "@selectorbody"
      }
      "}".action {
        token = "delimiter.curly"
        next = "@pop"
      }
    }
    "controlstatement" rules {
      "[@](if|else|for|while|each|media)".action {
        token = "keyword.flow"
        next = "@controlstatementdeclaration"
      }
    }
    "controlstatementdeclaration" rules {
      "(in|from|through|if|to)\\b".action {
        token = "keyword.flow"
      }
      include("@term")
      "{".action {
        token = "delimiter.curly"
        switchTo = "@selectorbody"
      }
    }
    "functionbody" rules {
      "[@](return)".action {
        token = "keyword"
      }
      include("@variabledeclaration")
      include("@term")
      include("@controlstatement")
      ";".token("delimiter")
      "}".action {
        token = "delimiter.curly"
        next = "@pop"
      }
    }
    "functioninvocation" rules {
      "@identifier\\(".action {
        token = "meta"
        next = "@functionarguments"
      }
    }
    "functionarguments" rules {
      "\\${'$'}@identifier@ws:".token("attribute.name")
      "[,]".token("delimiter")
      include("@term")
      "\\)".action {
        token = "meta"
        next = "@pop"
      }
    }
    "strings" rules {
      "~?\"".action {
        token = "string.delimiter"
        next = "@stringenddoublequote"
      }
      "~?'".action {
        token = "string.delimiter"
        next = "@stringendquote"
      }
    }
    "stringenddoublequote" rules {
      "\\\\.".token("string")
      "\"".action {
        token = "string.delimiter"
        next = "@pop"
      }
      ".".token("string")
    }
    "stringendquote" rules {
      "\\\\.".token("string")
      "'".action {
        token = "string.delimiter"
        next = "@pop"
      }
      ".".token("string")
    }
  }
}

