package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val JuliaLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".julia"
    brackets {
      bracket("(",")","delimiter.parenthesis")
      bracket("{","}","delimiter.curly")
      bracket("[","]","delimiter.square")
    }
    keywords("begin", "while", "if", "for", "try", "return", "break", "continue", "function",
        "macro", "quote", "let", "local", "global", "const", "do", "struct", "module", "baremodule",
        "using", "import", "export", "end", "else", "elseif", "catch", "finally", "mutable",
        "primitive", "abstract", "type", "in", "isa", "where", "new")
    "types" and listOf("LinRange", "LineNumberNode", "LinearIndices", "LoadError", "MIME", "Matrix",
        "Method", "MethodError", "Missing", "MissingException", "Module", "NTuple", "NamedTuple",
        "Nothing", "Number", "OrdinalRange", "OutOfMemoryError", "OverflowError", "Pair",
        "PartialQuickSort", "PermutedDimsArray", "Pipe", "Ptr", "QuoteNode", "Rational", "RawFD",
        "ReadOnlyMemoryError", "Real", "ReentrantLock", "Ref", "Regex", "RegexMatch",
        "RoundingMode", "SegmentationFault", "Set", "Signed", "Some", "StackOverflowError",
        "StepRange", "StepRangeLen", "StridedArray", "StridedMatrix", "StridedVecOrMat",
        "StridedVector", "String", "StringIndexError", "SubArray", "SubString",
        "SubstitutionString", "Symbol", "SystemError", "Task", "Text", "TextDisplay", "Timer",
        "Tuple", "Type", "TypeError", "TypeVar", "UInt", "UInt128", "UInt16", "UInt32", "UInt64",
        "UInt8", "UndefInitializer", "AbstractArray", "UndefKeywordError", "AbstractChannel",
        "UndefRefError", "AbstractChar", "UndefVarError", "AbstractDict", "Union",
        "AbstractDisplay", "UnionAll", "AbstractFloat", "UnitRange", "AbstractIrrational",
        "Unsigned", "AbstractMatrix", "AbstractRange", "Val", "AbstractSet", "Vararg",
        "AbstractString", "VecElement", "AbstractUnitRange", "VecOrMat", "AbstractVecOrMat",
        "Vector", "AbstractVector", "VersionNumber", "Any", "WeakKeyDict", "ArgumentError",
        "WeakRef", "Array", "AssertionError", "BigFloat", "BigInt", "BitArray", "BitMatrix",
        "BitSet", "BitVector", "Bool", "BoundsError", "CapturedException", "CartesianIndex",
        "CartesianIndices", "Cchar", "Cdouble", "Cfloat", "Channel", "Char", "Cint", "Cintmax_t",
        "Clong", "Clonglong", "Cmd", "Colon", "Complex", "ComplexF16", "ComplexF32", "ComplexF64",
        "CompositeException", "Condition", "Cptrdiff_t", "Cshort", "Csize_t", "Cssize_t", "Cstring",
        "Cuchar", "Cuint", "Cuintmax_t", "Culong", "Culonglong", "Cushort", "Cvoid", "Cwchar_t",
        "Cwstring", "DataType", "DenseArray", "DenseMatrix", "DenseVecOrMat", "DenseVector", "Dict",
        "DimensionMismatch", "Dims", "DivideError", "DomainError", "EOFError", "Enum",
        "ErrorException", "Exception", "ExponentialBackOff", "Expr", "Float16", "Float32",
        "Float64", "Function", "GlobalRef", "HTML", "IO", "IOBuffer", "IOContext", "IOStream",
        "IdDict", "IndexCartesian", "IndexLinear", "IndexStyle", "InexactError", "InitError", "Int",
        "Int128", "Int16", "Int32", "Int64", "Int8", "Integer", "InterruptException",
        "InvalidStateException", "Irrational", "KeyError")
    "keywordops" and listOf("<:", ">:", ":", "=>", "...", ".", "->", "?")
    "allops" and "[^\\w\\d\\s()\\[\\]{}\"'#]+"
    "constants" and listOf("true", "false", "nothing", "missing", "undef", "Inf", "pi", "NaN", "π",
        "ℯ", "ans", "PROGRAM_FILE", "ARGS", "C_NULL", "VERSION", "DEPOT_PATH", "LOAD_PATH")
    operators("!", "!=", "!==", "%", "&", "*", "+", "-", "/", "//", "<", "<<", "<=", "==", "===",
        "=>", ">", ">=", ">>", ">>>", "\\", "^", "|", "|>", "~", "÷", "∈", "∉", "∋", "∌", "∘", "√",
        "∛", "∩", "∪", "≈", "≉", "≠", "≡", "≢", "≤", "≥", "⊆", "⊇", "⊈", "⊉", "⊊", "⊋", "⊻")
    "ident" and "π|ℯ|\\b(?!\\d)\\w+\\b"
    "escape" and "(?:[abefnrstv\\\\\"'\\n\\r]|[0-7]{1,3}|x[0-9A-Fa-f]{1,2}|u[0-9A-Fa-f]{4})"
    escapes("\\\\(?:C\\-(@escape|.)|c(@escape|.)|@escape)")
    tokenizer {
      root {
        "(::)\\s*|\\b(isa)\\s+".action("keyword").state("@typeanno")
        "\\b(isa)(\\s*\\(@ident\\s*,\\s*)".actionArray {
          token("keyword")
          action("") {
            next = "@typeanno"
          }
        }
        "\\b(type|struct)[ \\t]+".action("keyword").state("@typeanno")
        "^\\s*:@ident[!?]?".token("metatag")
        "(return)(\\s*:@ident[!?]?)".actionArray {
          token("keyword")
          token("metatag")
        }
        "(\\(|\\[|\\{|@allops)(\\s*:@ident[!?]?)".actionArray {
          token("")
          token("metatag")
        }
        ":\\(".action("metatag").state("@quote")
        "r\"\"\"".action("regexp.delim").state("@tregexp")
        "r\"".action("regexp.delim").state("@sregexp")
        "raw\"\"\"".action("string.delim").state("@rtstring")
        "[bv]?\"\"\"".action("string.delim").state("@dtstring")
        "raw\"".action("string.delim").state("@rsstring")
        "[bv]?\"".action("string.delim").state("@dsstring")
        "(@ident)\\{".action {
          cases {
            "${'$'}1@types" and {
              token = "type"
              next = "@gen"
            }
            "@default" and {
              token = "type"
              next = "@gen"
            }
          }
        }
        "@ident[!?'']?(?=\\.?\\()".action {
          cases {
            "@types" and "type"
            "@keywords" and "keyword"
            "@constants" and "variable"
            "@default" and "keyword.flow"
          }
        }
        "@ident[!?']?".action {
          cases {
            "@types" and "type"
            "@keywords" and "keyword"
            "@constants" and "variable"
            "@default" and "identifier"
          }
        }
        "\\${'$'}\\w+".token("key")
        "\\${'$'}\\(".action("key").state("@paste")
        "@@@ident".token("annotation")
        include("@whitespace")
        "'(?:@escapes|.)'".token("string.character")
        "[()\\[\\]{}]".token("@brackets")
        "@allops".action {
          cases {
            "@keywordops" and "keyword"
            "@operators" and "operator"
          }
        }
        "[;,]".token("delimiter")
        "0[xX][0-9a-fA-F](_?[0-9a-fA-F])*".token("number.hex")
        "0[_oO][0-7](_?[0-7])*".token("number.octal")
        "0[bB][01](_?[01])*".token("number.binary")
        "[+\\-]?\\d+(\\.\\d+)?(im?|[eE][+\\-]?\\d+(\\.\\d+)?)?".token("number")
      }
      "typeanno" rules {
        "[a-zA-Z_]\\w*(?:\\.[a-zA-Z_]\\w*)*\\{".action("type").state("@gen")
        "([a-zA-Z_]\\w*(?:\\.[a-zA-Z_]\\w*)*)(\\s*<:\\s*)".actionArray {
          token("type")
          token("keyword")
        }
        "[a-zA-Z_]\\w*(?:\\.[a-zA-Z_]\\w*)*".action("type").state("@pop")
        "".action("").state("@pop")
      }
      "gen" rules {
        "[a-zA-Z_]\\w*(?:\\.[a-zA-Z_]\\w*)*\\{".action("type").state("@push")
        "[a-zA-Z_]\\w*(?:\\.[a-zA-Z_]\\w*)*".token("type")
        "<:".token("keyword")
        "(\\})(\\s*<:\\s*)".actionArray {
          token("type")
          action("keyword") {
            next = "@pop"
          }
        }
        "\\}".action("type").state("@pop")
        include("@root")
      }
      "quote" rules {
        "\\${'$'}\\(".action("key").state("@paste")
        "\\(".action("@brackets").state("@paren")
        "\\)".action("metatag").state("@pop")
        include("@root")
      }
      "paste" rules {
        ":\\(".action("metatag").state("@quote")
        "\\(".action("@brackets").state("@paren")
        "\\)".action("key").state("@pop")
        include("@root")
      }
      "paren" rules {
        "\\${'$'}\\(".action("key").state("@paste")
        ":\\(".action("metatag").state("@quote")
        "\\(".action("@brackets").state("@push")
        "\\)".action("@brackets").state("@pop")
        include("@root")
      }
      "sregexp" rules {
        "^.*".token("invalid")
        "[^\\\\\"()\\[\\]{}]".token("regexp")
        "[()\\[\\]{}]".token("@brackets")
        "\\\\.".token("operator.scss")
        "\"[imsx]*".action("regexp.delim").state("@pop")
      }
      "tregexp" rules {
        "[^\\\\\"()\\[\\]{}]".token("regexp")
        "[()\\[\\]{}]".token("@brackets")
        "\\\\.".token("operator.scss")
        "\"(?!\"\")".token("string")
        "\"\"\"[imsx]*".action("regexp.delim").state("@pop")
      }
      "rsstring" rules {
        "^.*".token("invalid")
        "[^\\\\\"]".token("string")
        "\\\\.".token("string.escape")
        "\"".action("string.delim").state("@pop")
      }
      "rtstring" rules {
        "[^\\\\\"]".token("string")
        "\\\\.".token("string.escape")
        "\"(?!\"\")".token("string")
        "\"\"\"".action("string.delim").state("@pop")
      }
      "dsstring" rules {
        "^.*".token("invalid")
        "[^\\\\\"\\${'$'}]".token("string")
        "\\${'$'}".action("").state("@interpolated")
        "@escapes".token("string.escape")
        "\\\\.".token("string.escape.invalid")
        "\"".action("string.delim").state("@pop")
      }
      "dtstring" rules {
        "[^\\\\\"\\${'$'}]".token("string")
        "\\${'$'}".action("").state("@interpolated")
        "@escapes".token("string.escape")
        "\\\\.".token("string.escape.invalid")
        "\"(?!\"\")".token("string")
        "\"\"\"".action("string.delim").state("@pop")
      }
      "interpolated" rules {
        "\\(".action {
          token = ""
          switchTo = "@interpolated_compound"
        }
        "[a-zA-Z_]\\w*".token("identifier")
        "".action("").state("@pop")
      }
      "interpolated_compound" rules {
        "\\)".action("").state("@pop")
        include("@root")
      }
      whitespace {
        "[ \\t\\r\\n]+".token("")
        "#=".action("comment").state("@multi_comment")
        "#.*${'$'}".token("comment")
      }
      "multi_comment" rules {
        "#=".action("comment").state("@push")
        "=#".action("comment").state("@pop")
        "=(?!#)|#(?!=)".token("comment")
        "[^#=]+".token("comment")
      }
    }
  }
}

