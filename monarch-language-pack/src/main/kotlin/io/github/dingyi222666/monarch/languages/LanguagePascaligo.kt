package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val PascaligoLanguage: IMonarchLanguage = buildMonarchLanguage {
  tokenPostfix = ".pascaligo"
  ignoreCase = true
  defaultToken = ""
  brackets {
    bracket("{","}","delimiter.curly")
    bracket("[","]","delimiter.square")
    bracket("(",")","delimiter.parenthesis")
    bracket("<",">","delimiter.angle")
  }
  keywords("begin", "block", "case", "const", "else", "end", "fail", "for", "from", "function",
      "if", "is", "nil", "of", "remove", "return", "skip", "then", "type", "var", "while", "with",
      "option", "None", "transaction")
  typeKeywords("bool", "int", "list", "map", "nat", "record", "string", "unit", "address", "map",
      "mtz", "xtz")
  operators("=", ">", "<", "<=", ">=", "<>", ":", ":=", "and", "mod", "or", "+", "-", "*", "/", "@",
      "&", "^", "%")
  symbols("[=><:@\\^&|+\\-*\\/\\^%]+")
  tokenizer {
    root {
      "[a-zA-Z_][\\w]*".action {
        cases {
          "@keywords" and {
            token = "keyword.${'$'}0"
          }
          "@default" and "identifier"
        }
      }
      include("@whitespace")
      "[{}()\\[\\]]".token("@brackets")
      "[<>](?!@symbols)".token("@brackets")
      "@symbols".action {
        cases {
          "@operators" and "delimiter"
          "@default" and ""
        }
      }
      "\\d*\\.\\d+([eE][\\-+]?\\d+)?".token("number.float")
      "\\${'$'}[0-9a-fA-F]{1,16}".token("number.hex")
      "\\d+".token("number")
      "[;,.]".token("delimiter")
      "'([^'\\\\]|\\\\.)*${'$'}".token("string.invalid")
      "'".action("string").state("@string")
      "'[^\\\\']'".token("string")
      "'".token("string.invalid")
      "\\#\\d+".token("string")
    }
    comment {
      "[^\\(\\*]+".token("comment")
      "\\*\\)".action("comment").state("@pop")
      "\\(\\*".token("comment")
    }
    string {
      "[^\\\\']+".token("string")
      "\\\\.".token("string.escape.invalid")
      "'".action {
        token = "string.quote"
        next = "@pop"
        bracket = "@close"
      }
    }
    whitespace {
      "[ \\t\\r\\n]+".token("white")
      "\\(\\*".action("comment").state("@comment")
      "\\/\\/.*${'$'}".token("comment")
    }
  }
}

