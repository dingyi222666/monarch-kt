package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val MarkdownLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".md"
    defaultToken = ""
    control("[\\\\`*_\\[\\]{}()#+\\-\\.!]")
    "noncontrol" and "[^\\\\`*_\\[\\]{}()#+\\-\\.!]"
    escapes("\\\\(?:@control)")
    "jsescapes" and "\\\\(?:[btnfr\\\\\"']|[0-7][0-7]?|[0-3][0-7]{2})"
    "empty" and listOf("area", "base", "basefont", "br", "col", "frame", "hr", "img", "input",
        "isindex", "link", "meta", "param")
    tokenizer {
      root {
        "^\\s*\\|".action("@rematch").state("@table_header")
        "^(\\s{0,3})(#+)((?:[^\\\\#]|@escapes)+)((?:#+)?)".actionArray {
          token("white")
          token("keyword")
          token("keyword")
          token("keyword")
        }
        "^\\s*(=+|\\-+)\\s*${'$'}".token("keyword")
        "^\\s*((\\*[ ]?)+)\\s*${'$'}".token("meta.separator")
        "^\\s*>+".token("comment")
        "^\\s*([\\*\\-+:]|\\d+\\.)\\s".token("keyword")
        "^(\\t|[ ]{4})[^ ].*${'$'}".token("string")
        "^\\s*~~~\\s*((?:\\w|[\\/\\-#])+)?\\s*${'$'}".action {
          token = "string"
          next = "@codeblock"
        }
        "^\\s*```\\s*((?:\\w|[\\/\\-#])+).*${'$'}".action {
          token = "string"
          next = "@codeblockgh"
          nextEmbedded = "${'$'}1"
          nextEmbedded = "${'$'}1"
        }
        "^\\s*```\\s*${'$'}".action {
          token = "string"
          next = "@codeblock"
        }
        include("@linecontent")
      }
      "table_header" rules {
        include("@table_common")
        "[^\\|]+".token("keyword.table.header")
      }
      "table_body" rules {
        include("@table_common")
        include("@linecontent")
      }
      "table_common" rules {
        "\\s*[\\-:]+\\s*".action {
          token = "keyword"
          switchTo = "table_body"
        }
        "^\\s*\\|".token("keyword.table.left")
        "^\\s*[^\\|]".action("@rematch").state("@pop")
        "^\\s*${'$'}".action("@rematch").state("@pop")
        "\\|".action {
          cases {
            "@eos" and "keyword.table.right"
            "@default" and "keyword.table.middle"
          }
        }
      }
      "codeblock" rules {
        "^\\s*~~~\\s*${'$'}".action {
          token = "string"
          next = "@pop"
        }
        "^\\s*```\\s*${'$'}".action {
          token = "string"
          next = "@pop"
        }
        ".*${'$'}".token("variable.source")
      }
      "codeblockgh" rules {
        "```\\s*${'$'}".action {
          token = "string"
          next = "@pop"
          nextEmbedded = "@pop"
          nextEmbedded = "@pop"
        }
        "[^`]+".token("variable.source")
      }
      "linecontent" rules {
        "&\\w+;".token("string.escape")
        "@escapes".token("escape")
        "\\b__([^\\\\_]|@escapes|_(?!_))+__\\b".token("strong")
        "\\*\\*([^\\\\*]|@escapes|\\*(?!\\*))+\\*\\*".token("strong")
        "\\b_[^_]+_\\b".token("emphasis")
        "\\*([^\\\\*]|@escapes)+\\*".token("emphasis")
        "`([^\\\\`]|@escapes)+`".token("variable")
        "\\{+[^}]+\\}+".token("string.target")
        "(!?\\[)((?:[^\\]\\\\]|@escapes)*)(\\]\\([^\\)]+\\))".actionArray {
          token("string.link")
          token("")
          token("string.link")
        }
        "(!?\\[)((?:[^\\]\\\\]|@escapes)*)(\\])".token("string.link")
        include("html")
      }
      "html" rules {
        "<(\\w+)\\/>".token("tag")
        "<(\\w+)(\\-|\\w)*".action {
          cases {
            "@empty" and {
              token = "tag"
              next = "@tag.${'$'}1"
            }
            "@default" and {
              token = "tag"
              next = "@tag.${'$'}1"
            }
          }
        }
        "<\\/(\\w+)(\\-|\\w)*\\s*>".action {
          token = "tag"
        }
        "<!--".action("comment").state("@comment")
      }
      comment {
        "[^<\\-]+".token("comment.content")
        "-->".action("comment").state("@pop")
        "<!--".token("comment.content.invalid")
        "[<\\-]".token("comment.content")
      }
      "tag" rules {
        "[ \\t\\r\\n]+".token("white")
        "(type)(\\s*=\\s*)(\")([^\"]+)(\")".actionArray {
          token("attribute.name.html")
          token("delimiter.html")
          token("string.html")
          action("string.html") {
            switchTo = "@tag.${'$'}S2.${'$'}4"
          }
          token("string.html")
        }
        "(type)(\\s*=\\s*)(')([^']+)(')".actionArray {
          token("attribute.name.html")
          token("delimiter.html")
          token("string.html")
          action("string.html") {
            switchTo = "@tag.${'$'}S2.${'$'}4"
          }
          token("string.html")
        }
        "(\\w+)(\\s*=\\s*)(\"[^\"]*\"|'[^']*')".actionArray {
          token("attribute.name.html")
          token("delimiter.html")
          token("string.html")
        }
        "\\w+".token("attribute.name.html")
        "\\/>".action("tag").state("@pop")
        ">".action {
          cases {
            "${'$'}S2==style" and {
              token = "tag"
              nextEmbedded = "text/css"
              switchTo = "embeddedStyle"
              nextEmbedded = "text/css"
            }
            "${'$'}S2==script" and {
              cases {
                "${'$'}S3" and {
                  token = "tag"
                  nextEmbedded = "${'$'}S3"
                  switchTo = "embeddedScript"
                  nextEmbedded = "${'$'}S3"
                }
                "@default" and {
                  token = "tag"
                  nextEmbedded = "text/javascript"
                  switchTo = "embeddedScript"
                  nextEmbedded = "text/javascript"
                }
              }
            }
            "@default" and {
              token = "tag"
              next = "@pop"
            }
          }
        }
      }
      "embeddedStyle" rules {
        "[^<]+".token("")
        "<\\/style\\s*>".action {
          token = "@rematch"
          next = "@pop"
          nextEmbedded = "@pop"
          nextEmbedded = "@pop"
        }
        "<".token("")
      }
      "embeddedScript" rules {
        "[^<]+".token("")
        "<\\/script\\s*>".action {
          token = "@rematch"
          next = "@pop"
          nextEmbedded = "@pop"
          nextEmbedded = "@pop"
        }
        "<".token("")
      }
    }
  }
}

