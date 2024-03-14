package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val MipsLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".mips"
    ignoreCase = false
    defaultToken = ""
    "regEx" and "\\/(?!\\/\\/)(?:[^\\/\\\\]|\\\\.)*\\/[igm]*"
    keywords(".data", ".text", "syscall", "trap", "add", "addu", "addi", "addiu", "and", "andi",
        "div", "divu", "mult", "multu", "nor", "or", "ori", "sll", "slv", "sra", "srav", "srl",
        "srlv", "sub", "subu", "xor", "xori", "lhi", "lho", "lhi", "llo", "slt", "slti", "sltu",
        "sltiu", "beq", "bgtz", "blez", "bne", "j", "jal", "jalr", "jr", "lb", "lbu", "lh", "lhu",
        "lw", "li", "la", "sb", "sh", "sw", "mfhi", "mflo", "mthi", "mtlo", "move")
    symbols("[\\.,\\:]+")
    escapes("\\\\(?:[abfnrtv\\\\\"'${'$'}]|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
    tokenizer {
      root {
        "\\${'$'}[a-zA-Z_]\\w*".token("variable.predefined")
        "[.a-zA-Z_]\\w*".action {
          cases {
            "this" and "variable.predefined"
            "@keywords" and {
              token = "keyword.${'$'}0"
            }
            "@default" and ""
          }
        }
        "[ \\t\\r\\n]+".token("")
        "#.*${'$'}".token("comment")
        "///".action {
          token = "regexp"
          next = "@hereregexp"
        }
        "^(\\s*)(@regEx)".actionArray {
          token("")
          token("regexp")
        }
        "(\\,)(\\s*)(@regEx)".actionArray {
          token("delimiter")
          token("")
          token("regexp")
        }
        "(\\:)(\\s*)(@regEx)".actionArray {
          token("delimiter")
          token("")
          token("regexp")
        }
        "@symbols".token("delimiter")
        "\\d+[eE]([\\-+]?\\d+)?".token("number.float")
        "\\d+\\.\\d+([eE][\\-+]?\\d+)?".token("number.float")
        "0[xX][0-9a-fA-F]+".token("number.hex")
        "0[0-7]+(?!\\d)".token("number.octal")
        "\\d+".token("number")
        "[,.]".token("delimiter")
        "\"\"\"".action("string").state("@herestring.\"\"\"")
        "'''".action("string").state("@herestring.'''")
        "\"".action {
          cases {
            "@eos" and "string"
            "@default" and {
              token = "string"
              next = "@string.\""
            }
          }
        }
        "'".action {
          cases {
            "@eos" and "string"
            "@default" and {
              token = "string"
              next = "@string.'"
            }
          }
        }
      }
      string {
        "[^\"'\\#\\\\]+".token("string")
        "@escapes".token("string.escape")
        "\\.".token("string.escape.invalid")
        "\\.".token("string.escape.invalid")
        "#{".action {
          cases {
            "${'$'}S2==\"" and {
              token = "string"
              next = "root.interpolatedstring"
            }
            "@default" and "string"
          }
        }
        "[\"']".action {
          cases {
            "${'$'}#==${'$'}S2" and {
              token = "string"
              next = "@pop"
            }
            "@default" and "string"
          }
        }
        "#".token("string")
      }
      "herestring" rules {
        "(\"\"\"|''')".action {
          cases {
            "${'$'}1==${'$'}S2" and {
              token = "string"
              next = "@pop"
            }
            "@default" and "string"
          }
        }
        "[^#\\\\'\"]+".token("string")
        "['\"]+".token("string")
        "@escapes".token("string.escape")
        "\\.".token("string.escape.invalid")
        "#{".action {
          token = "string.quote"
          next = "root.interpolatedstring"
        }
        "#".token("string")
      }
      comment {
        "[^#]+".token("comment")
        "#".token("comment")
      }
      "hereregexp" rules {
        "[^\\\\\\/#]+".token("regexp")
        "\\\\.".token("regexp")
        "#.*${'$'}".token("comment")
        "///[igm]*".action {
          token = "regexp"
          next = "@pop"
        }
        "\\/".token("regexp")
      }
    }
  }
}

