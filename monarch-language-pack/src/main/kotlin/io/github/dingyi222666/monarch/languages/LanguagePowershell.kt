package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val Ps1Language: IMonarchLanguage = buildMonarchLanguage {
  tokenPostfix = ".ps1"
  ignoreCase = true
  defaultToken = ""
  brackets {
    bracket("{","}","delimiter.curly")
    bracket("[","]","delimiter.square")
    bracket("(",")","delimiter.parenthesis")
  }
  keywords("begin", "break", "catch", "class", "continue", "data", "define", "do", "dynamicparam",
      "else", "elseif", "end", "exit", "filter", "finally", "for", "foreach", "from", "function",
      "if", "in", "param", "process", "return", "switch", "throw", "trap", "try", "until", "using",
      "var", "while", "workflow", "parallel", "sequence", "inlinescript", "configuration")
  "helpKeywords" and
      "SYNOPSIS|DESCRIPTION|PARAMETER|EXAMPLE|INPUTS|OUTPUTS|NOTES|LINK|COMPONENT|ROLE|FUNCTIONALITY|FORWARDHELPTARGETNAME|FORWARDHELPCATEGORY|REMOTEHELPRUNSPACE|EXTERNALHELP"
  symbols("[=><!~?&%|+\\-*\\/\\^;\\.,]+")
  escapes("`(?:[abfnrtv\\\\\"'${'$'}]|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
  tokenizer {
    root {
      "[a-zA-Z_][\\w-]*".action {
        cases {
          "@keywords" and {
            token = "keyword.${'$'}0"
          }
          "@default" and ""
        }
      }
      "[ \\t\\r\\n]+".token("")
      "^:\\w*".token("metatag")
      "\\${'$'}(\\{((global|local|private|script|using):)?[\\w]+\\}|((global|local|private|script|using):)?[\\w]+)".token("variable")
      "<#".action("comment").state("@comment")
      "#.*${'$'}".token("comment")
      "[{}()\\[\\]]".token("@brackets")
      "@symbols".token("delimiter")
      "\\d*\\.\\d+([eE][\\-+]?\\d+)?".token("number.float")
      "0[xX][0-9a-fA-F_]*[0-9a-fA-F]".token("number.hex")
      "\\d+?".token("number")
      "[;,.]".token("delimiter")
      "\\@\"".action("string").state("@herestring.\"")
      "\\@'".action("string").state("@herestring.'")
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
      "[^\"'\\${'$'}`]+".action {
        cases {
          "@eos" and {
            token = "string"
            next = "@popall"
          }
          "@default" and "string"
        }
      }
      "@escapes".action {
        cases {
          "@eos" and {
            token = "string.escape"
            next = "@popall"
          }
          "@default" and "string.escape"
        }
      }
      "`.".action {
        cases {
          "@eos" and {
            token = "string.escape.invalid"
            next = "@popall"
          }
          "@default" and "string.escape.invalid"
        }
      }
      "\\${'$'}[\\w]+${'$'}".action {
        cases {
          "${'$'}S2==\"" and {
            token = "variable"
            next = "@popall"
          }
          "@default" and {
            token = "string"
            next = "@popall"
          }
        }
      }
      "\\${'$'}[\\w]+".action {
        cases {
          "${'$'}S2==\"" and "variable"
          "@default" and "string"
        }
      }
      "[\"']".action {
        cases {
          "${'$'}#==${'$'}S2" and {
            token = "string"
            next = "@pop"
          }
          "@default" and {
            cases {
              "@eos" and {
                token = "string"
                next = "@popall"
              }
              "@default" and "string"
            }
          }
        }
      }
    }
    "herestring" rules {
      "^\\s*([\"'])@".action {
        cases {
          "${'$'}1==${'$'}S2" and {
            token = "string"
            next = "@pop"
          }
          "@default" and "string"
        }
      }
      "[^\\${'$'}`]+".token("string")
      "@escapes".token("string.escape")
      "`.".token("string.escape.invalid")
      "\\${'$'}[\\w]+".action {
        cases {
          "${'$'}S2==\"" and "variable"
          "@default" and "string"
        }
      }
    }
    comment {
      "[^#\\.]+".token("comment")
      "#>".action("comment").state("@pop")
      "(\\.)(@helpKeywords)(?!\\w)".action {
        token = "comment.keyword.${'$'}2"
      }
      "[\\.#]".token("comment")
    }
  }
}

