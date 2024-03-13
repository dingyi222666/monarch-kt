package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val ScalaLanguage: IMonarchLanguage = buildMonarchLanguage {
  tokenPostfix = ".scala"
  keywords("asInstanceOf", "catch", "class", "classOf", "def", "do", "else", "extends", "finally",
      "for", "foreach", "forSome", "if", "import", "isInstanceOf", "macro", "match", "new",
      "object", "package", "return", "throw", "trait", "try", "type", "until", "val", "var",
      "while", "with", "yield", "given", "enum", "then")
  "softKeywords" and listOf("as", "export", "extension", "end", "derives", "on")
  "constants" and listOf("true", "false", "null", "this", "super")
  "modifiers" and listOf("abstract", "final", "implicit", "lazy", "override", "private",
      "protected", "sealed")
  "softModifiers" and listOf("inline", "opaque", "open", "transparent", "using")
  "name" and "(?:[a-z_${'$'}][\\w${'$'}]*|`[^`]+`)"
  "type" and "(?:[A-Z][\\w${'$'}]*)"
  symbols("[=><!~?:&|+\\-*\\/^\\\\%@#]+")
  digits("\\d+(_+\\d+)*")
  hexdigits("[[0-9a-fA-F]+(_+[0-9a-fA-F]+)*")
  escapes("\\\\(?:[btnfr\\\\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
  "fstring_conv" and "[bBhHsScCdoxXeEfgGaAt]|[Tn](?:[HIklMSLNpzZsQ]|[BbhAaCYyjmde]|[RTrDFC])"
  tokenizer {
    root {
      "\\braw\"\"\"".action {
        token = "string.quote"
        next = "@rawstringt"
        bracket = "@open"
      }
      "\\braw\"".action {
        token = "string.quote"
        next = "@rawstring"
        bracket = "@open"
      }
      "\\bs\"\"\"".action {
        token = "string.quote"
        next = "@sstringt"
        bracket = "@open"
      }
      "\\bs\"".action {
        token = "string.quote"
        next = "@sstring"
        bracket = "@open"
      }
      "\\bf\"\"\"\"".action {
        token = "string.quote"
        next = "@fstringt"
        bracket = "@open"
      }
      "\\bf\"".action {
        token = "string.quote"
        next = "@fstring"
        bracket = "@open"
      }
      "\"\"\"".action {
        token = "string.quote"
        next = "@stringt"
        bracket = "@open"
      }
      "\"".action {
        token = "string.quote"
        next = "@string"
        bracket = "@open"
      }
      "(@digits)[eE]([\\-+]?(@digits))?[fFdD]?".action("number.float").state("@allowMethod")
      "(@digits)\\.(@digits)([eE][\\-+]?(@digits))?[fFdD]?".action("number.float").state("@allowMethod")
      "0[xX](@hexdigits)[Ll]?".action("number.hex").state("@allowMethod")
      "(@digits)[fFdD]".action("number.float").state("@allowMethod")
      "(@digits)[lL]?".action("number").state("@allowMethod")
      "\\b_\\*".token("key")
      "\\b(_)\\b".action("keyword").state("@allowMethod")
      "\\bimport\\b".action("keyword").state("@import")
      "\\b(case)([ \\t]+)(class)\\b".actionArray {
        token("keyword.modifier")
        token("white")
        token("keyword")
      }
      "\\bcase\\b".action("keyword").state("@case")
      "\\bva[lr]\\b".action("keyword").state("@vardef")
      "\\b(def)([ \\t]+)((?:unary_)?@symbols|@name(?:_=)|@name)".actionArray {
        token("keyword")
        token("white")
        token("identifier")
      }
      "@name(?=[ \\t]*:(?!:))".token("variable")
      "(\\.)(@name|@symbols)".actionArray {
        token("operator")
        action("@rematch") {
          next = "@allowMethod"
        }
      }
      "([{(])(\\s*)(@name(?=\\s*=>))".actionArray {
        token("@brackets")
        token("white")
        token("variable")
      }
      "@name".action {
        cases {
          "@keywords" and "keyword"
          "@softKeywords" and "keyword"
          "@modifiers" and "keyword.modifier"
          "@softModifiers" and "keyword.modifier"
          "@constants" and {
            token = "constant"
            next = "@allowMethod"
          }
          "@default" and {
            token = "identifier"
            next = "@allowMethod"
          }
        }
      }
      "@type".action("type").state("@allowMethod")
      include("@whitespace")
      "@[a-zA-Z_${'$'}][\\w${'$'}]*(?:\\.[a-zA-Z_${'$'}][\\w${'$'}]*)*".token("annotation")
      "[{(]".token("@brackets")
      "[})]".action("@brackets").state("@allowMethod")
      "\\[".token("operator.square")
      "](?!\\s*(?:va[rl]|def|type)\\b)".action("operator.square").state("@allowMethod")
      "]".token("operator.square")
      "([=-]>|<-|>:|<:|:>|<%)(?=[\\s\\w()[\\]{},\\.\"'`])".token("keyword")
      "@symbols".token("operator")
      "[;,\\.]".token("delimiter")
      "'[a-zA-Z${'$'}][\\w${'$'}]*(?!')".token("attribute.name")
      "'[^\\\\']'".action("string").state("@allowMethod")
      "(')(@escapes)(')".actionArray {
        token("string")
        token("string.escape")
        action("string") {
          next = "@allowMethod"
        }
      }
      "'".token("string.invalid")
    }
    "import" rules {
      ";".action("delimiter").state("@pop")
      "^|${'$'}".action("").state("@pop")
      "[ \\t]+".token("white")
      "[\\n\\r]+".action("white").state("@pop")
      "\\/\\*".action("comment").state("@comment")
      "@name|@type".token("type")
      "[(){}]".token("@brackets")
      "[[\\]]".token("operator.square")
      "[\\.,]".token("delimiter")
    }
    "allowMethod" rules {
      "^|${'$'}".action("").state("@pop")
      "[ \\t]+".token("white")
      "[\\n\\r]+".action("white").state("@pop")
      "\\/\\*".action("comment").state("@comment")
      "(?==>[\\s\\w([{])".action("keyword").state("@pop")
      "(@name|@symbols)(?=[ \\t]*[[({\"'`]|[ \\t]+(?:[+-]?\\.?\\d|\\w))".action {
        cases {
          "@keywords" and {
            token = "keyword"
            next = "@pop"
          }
          "->|<-|>:|<:|<%" and {
            token = "keyword"
            next = "@pop"
          }
          "@default" and {
            token = "@rematch"
            next = "@pop"
          }
        }
      }
      "".action("").state("@pop")
    }
    comment {
      "[^\\/*]+".token("comment")
      "\\/\\*".action("comment").state("@push")
      "\\*\\/".action("comment").state("@pop")
      "[\\/*]".token("comment")
    }
    "case" rules {
      "\\b_\\*".token("key")
      "\\b(_|true|false|null|this|super)\\b".action("keyword").state("@allowMethod")
      "\\bif\\b|=>".action("keyword").state("@pop")
      "`[^`]+`".action("identifier").state("@allowMethod")
      "@name".action("variable").state("@allowMethod")
      ":::?|\\||@(?![a-z_${'$'}])".token("keyword")
      include("@root")
    }
    "vardef" rules {
      "\\b_\\*".token("key")
      "\\b(_|true|false|null|this|super)\\b".token("keyword")
      "@name".token("variable")
      ":::?|\\||@(?![a-z_${'$'}])".token("keyword")
      "=|:(?!:)".action("operator").state("@pop")
      "${'$'}".action("white").state("@pop")
      include("@root")
    }
    string {
      "[^\\\\\"\\n\\r]+".token("string")
      "@escapes".token("string.escape")
      "\\\\.".token("string.escape.invalid")
      "\"".action {
        token = "string.quote"
        switchTo = "@allowMethod"
        bracket = "@close"
      }
    }
    "stringt" rules {
      "[^\\\\\"\\n\\r]+".token("string")
      "@escapes".token("string.escape")
      "\\\\.".token("string.escape.invalid")
      "\"(?=\"\"\")".token("string")
      "\"\"\"".action {
        token = "string.quote"
        switchTo = "@allowMethod"
        bracket = "@close"
      }
      "\"".token("string")
    }
    "fstring" rules {
      "@escapes".token("string.escape")
      "\"".action {
        token = "string.quote"
        switchTo = "@allowMethod"
        bracket = "@close"
      }
      "\\${'$'}\\${'$'}".token("string")
      "(\\${'$'})([a-z_]\\w*)".actionArray {
        token("operator")
        token("identifier")
      }
      "\\${'$'}\\{".action("operator").state("@interp")
      "%%".token("string")
      "(%)([\\-#+ 0,(])(\\d+|\\.\\d+|\\d+\\.\\d+)(@fstring_conv)".actionArray {
        token("metatag")
        token("keyword.modifier")
        token("number")
        token("metatag")
      }
      "(%)(\\d+|\\.\\d+|\\d+\\.\\d+)(@fstring_conv)".actionArray {
        token("metatag")
        token("number")
        token("metatag")
      }
      "(%)([\\-#+ 0,(])(@fstring_conv)".actionArray {
        token("metatag")
        token("keyword.modifier")
        token("metatag")
      }
      "(%)(@fstring_conv)".actionArray {
        token("metatag")
        token("metatag")
      }
      ".".token("string")
    }
    "fstringt" rules {
      "@escapes".token("string.escape")
      "\"(?=\"\"\")".token("string")
      "\"\"\"".action {
        token = "string.quote"
        switchTo = "@allowMethod"
        bracket = "@close"
      }
      "\\${'$'}\\${'$'}".token("string")
      "(\\${'$'})([a-z_]\\w*)".actionArray {
        token("operator")
        token("identifier")
      }
      "\\${'$'}\\{".action("operator").state("@interp")
      "%%".token("string")
      "(%)([\\-#+ 0,(])(\\d+|\\.\\d+|\\d+\\.\\d+)(@fstring_conv)".actionArray {
        token("metatag")
        token("keyword.modifier")
        token("number")
        token("metatag")
      }
      "(%)(\\d+|\\.\\d+|\\d+\\.\\d+)(@fstring_conv)".actionArray {
        token("metatag")
        token("number")
        token("metatag")
      }
      "(%)([\\-#+ 0,(])(@fstring_conv)".actionArray {
        token("metatag")
        token("keyword.modifier")
        token("metatag")
      }
      "(%)(@fstring_conv)".actionArray {
        token("metatag")
        token("metatag")
      }
      ".".token("string")
    }
    "sstring" rules {
      "@escapes".token("string.escape")
      "\"".action {
        token = "string.quote"
        switchTo = "@allowMethod"
        bracket = "@close"
      }
      "\\${'$'}\\${'$'}".token("string")
      "(\\${'$'})([a-z_]\\w*)".actionArray {
        token("operator")
        token("identifier")
      }
      "\\${'$'}\\{".action("operator").state("@interp")
      ".".token("string")
    }
    "sstringt" rules {
      "@escapes".token("string.escape")
      "\"(?=\"\"\")".token("string")
      "\"\"\"".action {
        token = "string.quote"
        switchTo = "@allowMethod"
        bracket = "@close"
      }
      "\\${'$'}\\${'$'}".token("string")
      "(\\${'$'})([a-z_]\\w*)".actionArray {
        token("operator")
        token("identifier")
      }
      "\\${'$'}\\{".action("operator").state("@interp")
      ".".token("string")
    }
    "interp" rules {
      "{".action("operator").state("@push")
      "}".action("operator").state("@pop")
      include("@root")
    }
    "rawstring" rules {
      "[^\"]".token("string")
      "\"".action {
        token = "string.quote"
        switchTo = "@allowMethod"
        bracket = "@close"
      }
    }
    "rawstringt" rules {
      "[^\"]".token("string")
      "\"(?=\"\"\")".token("string")
      "\"\"\"".action {
        token = "string.quote"
        switchTo = "@allowMethod"
        bracket = "@close"
      }
      "\"".token("string")
    }
    whitespace {
      "[ \\t\\r\\n]+".token("white")
      "\\/\\*".action("comment").state("@comment")
      "\\/\\/.*${'$'}".token("comment")
    }
  }
}

