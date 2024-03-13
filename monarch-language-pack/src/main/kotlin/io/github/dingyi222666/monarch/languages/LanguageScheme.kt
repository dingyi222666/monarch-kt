package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val SchemeLanguage: IMonarchLanguage = buildMonarchLanguage {
  tokenPostfix = ".scheme"
  ignoreCase = true
  defaultToken = ""
  brackets {
    bracket("(",")","delimiter.parenthesis")
    bracket("{","}","delimiter.curly")
    bracket("[","]","delimiter.square")
  }
  keywords("case", "do", "let", "loop", "if", "else", "when", "cons", "car", "cdr", "cond",
      "lambda", "lambda*", "syntax-rules", "format", "set!", "quote", "eval", "append", "list",
      "list?", "member?", "load")
  "constants" and listOf("#t", "#f")
  operators("eq?", "eqv?", "equal?", "and", "or", "not", "null?")
  tokenizer {
    root {
      "#[xXoObB][0-9a-fA-F]+".token("number.hex")
      "[+-]?\\d+(?:(?:\\.\\d*)?(?:[eE][+-]?\\d+)?)?".token("number.float")
      "(?:\\b(?:(define|define-syntax|define-macro))\\b)(\\s+)((?:\\w|\\-|\\!|\\?)*)".actionArray {
        token("keyword")
        token("white")
        token("variable")
      }
      include("@whitespace")
      include("@strings")
      "[a-zA-Z_#][a-zA-Z0-9_\\-\\?\\!\\*]*".action {
        cases {
          "@keywords" and "keyword"
          "@constants" and "constant"
          "@operators" and "operators"
          "@default" and "identifier"
        }
      }
    }
    comment {
      "[^\\|#]+".token("comment")
      "#\\|".action("comment").state("@push")
      "\\|#".action("comment").state("@pop")
      "[\\|#]".token("comment")
    }
    whitespace {
      "[ \\t\\r\\n]+".token("white")
      "#\\|".action("comment").state("@comment")
      ";.*${'$'}".token("comment")
    }
    "strings" rules {
      "\"${'$'}".action("string").state("@popall")
      "\"(?=.)".action("string").state("@multiLineString")
    }
    "multiLineString" rules {
      "[^\\\\\"]+${'$'}".action("string").state("@popall")
      "[^\\\\\"]+".token("string")
      "\\\\.".token("string.escape")
      "\"".action("string").state("@popall")
      "\\\\${'$'}".token("string")
    }
  }
}

