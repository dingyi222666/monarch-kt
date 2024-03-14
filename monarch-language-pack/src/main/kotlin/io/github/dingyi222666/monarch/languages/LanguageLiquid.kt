package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val LiquidLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ""
    defaultToken = ""
    "builtinTags" and listOf("if", "else", "elseif", "endif", "render", "assign", "capture",
        "endcapture", "case", "endcase", "comment", "endcomment", "cycle", "decrement", "for",
        "endfor", "include", "increment", "layout", "raw", "endraw", "render", "tablerow",
        "endtablerow", "unless", "endunless")
    "builtinFilters" and listOf("abs", "append", "at_least", "at_most", "capitalize", "ceil",
        "compact", "date", "default", "divided_by", "downcase", "escape", "escape_once", "first",
        "floor", "join", "json", "last", "lstrip", "map", "minus", "modulo", "newline_to_br",
        "plus", "prepend", "remove", "remove_first", "replace", "replace_first", "reverse", "round",
        "rstrip", "size", "slice", "sort", "sort_natural", "split", "strip", "strip_html",
        "strip_newlines", "times", "truncate", "truncatewords", "uniq", "upcase", "url_decode",
        "url_encode", "where")
    "constants" and listOf("true", "false")
    operators("==", "!=", ">", "<", ">=", "<=")
    "symbol" and "[=><!]+"
    "identifier" and "[a-zA-Z_][\\w]*"
    tokenizer {
      root {
        "\\{\\%\\s*comment\\s*\\%\\}".action("comment.start.liquid").state("@comment")
        "\\{\\{".action {
          token = "@rematch"
          switchTo = "@liquidState.root"
        }
        "\\{\\%".action {
          token = "@rematch"
          switchTo = "@liquidState.root"
        }
        "(<)([\\w\\-]+)(\\/>)".actionArray {
          token("delimiter.html")
          token("tag.html")
          token("delimiter.html")
        }
        "(<)([:\\w]+)".actionArray {
          token("delimiter.html")
          action("tag.html") {
            next = "@otherTag"
          }
        }
        "(<\\/)([\\w\\-]+)".actionArray {
          token("delimiter.html")
          action("tag.html") {
            next = "@otherTag"
          }
        }
        "<".token("delimiter.html")
        "\\{".token("delimiter.html")
      }
      comment {
        "\\{\\%\\s*endcomment\\s*\\%\\}".action("comment.end.liquid").state("@pop")
        ".".token("comment.content.liquid")
      }
      "otherTag" rules {
        "\\{\\{".action {
          token = "@rematch"
          switchTo = "@liquidState.otherTag"
        }
        "\\{\\%".action {
          token = "@rematch"
          switchTo = "@liquidState.otherTag"
        }
        "\\/?>".action("delimiter.html").state("@pop")
        "\"([^\"]*)\"".token("attribute.value")
        "'([^']*)'".token("attribute.value")
        "[\\w\\-]+".token("attribute.name")
        "=".token("delimiter")
      }
      "liquidState" rules {
        "\\{\\{".token("delimiter.output.liquid")
        "\\}\\}".action {
          token = "delimiter.output.liquid"
          switchTo = "@${'$'}S2.${'$'}S3"
        }
        "\\{\\%".token("delimiter.tag.liquid")
        "raw\\s*\\%\\}".action("delimiter.tag.liquid").state("@liquidRaw")
        "\\%\\}".action {
          token = "delimiter.tag.liquid"
          switchTo = "@${'$'}S2.${'$'}S3"
        }
        include("liquidRoot")
      }
      "liquidRaw" rules {
        "\\{\\%".token("delimiter.tag.liquid")
        "\\%\\}".action {
          token = "delimiter.tag.liquid"
          next = "@root"
        }
      }
      "liquidRoot" rules {
        "\\d+(\\.\\d+)?".token("number.liquid")
        "\"[^\"]*\"".token("string.liquid")
        "'[^']*'".token("string.liquid")
        "@symbol".action {
          cases {
            "@operators" and "operator.liquid"
            "@default" and ""
          }
        }
        "@identifier".action {
          cases {
            "@constants" and "keyword.liquid"
            "@builtinFilters" and "predefined.liquid"
            "@builtinTags" and "predefined.liquid"
            "@default" and "variable.liquid"
          }
        }
        "[^}|%]".token("variable.liquid")
      }
    }
  }
}

