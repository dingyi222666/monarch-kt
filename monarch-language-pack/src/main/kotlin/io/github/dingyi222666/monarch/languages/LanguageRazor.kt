package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val RazorLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ""
    defaultToken = ""
    "razorKeywords" and listOf("abstract", "as", "async", "await", "base", "bool", "break", "by",
        "byte", "case", "catch", "char", "checked", "class", "const", "continue", "decimal",
        "default", "delegate", "do", "double", "descending", "explicit", "event", "extern", "else",
        "enum", "false", "finally", "fixed", "float", "for", "foreach", "from", "goto", "group",
        "if", "implicit", "in", "int", "interface", "internal", "into", "is", "lock", "long",
        "nameof", "new", "null", "namespace", "object", "operator", "out", "override", "orderby",
        "params", "private", "protected", "public", "readonly", "ref", "return", "switch", "struct",
        "sbyte", "sealed", "short", "sizeof", "stackalloc", "static", "string", "select", "this",
        "throw", "true", "try", "typeof", "uint", "ulong", "unchecked", "unsafe", "ushort", "using",
        "var", "virtual", "volatile", "void", "when", "while", "where", "yield", "model", "inject")
    escapes("\\\\(?:[abfnrtv\\\\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
    tokenizer {
      root {
        "@[^@]".action {
          token = "@rematch"
          switchTo = "@razorInSimpleState.root"
        }
        "<!DOCTYPE".action("metatag.html").state("@doctype")
        "<!--".action("comment.html").state("@comment")
        "(<)([\\w\\-]+)(\\/>)".actionArray {
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
        "(<)([:\\w\\-]+)".actionArray {
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
      }
      "doctype" rules {
        "@[^@]".action {
          token = "@rematch"
          switchTo = "@razorInSimpleState.comment"
        }
        "[^>]+".token("metatag.content.html")
        ">".action("metatag.html").state("@pop")
      }
      comment {
        "@[^@]".action {
          token = "@rematch"
          switchTo = "@razorInSimpleState.comment"
        }
        "-->".action("comment.html").state("@pop")
        "[^-]+".token("comment.content.html")
        ".".token("comment.content.html")
      }
      "otherTag" rules {
        "@[^@]".action {
          token = "@rematch"
          switchTo = "@razorInSimpleState.otherTag"
        }
        "\\/?>".action("delimiter.html").state("@pop")
        "\"([^\"]*)\"".token("attribute.value")
        "'([^']*)'".token("attribute.value")
        "[\\w\\-]+".token("attribute.name")
        "=".token("delimiter")
      }
      script {
        "@[^@]".action {
          token = "@rematch"
          switchTo = "@razorInSimpleState.script"
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
        "@[^@]".action {
          token = "@rematch"
          switchTo = "@razorInSimpleState.scriptAfterType"
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
        "@[^@]".action {
          token = "@rematch"
          switchTo = "@razorInSimpleState.scriptAfterTypeEquals"
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
        "@[^@]".action {
          token = "@rematch"
          switchTo = "@razorInSimpleState.scriptWithCustomType.${'$'}S2"
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
        "@[^@]".action {
          token = "@rematch"
          nextEmbedded = "@pop"
          switchTo = "@razorInEmbeddedState.scriptEmbedded.${'$'}S2"
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
        "@[^@]".action {
          token = "@rematch"
          switchTo = "@razorInSimpleState.style"
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
        "@[^@]".action {
          token = "@rematch"
          switchTo = "@razorInSimpleState.styleAfterType"
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
        "@[^@]".action {
          token = "@rematch"
          switchTo = "@razorInSimpleState.styleAfterTypeEquals"
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
        "@[^@]".action {
          token = "@rematch"
          switchTo = "@razorInSimpleState.styleWithCustomType.${'$'}S2"
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
        "@[^@]".action {
          token = "@rematch"
          nextEmbedded = "@pop"
          switchTo = "@razorInEmbeddedState.styleEmbedded.${'$'}S2"
          nextEmbedded = "@pop"
        }
        "<\\/style".action {
          token = "@rematch"
          next = "@pop"
          nextEmbedded = "@pop"
          nextEmbedded = "@pop"
        }
      }
      "razorInSimpleState" rules {
        "@\\*".action("comment.cs").state("@razorBlockCommentTopLevel")
        "@[{(]".action("metatag.cs").state("@razorRootTopLevel")
        "(@)(\\s*[\\w]+)".actionArray {
          token("metatag.cs")
          action("identifier.cs") {
            switchTo = "@${'$'}S2.${'$'}S3"
          }
        }
        "[})]".action {
          token = "metatag.cs"
          switchTo = "@${'$'}S2.${'$'}S3"
        }
        "\\*@".action {
          token = "comment.cs"
          switchTo = "@${'$'}S2.${'$'}S3"
        }
      }
      "razorInEmbeddedState" rules {
        "@\\*".action("comment.cs").state("@razorBlockCommentTopLevel")
        "@[{(]".action("metatag.cs").state("@razorRootTopLevel")
        "(@)(\\s*[\\w]+)".actionArray {
          token("metatag.cs")
          action("identifier.cs") {
            nextEmbedded = "${'$'}S3"
            switchTo = "@${'$'}S2.${'$'}S3"
            nextEmbedded = "${'$'}S3"
          }
        }
        "[})]".action {
          token = "metatag.cs"
          nextEmbedded = "${'$'}S3"
          switchTo = "@${'$'}S2.${'$'}S3"
          nextEmbedded = "${'$'}S3"
        }
        "\\*@".action {
          token = "comment.cs"
          nextEmbedded = "${'$'}S3"
          switchTo = "@${'$'}S2.${'$'}S3"
          nextEmbedded = "${'$'}S3"
        }
      }
      "razorBlockCommentTopLevel" rules {
        "\\*@".action("@rematch").state("@pop")
        "[^*]+".token("comment.cs")
        ".".token("comment.cs")
      }
      "razorBlockComment" rules {
        "\\*@".action("comment.cs").state("@pop")
        "[^*]+".token("comment.cs")
        ".".token("comment.cs")
      }
      "razorRootTopLevel" rules {
        "\\{".action("delimiter.bracket.cs").state("@razorRoot")
        "\\(".action("delimiter.parenthesis.cs").state("@razorRoot")
        "[})]".action("@rematch").state("@pop")
        include("razorCommon")
      }
      "razorRoot" rules {
        "\\{".action("delimiter.bracket.cs").state("@razorRoot")
        "\\(".action("delimiter.parenthesis.cs").state("@razorRoot")
        "\\}".action("delimiter.bracket.cs").state("@pop")
        "\\)".action("delimiter.parenthesis.cs").state("@pop")
        include("razorCommon")
      }
      "razorCommon" rules {
        "[a-zA-Z_]\\w*".action {
          cases {
            "@razorKeywords" and {
              token = "keyword.cs"
            }
            "@default" and "identifier.cs"
          }
        }
        "[\\[\\]]".token("delimiter.array.cs")
        "\\/\\/.*${'$'}".token("comment.cs")
        "@\\*".action("comment.cs").state("@razorBlockComment")
        "\"([^\"]*)\"".token("string.cs")
        "'([^']*)'".token("string.cs")
        "(<)([\\w\\-]+)(\\/>)".actionArray {
          token("delimiter.html")
          token("tag.html")
          token("delimiter.html")
        }
        "(<)([\\w\\-]+)(>)".actionArray {
          token("delimiter.html")
          token("tag.html")
          token("delimiter.html")
        }
        "(<\\/)([\\w\\-]+)(>)".actionArray {
          token("delimiter.html")
          token("tag.html")
          token("delimiter.html")
        }
        "[\\+\\-\\*\\%\\&\\|\\^\\~\\!\\=\\<\\>\\/\\?\\;\\:\\.\\,]".token("delimiter.cs")
        "\\d*\\d+[eE]([\\-+]?\\d+)?".token("number.float.cs")
        "\\d*\\.\\d+([eE][\\-+]?\\d+)?".token("number.float.cs")
        "0[xX][0-9a-fA-F']*[0-9a-fA-F]".token("number.hex.cs")
        "0[0-7']*[0-7]".token("number.octal.cs")
        "0[bB][0-1']*[0-1]".token("number.binary.cs")
        "\\d[\\d']*".token("number.cs")
        "\\d".token("number.cs")
      }
    }
  }
}

