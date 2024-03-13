package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val RqLanguage: IMonarchLanguage = buildMonarchLanguage {
  tokenPostfix = ".rq"
  ignoreCase = true
  defaultToken = ""
  brackets {
    bracket("{","}","delimiter.curly")
    bracket("(",")","delimiter.parenthesis")
    bracket("[","]","delimiter.square")
    bracket("<",">","delimiter.angle")
  }
  keywords("add", "as", "asc", "ask", "base", "by", "clear", "construct", "copy", "create", "data",
      "delete", "desc", "describe", "distinct", "drop", "false", "filter", "from", "graph", "group",
      "having", "in", "insert", "limit", "load", "minus", "move", "named", "not", "offset",
      "optional", "order", "prefix", "reduced", "select", "service", "silent", "to", "true",
      "undef", "union", "using", "values", "where", "with")
  "builtinFunctions" and listOf("a", "abs", "avg", "bind", "bnode", "bound", "ceil", "coalesce",
      "concat", "contains", "count", "datatype", "day", "encode_for_uri", "exists", "floor",
      "group_concat", "hours", "if", "iri", "isblank", "isiri", "isliteral", "isnumeric", "isuri",
      "lang", "langmatches", "lcase", "max", "md5", "min", "minutes", "month", "now", "rand",
      "regex", "replace", "round", "sameterm", "sample", "seconds", "sha1", "sha256", "sha384",
      "sha512", "str", "strafter", "strbefore", "strdt", "strends", "strlang", "strlen",
      "strstarts", "struuid", "substr", "sum", "timezone", "tz", "ucase", "uri", "uuid", "year")
  tokenizer {
    root {
      "<[^\\s\\u00a0>]*>?".token("tag")
      include("@strings")
      "#.*".token("comment")
      "[{}()\\[\\]]".token("@brackets")
      "[;,.]".token("delimiter")
      "[_\\w\\d]+:(\\.(?=[\\w_\\-\\\\%])|[:\\w_-]|\\\\[-\\\\_~.!${'$'}&'()*+,;=/?#@%]|%[a-f\\d][a-f\\d])*".token("tag")
      ":(\\.(?=[\\w_\\-\\\\%])|[:\\w_-]|\\\\[-\\\\_~.!${'$'}&'()*+,;=/?#@%]|%[a-f\\d][a-f\\d])+".token("tag")
      "[${'$'}?]?[_\\w\\d]+".action {
        cases {
          "@keywords" and {
            token = "keyword"
          }
          "@builtinFunctions" and {
            token = "predefined.sql"
          }
          "@default" and "identifier"
        }
      }
      "\\^\\^".token("operator.sql")
      "\\^[*+\\-<>=&|^\\/!?]*".token("operator.sql")
      "[*+\\-<>=&|\\/!?]".token("operator.sql")
      "@[a-z\\d\\-]*".token("metatag.html")
      "\\s+".token("white")
    }
    "strings" rules {
      "'([^'\\\\]|\\\\.)*${'$'}".token("string.invalid")
      "'${'$'}".action("string.sql").state("@pop")
      "'".action("string.sql").state("@stringBody")
      "\"([^\"\\\\]|\\\\.)*${'$'}".token("string.invalid")
      "\"${'$'}".action("string.sql").state("@pop")
      "\"".action("string.sql").state("@dblStringBody")
    }
    "stringBody" rules {
      "[^\\\\']+".token("string.sql")
      "\\\\.".token("string.escape")
      "'".action("string.sql").state("@pop")
    }
    "dblStringBody" rules {
      "[^\\\\\"]+".token("string.sql")
      "\\\\.".token("string.escape")
      "\"".action("string.sql").state("@pop")
    }
  }
}

