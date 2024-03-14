package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val PascalLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".pascal"
    ignoreCase = true
    defaultToken = ""
    brackets {
      bracket("{","}","delimiter.curly")
      bracket("[","]","delimiter.square")
      bracket("(",")","delimiter.parenthesis")
      bracket("<",">","delimiter.angle")
    }
    keywords("absolute", "abstract", "all", "and_then", "array", "as", "asm", "attribute", "begin",
        "bindable", "case", "class", "const", "contains", "default", "div", "else", "end", "except",
        "exports", "external", "far", "file", "finalization", "finally", "forward", "generic",
        "goto", "if", "implements", "import", "in", "index", "inherited", "initialization",
        "interrupt", "is", "label", "library", "mod", "module", "name", "near", "not", "object",
        "of", "on", "only", "operator", "or_else", "otherwise", "override", "package", "packed",
        "pow", "private", "program", "protected", "public", "published", "interface",
        "implementation", "qualified", "read", "record", "resident", "requires", "resourcestring",
        "restricted", "segment", "set", "shl", "shr", "specialize", "stored", "strict", "then",
        "threadvar", "to", "try", "type", "unit", "uses", "var", "view", "virtual", "dynamic",
        "overload", "reintroduce", "with", "write", "xor", "true", "false", "procedure", "function",
        "constructor", "destructor", "property", "break", "continue", "exit", "abort", "while",
        "do", "for", "raise", "repeat", "until")
    typeKeywords("boolean", "double", "byte", "integer", "shortint", "char", "longint", "float",
        "string")
    operators("=", ">", "<", "<=", ">=", "<>", ":", ":=", "and", "or", "+", "-", "*", "/", "@", "&",
        "^", "%")
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
        "[^\\*\\}]+".token("comment")
        "\\}".action("comment").state("@pop")
        "[\\{]".token("comment")
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
        "\\{".action("comment").state("@comment")
        "\\/\\/.*${'$'}".token("comment")
      }
    }
  }
}

