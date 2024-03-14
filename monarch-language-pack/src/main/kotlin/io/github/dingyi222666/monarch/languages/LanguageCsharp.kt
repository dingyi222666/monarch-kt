package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val CsLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".cs"
    defaultToken = ""
    brackets {
      bracket("{","}","delimiter.curly")
      bracket("[","]","delimiter.square")
      bracket("(",")","delimiter.parenthesis")
      bracket("<",">","delimiter.angle")
    }
    keywords("extern", "alias", "using", "bool", "decimal", "sbyte", "byte", "short", "ushort",
        "int", "uint", "long", "ulong", "char", "float", "double", "object", "dynamic", "string",
        "assembly", "is", "as", "ref", "out", "this", "base", "new", "typeof", "void", "checked",
        "unchecked", "default", "delegate", "var", "const", "if", "else", "switch", "case", "while",
        "do", "for", "foreach", "in", "break", "continue", "goto", "return", "throw", "try",
        "catch", "finally", "lock", "yield", "from", "let", "where", "join", "on", "equals", "into",
        "orderby", "ascending", "descending", "select", "group", "by", "namespace", "partial",
        "class", "field", "event", "method", "param", "public", "protected", "internal", "private",
        "abstract", "sealed", "static", "struct", "readonly", "volatile", "virtual", "override",
        "params", "get", "set", "add", "remove", "operator", "true", "false", "implicit",
        "explicit", "interface", "enum", "null", "async", "await", "fixed", "sizeof", "stackalloc",
        "unsafe", "nameof", "when")
    "namespaceFollows" and listOf("namespace", "using")
    "parenFollows" and listOf("if", "for", "while", "switch", "foreach", "using", "catch", "when")
    operators("=", "??", "||", "&&", "|", "^", "&", "==", "!=", "<=", ">=", "<<", "+", "-", "*",
        "/", "%", "!", "~", "++", "--", "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=", "<<=",
        ">>=", ">>", "=>")
    symbols("[=><!~?:&|+\\-*\\/\\^%]+")
    escapes("\\\\(?:[abfnrtv\\\\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
    tokenizer {
      root {
        "\\@?[a-zA-Z_]\\w*".action {
          cases {
            "@namespaceFollows" and {
              token = "keyword.${'$'}0"
              next = "@namespace"
            }
            "@keywords" and {
              token = "keyword.${'$'}0"
              next = "@qualified"
            }
            "@default" and {
              token = "identifier"
              next = "@qualified"
            }
          }
        }
        include("@whitespace")
        "}".action {
          cases {
            "${'$'}S2==interpolatedstring" and {
              token = "string.quote"
              next = "@pop"
            }
            "${'$'}S2==litinterpstring" and {
              token = "string.quote"
              next = "@pop"
            }
            "@default" and "@brackets"
          }
        }
        "[{}()\\[\\]]".token("@brackets")
        "[<>](?!@symbols)".token("@brackets")
        "@symbols".action {
          cases {
            "@operators" and "delimiter"
            "@default" and ""
          }
        }
        "[0-9_]*\\.[0-9_]+([eE][\\-+]?\\d+)?[fFdD]?".token("number.float")
        "0[xX][0-9a-fA-F_]+".token("number.hex")
        "0[bB][01_]+".token("number.hex")
        "[0-9_]+".token("number")
        "[;,.]".token("delimiter")
        "\"([^\"\\\\]|\\\\.)*${'$'}".token("string.invalid")
        "\"".action {
          token = "string.quote"
          next = "@string"
        }
        "\\${'$'}\\@\"".action {
          token = "string.quote"
          next = "@litinterpstring"
        }
        "\\@\"".action {
          token = "string.quote"
          next = "@litstring"
        }
        "\\${'$'}\"".action {
          token = "string.quote"
          next = "@interpolatedstring"
        }
        "'[^\\\\']'".token("string")
        "(')(@escapes)(')".actionArray {
          token("string")
          token("string.escape")
          token("string")
        }
        "'".token("string.invalid")
      }
      "qualified" rules {
        "[a-zA-Z_][\\w]*".action {
          cases {
            "@keywords" and {
              token = "keyword.${'$'}0"
            }
            "@default" and "identifier"
          }
        }
        "\\.".token("delimiter")
        "".action("").state("@pop")
      }
      "namespace" rules {
        include("@whitespace")
        "[A-Z]\\w*".token("namespace")
        "[\\.=]".token("delimiter")
        "".action("").state("@pop")
      }
      comment {
        "[^\\/*]+".token("comment")
        "\\*/".action("comment").state("@pop")
        "[\\/*]".token("comment")
      }
      string {
        "[^\\\\\"]+".token("string")
        "@escapes".token("string.escape")
        "\\\\.".token("string.escape.invalid")
        "\"".action {
          token = "string.quote"
          next = "@pop"
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
      "litinterpstring" rules {
        "[^\"{]+".token("string")
        "\"\"".token("string.escape")
        "{{".token("string.escape")
        "}}".token("string.escape")
        "{".action {
          token = "string.quote"
          next = "root.litinterpstring"
        }
        "\"".action {
          token = "string.quote"
          next = "@pop"
        }
      }
      "interpolatedstring" rules {
        "[^\\\\\"{]+".token("string")
        "@escapes".token("string.escape")
        "\\\\.".token("string.escape.invalid")
        "{{".token("string.escape")
        "}}".token("string.escape")
        "{".action {
          token = "string.quote"
          next = "root.interpolatedstring"
        }
        "\"".action {
          token = "string.quote"
          next = "@pop"
        }
      }
      whitespace {
        "^[ \\t\\v\\f]*#((r)|(load))(?=\\s)".token("directive.csx")
        "^[ \\t\\v\\f]*#\\w.*${'$'}".token("namespace.cpp")
        "[ \\t\\v\\f\\r\\n]+".token("")
        "\\/\\*".action("comment").state("@comment")
        "\\/\\/.*${'$'}".token("comment")
      }
    }
  }
}

