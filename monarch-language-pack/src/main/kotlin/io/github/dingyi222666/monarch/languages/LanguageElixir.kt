package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val ElixirLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".elixir"
    defaultToken = "source"
    brackets {
      bracket("[","]","delimiter.square")
      bracket("(",")","delimiter.parenthesis")
      bracket("{","}","delimiter.curly")
      bracket("<<",">>","delimiter.angle.special")
    }
    "declarationKeywords" and listOf("def", "defp", "defn", "defnp", "defguard", "defguardp",
        "defmacro", "defmacrop", "defdelegate", "defcallback", "defmacrocallback", "defmodule",
        "defprotocol", "defexception", "defimpl", "defstruct")
    "operatorKeywords" and listOf("and", "in", "not", "or", "when")
    "namespaceKeywords" and listOf("alias", "import", "require", "use")
    "otherKeywords" and listOf("after", "case", "catch", "cond", "do", "else", "end", "fn", "for",
        "if", "quote", "raise", "receive", "rescue", "super", "throw", "try", "unless",
        "unquote_splicing", "unquote", "with")
    "constants" and listOf("true", "false", "nil")
    "nameBuiltin" and listOf("__MODULE__", "__DIR__", "__ENV__", "__CALLER__", "__STACKTRACE__")
    "operator" and
        "-[->]?|!={0,2}|\\*{1,2}|\\/|\\\\\\\\|&{1,3}|\\.\\.?|\\^(?:\\^\\^)?|\\+\\+?|<(?:-|<<|=|>|\\|>|~>?)?|=~|={1,3}|>(?:=|>>)?|\\|~>|\\|>|\\|{1,3}|~>>?|~~~|::"
    "variableName" and "[a-z_][a-zA-Z0-9_]*[?!]?"
    "atomName" and "[a-zA-Z_][a-zA-Z0-9_@]*[?!]?|@specialAtomName|@operator"
    "specialAtomName" and "\\.\\.\\.|<<>>|%\\{\\}|%|\\{\\}"
    "aliasPart" and "[A-Z][a-zA-Z0-9_]*"
    "moduleName" and "@aliasPart(?:\\.@aliasPart)*"
    "sigilSymmetricDelimiter" and "\"\"\"|'''|\"|'|\\/|\\|"
    "sigilStartDelimiter" and "@sigilSymmetricDelimiter|<|\\{|\\[|\\("
    "sigilEndDelimiter" and "@sigilSymmetricDelimiter|>|\\}|\\]|\\)"
    "sigilModifiers" and "[a-zA-Z0-9]*"
    "decimal" and "\\d(?:_?\\d)*"
    "hex" and "[0-9a-fA-F](_?[0-9a-fA-F])*"
    "octal" and "[0-7](_?[0-7])*"
    "binary" and "[01](_?[01])*"
    "escape" and "\\\\u[0-9a-fA-F]{4}|\\\\x[0-9a-fA-F]{2}|\\\\."
    tokenizer {
      root {
        include("@whitespace")
        include("@comments")
        include("@keywordsShorthand")
        include("@numbers")
        include("@identifiers")
        include("@strings")
        include("@atoms")
        include("@sigils")
        include("@attributes")
        include("@symbols")
      }
      whitespace {
        "\\s+".token("white")
      }
      comments {
        "(#)(.*)".actionArray {
          token("comment.punctuation")
          token("comment")
        }
      }
      "keywordsShorthand" rules {
        "(@atomName)(:)(\\s+)".actionArray {
          token("constant")
          token("constant.punctuation")
          token("white")
        }
        "\"(?=([^\"]|#\\{.*?\\}|\\\\\")*\":)".action {
          token = "constant.delimiter"
          next = "@doubleQuotedStringKeyword"
        }
        "'(?=([^']|#\\{.*?\\}|\\\\')*':)".action {
          token = "constant.delimiter"
          next = "@singleQuotedStringKeyword"
        }
      }
      "doubleQuotedStringKeyword" rules {
        "\":".action {
          token = "constant.delimiter"
          next = "@pop"
        }
        include("@stringConstantContentInterpol")
      }
      "singleQuotedStringKeyword" rules {
        "':".action {
          token = "constant.delimiter"
          next = "@pop"
        }
        include("@stringConstantContentInterpol")
      }
      "numbers" rules {
        "0b@binary".token("number.binary")
        "0o@octal".token("number.octal")
        "0x@hex".token("number.hex")
        "@decimal\\.@decimal([eE]-?@decimal)?".token("number.float")
        "@decimal".token("number")
      }
      "identifiers" rules {
        "\\b(defp?|defnp?|defmacrop?|defguardp?|defdelegate)(\\s+)(@variableName)(?!\\s+@operator)".actionArray {
          token("keyword.declaration")
          token("white")
          action {
            cases {
              "unquote" and "keyword"
              "@default" and "function"
            }
          }
        }
        "(@variableName)(?=\\s*\\.?\\s*\\()".action {
          cases {
            "@declarationKeywords" and "keyword.declaration"
            "@namespaceKeywords" and "keyword"
            "@otherKeywords" and "keyword"
            "@default" and "function.call"
          }
        }
        "(@moduleName)(\\s*)(\\.)(\\s*)(@variableName)".actionArray {
          token("type.identifier")
          token("white")
          token("operator")
          token("white")
          token("function.call")
        }
        "(:)(@atomName)(\\s*)(\\.)(\\s*)(@variableName)".actionArray {
          token("constant.punctuation")
          token("constant")
          token("white")
          token("operator")
          token("white")
          token("function.call")
        }
        "(\\|>)(\\s*)(@variableName)".actionArray {
          token("operator")
          token("white")
          action {
            cases {
              "@otherKeywords" and "keyword"
              "@default" and "function.call"
            }
          }
        }
        "(&)(\\s*)(@variableName)".actionArray {
          token("operator")
          token("white")
          token("function.call")
        }
        "@variableName".action {
          cases {
            "@declarationKeywords" and "keyword.declaration"
            "@operatorKeywords" and "keyword.operator"
            "@namespaceKeywords" and "keyword"
            "@otherKeywords" and "keyword"
            "@constants" and "constant.language"
            "@nameBuiltin" and "variable.language"
            "_.*" and "comment.unused"
            "@default" and "identifier"
          }
        }
        "@moduleName".token("type.identifier")
      }
      "strings" rules {
        "\"\"\"".action {
          token = "string.delimiter"
          next = "@doubleQuotedHeredoc"
        }
        "'''".action {
          token = "string.delimiter"
          next = "@singleQuotedHeredoc"
        }
        "\"".action {
          token = "string.delimiter"
          next = "@doubleQuotedString"
        }
        "'".action {
          token = "string.delimiter"
          next = "@singleQuotedString"
        }
      }
      "doubleQuotedHeredoc" rules {
        "\"\"\"".action {
          token = "string.delimiter"
          next = "@pop"
        }
        include("@stringContentInterpol")
      }
      "singleQuotedHeredoc" rules {
        "'''".action {
          token = "string.delimiter"
          next = "@pop"
        }
        include("@stringContentInterpol")
      }
      "doubleQuotedString" rules {
        "\"".action {
          token = "string.delimiter"
          next = "@pop"
        }
        include("@stringContentInterpol")
      }
      "singleQuotedString" rules {
        "'".action {
          token = "string.delimiter"
          next = "@pop"
        }
        include("@stringContentInterpol")
      }
      "atoms" rules {
        "(:)(@atomName)".actionArray {
          token("constant.punctuation")
          token("constant")
        }
        ":\"".action {
          token = "constant.delimiter"
          next = "@doubleQuotedStringAtom"
        }
        ":'".action {
          token = "constant.delimiter"
          next = "@singleQuotedStringAtom"
        }
      }
      "doubleQuotedStringAtom" rules {
        "\"".action {
          token = "constant.delimiter"
          next = "@pop"
        }
        include("@stringConstantContentInterpol")
      }
      "singleQuotedStringAtom" rules {
        "'".action {
          token = "constant.delimiter"
          next = "@pop"
        }
        include("@stringConstantContentInterpol")
      }
      "sigils" rules {
        "~[a-z]@sigilStartDelimiter".action {
          token = "@rematch"
          next = "@sigil.interpol"
        }
        "~([A-Z]+)@sigilStartDelimiter".action {
          token = "@rematch"
          next = "@sigil.noInterpol"
        }
      }
      "sigil" rules {
        "~([a-z]|[A-Z]+)\\{".action {
          token = "@rematch"
          switchTo = "@sigilStart.${'$'}S2.${'$'}1.{.}"
        }
        "~([a-z]|[A-Z]+)\\[".action {
          token = "@rematch"
          switchTo = "@sigilStart.${'$'}S2.${'$'}1.[.]"
        }
        "~([a-z]|[A-Z]+)\\(".action {
          token = "@rematch"
          switchTo = "@sigilStart.${'$'}S2.${'$'}1.(.)"
        }
        "~([a-z]|[A-Z]+)\\<".action {
          token = "@rematch"
          switchTo = "@sigilStart.${'$'}S2.${'$'}1.<.>"
        }
        "~([a-z]|[A-Z]+)(@sigilSymmetricDelimiter)".action {
          token = "@rematch"
          switchTo = "@sigilStart.${'$'}S2.${'$'}1.${'$'}2.${'$'}2"
        }
      }
      "sigilStart.interpol.s" rules {
        "~s@sigilStartDelimiter".action {
          token = "string.delimiter"
          switchTo = "@sigilContinue.${'$'}S2.${'$'}S3.${'$'}S4.${'$'}S5"
        }
      }
      "sigilContinue.interpol.s" rules {
        "(@sigilEndDelimiter)@sigilModifiers".action {
          cases {
            "${'$'}1==${'$'}S5" and {
              token = "string.delimiter"
              next = "@pop"
            }
            "@default" and "string"
          }
        }
        include("@stringContentInterpol")
      }
      "sigilStart.noInterpol.S" rules {
        "~S@sigilStartDelimiter".action {
          token = "string.delimiter"
          switchTo = "@sigilContinue.${'$'}S2.${'$'}S3.${'$'}S4.${'$'}S5"
        }
      }
      "sigilContinue.noInterpol.S" rules {
        "(^|[^\\\\])\\\\@sigilEndDelimiter".token("string")
        "(@sigilEndDelimiter)@sigilModifiers".action {
          cases {
            "${'$'}1==${'$'}S5" and {
              token = "string.delimiter"
              next = "@pop"
            }
            "@default" and "string"
          }
        }
        include("@stringContent")
      }
      "sigilStart.interpol.r" rules {
        "~r@sigilStartDelimiter".action {
          token = "regexp.delimiter"
          switchTo = "@sigilContinue.${'$'}S2.${'$'}S3.${'$'}S4.${'$'}S5"
        }
      }
      "sigilContinue.interpol.r" rules {
        "(@sigilEndDelimiter)@sigilModifiers".action {
          cases {
            "${'$'}1==${'$'}S5" and {
              token = "regexp.delimiter"
              next = "@pop"
            }
            "@default" and "regexp"
          }
        }
        include("@regexpContentInterpol")
      }
      "sigilStart.noInterpol.R" rules {
        "~R@sigilStartDelimiter".action {
          token = "regexp.delimiter"
          switchTo = "@sigilContinue.${'$'}S2.${'$'}S3.${'$'}S4.${'$'}S5"
        }
      }
      "sigilContinue.noInterpol.R" rules {
        "(^|[^\\\\])\\\\@sigilEndDelimiter".token("regexp")
        "(@sigilEndDelimiter)@sigilModifiers".action {
          cases {
            "${'$'}1==${'$'}S5" and {
              token = "regexp.delimiter"
              next = "@pop"
            }
            "@default" and "regexp"
          }
        }
        include("@regexpContent")
      }
      "sigilStart.interpol" rules {
        "~([a-z]|[A-Z]+)@sigilStartDelimiter".action {
          token = "sigil.delimiter"
          switchTo = "@sigilContinue.${'$'}S2.${'$'}S3.${'$'}S4.${'$'}S5"
        }
      }
      "sigilContinue.interpol" rules {
        "(@sigilEndDelimiter)@sigilModifiers".action {
          cases {
            "${'$'}1==${'$'}S5" and {
              token = "sigil.delimiter"
              next = "@pop"
            }
            "@default" and "sigil"
          }
        }
        include("@sigilContentInterpol")
      }
      "sigilStart.noInterpol" rules {
        "~([a-z]|[A-Z]+)@sigilStartDelimiter".action {
          token = "sigil.delimiter"
          switchTo = "@sigilContinue.${'$'}S2.${'$'}S3.${'$'}S4.${'$'}S5"
        }
      }
      "sigilContinue.noInterpol" rules {
        "(^|[^\\\\])\\\\@sigilEndDelimiter".token("sigil")
        "(@sigilEndDelimiter)@sigilModifiers".action {
          cases {
            "${'$'}1==${'$'}S5" and {
              token = "sigil.delimiter"
              next = "@pop"
            }
            "@default" and "sigil"
          }
        }
        include("@sigilContent")
      }
      "attributes" rules {
        "\\@(module|type)?doc (~[sS])?\"\"\"".action {
          token = "comment.block.documentation"
          next = "@doubleQuotedHeredocDocstring"
        }
        "\\@(module|type)?doc (~[sS])?'''".action {
          token = "comment.block.documentation"
          next = "@singleQuotedHeredocDocstring"
        }
        "\\@(module|type)?doc (~[sS])?\"".action {
          token = "comment.block.documentation"
          next = "@doubleQuotedStringDocstring"
        }
        "\\@(module|type)?doc (~[sS])?'".action {
          token = "comment.block.documentation"
          next = "@singleQuotedStringDocstring"
        }
        "\\@(module|type)?doc false".token("comment.block.documentation")
        "\\@(@variableName)".token("variable")
      }
      "doubleQuotedHeredocDocstring" rules {
        "\"\"\"".action {
          token = "comment.block.documentation"
          next = "@pop"
        }
        include("@docstringContent")
      }
      "singleQuotedHeredocDocstring" rules {
        "'''".action {
          token = "comment.block.documentation"
          next = "@pop"
        }
        include("@docstringContent")
      }
      "doubleQuotedStringDocstring" rules {
        "\"".action {
          token = "comment.block.documentation"
          next = "@pop"
        }
        include("@docstringContent")
      }
      "singleQuotedStringDocstring" rules {
        "'".action {
          token = "comment.block.documentation"
          next = "@pop"
        }
        include("@docstringContent")
      }
      "symbols" rules {
        "\\?(\\\\.|[^\\\\\\s])".token("number.constant")
        "&\\d+".token("operator")
        "<<<|>>>".token("operator")
        "[()\\[\\]\\{\\}]|<<|>>".token("@brackets")
        "\\.\\.\\.".token("identifier")
        "=>".token("punctuation")
        "@operator".token("operator")
        "[:;,.%]".token("punctuation")
      }
      "stringContentInterpol" rules {
        include("@interpolation")
        include("@escapeChar")
        include("@stringContent")
      }
      "stringContent" rules {
        ".".token("string")
      }
      "stringConstantContentInterpol" rules {
        include("@interpolation")
        include("@escapeChar")
        include("@stringConstantContent")
      }
      "stringConstantContent" rules {
        ".".token("constant")
      }
      "regexpContentInterpol" rules {
        include("@interpolation")
        include("@escapeChar")
        include("@regexpContent")
      }
      "regexpContent" rules {
        "(\\s)(#)(\\s.*)${'$'}".actionArray {
          token("white")
          token("comment.punctuation")
          token("comment")
        }
        ".".token("regexp")
      }
      "sigilContentInterpol" rules {
        include("@interpolation")
        include("@escapeChar")
        include("@sigilContent")
      }
      "sigilContent" rules {
        ".".token("sigil")
      }
      "docstringContent" rules {
        ".".token("comment.block.documentation")
      }
      "escapeChar" rules {
        "@escape".token("constant.character.escape")
      }
      "interpolation" rules {
        "#{".action {
          token = "delimiter.bracket.embed"
          next = "@interpolationContinue"
        }
      }
      "interpolationContinue" rules {
        "}".action {
          token = "delimiter.bracket.embed"
          next = "@pop"
        }
        include("@root")
      }
    }
  }
}

