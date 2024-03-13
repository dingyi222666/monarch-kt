package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val DartLanguage: IMonarchLanguage = buildMonarchLanguage {
  tokenPostfix = ".dart"
  defaultToken = "invalid"
  keywords("abstract", "dynamic", "implements", "show", "as", "else", "import", "static", "assert",
      "enum", "in", "super", "async", "export", "interface", "switch", "await", "extends", "is",
      "sync", "break", "external", "library", "this", "case", "factory", "mixin", "throw", "catch",
      "false", "new", "true", "class", "final", "null", "try", "const", "finally", "on", "typedef",
      "continue", "for", "operator", "var", "covariant", "Function", "part", "void", "default",
      "get", "rethrow", "while", "deferred", "hide", "return", "with", "do", "if", "set", "yield")
  typeKeywords("int", "double", "String", "bool")
  operators("+", "-", "*", "/", "~/", "%", "++", "--", "==", "!=", ">", "<", ">=", "<=", "=", "-=",
      "/=", "%=", ">>=", "^=", "+=", "*=", "~/=", "<<=", "&=", "!=", "||", "&&", "&", "|", "^", "~",
      "<<", ">>", "!", ">>>", "??", "?", ":", "|=")
  symbols("[=><!~?:&|+\\-*\\/\\^%]+")
  escapes("\\\\(?:[abfnrtv\\\\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
  digits("\\d+(_+\\d+)*")
  octaldigits("[0-7]+(_+[0-7]+)*")
  binarydigits("[0-1]+(_+[0-1]+)*")
  hexdigits("[[0-9a-fA-F]+(_+[0-9a-fA-F]+)*")
  "regexpctl" and "[(){}\\[\\]\\${'$'}\\^|\\-*+?\\.]"
  "regexpesc" and
      "\\\\(?:[bBdDfnrstvwWn0\\\\\\/]|@regexpctl|c[A-Z]|x[0-9a-fA-F]{2}|u[0-9a-fA-F]{4})"
  tokenizer {
    root {
      "[{}]".token("delimiter.bracket")
      include("common")
    }
    "common" rules {
      "[a-z_${'$'}][\\w${'$'}]*".action {
        cases {
          "@typeKeywords" and "type.identifier"
          "@keywords" and "keyword"
          "@default" and "identifier"
        }
      }
      "[A-Z_${'$'}][\\w\\${'$'}]*".token("type.identifier")
      include("@whitespace")
      "\\/(?=([^\\\\\\/]|\\\\.)+\\/([gimsuy]*)(\\s*)(\\.|;|,|\\)|\\]|\\}|${'$'}))".action {
        token = "regexp"
        next = "@regexp"
        bracket = "@open"
      }
      "@[a-zA-Z]+".token("annotation")
      "[()\\[\\]]".token("@brackets")
      "[<>](?!@symbols)".token("@brackets")
      "!(?=([^=]|${'$'}))".token("delimiter")
      "@symbols".action {
        cases {
          "@operators" and "delimiter"
          "@default" and ""
        }
      }
      "(@digits)[eE]([\\-+]?(@digits))?".token("number.float")
      "(@digits)\\.(@digits)([eE][\\-+]?(@digits))?".token("number.float")
      "0[xX](@hexdigits)n?".token("number.hex")
      "0[oO]?(@octaldigits)n?".token("number.octal")
      "0[bB](@binarydigits)n?".token("number.binary")
      "(@digits)n?".token("number")
      "[;,.]".token("delimiter")
      "\"([^\"\\\\]|\\\\.)*${'$'}".token("string.invalid")
      "'([^'\\\\]|\\\\.)*${'$'}".token("string.invalid")
      "\"".action("string").state("@string_double")
      "'".action("string").state("@string_single")
    }
    whitespace {
      "[ \\t\\r\\n]+".token("")
      "\\/\\*\\*(?!\\/)".action("comment.doc").state("@jsdoc")
      "\\/\\*".action("comment").state("@comment")
      "\\/\\/\\/.*${'$'}".token("comment.doc")
      "\\/\\/.*${'$'}".token("comment")
    }
    comment {
      "[^\\/*]+".token("comment")
      "\\*\\/".action("comment").state("@pop")
      "[\\/*]".token("comment")
    }
    "jsdoc" rules {
      "[^\\/*]+".token("comment.doc")
      "\\*\\/".action("comment.doc").state("@pop")
      "[\\/*]".token("comment.doc")
    }
    "regexp" rules {
      "(\\{)(\\d+(?:,\\d*)?)(\\})".actionArray {
        token("regexp.escape.control")
        token("regexp.escape.control")
        token("regexp.escape.control")
      }
      "(\\[)(\\^?)(?=(?:[^\\]\\\\\\/]|\\\\.)+)".actionArray {
        token("regexp.escape.control")
        action("regexp.escape.control") {
          next = "@regexrange"
        }
      }
      "(\\()(\\?:|\\?=|\\?!)".actionArray {
        token("regexp.escape.control")
        token("regexp.escape.control")
      }
      "[()]".token("regexp.escape.control")
      "@regexpctl".token("regexp.escape.control")
      "[^\\\\\\/]".token("regexp")
      "@regexpesc".token("regexp.escape")
      "\\\\\\.".token("regexp.invalid")
      "(\\/)([gimsuy]*)".actionArray {
        action("regexp") {
          next = "@pop"
          bracket = "@close"
        }
        token("keyword.other")
      }
    }
    "regexrange" rules {
      "-".token("regexp.escape.control")
      "\\^".token("regexp.invalid")
      "@regexpesc".token("regexp.escape")
      "[^\\]]".token("regexp")
      "\\]".action {
        token = "regexp.escape.control"
        next = "@pop"
        bracket = "@close"
      }
    }
    "string_double" rules {
      "[^\\\\\"\\${'$'}]+".token("string")
      "[^\\\\\"]+".token("string")
      "@escapes".token("string.escape")
      "\\\\.".token("string.escape.invalid")
      "\"".action("string").state("@pop")
      "\\${'$'}\\w+".token("identifier")
    }
    "string_single" rules {
      "[^\\\\'\\${'$'}]+".token("string")
      "@escapes".token("string.escape")
      "\\\\.".token("string.escape.invalid")
      "'".action("string").state("@pop")
      "\\${'$'}\\w+".token("identifier")
    }
  }
}

