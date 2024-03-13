package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val BatLanguage: IMonarchLanguage = buildMonarchLanguage {
  tokenPostfix = ".bat"
  ignoreCase = true
  defaultToken = ""
  brackets {
    bracket("{","}","delimiter.bracket")
    bracket("(",")","delimiter.parenthesis")
    bracket("[","]","delimiter.square")
  }
  "keywords" and
      "call|defined|echo|errorlevel|exist|for|goto|if|pause|set|shift|start|title|not|pushd|popd"
  symbols("[=><!~?&|+\\-*\\/\\^;\\.,]+")
  escapes("\\\\(?:[abfnrtv\\\\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
  tokenizer {
    root {
      "^(\\s*)(rem(?:\\s.*|))${'$'}".actionArray {
        token("")
        token("comment")
      }
      "(\\@?)(@keywords)(?!\\w)".actionArray {
        action("keyword") {
        }
        action("keyword.${'$'}2") {
        }
      }
      "[ \\t\\r\\n]+".token("")
      "setlocal(?!\\w)".token("keyword.tag-setlocal")
      "endlocal(?!\\w)".token("keyword.tag-setlocal")
      "[a-zA-Z_]\\w*".token("")
      ":\\w*".token("metatag")
      "%[^%]+%".token("variable")
      "%%[\\w]+(?!\\w)".token("variable")
      "[{}()\\[\\]]".token("@brackets")
      "@symbols".token("delimiter")
      "\\d*\\.\\d+([eE][\\-+]?\\d+)?".token("number.float")
      "0[xX][0-9a-fA-F_]*[0-9a-fA-F]".token("number.hex")
      "\\d+".token("number")
      "[;,.]".token("delimiter")
      "\"".action("string").state("@string.\"")
      "'".action("string").state("@string.'")
    }
    string {
      "[^\\\\\"'%]+".action {
        cases {
          "@eos" and {
            token = "string"
            next = "@popall"
          }
          "@default" and "string"
        }
      }
      "@escapes".token("string.escape")
      "\\\\.".token("string.escape.invalid")
      "%[\\w ]+%".token("variable")
      "%%[\\w]+(?!\\w)".token("variable")
      "[\"']".action {
        cases {
          "${'$'}#==${'$'}S2" and {
            token = "string"
            next = "@pop"
          }
          "@default" and "string"
        }
      }
      "${'$'}".action("string").state("@popall")
    }
  }
}

