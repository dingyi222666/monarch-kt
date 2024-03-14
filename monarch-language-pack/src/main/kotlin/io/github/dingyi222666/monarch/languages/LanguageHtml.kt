package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val HtmlLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".html"
    ignoreCase = true
    defaultToken = ""
    tokenizer {
      root {
        "<!DOCTYPE".action("metatag").state("@doctype")
        "<!--".action("comment").state("@comment")
        "(<)((?:[\\w\\-]+:)?[\\w\\-]+)(\\s*)(\\/>)".actionArray {
          token("delimiter")
          token("tag")
          token("")
          token("delimiter")
        }
        "(<)(script)".actionArray {
          token("delimiter")
          action("tag") {
            next = "@script"
          }
        }
        "(<)(style)".actionArray {
          token("delimiter")
          action("tag") {
            next = "@style"
          }
        }
        "(<)((?:[\\w\\-]+:)?[\\w\\-]+)".actionArray {
          token("delimiter")
          action("tag") {
            next = "@otherTag"
          }
        }
        "(<\\/)((?:[\\w\\-]+:)?[\\w\\-]+)".actionArray {
          token("delimiter")
          action("tag") {
            next = "@otherTag"
          }
        }
        "<".token("delimiter")
      }
      "doctype" rules {
        "[^>]+".token("metatag.content")
        ">".action("metatag").state("@pop")
      }
      comment {
        "-->".action("comment").state("@pop")
        "[^-]+".token("comment.content")
        ".".token("comment.content")
      }
      "otherTag" rules {
        "\\/?>".action("delimiter").state("@pop")
        "\"([^\"]*)\"".token("attribute.value")
        "'([^']*)'".token("attribute.value")
        "[\\w\\-]+".token("attribute.name")
        "=".token("delimiter")
      }
      script {
        "type".action("attribute.name").state("@scriptAfterType")
        "\"([^\"]*)\"".token("attribute.value")
        "'([^']*)'".token("attribute.value")
        "[\\w\\-]+".token("attribute.name")
        "=".token("delimiter")
        ">".action {
          token = "delimiter"
          next = "@scriptEmbedded"
          nextEmbedded = "text/javascript"
          nextEmbedded = "text/javascript"
        }
        "(<\\/)(script\\s*)(>)".actionArray {
          token("delimiter")
          token("tag")
          action("delimiter") {
            next = "@pop"
          }
        }
      }
      "scriptAfterType" rules {
        "=".action("delimiter").state("@scriptAfterTypeEquals")
        ">".action {
          token = "delimiter"
          next = "@scriptEmbedded"
          nextEmbedded = "text/javascript"
          nextEmbedded = "text/javascript"
        }
        "<\\/script\\s*>".action {
          token = "@rematch"
          next = "@pop"
        }
      }
      "scriptAfterTypeEquals" rules {
        "\"module\"".action {
          token = "attribute.value"
          switchTo = "@scriptWithCustomType.text/javascript"
        }
        "'module'".action {
          token = "attribute.value"
          switchTo = "@scriptWithCustomType.text/javascript"
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
          token = "delimiter"
          next = "@scriptEmbedded"
          nextEmbedded = "text/javascript"
          nextEmbedded = "text/javascript"
        }
        "<\\/script\\s*>".action {
          token = "@rematch"
          next = "@pop"
        }
      }
      "scriptWithCustomType" rules {
        ">".action {
          token = "delimiter"
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
        "<\\/script".action {
          token = "@rematch"
          next = "@pop"
          nextEmbedded = "@pop"
          nextEmbedded = "@pop"
        }
        "[^<]+".token("")
      }
      style {
        "type".action("attribute.name").state("@styleAfterType")
        "\"([^\"]*)\"".token("attribute.value")
        "'([^']*)'".token("attribute.value")
        "[\\w\\-]+".token("attribute.name")
        "=".token("delimiter")
        ">".action {
          token = "delimiter"
          next = "@styleEmbedded"
          nextEmbedded = "text/css"
          nextEmbedded = "text/css"
        }
        "(<\\/)(style\\s*)(>)".actionArray {
          token("delimiter")
          token("tag")
          action("delimiter") {
            next = "@pop"
          }
        }
      }
      "styleAfterType" rules {
        "=".action("delimiter").state("@styleAfterTypeEquals")
        ">".action {
          token = "delimiter"
          next = "@styleEmbedded"
          nextEmbedded = "text/css"
          nextEmbedded = "text/css"
        }
        "<\\/style\\s*>".action {
          token = "@rematch"
          next = "@pop"
        }
      }
      "styleAfterTypeEquals" rules {
        "\"([^\"]*)\"".action {
          token = "attribute.value"
          switchTo = "@styleWithCustomType.${'$'}1"
        }
        "'([^']*)'".action {
          token = "attribute.value"
          switchTo = "@styleWithCustomType.${'$'}1"
        }
        ">".action {
          token = "delimiter"
          next = "@styleEmbedded"
          nextEmbedded = "text/css"
          nextEmbedded = "text/css"
        }
        "<\\/style\\s*>".action {
          token = "@rematch"
          next = "@pop"
        }
      }
      "styleWithCustomType" rules {
        ">".action {
          token = "delimiter"
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
        "<\\/style".action {
          token = "@rematch"
          next = "@pop"
          nextEmbedded = "@pop"
          nextEmbedded = "@pop"
        }
        "[^<]+".token("")
      }
    }
  }
}

