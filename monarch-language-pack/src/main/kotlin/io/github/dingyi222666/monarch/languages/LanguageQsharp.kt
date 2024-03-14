package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val QsharpLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    keywords("namespace", "open", "as", "operation", "function", "body", "adjoint", "newtype",
        "controlled", "if", "elif", "else", "repeat", "until", "fixup", "for", "in", "while",
        "return", "fail", "within", "apply", "Adjoint", "Controlled", "Adj", "Ctl", "is", "self",
        "auto", "distribute", "invert", "intrinsic", "let", "set", "w/", "new", "not", "and", "or",
        "use", "borrow", "using", "borrowing", "mutable", "internal")
    typeKeywords("Unit", "Int", "BigInt", "Double", "Bool", "String", "Qubit", "Result", "Pauli",
        "Range")
    "invalidKeywords" and listOf("abstract", "base", "bool", "break", "byte", "case", "catch",
        "char", "checked", "class", "const", "continue", "decimal", "default", "delegate", "do",
        "double", "enum", "event", "explicit", "extern", "finally", "fixed", "float", "foreach",
        "goto", "implicit", "int", "interface", "lock", "long", "null", "object", "operator", "out",
        "override", "params", "private", "protected", "public", "readonly", "ref", "sbyte",
        "sealed", "short", "sizeof", "stackalloc", "static", "string", "struct", "switch", "this",
        "throw", "try", "typeof", "unit", "ulong", "unchecked", "unsafe", "ushort", "virtual",
        "void", "volatile")
    "constants" and listOf("true", "false", "PauliI", "PauliX", "PauliY", "PauliZ", "One", "Zero")
    "builtin" and listOf("X", "Y", "Z", "H", "HY", "S", "T", "SWAP", "CNOT", "CCNOT", "MultiX", "R",
        "RFrac", "Rx", "Ry", "Rz", "R1", "R1Frac", "Exp", "ExpFrac", "Measure", "M", "MultiM",
        "Message", "Length", "Assert", "AssertProb", "AssertEqual")
    operators("and=", "<-", "->", "*", "*=", "@", "!", "^", "^=", ":", "::", "..", "==", "...", "=",
        "=>", ">", ">=", "<", "<=", "-", "-=", "!=", "or=", "%", "%=", "|", "+", "+=", "?", "/",
        "/=", "&&&", "&&&=", "^^^", "^^^=", ">>>", ">>>=", "<<<", "<<<=", "|||", "|||=", "~~~", "_",
        "w/", "w/=")
    "namespaceFollows" and listOf("namespace", "open")
    symbols("[=><!~?:&|+\\-*\\/\\^%@._]+")
    escapes("\\\\[\\s\\S]")
    tokenizer {
      root {
        "[a-zA-Z_${'$'}][\\w${'$'}]*".action {
          cases {
            "@namespaceFollows" and {
              token = "keyword.${'$'}0"
              next = "@namespace"
            }
            "@typeKeywords" and "type"
            "@keywords" and "keyword"
            "@constants" and "constant"
            "@builtin" and "keyword"
            "@invalidKeywords" and "invalid"
            "@default" and "identifier"
          }
        }
        include("@whitespace")
        "[{}()\\[\\]]".token("@brackets")
        "@symbols".action {
          cases {
            "@operators" and "operator"
            "@default" and ""
          }
        }
        "\\d*\\.\\d+([eE][\\-+]?\\d+)?".token("number.float")
        "\\d+".token("number")
        "[;,.]".token("delimiter")
        "\"".action {
          token = "string.quote"
          next = "@string"
          bracket = "@open"
        }
      }
      string {
        "[^\\\\\"]+".token("string")
        "@escapes".token("string.escape")
        "\"".action {
          token = "string.quote"
          next = "@pop"
          bracket = "@close"
        }
      }
      "namespace" rules {
        include("@whitespace")
        "[A-Za-z]\\w*".token("namespace")
        "[\\.=]".token("delimiter")
        "".action("").state("@pop")
      }
      whitespace {
        "[ \\t\\r\\n]+".token("white")
        "(\\/\\/).*".token("comment")
      }
    }
  }
}

