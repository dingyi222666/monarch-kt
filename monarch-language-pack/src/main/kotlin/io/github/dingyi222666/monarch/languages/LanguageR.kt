package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val RLanguage: IMonarchLanguage = buildMonarchLanguage {
  tokenPostfix = ".r"
  defaultToken = ""
  brackets {
    bracket("{","}","delimiter.curly")
    bracket("[","]","delimiter.bracket")
    bracket("(",")","delimiter.parenthesis")
  }
  "roxygen" and listOf("@alias", "@aliases", "@assignee", "@author", "@backref", "@callGraph",
      "@callGraphDepth", "@callGraphPrimitives", "@concept", "@describeIn", "@description",
      "@details", "@docType", "@encoding", "@evalNamespace", "@evalRd", "@example", "@examples",
      "@export", "@exportClass", "@exportMethod", "@exportPattern", "@family", "@field", "@formals",
      "@format", "@import", "@importClassesFrom", "@importFrom", "@importMethodsFrom", "@include",
      "@inherit", "@inheritDotParams", "@inheritParams", "@inheritSection", "@keywords", "@md",
      "@method", "@name", "@noMd", "@noRd", "@note", "@param", "@rawNamespace", "@rawRd", "@rdname",
      "@references", "@return", "@S3method", "@section", "@seealso", "@setClass", "@slot",
      "@source", "@template", "@templateVar", "@title", "@TODO", "@usage", "@useDynLib")
  "constants" and listOf("NULL", "FALSE", "TRUE", "NA", "Inf", "NaN", "NA_integer_", "NA_real_",
      "NA_complex_", "NA_character_", "T", "F", "LETTERS", "letters", "month.abb", "month.name",
      "pi", "R.version.string")
  keywords("break", "next", "return", "if", "else", "for", "in", "repeat", "while", "array",
      "category", "character", "complex", "double", "function", "integer", "list", "logical",
      "matrix", "numeric", "vector", "data.frame", "factor", "library", "require", "attach",
      "detach", "source")
  "special" and listOf("\\n", "\\r", "\\t", "\\b", "\\a", "\\f", "\\v", "\\'", "\\"", "\\\\")
  tokenizer {
    root {
      include("@numbers")
      include("@strings")
      "[{}\\[\\]()]".token("@brackets")
      include("@operators")
      "#'${'$'}".token("comment.doc")
      "#'".action("comment.doc").state("@roxygen")
      "(^#.*${'$'})".token("comment")
      "\\s+".token("white")
      "[,:;]".token("delimiter")
      "@[a-zA-Z]\\w*".token("tag")
      "[a-zA-Z]\\w*".action {
        cases {
          "@keywords" and "keyword"
          "@constants" and "constant"
          "@default" and "identifier"
        }
      }
    }
    "roxygen" rules {
      "@\\w+".action {
        cases {
          "@roxygen" and "tag"
          "@eos" and {
            token = "comment.doc"
            next = "@pop"
          }
          "@default" and "comment.doc"
        }
      }
      "\\s+".action {
        cases {
          "@eos" and {
            token = "comment.doc"
            next = "@pop"
          }
          "@default" and "comment.doc"
        }
      }
      ".*".action {
        token = "comment.doc"
        next = "@pop"
      }
    }
    "numbers" rules {
      "0[xX][0-9a-fA-F]+".token("number.hex")
      "-?(\\d*\\.)?\\d+([eE][+\\-]?\\d+)?".token("number")
    }
    "operators" rules {
      "<{1,2}-".token("operator")
      "->{1,2}".token("operator")
      "%[^%\\s]+%".token("operator")
      "\\*\\*".token("operator")
      "%%".token("operator")
      "&&".token("operator")
      "\\|\\|".token("operator")
      "<<".token("operator")
      ">>".token("operator")
      "[-+=&|!<>^~*/:${'$'}]".token("operator")
    }
    "strings" rules {
      "'".action("string.escape").state("@stringBody")
      "\"".action("string.escape").state("@dblStringBody")
    }
    "stringBody" rules {
      "\\\\.".action {
        cases {
          "@special" and "string"
          "@default" and "error-token"
        }
      }
      "'".action("string.escape").state("@popall")
      ".".token("string")
    }
    "dblStringBody" rules {
      "\\\\.".action {
        cases {
          "@special" and "string"
          "@default" and "error-token"
        }
      }
      "\"".action("string.escape").state("@popall")
      ".".token("string")
    }
  }
}

