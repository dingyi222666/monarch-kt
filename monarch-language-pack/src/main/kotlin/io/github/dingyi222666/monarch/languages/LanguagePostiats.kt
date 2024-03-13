package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val PatsLanguage: IMonarchLanguage = buildMonarchLanguage {
  tokenPostfix = ".pats"
  start = "root"
  defaultToken = "invalid"
  brackets {
    bracket(",(",")","delimiter.parenthesis")
    bracket("`(",")","delimiter.parenthesis")
    bracket("%(",")","delimiter.parenthesis")
    bracket("'(",")","delimiter.parenthesis")
    bracket("'{","}","delimiter.parenthesis")
    bracket("@(",")","delimiter.parenthesis")
    bracket("@{","}","delimiter.brace")
    bracket("@[","]","delimiter.square")
    bracket("#[","]","delimiter.square")
    bracket("{","}","delimiter.curly")
    bracket("[","]","delimiter.square")
    bracket("(",")","delimiter.parenthesis")
    bracket("<",">","delimiter.angle")
  }
  keywords("abstype", "abst0ype", "absprop", "absview", "absvtype", "absviewtype", "absvt0ype",
      "absviewt0ype", "as", "and", "assume", "begin", "classdec", "datasort", "datatype",
      "dataprop", "dataview", "datavtype", "dataviewtype", "do", "end", "extern", "extype",
      "extvar", "exception", "fn", "fnx", "fun", "prfn", "prfun", "praxi", "castfn", "if", "then",
      "else", "ifcase", "in", "infix", "infixl", "infixr", "prefix", "postfix", "implmnt",
      "implement", "primplmnt", "primplement", "import", "let", "local", "macdef", "macrodef",
      "nonfix", "symelim", "symintr", "overload", "of", "op", "rec", "sif", "scase", "sortdef",
      "sta", "stacst", "stadef", "static", "staload", "dynload", "try", "tkindef", "typedef",
      "propdef", "viewdef", "vtypedef", "viewtypedef", "prval", "var", "prvar", "when", "where",
      "with", "withtype", "withprop", "withview", "withvtype", "withviewtype")
  "keywords_dlr" and listOf("\$delay", "\$ldelay", "\$arrpsz", "\$arrptrsize", "\$d2ctype",
      "\$effmask", "\$effmask_ntm", "\$effmask_exn", "\$effmask_ref", "\$effmask_wrt",
      "\$effmask_all", "\$extern", "\$extkind", "\$extype", "\$extype_struct", "\$extval",
      "\$extfcall", "\$extmcall", "\$literal", "\$myfilename", "\$mylocation", "\$myfunction",
      "\$lst", "\$lst_t", "\$lst_vt", "\$list", "\$list_t", "\$list_vt", "\$rec", "\$rec_t",
      "\$rec_vt", "\$record", "\$record_t", "\$record_vt", "\$tup", "\$tup_t", "\$tup_vt",
      "\$tuple", "\$tuple_t", "\$tuple_vt", "\$break", "\$continue", "\$raise", "\$showtype",
      "\$vcopyenv_v", "\$vcopyenv_vt", "\$tempenver", "\$solver_assert", "\$solver_verify")
  "keywords_srp" and listOf("#if", "#ifdef", "#ifndef", "#then", "#elif", "#elifdef", "#elifndef",
      "#else", "#endif", "#error", "#prerr", "#print", "#assert", "#undef", "#define", "#include",
      "#require", "#pragma", "#codegen2", "#codegen3")
  "irregular_keyword_list" and listOf("val+", "val-", "val", "case+", "case-", "case", "addr@",
      "addr", "fold@", "free@", "fix@", "fix", "lam@", "lam", "llam@", "llam", "viewt@ype+",
      "viewt@ype-", "viewt@ype", "viewtype+", "viewtype-", "viewtype", "view+", "view-", "view@",
      "view", "type+", "type-", "type", "vtype+", "vtype-", "vtype", "vt@ype+", "vt@ype-", "vt@ype",
      "viewt@ype+", "viewt@ype-", "viewt@ype", "viewtype+", "viewtype-", "viewtype", "prop+",
      "prop-", "prop", "type+", "type-", "type", "t@ype", "t@ype+", "t@ype-", "abst@ype", "abstype",
      "absviewt@ype", "absvt@ype", "for*", "for", "while*", "while")
  "keywords_types" and listOf("bool", "double", "byte", "int", "short", "char", "void", "unit",
      "long", "float", "string", "strptr")
  "keywords_effects" and listOf("0", "fun", "clo", "prf", "funclo", "cloptr", "cloref", "ref",
      "ntm", "1")
  operators("@", "!", "|", "`", ":", "\$", ".", "=", "#", "~", "..", "...", "=>", "=<>", "=/=>",
      "=>>", "=/=>>", "<", ">", "><", ".<", ">.", ".<>.", "->", "-<>")
  symbols("[=><!~?:&|+\\-*\\/\\^%]+")
  "IDENTFST" and "[a-zA-Z_]"
  "IDENTRST" and "[a-zA-Z0-9_'${'$'}]"
  "symbolic" and "[%&+-./:=@~`^|*!${'$'}#?<>]"
  "digit" and "[0-9]"
  "digitseq0" and "@digit*"
  "xdigit" and "[0-9A-Za-z]"
  "xdigitseq0" and "@xdigit*"
  "INTSP" and "[lLuU]"
  "FLOATSP" and "[fFlL]"
  "fexponent" and "[eE][+-]?[0-9]+"
  "fexponent_bin" and "[pP][+-]?[0-9]+"
  "deciexp" and "\\.[0-9]*@fexponent?"
  "hexiexp" and "\\.[0-9a-zA-Z]*@fexponent_bin?"
  "irregular_keywords" and
      "val[+-]?|case[+-]?|addr\\@?|fold\\@|free\\@|fix\\@?|lam\\@?|llam\\@?|prop[+-]?|type[+-]?|view[+-@]?|viewt@?ype[+-]?|t@?ype[+-]?|v(iew)?t@?ype[+-]?|abst@?ype|absv(iew)?t@?ype|for\\*?|while\\*?"
  "ESCHAR" and "[ntvbrfa\\\\\\?'\"\\(\\[\\{]"
  tokenizer {
    root {
      "[ \\t\\r\\n]+".action {
        token = ""
      }
      "\\(\\*\\)".action {
        token = "invalid"
      }
      "\\(\\*".action {
        token = "comment"
        next = "lexing_COMMENT_block_ml"
      }
      "\\(".token("@brackets")
      "\\)".token("@brackets")
      "\\[".token("@brackets")
      "\\]".token("@brackets")
      "\\{".token("@brackets")
      "\\}".token("@brackets")
      ",\\(".token("@brackets")
      ",".action {
        token = "delimiter.comma"
      }
      ";".action {
        token = "delimiter.semicolon"
      }
      "@\\(".token("@brackets")
      "@\\[".token("@brackets")
      "@\\{".token("@brackets")
      ":<".action {
        token = "keyword"
        next = "@lexing_EFFECT_commaseq0"
      }
      "\\.@symbolic+".action {
        token = "identifier.sym"
      }
      "\\.@digit*@fexponent@FLOATSP*".action {
        token = "number.float"
      }
      "\\.@digit+".action {
        token = "number.float"
      }
      "\\${'$'}@IDENTFST@IDENTRST*".action {
        cases {
          "@keywords_dlr" and {
            token = "keyword.dlr"
          }
          "@default" and {
            token = "namespace"
          }
        }
      }
      "\\#@IDENTFST@IDENTRST*".action {
        cases {
          "@keywords_srp" and {
            token = "keyword.srp"
          }
          "@default" and {
            token = "identifier"
          }
        }
      }
      "%\\(".action {
        token = "delimiter.parenthesis"
      }
      "^%{(#|\\^|\\${'$'})?".action {
        token = "keyword"
        next = "@lexing_EXTCODE"
        nextEmbedded = "text/javascript"
        nextEmbedded = "text/javascript"
      }
      "^%}".action {
        token = "keyword"
      }
      "'\\(".action {
        token = "delimiter.parenthesis"
      }
      "'\\[".action {
        token = "delimiter.bracket"
      }
      "'\\{".action {
        token = "delimiter.brace"
      }
      "(')(\\\\@ESCHAR|\\\\[xX]@xdigit+|\\\\@digit+)(')".actionArray {
        token("string")
        token("string.escape")
        token("string")
      }
      "'[^\\\\']'".token("string")
      "\"".action("string.quote").state("@lexing_DQUOTE")
      "`\\(".token("@brackets")
      "\\\\".action {
        token = "punctuation"
      }
      "@irregular_keywords(?!@IDENTRST)".action {
        token = "keyword"
      }
      "@IDENTFST@IDENTRST*[<!\\[]?".action {
        cases {
          "@keywords" and {
            token = "keyword"
          }
          "@keywords_types" and {
            token = "type"
          }
          "@default" and {
            token = "identifier"
          }
        }
      }
      "\\/\\/\\/\\/".action {
        token = "comment"
        next = "@lexing_COMMENT_rest"
      }
      "\\/\\/.*${'$'}".action {
        token = "comment"
      }
      "\\/\\*".action {
        token = "comment"
        next = "@lexing_COMMENT_block_c"
      }
      "-<|=<".action {
        token = "keyword"
        next = "@lexing_EFFECT_commaseq0"
      }
      "@symbolic+".action {
        cases {
          "@operators" and "keyword"
          "@default" and "operator"
        }
      }
      "0[xX]@xdigit+(@hexiexp|@fexponent_bin)@FLOATSP*".action {
        token = "number.float"
      }
      "0[xX]@xdigit+@INTSP*".action {
        token = "number.hex"
      }
      "0[0-7]+(?![0-9])@INTSP*".action {
        token = "number.octal"
      }
      "@digit+(@fexponent|@deciexp)@FLOATSP*".action {
        token = "number.float"
      }
      "@digit@digitseq0@INTSP*".action {
        token = "number.decimal"
      }
      "@digit+@INTSP*".action {
        token = "number"
      }
    }
    "lexing_COMMENT_block_ml" rules {
      "[^\\(\\*]+".token("comment")
      "\\(\\*".action("comment").state("@push")
      "\\(\\*".token("comment.invalid")
      "\\*\\)".action("comment").state("@pop")
      "\\*".token("comment")
    }
    "lexing_COMMENT_block_c" rules {
      "[^\\/*]+".token("comment")
      "\\*\\/".action("comment").state("@pop")
      "[\\/*]".token("comment")
    }
    "lexing_COMMENT_rest" rules {
      "${'$'}".action("comment").state("@pop")
      ".*".token("comment")
    }
    "lexing_EFFECT_commaseq0" rules {
      "@IDENTFST@IDENTRST+|@digit+".action {
        cases {
          "@keywords_effects" and {
            token = "type.effect"
          }
          "@default" and {
            token = "identifier"
          }
        }
      }
      ",".action {
        token = "punctuation"
      }
      ">".action {
        token = "@rematch"
        next = "@pop"
      }
    }
    "lexing_EXTCODE" rules {
      "^%}".action {
        token = "@rematch"
        next = "@pop"
        nextEmbedded = "@pop"
        nextEmbedded = "@pop"
      }
      "[^%]+".token("")
    }
    "lexing_DQUOTE" rules {
      "\"".action {
        token = "string.quote"
        next = "@pop"
      }
      "(\\{\\${'$'})(@IDENTFST@IDENTRST*)(\\})".actionArray {
        action("string.escape") {
        }
        action("identifier") {
        }
        action("string.escape") {
        }
      }
      "\\\\${'$'}".action {
        token = "string.escape"
      }
      "\\\\(@ESCHAR|[xX]@xdigit+|@digit+)".action {
        token = "string.escape"
      }
      "[^\\\\\"]+".action {
        token = "string"
      }
    }
  }
}

