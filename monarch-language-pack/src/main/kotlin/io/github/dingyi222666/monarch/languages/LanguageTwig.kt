package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val TwigLanguage: IMonarchLanguage = buildMonarchLanguage {
  tokenPostfix = ""
  ignoreCase = true
  defaultToken = ""
  keywords("apply", "autoescape", "block", "deprecated", "do", "embed", "extends", "flush", "for",
      "from", "if", "import", "include", "macro", "sandbox", "set", "use", "verbatim", "with",
      "endapply", "endautoescape", "endblock", "endembed", "endfor", "endif", "endmacro",
      "endsandbox", "endset", "endwith", "true", "false")
  tokenizer {
    root {
      "{#".action("comment.twig").state("@commentState")
      "{%[-~]?".action("delimiter.twig").state("@blockState")
      "{{[-~]?".action("delimiter.twig").state("@variableState")
      "<!DOCTYPE".action("metatag.html").state("@doctype")
      "<!--".action("comment.html").state("@comment")
      "(<)((?:[\\w\\-]+:)?[\\w\\-]+)(\\s*)(\\/>)".actionArray {
        token("delimiter.html")
        token("tag.html")
        token("")
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
      "(<)((?:[\\w\\-]+:)?[\\w\\-]+)".actionArray {
        token("delimiter.html")
        action("tag.html") {
          next = "@otherTag"
        }
      }
      "(<\\/)((?:[\\w\\-]+:)?[\\w\\-]+)".actionArray {
        token("delimiter.html")
        action("tag.html") {
          next = "@otherTag"
        }
      }
      "<".token("delimiter.html")
    }
    "commentState" rules {
      "#}".action("comment.twig").state("@pop")
      ".".token("comment.twig")
    }
    "blockState" rules {
      "[-~]?%}".action("delimiter.twig").state("@pop")
      "(verbatim)(\\s*)([-~]?%})".actionArray {
        token("keyword.twig")
        token("")
        action("delimiter.twig") {
          next = "@rawDataState"
        }
      }
      include("expression")
    }
    "rawDataState" rules {
      "({%[-~]?)(\\s*)(endverbatim)(\\s*)([-~]?%})".actionArray {
        token("delimiter.twig")
        token("")
        token("keyword.twig")
        token("")
        action("delimiter.twig") {
          next = "@popall"
        }
      }
      ".".token("string.twig")
    }
    "variableState" rules {
      "[-~]?}}".action("delimiter.twig").state("@pop")
      include("expression")
    }
    "stringState" rules {
      "\"".action("string.twig").state("@pop")
      "#{\\s*".action("string.twig").state("@interpolationState")
      "[^#\"\\\\]*(?:(?:\\\\.|#(?!\\{))[^#\"\\\\]*)*".token("string.twig")
    }
    "interpolationState" rules {
      "}".action("string.twig").state("@pop")
      include("expression")
    }
    "expression" rules {
      "\\+|-|\\/{1,2}|%|\\*{1,2}".token("operators.twig")
      "(and|or|not|b-and|b-xor|b-or)(\\s+)".actionArray {
        token("operators.twig")
        token("")
      }
      "==|!=|<|>|>=|<=".token("operators.twig")
      "(starts with|ends with|matches)(\\s+)".actionArray {
        token("operators.twig")
        token("")
      }
      "(in)(\\s+)".actionArray {
        token("operators.twig")
        token("")
      }
      "(is)(\\s+)".actionArray {
        token("operators.twig")
        token("")
      }
      "\\||~|:|\\.{1,2}|\\?{1,2}".token("operators.twig")
      "[^\\W\\d][\\w]*".action {
        cases {
          "@keywords" and "keyword.twig"
          "@default" and "variable.twig"
        }
      }
      "\\d+(\\.\\d+)?".token("number.twig")
      "\\(|\\)|\\[|\\]|{|}|,".token("delimiter.twig")
      "\"([^#\"\\\\]*(?:\\\\.[^#\"\\\\]*)*)\"|\\'([^\\'\\\\]*(?:\\\\.[^\\'\\\\]*)*)\\'".token("string.twig")
      "\"".action("string.twig").state("@stringState")
      "=>".token("operators.twig")
      "=".token("operators.twig")
    }
    "doctype" rules {
      "[^>]+".token("metatag.content.html")
      ">".action("metatag.html").state("@pop")
    }
    comment {
      "-->".action("comment.html").state("@pop")
      "[^-]+".token("comment.content.html")
      ".".token("comment.content.html")
    }
    "otherTag" rules {
      "\\/?>".action("delimiter.html").state("@pop")
      "\"([^\"]*)\"".token("attribute.value.html")
      "'([^']*)'".token("attribute.value.html")
      "[\\w\\-]+".token("attribute.name.html")
      "=".token("delimiter.html")
    }
    script {
      "type".action("attribute.name.html").state("@scriptAfterType")
      "\"([^\"]*)\"".token("attribute.value.html")
      "'([^']*)'".token("attribute.value.html")
      "[\\w\\-]+".token("attribute.name.html")
      "=".token("delimiter.html")
      ">".action {
        token = "delimiter.html"
        next = "@scriptEmbedded"
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
      "=".action("delimiter.html").state("@scriptAfterTypeEquals")
      ">".action {
        token = "delimiter.html"
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
      "\"([^\"]*)\"".action {
        token = "attribute.value.html"
        switchTo = "@scriptWithCustomType.${'$'}1"
      }
      "'([^']*)'".action {
        token = "attribute.value.html"
        switchTo = "@scriptWithCustomType.${'$'}1"
      }
      ">".action {
        token = "delimiter.html"
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
        token = "delimiter.html"
        next = "@scriptEmbedded.${'$'}S2"
        nextEmbedded = "${'$'}S2"
        nextEmbedded = "${'$'}S2"
      }
      "\"([^\"]*)\"".token("attribute.value.html")
      "'([^']*)'".token("attribute.value.html")
      "[\\w\\-]+".token("attribute.name.html")
      "=".token("delimiter.html")
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
      "type".action("attribute.name.html").state("@styleAfterType")
      "\"([^\"]*)\"".token("attribute.value.html")
      "'([^']*)'".token("attribute.value.html")
      "[\\w\\-]+".token("attribute.name.html")
      "=".token("delimiter.html")
      ">".action {
        token = "delimiter.html"
        next = "@styleEmbedded"
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
      "=".action("delimiter.html").state("@styleAfterTypeEquals")
      ">".action {
        token = "delimiter.html"
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
        token = "attribute.value.html"
        switchTo = "@styleWithCustomType.${'$'}1"
      }
      "'([^']*)'".action {
        token = "attribute.value.html"
        switchTo = "@styleWithCustomType.${'$'}1"
      }
      ">".action {
        token = "delimiter.html"
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
        token = "delimiter.html"
        next = "@styleEmbedded.${'$'}S2"
        nextEmbedded = "${'$'}S2"
        nextEmbedded = "${'$'}S2"
      }
      "\"([^\"]*)\"".token("attribute.value.html")
      "'([^']*)'".token("attribute.value.html")
      "[\\w\\-]+".token("attribute.name.html")
      "=".token("delimiter.html")
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

