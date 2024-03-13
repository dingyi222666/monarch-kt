package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val CoffeeLanguage: IMonarchLanguage = buildMonarchLanguage {
  tokenPostfix = ".coffee"
  ignoreCase = true
  defaultToken = ""
  brackets {
    bracket("{","}","delimiter.curly")
    bracket("[","]","delimiter.square")
    bracket("(",")","delimiter.parenthesis")
  }
  "regEx" and "\\/(?!\\/\\/)(?:[^\\/\\\\]|\\\\.)*\\/[igm]*"
  keywords("and", "or", "is", "isnt", "not", "on", "yes", "@", "no", "off", "true", "false", "null",
      "this", "new", "delete", "typeof", "in", "instanceof", "return", "throw", "break", "continue",
      "debugger", "if", "else", "switch", "for", "while", "do", "try", "catch", "finally", "class",
      "extends", "super", "undefined", "then", "unless", "until", "loop", "of", "by", "when")
  symbols("[=><!~?&%|+\\-*\\/\\^\\.,\\:]+")
  escapes("\\\\(?:[abfnrtv\\\\\"'${'$'}]|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
  tokenizer {
    root {
      "\\@[a-zA-Z_]\\w*".token("variable.predefined")
      "[a-zA-Z_]\\w*".action {
        cases {
          "this" and "variable.predefined"
          "@keywords" and {
            token = "keyword.${'$'}0"
          }
          "@default" and ""
        }
      }
      "[ \\t\\r\\n]+".token("")
      "###".action("comment").state("@comment")
      "#.*${'$'}".token("comment")
      "///".action {
        token = "regexp"
        next = "@hereregexp"
      }
      "^(\\s*)(@regEx)".actionArray {
        token("")
        token("regexp")
      }
      "(\\()(\\s*)(@regEx)".actionArray {
        token("@brackets")
        token("")
        token("regexp")
      }
      "(\\,)(\\s*)(@regEx)".actionArray {
        token("delimiter")
        token("")
        token("regexp")
      }
      "(\\=)(\\s*)(@regEx)".actionArray {
        token("delimiter")
        token("")
        token("regexp")
      }
      "(\\:)(\\s*)(@regEx)".actionArray {
        token("delimiter")
        token("")
        token("regexp")
      }
      "(\\[)(\\s*)(@regEx)".actionArray {
        token("@brackets")
        token("")
        token("regexp")
      }
      "(\\!)(\\s*)(@regEx)".actionArray {
        token("delimiter")
        token("")
        token("regexp")
      }
      "(\\&)(\\s*)(@regEx)".actionArray {
        token("delimiter")
        token("")
        token("regexp")
      }
      "(\\|)(\\s*)(@regEx)".actionArray {
        token("delimiter")
        token("")
        token("regexp")
      }
      "(\\?)(\\s*)(@regEx)".actionArray {
        token("delimiter")
        token("")
        token("regexp")
      }
      "(\\{)(\\s*)(@regEx)".actionArray {
        token("@brackets")
        token("")
        token("regexp")
      }
      "(\\;)(\\s*)(@regEx)".actionArray {
        token("")
        token("")
        token("regexp")
      }
      "}".action {
        cases {
          "${'$'}S2==interpolatedstring" and {
            token = "string"
            next = "@pop"
          }
          "@default" and "@brackets"
        }
      }
      "[{}()\\[\\]]".token("@brackets")
      "@symbols".token("delimiter")
      "\\d+[eE]([\\-+]?\\d+)?".token("number.float")
      "\\d+\\.\\d+([eE][\\-+]?\\d+)?".token("number.float")
      "0[xX][0-9a-fA-F]+".token("number.hex")
      "0[0-7]+(?!\\d)".token("number.octal")
      "\\d+".token("number")
      "[,.]".token("delimiter")
      "\"\"\"".action("string").state("@herestring.\"\"\"")
      "'''".action("string").state("@herestring.'''")
      "\"".action {
        cases {
          "@eos" and "string"
          "@default" and {
            token = "string"
            next = "@string.\""
          }
        }
      }
      "'".action {
        cases {
          "@eos" and "string"
          "@default" and {
            token = "string"
            next = "@string.'"
          }
        }
      }
    }
    string {
      "[^\"'\\#\\\\]+".token("string")
      "@escapes".token("string.escape")
      "\\.".token("string.escape.invalid")
      "\\.".token("string.escape.invalid")
      "#{".action {
        cases {
          "${'$'}S2==\"" and {
            token = "string"
            next = "root.interpolatedstring"
          }
          "@default" and "string"
        }
      }
      "[\"']".action {
        cases {
          "${'$'}#==${'$'}S2" and {
            token = "string"
            next = "@pop"
          }
          "@default" and "string"
        }
      }
      "#".token("string")
    }
    "herestring" rules {
      "(\"\"\"|''')".action {
        cases {
          "${'$'}1==${'$'}S2" and {
            token = "string"
            next = "@pop"
          }
          "@default" and "string"
        }
      }
      "[^#\\\\'\"]+".token("string")
      "['\"]+".token("string")
      "@escapes".token("string.escape")
      "\\.".token("string.escape.invalid")
      "#{".action {
        token = "string.quote"
        next = "root.interpolatedstring"
      }
      "#".token("string")
    }
    comment {
      "[^#]+".token("comment")
      "###".action("comment").state("@pop")
      "#".token("comment")
    }
    "hereregexp" rules {
      "[^\\\\\\/#]+".token("regexp")
      "\\\\.".token("regexp")
      "#.*${'$'}".token("comment")
      "///[igm]*".action {
        token = "regexp"
        next = "@pop"
      }
      "\\/".token("regexp")
    }
  }
}

