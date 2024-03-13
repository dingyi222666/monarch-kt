package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val RubyLanguage: IMonarchLanguage = buildMonarchLanguage {
  tokenPostfix = ".ruby"
  brackets {
    bracket("(",")","delimiter.parenthesis")
    bracket("{","}","delimiter.curly")
    bracket("[","]","delimiter.square")
  }
  keywords("__LINE__", "__ENCODING__", "__FILE__", "BEGIN", "END", "alias", "and", "begin", "break",
      "case", "class", "def", "defined?", "do", "else", "elsif", "end", "ensure", "for", "false",
      "if", "in", "module", "next", "nil", "not", "or", "redo", "rescue", "retry", "return", "self",
      "super", "then", "true", "undef", "unless", "until", "when", "while", "yield")
  "keywordops" and listOf("::", "..", "...", "?", ":", "=>")
  "builtins" and listOf("require", "public", "private", "include", "extend", "attr_reader",
      "protected", "private_class_method", "protected_class_method", "new")
  "declarations" and listOf("module", "class", "def", "case", "do", "begin", "for", "if", "while",
      "until", "unless")
  "linedecls" and listOf("def", "case", "do", "begin", "for", "if", "while", "until", "unless")
  operators("^", "&", "|", "<=>", "==", "===", "!~", "=~", ">", ">=", "<", "<=", "<<", ">>", "+",
      "-", "*", "/", "%", "**", "~", "+@", "-@", "[]", "[]=", "`", "+=", "-=", "*=", "**=", "/=",
      "^=", "%=", "<<=", ">>=", "&=", "&&=", "||=", "|=")
  symbols("[=><!~?:&|+\\-*\\/\\^%\\.]+")
  "escape" and "(?:[abefnrstv\\\\\"'\\n\\r]|[0-7]{1,3}|x[0-9A-Fa-f]{1,2}|u[0-9A-Fa-f]{4})"
  escapes("\\\\(?:C\\-(@escape|.)|c(@escape|.)|@escape)")
  "decpart" and "\\d(_?\\d)*"
  "decimal" and "0|@decpart"
  "delim" and "[^a-zA-Z0-9\\s\\n\\r]"
  "heredelim" and "(?:\\w+|'[^']*'|\"[^\"]*\"|`[^`]*`)"
  "regexpctl" and "[(){}\\[\\]\\${'$'}\\^|\\-*+?\\.]"
  "regexpesc" and
      "\\\\(?:[AzZbBdDfnrstvwWn0\\\\\\/]|@regexpctl|c[A-Z]|x[0-9a-fA-F]{2}|u[0-9a-fA-F]{4})?"
  tokenizer {
    root {
      "^(\\s*)([a-z_]\\w*[!?=]?)".actionArray {
        token("white")
        action {
          cases {
            "for|until|while" and {
              token = "keyword.${'$'}2"
              next = "@dodecl.${'$'}2"
            }
            "@declarations" and {
              token = "keyword.${'$'}2"
              next = "@root.${'$'}2"
            }
            "end" and {
              token = "keyword.${'$'}S2"
              next = "@pop"
            }
            "@keywords" and "keyword"
            "@builtins" and "predefined"
            "@default" and "identifier"
          }
        }
      }
      "[a-z_]\\w*[!?=]?".action {
        cases {
          "if|unless|while|until" and {
            token = "keyword.${'$'}0x"
            next = "@modifier.${'$'}0x"
          }
          "for" and {
            token = "keyword.${'$'}2"
            next = "@dodecl.${'$'}2"
          }
          "@linedecls" and {
            token = "keyword.${'$'}0"
            next = "@root.${'$'}0"
          }
          "end" and {
            token = "keyword.${'$'}S2"
            next = "@pop"
          }
          "@keywords" and "keyword"
          "@builtins" and "predefined"
          "@default" and "identifier"
        }
      }
      "[A-Z][\\w]*[!?=]?".token("constructor.identifier")
      "\\${'$'}[\\w]*".token("global.constant")
      "@[\\w]*".token("namespace.instance.identifier")
      "@@@[\\w]*".token("namespace.class.identifier")
      "<<[-~](@heredelim).*".action {
        token = "string.heredoc.delimiter"
        next = "@heredoc.${'$'}1"
      }
      "[ \\t\\r\\n]+<<(@heredelim).*".action {
        token = "string.heredoc.delimiter"
        next = "@heredoc.${'$'}1"
      }
      "^<<(@heredelim).*".action {
        token = "string.heredoc.delimiter"
        next = "@heredoc.${'$'}1"
      }
      include("@whitespace")
      "\"".action {
        token = "string.d.delim"
        next = "@dstring.d.\""
      }
      "'".action {
        token = "string.sq.delim"
        next = "@sstring.sq"
      }
      "%([rsqxwW]|Q?)".action {
        token = "@rematch"
        next = "pstring"
      }
      "`".action {
        token = "string.x.delim"
        next = "@dstring.x.`"
      }
      ":(\\w|[${'$'}@])\\w*[!?=]?".token("string.s")
      ":\"".action {
        token = "string.s.delim"
        next = "@dstring.s.\""
      }
      ":'".action {
        token = "string.s.delim"
        next = "@sstring.s"
      }
      "\\/(?=(\\\\\\/|[^\\/\\n])+\\/)".action {
        token = "regexp.delim"
        next = "@regexp"
      }
      "[{}()\\[\\]]".token("@brackets")
      "@symbols".action {
        cases {
          "@keywordops" and "keyword"
          "@operators" and "operator"
          "@default" and ""
        }
      }
      "[;,]".token("delimiter")
      "0[xX][0-9a-fA-F](_?[0-9a-fA-F])*".token("number.hex")
      "0[_oO][0-7](_?[0-7])*".token("number.octal")
      "0[bB][01](_?[01])*".token("number.binary")
      "0[dD]@decpart".token("number")
      "@decimal((\\.@decpart)?([eE][\\-+]?@decpart)?)".action {
        cases {
          "${'$'}1" and "number.float"
          "@default" and "number"
        }
      }
    }
    "dodecl" rules {
      "^".action {
        token = ""
        switchTo = "@root.${'$'}S2"
      }
      "[a-z_]\\w*[!?=]?".action {
        cases {
          "end" and {
            token = "keyword.${'$'}S2"
            next = "@pop"
          }
          "do" and {
            token = "keyword"
            switchTo = "@root.${'$'}S2"
          }
          "@linedecls" and {
            token = "@rematch"
            switchTo = "@root.${'$'}S2"
          }
          "@keywords" and "keyword"
          "@builtins" and "predefined"
          "@default" and "identifier"
        }
      }
      include("@root")
    }
    "modifier" rules {
      "^".action("").state("@pop")
      "[a-z_]\\w*[!?=]?".action {
        cases {
          "end" and {
            token = "keyword.${'$'}S2"
            next = "@pop"
          }
          "then|else|elsif|do" and {
            token = "keyword"
            switchTo = "@root.${'$'}S2"
          }
          "@linedecls" and {
            token = "@rematch"
            switchTo = "@root.${'$'}S2"
          }
          "@keywords" and "keyword"
          "@builtins" and "predefined"
          "@default" and "identifier"
        }
      }
      include("@root")
    }
    "sstring" rules {
      "[^\\\\']+".token("string.${'$'}S2")
      "\\\\\\\\|\\\\'|\\\\${'$'}".token("string.${'$'}S2.escape")
      "\\\\.".token("string.${'$'}S2.invalid")
      "'".action {
        token = "string.${'$'}S2.delim"
        next = "@pop"
      }
    }
    "dstring" rules {
      "[^\\\\`\"#]+".token("string.${'$'}S2")
      "#".action("string.${'$'}S2.escape").state("@interpolated")
      "\\\\${'$'}".token("string.${'$'}S2.escape")
      "@escapes".token("string.${'$'}S2.escape")
      "\\\\.".token("string.${'$'}S2.escape.invalid")
      "[`\"]".action {
        cases {
          "${'$'}#==${'$'}S3" and {
            token = "string.${'$'}S2.delim"
            next = "@pop"
          }
          "@default" and "string.${'$'}S2"
        }
      }
    }
    "heredoc" rules {
      "^(\\s*)(@heredelim)${'$'}".action {
        cases {
          "${'$'}2==${'$'}S2" actionArray {
            token("string.heredoc")
            action("string.heredoc.delimiter") {
              next = "@pop"
            }
          }
          "@default" actionArray {
            token("string.heredoc")
            token("string.heredoc")
          }
        }
      }
      ".*".token("string.heredoc")
    }
    "interpolated" rules {
      "\\${'$'}\\w*".action("global.constant").state("@pop")
      "@\\w*".action("namespace.class.identifier").state("@pop")
      "@@@\\w*".action("namespace.instance.identifier").state("@pop")
      "[{]".action {
        token = "string.escape.curly"
        switchTo = "@interpolated_compound"
      }
      "".action("").state("@pop")
    }
    "interpolated_compound" rules {
      "[}]".action {
        token = "string.escape.curly"
        next = "@pop"
      }
      include("@root")
    }
    "pregexp" rules {
      include("@whitespace")
      "[^\\(\\{\\[\\\\]".action {
        cases {
          "${'$'}#==${'$'}S3" and {
            token = "regexp.delim"
            next = "@pop"
          }
          "${'$'}#==${'$'}S2" and {
            token = "regexp.delim"
            next = "@push"
          }
          "~[)}\\]]" and "@brackets.regexp.escape.control"
          "~@regexpctl" and "regexp.escape.control"
          "@default" and "regexp"
        }
      }
      include("@regexcontrol")
    }
    "regexp" rules {
      include("@regexcontrol")
      "[^\\\\\\/]".token("regexp")
      "/[ixmp]*" action "regexp.delim" state "@pop"
    }
    "regexcontrol" rules {
      "(\\{)(\\d+(?:,\\d*)?)(\\})".actionArray {
        token("@brackets.regexp.escape.control")
        token("regexp.escape.control")
        token("@brackets.regexp.escape.control")
      }
      "(\\[)(\\^?)".actionArray {
        token("@brackets.regexp.escape.control")
        action("regexp.escape.control") {
          next = "@regexrange"
        }
      }
      "(\\()(\\?[:=!])".actionArray {
        token("@brackets.regexp.escape.control")
        token("regexp.escape.control")
      }
      "\\(\\?#".action {
        token = "regexp.escape.control"
        next = "@regexpcomment"
      }
      "[()]".token("@brackets.regexp.escape.control")
      "@regexpctl".token("regexp.escape.control")
      "\\\\${'$'}".token("regexp.escape")
      "@regexpesc".token("regexp.escape")
      "\\\\\\.".token("regexp.invalid")
      "#".action("regexp.escape").state("@interpolated")
    }
    "regexrange" rules {
      "-".token("regexp.escape.control")
      "\\^".token("regexp.invalid")
      "\\\\${'$'}".token("regexp.escape")
      "@regexpesc".token("regexp.escape")
      "[^\\]]".token("regexp")
      "\\]".action("@brackets.regexp.escape.control").state("@pop")
    }
    "regexpcomment" rules {
      "[^)]+".token("comment")
      "\\)".action {
        token = "regexp.escape.control"
        next = "@pop"
      }
    }
    "pstring" rules {
      "%([qws])\\(".action {
        token = "string.${'$'}1.delim"
        switchTo = "@qstring.${'$'}1.(.)"
      }
      "%([qws])\\[".action {
        token = "string.${'$'}1.delim"
        switchTo = "@qstring.${'$'}1.[.]"
      }
      "%([qws])\\{".action {
        token = "string.${'$'}1.delim"
        switchTo = "@qstring.${'$'}1.{.}"
      }
      "%([qws])<".action {
        token = "string.${'$'}1.delim"
        switchTo = "@qstring.${'$'}1.<.>"
      }
      "%([qws])(@delim)".action {
        token = "string.${'$'}1.delim"
        switchTo = "@qstring.${'$'}1.${'$'}2.${'$'}2"
      }
      "%r\\(".action {
        token = "regexp.delim"
        switchTo = "@pregexp.(.)"
      }
      "%r\\[".action {
        token = "regexp.delim"
        switchTo = "@pregexp.[.]"
      }
      "%r\\{".action {
        token = "regexp.delim"
        switchTo = "@pregexp.{.}"
      }
      "%r<".action {
        token = "regexp.delim"
        switchTo = "@pregexp.<.>"
      }
      "%r(@delim)".action {
        token = "regexp.delim"
        switchTo = "@pregexp.${'$'}1.${'$'}1"
      }
      "%(x|W|Q?)\\(".action {
        token = "string.${'$'}1.delim"
        switchTo = "@qqstring.${'$'}1.(.)"
      }
      "%(x|W|Q?)\\[".action {
        token = "string.${'$'}1.delim"
        switchTo = "@qqstring.${'$'}1.[.]"
      }
      "%(x|W|Q?)\\{".action {
        token = "string.${'$'}1.delim"
        switchTo = "@qqstring.${'$'}1.{.}"
      }
      "%(x|W|Q?)<".action {
        token = "string.${'$'}1.delim"
        switchTo = "@qqstring.${'$'}1.<.>"
      }
      "%(x|W|Q?)(@delim)".action {
        token = "string.${'$'}1.delim"
        switchTo = "@qqstring.${'$'}1.${'$'}2.${'$'}2"
      }
      "%([rqwsxW]|Q?).".action {
        token = "invalid"
        next = "@pop"
      }
      ".".action {
        token = "invalid"
        next = "@pop"
      }
    }
    "qstring" rules {
      "\\\\${'$'}".token("string.${'$'}S2.escape")
      "\\\\.".token("string.${'$'}S2.escape")
      ".".action {
        cases {
          "${'$'}#==${'$'}S4" and {
            token = "string.${'$'}S2.delim"
            next = "@pop"
          }
          "${'$'}#==${'$'}S3" and {
            token = "string.${'$'}S2.delim"
            next = "@push"
          }
          "@default" and "string.${'$'}S2"
        }
      }
    }
    "qqstring" rules {
      "#".action("string.${'$'}S2.escape").state("@interpolated")
      include("@qstring")
    }
    whitespace {
      "[ \\t\\r\\n]+".token("")
      "^\\s*=begin\\b".action("comment").state("@comment")
      "#.*${'$'}".token("comment")
    }
    comment {
      "[^=]+".token("comment")
      "^\\s*=begin\\b".token("comment.invalid")
      "^\\s*=end\\b.*".action("comment").state("@pop")
      "[=]".token("comment")
    }
  }
}

