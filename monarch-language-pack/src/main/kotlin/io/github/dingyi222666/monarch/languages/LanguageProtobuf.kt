package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val ProtobufLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".proto"
    defaultToken = ""
    brackets {
      bracket("{","}","delimiter.curly")
      bracket("[","]","delimiter.square")
      bracket("(",")","delimiter.parenthesis")
      bracket("<",">","delimiter.angle")
    }
    symbols("[=><!~?:&|+\\-*/^%]+")
    keywords("syntax", "import", "weak", "public", "package", "option", "repeated", "oneof", "map",
        "reserved", "to", "max", "enum", "message", "service", "rpc", "stream", "returns",
        "package", "optional", "true", "false")
    "builtinTypes" and listOf("double", "float", "int32", "int64", "uint32", "uint64", "sint32",
        "sint64", "fixed32", "fixed64", "sfixed32", "sfixed64", "bool", "string", "bytes")
    operators("=", "+", "-")
    "namedLiterals" and listOf("true", "false")
    escapes("\\\\(u{[0-9A-Fa-f]+}|n|r|t|\\\\|'|\\${'$'}{)")
    "identifier" and "[a-zA-Z]\\w*"
    "fullIdentifier" and "@identifier(?:\\s*\\.\\s*@identifier)*"
    "optionName" and "(?:@identifier|\\(\\s*@fullIdentifier\\s*\\))(?:\\s*\\.\\s*@identifier)*"
    "messageName" and "@identifier"
    "enumName" and "@identifier"
    "messageType" and "\\.?\\s*(?:@identifier\\s*\\.\\s*)*@messageName"
    "enumType" and "\\.?\\s*(?:@identifier\\s*\\.\\s*)*@enumName"
    "floatLit" and "[0-9]+\\s*\\.\\s*[0-9]*(?:@exponent)?|[0-9]+@exponent|\\.[0-9]+(?:@exponent)?"
    "exponent" and "[eE]\\s*[+-]?\\s*[0-9]+"
    "boolLit" and "true\\b|false\\b"
    "decimalLit" and "[1-9][0-9]*"
    "octalLit" and "0[0-7]*"
    "hexLit" and "0[xX][0-9a-fA-F]+"
    "type" and
        "double|float|int32|int64|uint32|uint64|sint32|sint64|fixed32|fixed64|sfixed32|sfixed64|bool|string|bytes|@messageType|@enumType"
    "keyType" and
        "int32|int64|uint32|uint64|sint32|sint64|fixed32|fixed64|sfixed32|sfixed64|bool|string"
    tokenizer {
      root {
        include("@whitespace")
        "syntax".token("keyword")
        "=".token("operators")
        ";".token("delimiter")
        "(\")(proto3)(\")".actionArray {
          token("string.quote")
          token("string")
          action("string.quote") {
            switchTo = "@topLevel.proto3"
          }
        }
        "(\")(proto2)(\")".actionArray {
          token("string.quote")
          token("string")
          action("string.quote") {
            switchTo = "@topLevel.proto2"
          }
        }
        ".*?".action {
          token = ""
          switchTo = "@topLevel.proto2"
        }
      }
      "topLevel" rules {
        include("@whitespace")
        include("@constant")
        "=".token("operators")
        "[;.]".token("delimiter")
        "@fullIdentifier".action {
          cases {
            "option" and {
              token = "keyword"
              next = "@option.${'$'}S2"
            }
            "enum" and {
              token = "keyword"
              next = "@enumDecl.${'$'}S2"
            }
            "message" and {
              token = "keyword"
              next = "@messageDecl.${'$'}S2"
            }
            "service" and {
              token = "keyword"
              next = "@serviceDecl.${'$'}S2"
            }
            "extend" and {
              cases {
                "${'$'}S2==proto2" and {
                  token = "keyword"
                  next = "@extendDecl.${'$'}S2"
                }
              }
            }
            "@keywords" and "keyword"
            "@default" and "identifier"
          }
        }
      }
      "enumDecl" rules {
        include("@whitespace")
        "@identifier".token("type.identifier")
        "{".action {
          token = "@brackets"
          switchTo = "@enumBody.${'$'}S2"
          bracket = "@open"
        }
      }
      "enumBody" rules {
        include("@whitespace")
        include("@constant")
        "=".token("operators")
        ";".token("delimiter")
        "option\\b".action("keyword").state("@option.${'$'}S2")
        "@identifier".token("identifier")
        "\\[".action {
          token = "@brackets"
          next = "@options.${'$'}S2"
          bracket = "@open"
        }
        "}".action {
          token = "@brackets"
          next = "@pop"
          bracket = "@close"
        }
      }
      "messageDecl" rules {
        include("@whitespace")
        "@identifier".token("type.identifier")
        "{".action {
          token = "@brackets"
          switchTo = "@messageBody.${'$'}S2"
          bracket = "@open"
        }
      }
      "messageBody" rules {
        include("@whitespace")
        include("@constant")
        "=".token("operators")
        ";".token("delimiter")
        "(map)(s*)(<)".actionArray {
          token("keyword")
          token("white")
          action("@brackets") {
            next = "@map.${'$'}S2"
            bracket = "@open"
          }
        }
        "@identifier".action {
          cases {
            "option" and {
              token = "keyword"
              next = "@option.${'$'}S2"
            }
            "enum" and {
              token = "keyword"
              next = "@enumDecl.${'$'}S2"
            }
            "message" and {
              token = "keyword"
              next = "@messageDecl.${'$'}S2"
            }
            "oneof" and {
              token = "keyword"
              next = "@oneofDecl.${'$'}S2"
            }
            "extensions" and {
              cases {
                "${'$'}S2==proto2" and {
                  token = "keyword"
                  next = "@reserved.${'$'}S2"
                }
              }
            }
            "reserved" and {
              token = "keyword"
              next = "@reserved.${'$'}S2"
            }
            "(?:repeated|optional)" and {
              token = "keyword"
              next = "@field.${'$'}S2"
            }
            "required" and {
              cases {
                "${'$'}S2==proto2" and {
                  token = "keyword"
                  next = "@field.${'$'}S2"
                }
              }
            }
            "${'$'}S2==proto3" and {
              token = "@rematch"
              next = "@field.${'$'}S2"
            }
          }
        }
        "\\[".action {
          token = "@brackets"
          next = "@options.${'$'}S2"
          bracket = "@open"
        }
        "}".action {
          token = "@brackets"
          next = "@pop"
          bracket = "@close"
        }
      }
      "extendDecl" rules {
        include("@whitespace")
        "@identifier".token("type.identifier")
        "{".action {
          token = "@brackets"
          switchTo = "@extendBody.${'$'}S2"
          bracket = "@open"
        }
      }
      "extendBody" rules {
        include("@whitespace")
        include("@constant")
        ";".token("delimiter")
        "(?:repeated|optional|required)".action("keyword").state("@field.${'$'}S2")
        "\\[".action {
          token = "@brackets"
          next = "@options.${'$'}S2"
          bracket = "@open"
        }
        "}".action {
          token = "@brackets"
          next = "@pop"
          bracket = "@close"
        }
      }
      "options" rules {
        include("@whitespace")
        include("@constant")
        ";".token("delimiter")
        "@optionName".token("annotation")
        "[()]".token("annotation.brackets")
        "=".token("operator")
        "\\]".action {
          token = "@brackets"
          next = "@pop"
          bracket = "@close"
        }
      }
      "option" rules {
        include("@whitespace")
        "@optionName".token("annotation")
        "[()]".token("annotation.brackets")
        "=".action("operator").state("@pop")
      }
      "oneofDecl" rules {
        include("@whitespace")
        "@identifier".token("identifier")
        "{".action {
          token = "@brackets"
          switchTo = "@oneofBody.${'$'}S2"
          bracket = "@open"
        }
      }
      "oneofBody" rules {
        include("@whitespace")
        include("@constant")
        ";".token("delimiter")
        "(@identifier)(\\s*)(=)".actionArray {
          token("identifier")
          token("white")
          token("delimiter")
        }
        "@fullIdentifier|\\.".action {
          cases {
            "@builtinTypes" and "keyword"
            "@default" and "type.identifier"
          }
        }
        "\\[".action {
          token = "@brackets"
          next = "@options.${'$'}S2"
          bracket = "@open"
        }
        "}".action {
          token = "@brackets"
          next = "@pop"
          bracket = "@close"
        }
      }
      "reserved" rules {
        include("@whitespace")
        ",".token("delimiter")
        ";".action("delimiter").state("@pop")
        include("@constant")
        "to\\b|max\\b".token("keyword")
      }
      "map" rules {
        include("@whitespace")
        "@fullIdentifier|\\.".action {
          cases {
            "@builtinTypes" and "keyword"
            "@default" and "type.identifier"
          }
        }
        ",".token("delimiter")
        ">".action {
          token = "@brackets"
          switchTo = "identifier"
          bracket = "@close"
        }
      }
      "field" rules {
        include("@whitespace")
        "group".action {
          cases {
            "${'$'}S2==proto2" and {
              token = "keyword"
              switchTo = "@groupDecl.${'$'}S2"
            }
          }
        }
        "(@identifier)(\\s*)(=)".actionArray {
          token("identifier")
          token("white")
          action("delimiter") {
            next = "@pop"
          }
        }
        "@fullIdentifier|\\.".action {
          cases {
            "@builtinTypes" and "keyword"
            "@default" and "type.identifier"
          }
        }
      }
      "groupDecl" rules {
        include("@whitespace")
        "@identifier".token("identifier")
        "=".token("operator")
        "{".action {
          token = "@brackets"
          switchTo = "@messageBody.${'$'}S2"
          bracket = "@open"
        }
        include("@constant")
      }
      "type" rules {
        include("@whitespace")
        "@identifier".action("type.identifier").state("@pop")
        ".".token("delimiter")
      }
      "identifier" rules {
        include("@whitespace")
        "@identifier".action("identifier").state("@pop")
      }
      "serviceDecl" rules {
        include("@whitespace")
        "@identifier".token("identifier")
        "{".action {
          token = "@brackets"
          switchTo = "@serviceBody.${'$'}S2"
          bracket = "@open"
        }
      }
      "serviceBody" rules {
        include("@whitespace")
        include("@constant")
        ";".token("delimiter")
        "option\\b".action("keyword").state("@option.${'$'}S2")
        "rpc\\b".action("keyword").state("@rpc.${'$'}S2")
        "\\[".action {
          token = "@brackets"
          next = "@options.${'$'}S2"
          bracket = "@open"
        }
        "}".action {
          token = "@brackets"
          next = "@pop"
          bracket = "@close"
        }
      }
      "rpc" rules {
        include("@whitespace")
        "@identifier".token("identifier")
        "\\(".action {
          token = "@brackets"
          switchTo = "@request.${'$'}S2"
          bracket = "@open"
        }
        "{".action {
          token = "@brackets"
          next = "@methodOptions.${'$'}S2"
          bracket = "@open"
        }
        ";".action("delimiter").state("@pop")
      }
      "request" rules {
        include("@whitespace")
        "@messageType".action {
          cases {
            "stream" and {
              token = "keyword"
              next = "@type.${'$'}S2"
            }
            "@default" and "type.identifier"
          }
        }
        "\\)".action {
          token = "@brackets"
          switchTo = "@returns.${'$'}S2"
          bracket = "@close"
        }
      }
      "returns" rules {
        include("@whitespace")
        "returns\\b".token("keyword")
        "\\(".action {
          token = "@brackets"
          switchTo = "@response.${'$'}S2"
          bracket = "@open"
        }
      }
      "response" rules {
        include("@whitespace")
        "@messageType".action {
          cases {
            "stream" and {
              token = "keyword"
              next = "@type.${'$'}S2"
            }
            "@default" and "type.identifier"
          }
        }
        "\\)".action {
          token = "@brackets"
          switchTo = "@rpc.${'$'}S2"
          bracket = "@close"
        }
      }
      "methodOptions" rules {
        include("@whitespace")
        include("@constant")
        ";".token("delimiter")
        "option".token("keyword")
        "@optionName".token("annotation")
        "[()]".token("annotation.brackets")
        "=".token("operator")
        "}".action {
          token = "@brackets"
          next = "@pop"
          bracket = "@close"
        }
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
      "stringSingle" rules {
        "[^\\\\']+".token("string")
        "@escapes".token("string.escape")
        "\\\\.".token("string.escape.invalid")
        "'".action {
          token = "string.quote"
          next = "@pop"
          bracket = "@close"
        }
      }
      "constant" rules {
        "@boolLit".token("keyword.constant")
        "@hexLit".token("number.hex")
        "@octalLit".token("number.octal")
        "@decimalLit".token("number")
        "@floatLit".token("number.float")
        "(\"([^\"\\\\]|\\\\.)*|'([^'\\\\]|\\\\.)*)${'$'}".token("string.invalid")
        "\"".action {
          token = "string.quote"
          next = "@string"
          bracket = "@open"
        }
        "'".action {
          token = "string.quote"
          next = "@stringSingle"
          bracket = "@open"
        }
        "{".action {
          token = "@brackets"
          next = "@prototext"
          bracket = "@open"
        }
        "identifier".token("identifier")
      }
      whitespace {
        "[ \\t\\r\\n]+".token("white")
        "\\/\\*".action("comment").state("@comment")
        "\\/\\/.*${'$'}".token("comment")
      }
      "prototext" rules {
        include("@whitespace")
        include("@constant")
        "@identifier".token("identifier")
        "[:;]".token("delimiter")
        "}".action {
          token = "@brackets"
          next = "@pop"
          bracket = "@close"
        }
      }
    }
  }
}

