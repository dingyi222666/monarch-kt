package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val SystemverilogLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".sv"
    defaultToken = ""
    brackets {
      bracket("{","}","delimiter.curly")
      bracket("(",")","delimiter.parenthesis")
      bracket("[","]","delimiter.square")
      bracket("<",">","delimiter.angle")
    }
    keywords("accept_on", "alias", "always", "always_comb", "always_ff", "always_latch", "and",
        "assert", "assign", "assume", "automatic", "before", "begin", "bind", "bins", "binsof",
        "bit", "break", "buf", "bufif0", "bufif1", "byte", "case", "casex", "casez", "cell",
        "chandle", "checker", "class", "clocking", "cmos", "config", "const", "constraint",
        "context", "continue", "cover", "covergroup", "coverpoint", "cross", "deassign", "default",
        "defparam", "design", "disable", "dist", "do", "edge", "else", "end", "endcase",
        "endchecker", "endclass", "endclocking", "endconfig", "endfunction", "endgenerate",
        "endgroup", "endinterface", "endmodule", "endpackage", "endprimitive", "endprogram",
        "endproperty", "endspecify", "endsequence", "endtable", "endtask", "enum", "event",
        "eventually", "expect", "export", "extends", "extern", "final", "first_match", "for",
        "force", "foreach", "forever", "fork", "forkjoin", "function", "generate", "genvar",
        "global", "highz0", "highz1", "if", "iff", "ifnone", "ignore_bins", "illegal_bins",
        "implements", "implies", "import", "incdir", "include", "initial", "inout", "input",
        "inside", "instance", "int", "integer", "interconnect", "interface", "intersect", "join",
        "join_any", "join_none", "large", "let", "liblist", "library", "local", "localparam",
        "logic", "longint", "macromodule", "matches", "medium", "modport", "module", "nand",
        "negedge", "nettype", "new", "nexttime", "nmos", "nor", "noshowcancelled", "not", "notif0",
        "notif1", "null", "or", "output", "package", "packed", "parameter", "pmos", "posedge",
        "primitive", "priority", "program", "property", "protected", "pull0", "pull1", "pulldown",
        "pullup", "pulsestyle_ondetect", "pulsestyle_onevent", "pure", "rand", "randc", "randcase",
        "randsequence", "rcmos", "real", "realtime", "ref", "reg", "reject_on", "release", "repeat",
        "restrict", "return", "rnmos", "rpmos", "rtran", "rtranif0", "rtranif1", "s_always",
        "s_eventually", "s_nexttime", "s_until", "s_until_with", "scalared", "sequence", "shortint",
        "shortreal", "showcancelled", "signed", "small", "soft", "solve", "specify", "specparam",
        "static", "string", "strong", "strong0", "strong1", "struct", "super", "supply0", "supply1",
        "sync_accept_on", "sync_reject_on", "table", "tagged", "task", "this", "throughout", "time",
        "timeprecision", "timeunit", "tran", "tranif0", "tranif1", "tri", "tri0", "tri1", "triand",
        "trior", "trireg", "type", "typedef", "union", "unique", "unique0", "unsigned", "until",
        "until_with", "untyped", "use", "uwire", "var", "vectored", "virtual", "void", "wait",
        "wait_order", "wand", "weak", "weak0", "weak1", "while", "wildcard", "wire", "with",
        "within", "wor", "xnor", "xor")
    "builtin_gates" and listOf("and", "nand", "nor", "or", "xor", "xnor", "buf", "not", "bufif0",
        "bufif1", "notif1", "notif0", "cmos", "nmos", "pmos", "rcmos", "rnmos", "rpmos", "tran",
        "tranif1", "tranif0", "rtran", "rtranif1", "rtranif0")
    operators("=", "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=", "<<=", ">>+", "<<<=", ">>>=",
        "?", ":", "+", "-", "!", "~", "&", "~&", "|", "~|", "^", "~^", "^~", "+", "-", "*", "/",
        "%", "==", "!=", "===", "!==", "==?", "!=?", "&&", "||", "**", "<", "<=", ">", ">=", "&",
        "|", "^", ">>", "<<", ">>>", "<<<", "++", "--", "->", "<->", "inside", "dist", "::", "+:",
        "-:", "*>", "&&&", "|->", "|=>", "#=#")
    symbols("[=><!~?:&|+\\-*\\/\\^%#]+")
    escapes("%%|\\\\(?:[antvf\\\\\"']|x[0-9A-Fa-f]{1,2}|[0-7]{1,3})")
    "identifier" and "(?:[a-zA-Z_][a-zA-Z0-9_${'$'}\\.]*|\\\\\\S+ )"
    "systemcall" and "[${'$'}][a-zA-Z0-9_]+"
    "timeunits" and "s|ms|us|ns|ps|fs"
    tokenizer {
      root {
        "^(\\s*)(@identifier)".actionArray {
          token("")
          action {
            cases {
              "@builtin_gates" and {
                token = "keyword.${'$'}2"
                next = "@module_instance"
              }
              "table" and {
                token = "keyword.${'$'}2"
                next = "@table"
              }
              "@keywords" and {
                token = "keyword.${'$'}2"
              }
              "@default" and {
                token = "identifier"
                next = "@module_instance"
              }
            }
          }
        }
        "^\\s*`include".action {
          token = "keyword.directive.include"
          next = "@include"
        }
        "^\\s*`\\s*\\w+".token("keyword")
        include("@identifier_or_keyword")
        include("@whitespace")
        "\\(\\*.*\\*\\)".token("annotation")
        "@systemcall".token("variable.predefined")
        "[{}()\\[\\]]".token("@brackets")
        "[<>](?!@symbols)".token("@brackets")
        "@symbols".action {
          cases {
            "@operators" and "delimiter"
            "@default" and ""
          }
        }
        include("@numbers")
        "[;,.]".token("delimiter")
        include("@strings")
      }
      "identifier_or_keyword" rules {
        "@identifier".action {
          cases {
            "@keywords" and {
              token = "keyword.${'$'}0"
            }
            "@default" and "identifier"
          }
        }
      }
      "numbers" rules {
        "\\d+?[\\d_]*(?:\\.[\\d_]+)?[eE][\\-+]?\\d+".token("number.float")
        "\\d+?[\\d_]*\\.[\\d_]+(?:\\s*@timeunits)?".token("number.float")
        "(?:\\d+?[\\d_]*\\s*)?'[sS]?[dD]\\s*[0-9xXzZ?]+?[0-9xXzZ?_]*".token("number")
        "(?:\\d+?[\\d_]*\\s*)?'[sS]?[bB]\\s*[0-1xXzZ?]+?[0-1xXzZ?_]*".token("number.binary")
        "(?:\\d+?[\\d_]*\\s*)?'[sS]?[oO]\\s*[0-7xXzZ?]+?[0-7xXzZ?_]*".token("number.octal")
        "(?:\\d+?[\\d_]*\\s*)?'[sS]?[hH]\\s*[0-9a-fA-FxXzZ?]+?[0-9a-fA-FxXzZ?_]*".token("number.hex")
        "1step".token("number")
        "[\\dxXzZ]+?[\\dxXzZ_]*(?:\\s*@timeunits)?".token("number")
        "'[01xXzZ]+".token("number")
      }
      "module_instance" rules {
        include("@whitespace")
        "(#?)(\\()".actionArray {
          token("")
          action("@brackets") {
            next = "@port_connection"
          }
        }
        "@identifier\\s*[;={}\\[\\],]".action {
          token = "@rematch"
          next = "@pop"
        }
        "@symbols|[;={}\\[\\],]".action {
          token = "@rematch"
          next = "@pop"
        }
        "@identifier".token("type")
        ";".action("delimiter").state("@pop")
      }
      "port_connection" rules {
        include("@identifier_or_keyword")
        include("@whitespace")
        "@systemcall".token("variable.predefined")
        include("@numbers")
        include("@strings")
        "[,]".token("delimiter")
        "\\(".action("@brackets").state("@port_connection")
        "\\)".action("@brackets").state("@pop")
      }
      whitespace {
        "[ \\t\\r\\n]+".token("")
        "\\/\\*".action("comment").state("@comment")
        "\\/\\/.*${'$'}".token("comment")
      }
      comment {
        "[^\\/*]+".token("comment")
        "\\*\\/".action("comment").state("@pop")
        "[\\/*]".token("comment")
      }
      "strings" rules {
        "\"([^\"\\\\]|\\\\.)*${'$'}".token("string.invalid")
        "\"".action("string").state("@string")
      }
      string {
        "[^\\\\\"]+".token("string")
        "@escapes".token("string.escape")
        "\\\\.".token("string.escape.invalid")
        "\"".action("string").state("@pop")
      }
      "include" rules {
        "(\\s*)(\")([\\w*\\/*]*)(.\\w*)(\")".actionArray {
          token("")
          token("string.include.identifier")
          token("string.include.identifier")
          token("string.include.identifier")
          action("string.include.identifier") {
            next = "@pop"
          }
        }
        "(\\s*)(<)([\\w*\\/*]*)(.\\w*)(>)".actionArray {
          token("")
          token("string.include.identifier")
          token("string.include.identifier")
          token("string.include.identifier")
          action("string.include.identifier") {
            next = "@pop"
          }
        }
      }
      "table" rules {
        include("@whitespace")
        "[()]".token("@brackets")
        "[:;]".token("delimiter")
        "[01\\-*?xXbBrRfFpPnN]".token("variable.predefined")
        "endtable".action("keyword.endtable").state("@pop")
      }
    }
  }
}

