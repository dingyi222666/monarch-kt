package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val M3Language: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".m3"
    defaultToken = ""
    brackets {
      bracket("{","}","delimiter.curly")
      bracket("(",")","delimiter.parenthesis")
      bracket("[","]","delimiter.square")
    }
    keywords("AND", "ANY", "ARRAY", "AS", "BEGIN", "BITS", "BRANDED", "BY", "CASE", "CONST", "DIV",
        "DO", "ELSE", "ELSIF", "END", "EVAL", "EXCEPT", "EXCEPTION", "EXIT", "EXPORTS", "FINALLY",
        "FOR", "FROM", "GENERIC", "IF", "IMPORT", "IN", "INTERFACE", "LOCK", "LOOP", "METHODS",
        "MOD", "MODULE", "NOT", "OBJECT", "OF", "OR", "OVERRIDES", "PROCEDURE", "RAISE", "RAISES",
        "READONLY", "RECORD", "REF", "REPEAT", "RETURN", "REVEAL", "SET", "THEN", "TO", "TRY",
        "TYPE", "TYPECASE", "UNSAFE", "UNTIL", "UNTRACED", "VALUE", "VAR", "WHILE", "WITH")
    "reservedConstNames" and listOf("ABS", "ADR", "ADRSIZE", "BITSIZE", "BYTESIZE", "CEILING",
        "DEC", "DISPOSE", "FALSE", "FIRST", "FLOAT", "FLOOR", "INC", "ISTYPE", "LAST", "LOOPHOLE",
        "MAX", "MIN", "NARROW", "NEW", "NIL", "NUMBER", "ORD", "ROUND", "SUBARRAY", "TRUE", "TRUNC",
        "TYPECODE", "VAL")
    "reservedTypeNames" and listOf("ADDRESS", "ANY", "BOOLEAN", "CARDINAL", "CHAR", "EXTENDED",
        "INTEGER", "LONGCARD", "LONGINT", "LONGREAL", "MUTEX", "NULL", "REAL", "REFANY", "ROOT",
        "TEXT")
    operators("+", "-", "*", "/", "&", "^", ".")
    "relations" and listOf("=", "#", "<", "<=", ">", ">=", "<:", ":")
    "delimiters" and listOf("|", "..", "=>", ",", ";", ":=")
    symbols("[>=<#.,:;+\\-*/&^]+")
    escapes("\\\\(?:[\\\\fnrt\"']|[0-7]{3})")
    tokenizer {
      root {
        "_\\w*".token("invalid")
        "[a-zA-Z][a-zA-Z0-9_]*".action {
          cases {
            "@keywords" and {
              token = "keyword.${'$'}0"
            }
            "@reservedConstNames" and {
              token = "constant.reserved.${'$'}0"
            }
            "@reservedTypeNames" and {
              token = "type.reserved.${'$'}0"
            }
            "@default" and "identifier"
          }
        }
        include("@whitespace")
        "[{}()\\[\\]]".token("@brackets")
        "[0-9]+\\.[0-9]+(?:[DdEeXx][\\+\\-]?[0-9]+)?".token("number.float")
        "[0-9]+(?:\\_[0-9a-fA-F]+)?L?".token("number")
        "@symbols".action {
          cases {
            "@operators" and "operators"
            "@relations" and "operators"
            "@delimiters" and "delimiter"
            "@default" and "invalid"
          }
        }
        "'[^\\\\']'".token("string.char")
        "(')(@escapes)(')".actionArray {
          token("string.char")
          token("string.escape")
          token("string.char")
        }
        "'".token("invalid")
        "\"([^\"\\\\]|\\\\.)*${'$'}".token("invalid")
        "\"".action("string.text").state("@text")
      }
      "text" rules {
        "[^\\\\\"]+".token("string.text")
        "@escapes".token("string.escape")
        "\\\\.".token("invalid")
        "\"".action("string.text").state("@pop")
      }
      comment {
        "\\(\\*".action("comment").state("@push")
        "\\*\\)".action("comment").state("@pop")
        ".".token("comment")
      }
      "pragma" rules {
        "<\\*".action("keyword.pragma").state("@push")
        "\\*>".action("keyword.pragma").state("@pop")
        ".".token("keyword.pragma")
      }
      whitespace {
        "[ \\t\\r\\n]+".token("white")
        "\\(\\*".action("comment").state("@comment")
        "<\\*".action("keyword.pragma").state("@pragma")
      }
    }
  }
}

