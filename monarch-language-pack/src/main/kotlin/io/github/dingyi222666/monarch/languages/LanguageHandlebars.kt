package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val HandlebarsLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ""
    defaultToken = ""
    tokenizer {
      root {
        "\\{\\{!--".action("comment.block.start.handlebars").state("@commentBlock")
        "\\{\\{!".action("comment.start.handlebars").state("@comment")
        "\\{\\{".action {
          token = "@rematch"
          switchTo = "@handlebarsInSimpleState.root"
        }
        "<!DOCTYPE".action("metatag.html").state("@doctype")
        "<!--".action("comment.html").state("@commentHtml")
        "(<)(\\w+)(\\/>)".actionArray {
          token("delimiter.html")
          token("tag.html")
          token("delimiter.html")
        }
        "(<)(script)".actionArray {
          token("delimiter.html")
          action("tag.html") {
            next = "@script"
          }
        }
        "(<)(style)".actionArray {
          token("delimiter.html")
          action("tag.html") {
            next = "@style"
          }
        }
        "(<)([:\\w]+)".actionArray {
          token("delimiter.html")
          action("tag.html") {
            next = "@otherTag"
          }
        }
        "(<\\/)(\\w+)".actionArray {
          token("delimiter.html")
          action("tag.html") {
            next = "@otherTag"
          }
        }
        "<".token("delimiter.html")
        "\\{".token("delimiter.html")
      }
      "doctype" rules {
        "\\{\\{".action {
          token = "@rematch"
          switchTo = "@handlebarsInSimpleState.comment"
        }
        "[^>]+".token("metatag.content.html")
        ">".action("metatag.html").state("@pop")
      }
      comment {
        "\\}\\}".action("comment.end.handlebars").state("@pop")
        ".".token("comment.content.handlebars")
      }
      "commentBlock" rules {
        "--\\}\\}".action("comment.block.end.handlebars").state("@pop")
        ".".token("comment.content.handlebars")
      }
      "commentHtml" rules {
        "\\{\\{".action {
          token = "@rematch"
          switchTo = "@handlebarsInSimpleState.comment"
        }
        "-->".action("comment.html").state("@pop")
        "[^-]+".token("comment.content.html")
        ".".token("comment.content.html")
      }
      "otherTag" rules {
        "\\{\\{".action {
          token = "@rematch"
          switchTo = "@handlebarsInSimpleState.otherTag"
        }
        "\\/?>".action("delimiter.html").state("@pop")
        "\"([^\"]*)\"".token("attribute.value")
        "'([^']*)'".token("attribute.value")
        "[\\w\\-]+".token("attribute.name")
        "=".token("delimiter")
      }
      script {
        "\\{\\{".action {
          token = "@rematch"
          switchTo = "@handlebarsInSimpleState.script"
        }
        "type".action("attribute.name").state("@scriptAfterType")
        "\"([^\"]*)\"".token("attribute.value")
        "'([^']*)'".token("attribute.value")
        "[\\w\\-]+".token("attribute.name")
        "=".token("delimiter")
        ">".action {
          token = "delimiter.html"
          next = "@scriptEmbedded.text/javascript"
          nextEmbedded = "text/javascript"
          nextEmbedded = "text/javascript"
        }
        "(<\\/)(script\\s*)(>)".actionArray {
          token("delimiter.html")
          token("tag.html")
          action("delimiter.html") {
            next = "@pop"
          }
        }
      }
      "scriptAfterType" rules {
        "\\{\\{".action {
          token = "@rematch"
          switchTo = "@handlebarsInSimpleState.scriptAfterType"
        }
        "=".action("delimiter").state("@scriptAfterTypeEquals")
        ">".action {
          token = "delimiter.html"
          next = "@scriptEmbedded.text/javascript"
          nextEmbedded = "text/javascript"
          nextEmbedded = "text/javascript"
        }
        "<\\/script\\s*>".action {
          token = "@rematch"
          next = "@pop"
        }
      }
      "scriptAfterTypeEquals" rules {
        "\\{\\{".action {
          token = "@rematch"
          switchTo = "@handlebarsInSimpleState.scriptAfterTypeEquals"
        }
        "\"([^\"]*)\"".action {
          token = "attribute.value"
          switchTo = "@scriptWithCustomType.${'$'}1"
        }
        "'([^']*)'".action {
          token = "attribute.value"
          switchTo = "@scriptWithCustomType.${'$'}1"
        }
        ">".action {
          token = "delimiter.html"
          next = "@scriptEmbedded.text/javascript"
          nextEmbedded = "text/javascript"
          nextEmbedded = "text/javascript"
        }
        "<\\/script\\s*>".action {
          token = "@rematch"
          next = "@pop"
        }
      }
      "scriptWithCustomType" rules {
        "\\{\\{".action {
          token = "@rematch"
          switchTo = "@handlebarsInSimpleState.scriptWithCustomType.${'$'}S2"
        }
        ">".action {
          token = "delimiter.html"
          next = "@scriptEmbedded.${'$'}S2"
          nextEmbedded = "${'$'}S2"
          nextEmbedded = "${'$'}S2"
        }
        "\"([^\"]*)\"".token("attribute.value")
        "'([^']*)'".token("attribute.value")
        "[\\w\\-]+".token("attribute.name")
        "=".token("delimiter")
        "<\\/script\\s*>".action {
          token = "@rematch"
          next = "@pop"
        }
      }
      "scriptEmbedded" rules {
        "\\{\\{".action {
          token = "@rematch"
          nextEmbedded = "@pop"
          switchTo = "@handlebarsInEmbeddedState.scriptEmbedded.${'$'}S2"
          nextEmbedded = "@pop"
        }
        "<\\/script".action {
          token = "@rematch"
          next = "@pop"
          nextEmbedded = "@pop"
          nextEmbedded = "@pop"
        }
      }
      style {
        "\\{\\{".action {
          token = "@rematch"
          switchTo = "@handlebarsInSimpleState.style"
        }
        "type".action("attribute.name").state("@styleAfterType")
        "\"([^\"]*)\"".token("attribute.value")
        "'([^']*)'".token("attribute.value")
        "[\\w\\-]+".token("attribute.name")
        "=".token("delimiter")
        ">".action {
          token = "delimiter.html"
          next = "@styleEmbedded.text/css"
          nextEmbedded = "text/css"
          nextEmbedded = "text/css"
        }
        "(<\\/)(style\\s*)(>)".actionArray {
          token("delimiter.html")
          token("tag.html")
          action("delimiter.html") {
            next = "@pop"
          }
        }
      }
      "styleAfterType" rules {
        "\\{\\{".action {
          token = "@rematch"
          switchTo = "@handlebarsInSimpleState.styleAfterType"
        }
        "=".action("delimiter").state("@styleAfterTypeEquals")
        ">".action {
          token = "delimiter.html"
          next = "@styleEmbedded.text/css"
          nextEmbedded = "text/css"
          nextEmbedded = "text/css"
        }
        "<\\/style\\s*>".action {
          token = "@rematch"
          next = "@pop"
        }
      }
      "styleAfterTypeEquals" rules {
        "\\{\\{".action {
          token = "@rematch"
          switchTo = "@handlebarsInSimpleState.styleAfterTypeEquals"
        }
        "\"([^\"]*)\"".action {
          token = "attribute.value"
          switchTo = "@styleWithCustomType.${'$'}1"
        }
        "'([^']*)'".action {
          token = "attribute.value"
          switchTo = "@styleWithCustomType.${'$'}1"
        }
        ">".action {
          token = "delimiter.html"
          next = "@styleEmbedded.text/css"
          nextEmbedded = "text/css"
          nextEmbedded = "text/css"
        }
        "<\\/style\\s*>".action {
          token = "@rematch"
          next = "@pop"
        }
      }
      "styleWithCustomType" rules {
        "\\{\\{".action {
          token = "@rematch"
          switchTo = "@handlebarsInSimpleState.styleWithCustomType.${'$'}S2"
        }
        ">".action {
          token = "delimiter.html"
          next = "@styleEmbedded.${'$'}S2"
          nextEmbedded = "${'$'}S2"
          nextEmbedded = "${'$'}S2"
        }
        "\"([^\"]*)\"".token("attribute.value")
        "'([^']*)'".token("attribute.value")
        "[\\w\\-]+".token("attribute.name")
        "=".token("delimiter")
        "<\\/style\\s*>".action {
          token = "@rematch"
          next = "@pop"
        }
      }
      "styleEmbedded" rules {
        "\\{\\{".action {
          token = "@rematch"
          nextEmbedded = "@pop"
          switchTo = "@handlebarsInEmbeddedState.styleEmbedded.${'$'}S2"
          nextEmbedded = "@pop"
        }
        "<\\/style".action {
          token = "@rematch"
          next = "@pop"
          nextEmbedded = "@pop"
          nextEmbedded = "@pop"
        }
      }
      "handlebarsInSimpleState" rules {
        "\\{\\{\\{?".token("delimiter.handlebars")
        "\\}\\}\\}?".action {
          token = "delimiter.handlebars"
          switchTo = "@${'$'}S2.${'$'}S3"
        }
        include("handlebarsRoot")
      }
      "handlebarsInEmbeddedState" rules {
        "\\{\\{\\{?".token("delimiter.handlebars")
        "\\}\\}\\}?".action {
          token = "delimiter.handlebars"
          nextEmbedded = "${'$'}S3"
          switchTo = "@${'$'}S2.${'$'}S3"
          nextEmbedded = "${'$'}S3"
        }
        include("handlebarsRoot")
      }
      "handlebarsRoot" rules {
        "\"[^\"]*\"".token("string.handlebars")
        "[#/][^\\s}]+".token("keyword.helper.handlebars")
        "else\\b".token("keyword.helper.handlebars")
        "[^}]".token("variable.parameter.handlebars")
      }
    }
  }
}

