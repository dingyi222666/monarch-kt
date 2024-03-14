package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val CppLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".cpp"
    defaultToken = ""
    brackets {
      bracket("{","}","delimiter.curly")
      bracket("(",")","delimiter.parenthesis")
      bracket("[","]","delimiter.square")
      bracket("<",">","delimiter.angle")
    }
    keywords("abstract", "amp", "array", "auto", "bool", "break", "case", "catch", "char", "class",
        "const", "constexpr", "const_cast", "continue", "cpu", "decltype", "default", "delegate",
        "delete", "do", "double", "dynamic_cast", "each", "else", "enum", "event", "explicit",
        "export", "extern", "false", "final", "finally", "float", "for", "friend", "gcnew",
        "generic", "goto", "if", "in", "initonly", "inline", "int", "interface", "interior_ptr",
        "internal", "literal", "long", "mutable", "namespace", "new", "noexcept", "nullptr",
        "__nullptr", "operator", "override", "partial", "pascal", "pin_ptr", "private", "property",
        "protected", "public", "ref", "register", "reinterpret_cast", "restrict", "return",
        "safe_cast", "sealed", "short", "signed", "sizeof", "static", "static_assert",
        "static_cast", "struct", "switch", "template", "this", "thread_local", "throw",
        "tile_static", "true", "try", "typedef", "typeid", "typename", "union", "unsigned", "using",
        "virtual", "void", "volatile", "wchar_t", "where", "while", "_asm", "_based", "_cdecl",
        "_declspec", "_fastcall", "_if_exists", "_if_not_exists", "_inline",
        "_multiple_inheritance", "_pascal", "_single_inheritance", "_stdcall",
        "_virtual_inheritance", "_w64", "__abstract", "__alignof", "__asm", "__assume", "__based",
        "__box", "__builtin_alignof", "__cdecl", "__clrcall", "__declspec", "__delegate", "__event",
        "__except", "__fastcall", "__finally", "__forceinline", "__gc", "__hook", "__identifier",
        "__if_exists", "__if_not_exists", "__inline", "__int128", "__int16", "__int32", "__int64",
        "__int8", "__interface", "__leave", "__m128", "__m128d", "__m128i", "__m256", "__m256d",
        "__m256i", "__m512", "__m512d", "__m512i", "__m64", "__multiple_inheritance", "__newslot",
        "__nogc", "__noop", "__nounwind", "__novtordisp", "__pascal", "__pin", "__pragma",
        "__property", "__ptr32", "__ptr64", "__raise", "__restrict", "__resume", "__sealed",
        "__single_inheritance", "__stdcall", "__super", "__thiscall", "__try", "__try_cast",
        "__typeof", "__unaligned", "__unhook", "__uuidof", "__value", "__virtual_inheritance",
        "__w64", "__wchar_t")
    operators("=", ">", "<", "!", "~", "?", ":", "==", "<=", ">=", "!=", "&&", "||", "++", "--",
        "+", "-", "*", "/", "&", "|", "^", "%", "<<", ">>", "+=", "-=", "*=", "/=", "&=", "|=",
        "^=", "%=", "<<=", ">>=")
    symbols("[=><!~?:&|+\\-*\\/\\^%]+")
    escapes("\\\\(?:[0abfnrtv\\\\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
    "integersuffix" and "([uU](ll|LL|l|L)|(ll|LL|l|L)?[uU]?)"
    "floatsuffix" and "[fFlL]?"
    "encoding" and "u|u8|U|L"
    tokenizer {
      root {
        "@encoding?R\\\"(?:([^ ()\\\\\\t]*))\\(".action {
          token = "string.raw.begin"
          next = "@raw.${'$'}1"
        }
        "[a-zA-Z_]\\w*".action {
          cases {
            "@keywords" and {
              token = "keyword.${'$'}0"
            }
            "@default" and "identifier"
          }
        }
        "^\\s*#\\s*include".action {
          token = "keyword.directive.include"
          next = "@include"
        }
        "^\\s*#\\s*\\w+".token("keyword.directive")
        include("@whitespace")
        "\\[\\s*\\[".action {
          token = "annotation"
          next = "@annotation"
        }
        "[{}()<>\\[\\]]".token("@brackets")
        "@symbols".action {
          cases {
            "@operators" and "delimiter"
            "@default" and ""
          }
        }
        "\\d*\\d+[eE]([\\-+]?\\d+)?(@floatsuffix)".token("number.float")
        "\\d*\\.\\d+([eE][\\-+]?\\d+)?(@floatsuffix)".token("number.float")
        "0[xX][0-9a-fA-F']*[0-9a-fA-F](@integersuffix)".token("number.hex")
        "0[0-7']*[0-7](@integersuffix)".token("number.octal")
        "0[bB][0-1']*[0-1](@integersuffix)".token("number.binary")
        "\\d[\\d']*\\d(@integersuffix)".token("number")
        "\\d(@integersuffix)".token("number")
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
        "[ \\t\\r\\n]+".token("")
        "\\/\\*\\*(?!\\/)".action("comment.doc").state("@doccomment")
        "\\/\\*".action("comment").state("@comment")
        "\\/\\/.*\\\\${'$'}".action("comment").state("@linecomment")
        "\\/\\/.*${'$'}".token("comment")
      }
      comment {
        "[^\\/*]+".token("comment")
        "\\*\\/".action("comment").state("@pop")
        "[\\/*]".token("comment")
      }
      "linecomment" rules {
        ".*[^\\\\]${'$'}".action("comment").state("@pop")
        "[^]+".token("comment")
      }
      "doccomment" rules {
        "[^\\/*]+".token("comment.doc")
        "\\*\\/".action("comment.doc").state("@pop")
        "[\\/*]".token("comment.doc")
      }
      string {
        "[^\\\\\"]+".token("string")
        "@escapes".token("string.escape")
        "\\\\.".token("string.escape.invalid")
        "\"".action("string").state("@pop")
      }
      "raw" rules {
        "(.*)(\\))(?:([^ ()\\\\\\t\"]*))(\\\")".action {
          cases {
            "${'$'}3==${'$'}S2" actionArray {
              token("string.raw")
              token("string.raw.end")
              token("string.raw.end")
              action("string.raw.end") {
                next = "@pop"
              }
            }
            "@default" actionArray {
              token("string.raw")
              token("string.raw")
              token("string.raw")
              token("string.raw")
            }
          }
        }
        ".*".token("string.raw")
      }
      "annotation" rules {
        include("@whitespace")
        "using|alignas".token("keyword")
        "[a-zA-Z0-9_]+".token("annotation")
        "[,:]".token("delimiter")
        "[()]".token("@brackets")
        "\\]\\s*\\]".action {
          token = "annotation"
          next = "@pop"
        }
      }
      "include" rules {
        "(\\s*)(<)([^<>]*)(>)".actionArray {
          token("")
          token("keyword.directive.include.begin")
          token("string.include.identifier")
          action("keyword.directive.include.end") {
            next = "@pop"
          }
        }
        "(\\s*)(\")([^\"]*)(\")".actionArray {
          token("")
          token("keyword.directive.include.begin")
          token("string.include.identifier")
          action("keyword.directive.include.end") {
            next = "@pop"
          }
        }
      }
    }
  }
}

