package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val RustLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".rust"
    defaultToken = "invalid"
    keywords("as", "async", "await", "box", "break", "const", "continue", "crate", "dyn", "else",
        "enum", "extern", "false", "fn", "for", "if", "impl", "in", "let", "loop", "match", "mod",
        "move", "mut", "pub", "ref", "return", "self", "static", "struct", "super", "trait", "true",
        "try", "type", "unsafe", "use", "where", "while", "catch", "default", "union", "static",
        "abstract", "alignof", "become", "do", "final", "macro", "offsetof", "override", "priv",
        "proc", "pure", "sizeof", "typeof", "unsized", "virtual", "yield")
    typeKeywords("Self", "m32", "m64", "m128", "f80", "f16", "f128", "int", "uint", "float", "char",
        "bool", "u8", "u16", "u32", "u64", "f32", "f64", "i8", "i16", "i32", "i64", "str", "Option",
        "Either", "c_float", "c_double", "c_void", "FILE", "fpos_t", "DIR", "dirent", "c_char",
        "c_schar", "c_uchar", "c_short", "c_ushort", "c_int", "c_uint", "c_long", "c_ulong",
        "size_t", "ptrdiff_t", "clock_t", "time_t", "c_longlong", "c_ulonglong", "intptr_t",
        "uintptr_t", "off_t", "dev_t", "ino_t", "pid_t", "mode_t", "ssize_t")
    "constants" and listOf("true", "false", "Some", "None", "Left", "Right", "Ok", "Err")
    "supportConstants" and listOf("EXIT_FAILURE", "EXIT_SUCCESS", "RAND_MAX", "EOF", "SEEK_SET",
        "SEEK_CUR", "SEEK_END", "_IOFBF", "_IONBF", "_IOLBF", "BUFSIZ", "FOPEN_MAX", "FILENAME_MAX",
        "L_tmpnam", "TMP_MAX", "O_RDONLY", "O_WRONLY", "O_RDWR", "O_APPEND", "O_CREAT", "O_EXCL",
        "O_TRUNC", "S_IFIFO", "S_IFCHR", "S_IFBLK", "S_IFDIR", "S_IFREG", "S_IFMT", "S_IEXEC",
        "S_IWRITE", "S_IREAD", "S_IRWXU", "S_IXUSR", "S_IWUSR", "S_IRUSR", "F_OK", "R_OK", "W_OK",
        "X_OK", "STDIN_FILENO", "STDOUT_FILENO", "STDERR_FILENO")
    "supportMacros" and listOf("format!", "print!", "println!", "panic!", "format_args!",
        "unreachable!", "write!", "writeln!")
    operators("!", "!=", "%", "%=", "&", "&=", "&&", "*", "*=", "+", "+=", "-", "-=", "->", ".",
        "..", "...", "/", "/=", ":", ";", "<<", "<<=", "<", "<=", "=", "==", "=>", ">", ">=", ">>",
        ">>=", "@", "^", "^=", "|", "|=", "||", "_", "?", "#")
    escapes("\\\\([nrt0\\\"''\\\\]|x\\h{2}|u\\{\\h{1,6}\\})")
    "delimiters" and "[,]"
    symbols("[\\#\\!\\%\\&\\*\\+\\-\\.\\/\\:\\;\\<\\=\\>\\@\\^\\|_\\?]+")
    "intSuffixes" and "[iu](8|16|32|64|128|size)"
    "floatSuffixes" and "f(32|64)"
    tokenizer {
      root {
        "r(#*)\"".action {
          token = "string.quote"
          next = "@stringraw.${'$'}1"
          bracket = "@open"
        }
        "[a-zA-Z][a-zA-Z0-9_]*!?|_[a-zA-Z0-9_]+".action {
          cases {
            "@typeKeywords" and "keyword.type"
            "@keywords" and "keyword"
            "@supportConstants" and "keyword"
            "@supportMacros" and "keyword"
            "@constants" and "keyword"
            "@default" and "identifier"
          }
        }
        "\\${'$'}".token("identifier")
        "'[a-zA-Z_][a-zA-Z0-9_]*(?=[^\\'])".token("identifier")
        "'(\\S|@escapes)'".token("string.byteliteral")
        "\"".action {
          token = "string.quote"
          next = "@string"
          bracket = "@open"
        }
        include("@numbers")
        include("@whitespace")
        "@delimiters".action {
          cases {
            "@keywords" and "keyword"
            "@default" and "delimiter"
          }
        }
        "[{}()\\[\\]<>]".token("@brackets")
        "@symbols".action {
          cases {
            "@operators" and "operator"
            "@default" and ""
          }
        }
      }
      whitespace {
        "[ \\t\\r\\n]+".token("white")
        "\\/\\*".action("comment").state("@comment")
        "\\/\\/.*${'$'}".token("comment")
      }
      comment {
        "[^\\/*]+".token("comment")
        "\\/\\*".action("comment").state("@push")
        "\\*/".action("comment").state("@pop")
        "[\\/*]".token("comment")
      }
      string {
        "[^\\\\\"]+".token("string")
        "@escapes".token("string.escape")
        "\\\\.".token("string.escape.invalid")
        "\"".action {
          token = "string.quote"
          next = "@pop"
          bracket = "@close"
        }
      }
      "stringraw" rules {
        "[^\"#]+".action {
          token = "string"
        }
        "\"(#*)".action {
          cases {
            "${'$'}1==${'$'}S2" and {
              token = "string.quote"
              next = "@pop"
              bracket = "@close"
            }
            "@default" and {
              token = "string"
            }
          }
        }
        "[\"#]".action {
          token = "string"
        }
      }
      "numbers" rules {
        "(0o[0-7_]+)(@intSuffixes)?".action {
          token = "number"
        }
        "(0b[0-1_]+)(@intSuffixes)?".action {
          token = "number"
        }
        "[\\d][\\d_]*(\\.[\\d][\\d_]*)?[eE][+-][\\d_]+(@floatSuffixes)?".action {
          token = "number"
        }
        "\\b(\\d\\.?[\\d_]*)(@floatSuffixes)?\\b".action {
          token = "number"
        }
        "(0x[\\da-fA-F]+)_?(@intSuffixes)?".action {
          token = "number"
        }
        "[\\d][\\d_]*(@intSuffixes?)?".action {
          token = "number"
        }
      }
    }
  }
}

