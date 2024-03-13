package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val EclLanguage: IMonarchLanguage = buildMonarchLanguage {
  tokenPostfix = ".ecl"
  ignoreCase = true
  defaultToken = ""
  brackets {
    bracket("{","}","delimiter.curly")
    bracket("[","]","delimiter.square")
    bracket("(",")","delimiter.parenthesis")
    bracket("<",">","delimiter.angle")
  }
  "pounds" and
      "append|break|declare|demangle|end|for|getdatatype|if|inmodule|loop|mangle|onwarning|option|set|stored|uniquename"
  keywords("__compressed__", "after", "all", "and", "any", "as", "atmost", "before", "beginc",
      "best", "between", "case", "cluster", "compressed", "compression", "const", "counter", "csv",
      "default", "descend", "embed", "encoding", "encrypt", "end", "endc", "endembed", "endmacro",
      "enum", "escape", "except", "exclusive", "expire", "export", "extend", "fail", "few",
      "fileposition", "first", "flat", "forward", "from", "full", "function", "functionmacro",
      "group", "grouped", "heading", "hole", "ifblock", "import", "in", "inner", "interface",
      "internal", "joined", "keep", "keyed", "last", "left", "limit", "linkcounted", "literal",
      "little_endian", "load", "local", "locale", "lookup", "lzw", "macro", "many", "maxcount",
      "maxlength", "min skew", "module", "mofn", "multiple", "named", "namespace", "nocase",
      "noroot", "noscan", "nosort", "not", "noxpath", "of", "onfail", "only", "opt", "or", "outer",
      "overwrite", "packed", "partition", "penalty", "physicallength", "pipe", "prefetch", "quote",
      "record", "repeat", "retry", "return", "right", "right1", "right2", "rows", "rowset", "scan",
      "scope", "self", "separator", "service", "shared", "skew", "skip", "smart", "soapaction",
      "sql", "stable", "store", "terminator", "thor", "threshold", "timelimit", "timeout", "token",
      "transform", "trim", "type", "unicodeorder", "unordered", "unsorted", "unstable", "update",
      "use", "validate", "virtual", "whole", "width", "wild", "within", "wnotrim", "xml", "xpath")
  "functions" and listOf("abs", "acos", "aggregate", "allnodes", "apply", "ascii", "asin", "assert",
      "asstring", "atan", "atan2", "ave", "build", "buildindex", "case", "catch", "choose",
      "choosen", "choosesets", "clustersize", "combine", "correlation", "cos", "cosh", "count",
      "covariance", "cron", "dataset", "dedup", "define", "denormalize", "dictionary", "distribute",
      "distributed", "distribution", "ebcdic", "enth", "error", "evaluate", "event", "eventextra",
      "eventname", "exists", "exp", "fail", "failcode", "failmessage", "fetch", "fromunicode",
      "fromxml", "getenv", "getisvalid", "global", "graph", "group", "hash", "hash32", "hash64",
      "hashcrc", "hashmd5", "having", "httpcall", "httpheader", "if", "iff", "index", "intformat",
      "isvalid", "iterate", "join", "keydiff", "keypatch", "keyunicode", "length", "library",
      "limit", "ln", "loadxml", "local", "log", "loop", "map", "matched", "matchlength",
      "matchposition", "matchtext", "matchunicode", "max", "merge", "mergejoin", "min", "nofold",
      "nolocal", "nonempty", "normalize", "nothor", "notify", "output", "parallel", "parse", "pipe",
      "power", "preload", "process", "project", "pull", "random", "range", "rank", "ranked",
      "realformat", "recordof", "regexfind", "regexreplace", "regroup", "rejected", "rollup",
      "round", "roundup", "row", "rowdiff", "sample", "sequential", "set", "sin", "sinh", "sizeof",
      "soapcall", "sort", "sorted", "sqrt", "stepped", "stored", "sum", "table", "tan", "tanh",
      "thisnode", "topn", "tounicode", "toxml", "transfer", "transform", "trim", "truncate",
      "typeof", "ungroup", "unicodeorder", "variance", "wait", "which", "workunit", "xmldecode",
      "xmlencode", "xmltext", "xmlunicode")
  "typesint" and "integer|unsigned"
  "typesnum" and listOf("data", "qstring", "string", "unicode", "utf8", "varstring", "varunicode")
  "typesone" and
      "ascii|big_endian|boolean|data|decimal|ebcdic|grouped|integer|linkcounted|pattern|qstring|real|record|rule|set of|streamed|string|token|udecimal|unicode|unsigned|utf8|varstring|varunicode"
  operators("+", "-", "/", ":=", "<", "<>", "=", ">", "\\", "and", "in", "not", "or")
  symbols("[=><!~?:&|+\\-*\\/\\^%]+")
  escapes("\\\\(?:[abfnrtv\\\\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
  tokenizer {
    root {
      "@typesint[4|8]".token("type")
      "#(@pounds)".token("type")
      "@typesone".token("type")
      "[a-zA-Z_${'$'}][\\w-${'$'}]*".action {
        cases {
          "@functions" and "keyword.function"
          "@keywords" and "keyword"
          "@operators" and "operator"
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
      "[0-9_]*\\.[0-9_]+([eE][\\-+]?\\d+)?".token("number.float")
      "0[xX][0-9a-fA-F_]+".token("number.hex")
      "0[bB][01]+".token("number.hex")
      "[0-9_]+".token("number")
      "[;,.]".token("delimiter")
      "\"([^\"\\\\]|\\\\.)*${'$'}".token("string.invalid")
      "\"".action("string").state("@string")
      "'[^\\\\']'".token("string")
      "(')(@escapes)(')".actionArray {
        token("string")
        token("string.escape")
        token("string")
      }
      "'".token("string.invalid")
    }
    whitespace {
      "[ \\t\\v\\f\\r\\n]+".token("")
      "\\/\\*".action("comment").state("@comment")
      "\\/\\/.*${'$'}".token("comment")
    }
    comment {
      "[^\\/*]+".token("comment")
      "\\*\\/".action("comment").state("@pop")
      "[\\/*]".token("comment")
    }
    string {
      "[^\\\\']+".token("string")
      "@escapes".token("string.escape")
      "\\\\.".token("string.escape.invalid")
      "'".action("string").state("@pop")
    }
  }
}

