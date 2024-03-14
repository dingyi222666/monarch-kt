package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val MdxLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".mdx"
    defaultToken = ""
    control("[!#()*+.[\\\\\\]_`{}\\-]")
    escapes("\\\\@control")
    tokenizer {
      root {
        "^---${'$'}".action {
          token = "meta.content"
          next = "@frontmatter"
          nextEmbedded = "yaml"
          nextEmbedded = "yaml"
        }
        "^\\s*import".action {
          token = "keyword"
          next = "@import"
          nextEmbedded = "js"
          nextEmbedded = "js"
        }
        "^\\s*export".action {
          token = "keyword"
          next = "@export"
          nextEmbedded = "js"
          nextEmbedded = "js"
        }
        "<\\w+".action {
          token = "type.identifier"
          next = "@jsx"
        }
        "<\\/?\\w+>".token("type.identifier")
        "^(\\s*)(>*\\s*)(#{1,6}\\s)".actionArray {
          action("white") {
          }
          action("comment") {
          }
          action("keyword") {
            next = "@header"
          }
        }
        "^(\\s*)(>*\\s*)([*+-])(\\s+)".actionArray {
          token("white")
          token("comment")
          token("keyword")
          token("white")
        }
        "^(\\s*)(>*\\s*)(\\d{1,9}\\.)(\\s+)".actionArray {
          token("white")
          token("comment")
          token("number")
          token("white")
        }
        "^(\\s*)(>*\\s*)(\\d{1,9}\\.)(\\s+)".actionArray {
          token("white")
          token("comment")
          token("number")
          token("white")
        }
        "^(\\s*)(>*\\s*)(-{3,}|\\*{3,}|_{3,})${'$'}".actionArray {
          token("white")
          token("comment")
          token("keyword")
        }
        "`{3,}(\\s.*)?${'$'}".action {
          token = "string"
          next = "@codeblock_backtick"
        }
        "~{3,}(\\s.*)?${'$'}".action {
          token = "string"
          next = "@codeblock_tilde"
        }
        "`{3,}(\\S+).*${'$'}".action {
          token = "string"
          next = "@codeblock_highlight_backtick"
          nextEmbedded = "${'$'}1"
          nextEmbedded = "${'$'}1"
        }
        "~{3,}(\\S+).*${'$'}".action {
          token = "string"
          next = "@codeblock_highlight_tilde"
          nextEmbedded = "${'$'}1"
          nextEmbedded = "${'$'}1"
        }
        "^(\\s*)(-{4,})${'$'}".actionArray {
          token("white")
          token("comment")
        }
        "^(\\s*)(>+)".actionArray {
          token("white")
          token("comment")
        }
        include("content")
      }
      "content" rules {
        "(\\[)(.+)(]\\()(.+)(\\s+\".*\")(\\))".actionArray {
          token("")
          token("string.link")
          token("")
          token("type.identifier")
          token("string.link")
          token("")
        }
        "(\\[)(.+)(]\\()(.+)(\\))".actionArray {
          token("")
          token("type.identifier")
          token("")
          token("string.link")
          token("")
        }
        "(\\[)(.+)(]\\[)(.+)(])".actionArray {
          token("")
          token("type.identifier")
          token("")
          token("type.identifier")
          token("")
        }
        "(\\[)(.+)(]:\\s+)(\\S*)".actionArray {
          token("")
          token("type.identifier")
          token("")
          token("string.link")
        }
        "(\\[)(.+)(])".actionArray {
          token("")
          token("type.identifier")
          token("")
        }
        "`.*`".token("variable.source")
        "_".action {
          token = "emphasis"
          next = "@emphasis_underscore"
        }
        "\\*(?!\\*)".action {
          token = "emphasis"
          next = "@emphasis_asterisk"
        }
        "\\*\\*".action {
          token = "strong"
          next = "@strong"
        }
        "{".action {
          token = "delimiter.bracket"
          next = "@expression"
          nextEmbedded = "js"
          nextEmbedded = "js"
        }
      }
      "import" rules {
        "'\\s*(;|${'$'})".action {
          token = "string"
          next = "@pop"
          nextEmbedded = "@pop"
          nextEmbedded = "@pop"
        }
      }
      "expression" rules {
        "{".action {
          token = "delimiter.bracket"
          next = "@expression"
        }
        "}".action {
          token = "delimiter.bracket"
          next = "@pop"
          nextEmbedded = "@pop"
          nextEmbedded = "@pop"
        }
      }
      "export" rules {
        "^\\s*${'$'}".action {
          token = "delimiter.bracket"
          next = "@pop"
          nextEmbedded = "@pop"
          nextEmbedded = "@pop"
        }
      }
      "jsx" rules {
        "\\s+".token("")
        "(\\w+)(=)(\"(?:[^\"\\\\]|\\\\.)*\")".actionArray {
          token("attribute.name")
          token("operator")
          token("string")
        }
        "(\\w+)(=)('(?:[^'\\\\]|\\\\.)*')".actionArray {
          token("attribute.name")
          token("operator")
          token("string")
        }
        "(\\w+(?=\\s|>|={|${'$'}))".actionArray {
          token("attribute.name")
        }
        "={".action {
          token = "delimiter.bracket"
          next = "@expression"
          nextEmbedded = "js"
          nextEmbedded = "js"
        }
        ">".action {
          token = "type.identifier"
          next = "@pop"
        }
      }
      "header" rules {
        ".${'$'}".action {
          token = "keyword"
          next = "@pop"
        }
        include("content")
        ".".action {
          token = "keyword"
        }
      }
      "strong" rules {
        "\\*\\*".action {
          token = "strong"
          next = "@pop"
        }
        include("content")
        ".".action {
          token = "strong"
        }
      }
      "emphasis_underscore" rules {
        "_".action {
          token = "emphasis"
          next = "@pop"
        }
        include("content")
        ".".action {
          token = "emphasis"
        }
      }
      "emphasis_asterisk" rules {
        "\\*(?!\\*)".action {
          token = "emphasis"
          next = "@pop"
        }
        include("content")
        ".".action {
          token = "emphasis"
        }
      }
      "frontmatter" rules {
        "^---${'$'}".action {
          token = "meta.content"
          next = "@pop"
          nextEmbedded = "@pop"
          nextEmbedded = "@pop"
        }
      }
      "codeblock_highlight_backtick" rules {
        "\\s*`{3,}\\s*${'$'}".action {
          token = "string"
          next = "@pop"
          nextEmbedded = "@pop"
          nextEmbedded = "@pop"
        }
        ".*${'$'}".token("variable.source")
      }
      "codeblock_highlight_tilde" rules {
        "\\s*~{3,}\\s*${'$'}".action {
          token = "string"
          next = "@pop"
          nextEmbedded = "@pop"
          nextEmbedded = "@pop"
        }
        ".*${'$'}".token("variable.source")
      }
      "codeblock_backtick" rules {
        "\\s*`{3,}\\s*${'$'}".action {
          token = "string"
          next = "@pop"
        }
        ".*${'$'}".token("variable.source")
      }
      "codeblock_tilde" rules {
        "\\s*~{3,}\\s*${'$'}".action {
          token = "string"
          next = "@pop"
        }
        ".*${'$'}".token("variable.source")
      }
    }
  }
}

