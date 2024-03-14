package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val XmlLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".xml"
    ignoreCase = true
    defaultToken = ""
    "qualifiedName" and "(?:[\\w\\.\\-]+:)?[\\w\\.\\-]+"
    tokenizer {
      root {
        "[^<&]+".token("")
        include("@whitespace")
        "(<)(@qualifiedName)".actionArray {
          action("delimiter") {
          }
          action("tag") {
            next = "@tag"
          }
        }
        "(<\\/)(@qualifiedName)(\\s*)(>)".actionArray {
          action("delimiter") {
          }
          action("tag") {
          }
          token("")
          action("delimiter") {
          }
        }
        "(<\\?)(@qualifiedName)".actionArray {
          action("delimiter") {
          }
          action("metatag") {
            next = "@tag"
          }
        }
        "(<\\!)(@qualifiedName)".actionArray {
          action("delimiter") {
          }
          action("metatag") {
            next = "@tag"
          }
        }
        "<\\!\\[CDATA\\[".action {
          token = "delimiter.cdata"
          next = "@cdata"
        }
        "&\\w+;".token("string.escape")
      }
      "cdata" rules {
        "[^\\]]+".token("")
        "\\]\\]>".action {
          token = "delimiter.cdata"
          next = "@pop"
        }
        "\\]".token("")
      }
      "tag" rules {
        "[ \\t\\r\\n]+".token("")
        "(@qualifiedName)(\\s*=\\s*)(\"[^\"]*\"|'[^']*')".actionArray {
          token("attribute.name")
          token("")
          token("attribute.value")
        }
        "(@qualifiedName)(\\s*=\\s*)(\"[^\">?\\/]*|'[^'>?\\/]*)(?=[\\?\\/]\\>)".actionArray {
          token("attribute.name")
          token("")
          token("attribute.value")
        }
        "(@qualifiedName)(\\s*=\\s*)(\"[^\">]*|'[^'>]*)".actionArray {
          token("attribute.name")
          token("")
          token("attribute.value")
        }
        "@qualifiedName".token("attribute.name")
        "\\?>".action {
          token = "delimiter"
          next = "@pop"
        }
        "(\\/)(>)".actionArray {
          action("tag") {
          }
          action("delimiter") {
            next = "@pop"
          }
        }
        ">".action {
          token = "delimiter"
          next = "@pop"
        }
      }
      whitespace {
        "[ \\t\\r\\n]+".token("")
        "<!--".action {
          token = "comment"
          next = "@comment"
        }
      }
      comment {
        "[^<\\-]+".token("comment.content")
        "-->".action {
          token = "comment"
          next = "@pop"
        }
        "<!--".token("comment.content.invalid")
        "[<\\-]".token("comment.content")
      }
    }
  }
}

