package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val PhpLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ""
    defaultToken = ""
    "phpKeywords" and listOf("abstract", "and", "array", "as", "break", "callable", "case", "catch",
        "cfunction", "class", "clone", "const", "continue", "declare", "default", "do", "else",
        "elseif", "enddeclare", "endfor", "endforeach", "endif", "endswitch", "endwhile", "extends",
        "false", "final", "for", "foreach", "function", "global", "goto", "if", "implements",
        "interface", "instanceof", "insteadof", "namespace", "new", "null", "object",
        "old_function", "or", "private", "protected", "public", "resource", "static", "switch",
        "throw", "trait", "try", "true", "use", "var", "while", "xor", "die", "echo", "empty",
        "exit", "eval", "include", "include_once", "isset", "list", "require", "require_once",
        "return", "print", "unset", "yield", "__construct")
    "phpCompileTimeConstants" and listOf("__CLASS__", "__DIR__", "__FILE__", "__LINE__",
        "__NAMESPACE__", "__METHOD__", "__FUNCTION__", "__TRAIT__")
    "phpPreDefinedVariables" and listOf("\$GLOBALS", "\$_SERVER", "\$_GET", "\$_POST", "\$_FILES",
        "\$_REQUEST", "\$_SESSION", "\$_ENV", "\$_COOKIE", "\$php_errormsg", "\$HTTP_RAW_POST_DATA",
        "\$http_response_header", "\$argc", "\$argv")
    escapes("\\\\(?:[abfnrtv\\\\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
    tokenizer {
      root {
        "<\\?((php)|=)?".action {
          token = "@rematch"
          switchTo = "@phpInSimpleState.root"
        }
        "<!DOCTYPE".action("metatag.html").state("@doctype")
        "<!--".action("comment.html").state("@comment")
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
      }
      "doctype" rules {
        "<\\?((php)|=)?".action {
          token = "@rematch"
          switchTo = "@phpInSimpleState.comment"
        }
        "[^>]+".token("metatag.content.html")
        ">".action("metatag.html").state("@pop")
      }
      comment {
        "<\\?((php)|=)?".action {
          token = "@rematch"
          switchTo = "@phpInSimpleState.comment"
        }
        "-->".action("comment.html").state("@pop")
        "[^-]+".token("comment.content.html")
        ".".token("comment.content.html")
      }
      "otherTag" rules {
        "<\\?((php)|=)?".action {
          token = "@rematch"
          switchTo = "@phpInSimpleState.otherTag"
        }
        "\\/?>".action("delimiter.html").state("@pop")
        "\"([^\"]*)\"".token("attribute.value")
        "'([^']*)'".token("attribute.value")
        "[\\w\\-]+".token("attribute.name")
        "=".token("delimiter")
      }
      script {
        "<\\?((php)|=)?".action {
          token = "@rematch"
          switchTo = "@phpInSimpleState.script"
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
        "<\\?((php)|=)?".action {
          token = "@rematch"
          switchTo = "@phpInSimpleState.scriptAfterType"
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
        "<\\?((php)|=)?".action {
          token = "@rematch"
          switchTo = "@phpInSimpleState.scriptAfterTypeEquals"
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
        "<\\?((php)|=)?".action {
          token = "@rematch"
          switchTo = "@phpInSimpleState.scriptWithCustomType.${'$'}S2"
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
        "<\\?((php)|=)?".action {
          token = "@rematch"
          nextEmbedded = "@pop"
          switchTo = "@phpInEmbeddedState.scriptEmbedded.${'$'}S2"
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
        "<\\?((php)|=)?".action {
          token = "@rematch"
          switchTo = "@phpInSimpleState.style"
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
        "<\\?((php)|=)?".action {
          token = "@rematch"
          switchTo = "@phpInSimpleState.styleAfterType"
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
        "<\\?((php)|=)?".action {
          token = "@rematch"
          switchTo = "@phpInSimpleState.styleAfterTypeEquals"
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
        "<\\?((php)|=)?".action {
          token = "@rematch"
          switchTo = "@phpInSimpleState.styleWithCustomType.${'$'}S2"
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
        "<\\?((php)|=)?".action {
          token = "@rematch"
          nextEmbedded = "@pop"
          switchTo = "@phpInEmbeddedState.styleEmbedded.${'$'}S2"
          nextEmbedded = "@pop"
        }
        "<\\/style".action {
          token = "@rematch"
          next = "@pop"
          nextEmbedded = "@pop"
          nextEmbedded = "@pop"
        }
      }
      "phpInSimpleState" rules {
        "<\\?((php)|=)?".token("metatag.php")
        "\\?>".action {
          token = "metatag.php"
          switchTo = "@${'$'}S2.${'$'}S3"
        }
        include("phpRoot")
      }
      "phpInEmbeddedState" rules {
        "<\\?((php)|=)?".token("metatag.php")
        "\\?>".action {
          token = "metatag.php"
          nextEmbedded = "${'$'}S3"
          switchTo = "@${'$'}S2.${'$'}S3"
          nextEmbedded = "${'$'}S3"
        }
        include("phpRoot")
      }
      "phpRoot" rules {
        "[a-zA-Z_]\\w*".action {
          cases {
            "@phpKeywords" and {
              token = "keyword.php"
            }
            "@phpCompileTimeConstants" and {
              token = "constant.php"
            }
            "@default" and "identifier.php"
          }
        }
        "[${'$'}a-zA-Z_]\\w*".action {
          cases {
            "@phpPreDefinedVariables" and {
              token = "variable.predefined.php"
            }
            "@default" and "variable.php"
          }
        }
        "[{}]".token("delimiter.bracket.php")
        "[\\[\\]]".token("delimiter.array.php")
        "[()]".token("delimiter.parenthesis.php")
        "(#|\\/\\/)${'$'}".token("comment.php")
        "(#|\\/\\/)".action("comment.php").state("@phpLineComment")
        "\\/\\*".action("comment.php").state("@phpComment")
        "\"".action("string.php").state("@phpDoubleQuoteString")
        "'".action("string.php").state("@phpSingleQuoteString")
        "[\\+\\-\\*\\%\\&\\|\\^\\~\\!\\=\\<\\>\\/\\?\\;\\:\\.\\,\\@]".token("delimiter.php")
        "\\d*\\d+[eE]([\\-+]?\\d+)?".token("number.float.php")
        "\\d*\\.\\d+([eE][\\-+]?\\d+)?".token("number.float.php")
        "0[xX][0-9a-fA-F']*[0-9a-fA-F]".token("number.hex.php")
        "0[0-7']*[0-7]".token("number.octal.php")
        "0[bB][0-1']*[0-1]".token("number.binary.php")
        "\\d[\\d']*".token("number.php")
        "\\d".token("number.php")
      }
      "phpComment" rules {
        "\\*\\/".action("comment.php").state("@pop")
        "[^*]+".token("comment.php")
        ".".token("comment.php")
      }
      "phpLineComment" rules {
        "\\?>".action {
          token = "@rematch"
          next = "@pop"
        }
        ".${'$'}".action("comment.php").state("@pop")
        "[^?]+${'$'}".action("comment.php").state("@pop")
        "[^?]+".token("comment.php")
        ".".token("comment.php")
      }
      "phpDoubleQuoteString" rules {
        "[^\\\\\"]+".token("string.php")
        "@escapes".token("string.escape.php")
        "\\\\.".token("string.escape.invalid.php")
        "\"".action("string.php").state("@pop")
      }
      "phpSingleQuoteString" rules {
        "[^\\\\']+".token("string.php")
        "@escapes".token("string.escape.php")
        "\\\\.".token("string.escape.invalid.php")
        "'".action("string.php").state("@pop")
      }
    }
  }
}

