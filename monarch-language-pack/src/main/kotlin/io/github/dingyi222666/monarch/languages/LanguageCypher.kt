package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val CypherLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".cypher"
    ignoreCase = true
    defaultToken = ""
    brackets {
      bracket("{","}","delimiter.curly")
      bracket("[","]","delimiter.bracket")
      bracket("(",")","delimiter.parenthesis")
    }
    keywords("ALL", "AND", "AS", "ASC", "ASCENDING", "BY", "CALL", "CASE", "CONTAINS", "CREATE",
        "DELETE", "DESC", "DESCENDING", "DETACH", "DISTINCT", "ELSE", "END", "ENDS", "EXISTS", "IN",
        "IS", "LIMIT", "MANDATORY", "MATCH", "MERGE", "NOT", "ON", "ON", "OPTIONAL", "OR", "ORDER",
        "REMOVE", "RETURN", "SET", "SKIP", "STARTS", "THEN", "UNION", "UNWIND", "WHEN", "WHERE",
        "WITH", "XOR", "YIELD")
    "builtinLiterals" and listOf("true", "TRUE", "false", "FALSE", "null", "NULL")
    "builtinFunctions" and listOf("abs", "acos", "asin", "atan", "atan2", "avg", "ceil", "coalesce",
        "collect", "cos", "cot", "count", "degrees", "e", "endNode", "exists", "exp", "floor",
        "head", "id", "keys", "labels", "last", "left", "length", "log", "log10", "lTrim", "max",
        "min", "nodes", "percentileCont", "percentileDisc", "pi", "properties", "radians", "rand",
        "range", "relationships", "replace", "reverse", "right", "round", "rTrim", "sign", "sin",
        "size", "split", "sqrt", "startNode", "stDev", "stDevP", "substring", "sum", "tail", "tan",
        "timestamp", "toBoolean", "toFloat", "toInteger", "toLower", "toString", "toUpper", "trim",
        "type")
    operators("+", "-", "*", "/", "%", "^", "=", "<>", "<", ">", "<=", ">=", "->", "<-", "-->",
        "<--")
    escapes("\\\\(?:[tbnrf\\\\\"'`]|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
    digits("\\d+")
    octaldigits("[0-7]+")
    hexdigits("[0-9a-fA-F]+")
    tokenizer {
      root {
        "[{}[\\]()]".token("@brackets")
        include("common")
      }
      "common" rules {
        include("@whitespace")
        include("@numbers")
        include("@strings")
        ":[a-zA-Z_][\\w]*".token("type.identifier")
        "[a-zA-Z_][\\w]*(?=\\()".action {
          cases {
            "@builtinFunctions" and "predefined.function"
          }
        }
        "[a-zA-Z_${'$'}][\\w${'$'}]*".action {
          cases {
            "@keywords" and "keyword"
            "@builtinLiterals" and "predefined.literal"
            "@default" and "identifier"
          }
        }
        "`".action("identifier.escape").state("@identifierBacktick")
        "[;,.:|]".token("delimiter")
        "[<>=%+\\-*/^]+".action {
          cases {
            "@operators" and "delimiter"
            "@default" and ""
          }
        }
      }
      "numbers" rules {
        "-?(@digits)[eE](-?(@digits))?".token("number.float")
        "-?(@digits)?\\.(@digits)([eE]-?(@digits))?".token("number.float")
        "-?0x(@hexdigits)".token("number.hex")
        "-?0(@octaldigits)".token("number.octal")
        "-?(@digits)".token("number")
      }
      "strings" rules {
        "\"([^\"\\\\]|\\\\.)*${'$'}".token("string.invalid")
        "'([^'\\\\]|\\\\.)*${'$'}".token("string.invalid")
        "\"".action("string").state("@stringDouble")
        "'".action("string").state("@stringSingle")
      }
      whitespace {
        "[ \\t\\r\\n]+".token("white")
        "\\/\\*".action("comment").state("@comment")
        "\\/\\/.*${'$'}".token("comment")
      }
      comment {
        "\\/\\/.*".token("comment")
        "[^/*]+".token("comment")
        "\\*\\/".action("comment").state("@pop")
        "[/*]".token("comment")
      }
      "stringDouble" rules {
        "[^\\\\\"]+".token("string")
        "@escapes".token("string")
        "\\\\.".token("string.invalid")
        "\"".action("string").state("@pop")
      }
      "stringSingle" rules {
        "[^\\\\']+".token("string")
        "@escapes".token("string")
        "\\\\.".token("string.invalid")
        "'".action("string").state("@pop")
      }
      "identifierBacktick" rules {
        "[^\\\\`]+".token("identifier.escape")
        "@escapes".token("identifier.escape")
        "\\\\.".token("identifier.escape.invalid")
        "`".action("identifier.escape").state("@pop")
      }
    }
  }
}

