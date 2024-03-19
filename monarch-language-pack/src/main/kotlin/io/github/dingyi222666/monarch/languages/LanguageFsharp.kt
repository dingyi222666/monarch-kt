package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val FsharpLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".fs"
    defaultToken = ""
    keywords("abstract", "and", "atomic", "as", "assert", "asr", "base", "begin", "break",
        "checked", "component", "const", "constraint", "constructor", "continue", "class",
        "default", "delegate", "do", "done", "downcast", "downto", "elif", "else", "end",
        "exception", "eager", "event", "external", "extern", "false", "finally", "for", "fun",
        "function", "fixed", "functor", "global", "if", "in", "include", "inherit", "inline",
        "interface", "internal", "land", "lor", "lsl", "lsr", "lxor", "lazy", "let", "match",
        "member", "mod", "module", "mutable", "namespace", "method", "mixin", "new", "not", "null",
        "of", "open", "or", "object", "override", "private", "parallel", "process", "protected",
        "pure", "public", "rec", "return", "static", "sealed", "struct", "sig", "then", "to",
        "true", "tailcall", "trait", "try", "type", "upcast", "use", "val", "void", "virtual",
        "volatile", "when", "while", "with", "yield")
    symbols("[=><!~?:&|+\\-*\\^%;\\.,\\/]+")
    escapes("\\\\(?:[abfnrtv\\\\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
    "integersuffix" and "[uU]?[yslnLI]?"
    "floatsuffix" and "[fFmM]?"
    tokenizer {
      root {
        "[a-zA-Z_]\\w*".action {
          cases {
            "@keywords" and {
              token = "keyword.${'$'}0"
            }
            "@default" and "identifier"
          }
        }
        include("@whitespace")
        "\\[<.*>\\]".token("annotation")
        "^#(if|else|endif)".token("keyword")
        "[{}()\\[\\]]".token("@brackets")
        "[<>](?!@symbols)".token("@brackets")
        "@symbols".token("delimiter")
        "\\d*\\d+[eE]([\\-+]?\\d+)?(@floatsuffix)".token("number.float")
        "\\d*\\.\\d+([eE][\\-+]?\\d+)?(@floatsuffix)".token("number.float")
        "0x[0-9a-fA-F]+LF".token("number.float")
        "0x[0-9a-fA-F]+(@integersuffix)".token("number.hex")
        "0b[0-1]+(@integersuffix)".token("number.bin")
        "\\d+(@integersuffix)".token("number")
        "[;,.]".token("delimiter")
        "\"([^\"\\\\]|\\\\.)*${'$'}".token("string.invalid")
        "\"\"\"".action("string").state("@string.\"\"\"")
        "\"".action("string").state("@string.\"")
        "\\@\"".action {
          token = "string.quote"
          next = "@litstring"
        }
        "'[^\\\\']'B?".token("string")
        "(')(@escapes)(')".actionArray {
          token("string")
          token("string.escape")
          token("string")
        }
        "'".token("string.invalid")
      }
      whitespace {
        "[ \\t\\r\\n]+".token("")
        "\\(\\*(?!\\))".action("comment").state("@comment")
        "\\/\\/.*${'$'}".token("comment")
      }
      comment {
        "[^*(]+".token("comment")
        "\\*\\)".action("comment").state("@pop")
        "\\*".token("comment")
        "\\(\\*\\)".token("comment")
        "\\(".token("comment")
      }
      string {
        "[^\\\\\"]+".token("string")
        "@escapes".token("string.escape")
        "\\\\.".token("string.escape.invalid")
        "(\"\"\"|\"B?)".action {
          cases {
            "${'$'}#==${'$'}S2" and {
              token = "string"
              next = "@pop"
            }
            "@default" and "string"
          }
        }
      }
      "litstring" rules {
        "[^\"]+".token("string")
        "\"\"".token("string.escape")
        "\"".action {
          token = "string.quote"
          next = "@pop"
        }
      }
    }
  }
}

