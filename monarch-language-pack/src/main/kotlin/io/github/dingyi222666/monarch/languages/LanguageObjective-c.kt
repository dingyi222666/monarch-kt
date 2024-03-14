package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val `Objective-cLanguage`: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".objective-c"
    defaultToken = ""
    keywords("#import", "#include", "#define", "#else", "#endif", "#if", "#ifdef", "#ifndef",
        "#ident", "#undef", "@class", "@defs", "@dynamic", "@encode", "@end", "@implementation",
        "@interface", "@package", "@private", "@protected", "@property", "@protocol", "@public",
        "@selector", "@synthesize", "__declspec", "assign", "auto", "BOOL", "break", "bycopy",
        "byref", "case", "char", "Class", "const", "copy", "continue", "default", "do", "double",
        "else", "enum", "extern", "FALSE", "false", "float", "for", "goto", "if", "in", "int", "id",
        "inout", "IMP", "long", "nil", "nonatomic", "NULL", "oneway", "out", "private", "public",
        "protected", "readwrite", "readonly", "register", "return", "SEL", "self", "short",
        "signed", "sizeof", "static", "struct", "super", "switch", "typedef", "TRUE", "true",
        "union", "unsigned", "volatile", "void", "while")
    "decpart" and "\\d(_?\\d)*"
    "decimal" and "0|@decpart"
    tokenizer {
      root {
        include("@comments")
        include("@whitespace")
        include("@numbers")
        include("@strings")
        "[,:;]".token("delimiter")
        "[{}\\[\\]()<>]".token("@brackets")
        "[a-zA-Z@#]\\w*".action {
          cases {
            "@keywords" and "keyword"
            "@default" and "identifier"
          }
        }
        "[<>=\\\\+\\\\-\\\\*\\\\/\\\\^\\\\|\\\\~,]|and\\\\b|or\\\\b|not\\\\b]".token("operator")
      }
      whitespace {
        "\\s+".token("white")
      }
      comments {
        "\\/\\*".action("comment").state("@comment")
        "\\/\\/+.*".token("comment")
      }
      comment {
        "\\*\\/".action("comment").state("@pop")
        ".".token("comment")
      }
      "numbers" rules {
        "0[xX][0-9a-fA-F]*(_?[0-9a-fA-F])*".token("number.hex")
        "@decimal((\\.@decpart)?([eE][\\-+]?@decpart)?)[fF]*".action {
          cases {
            "(\\d)*" and "number"
            "${'$'}0" and "number.float"
          }
        }
      }
      "strings" rules {
        "'${'$'}".action("string.escape").state("@popall")
        "'".action("string.escape").state("@stringBody")
        "\"${'$'}".action("string.escape").state("@popall")
        "\"".action("string.escape").state("@dblStringBody")
      }
      "stringBody" rules {
        "[^\\\\']+${'$'}".action("string").state("@popall")
        "[^\\\\']+".token("string")
        "\\\\.".token("string")
        "'".action("string.escape").state("@popall")
        "\\\\${'$'}".token("string")
      }
      "dblStringBody" rules {
        "[^\\\\\"]+${'$'}".action("string").state("@popall")
        "[^\\\\\"]+".token("string")
        "\\\\.".token("string")
        "\"".action("string.escape").state("@popall")
        "\\\\${'$'}".token("string")
      }
    }
  }
}

