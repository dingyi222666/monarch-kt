package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val CameligoLanguage: IMonarchLanguage = buildMonarchLanguage {
  tokenPostfix = ".cameligo"
  ignoreCase = true
  defaultToken = ""
  brackets {
    bracket("{","}","delimiter.curly")
    bracket("[","]","delimiter.square")
    bracket("(",")","delimiter.parenthesis")
    bracket("<",">","delimiter.angle")
  }
  keywords("abs", "assert", "block", "Bytes", "case", "Crypto", "Current", "else", "failwith",
      "false", "for", "fun", "if", "in", "let", "let%entry", "let%init", "List", "list", "Map",
      "map", "match", "match%nat", "mod", "not", "operation", "Operation", "of", "record", "Set",
      "set", "sender", "skip", "source", "String", "then", "to", "true", "type", "with")
  typeKeywords("int", "unit", "string", "tz", "nat", "bool")
  operators("=", ">", "<", "<=", ">=", "<>", ":", ":=", "and", "mod", "or", "+", "-", "*", "/", "@",
      "&", "^", "%", "->", "<-", "&&", "||")
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

