package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val SwiftLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".swift"
    defaultToken = ""
    "identifier" and "[a-zA-Z_][\\w${'$'}]*"
    "attributes" and listOf("@GKInspectable", "@IBAction", "@IBDesignable", "@IBInspectable",
        "@IBOutlet", "@IBSegueAction", "@NSApplicationMain", "@NSCopying", "@NSManaged",
        "@Sendable", "@UIApplicationMain", "@autoclosure", "@actorIndependent", "@asyncHandler",
        "@available", "@convention", "@derivative", "@differentiable", "@discardableResult",
        "@dynamicCallable", "@dynamicMemberLookup", "@escaping", "@frozen", "@globalActor",
        "@inlinable", "@inline", "@main", "@noDerivative", "@nonobjc", "@noreturn", "@objc",
        "@objcMembers", "@preconcurrency", "@propertyWrapper", "@requires_stored_property_inits",
        "@resultBuilder", "@testable", "@unchecked", "@unknown", "@usableFromInline",
        "@warn_unqualified_access")
    "accessmodifiers" and listOf("open", "public", "internal", "fileprivate", "private")
    keywords("#available", "#colorLiteral", "#column", "#dsohandle", "#else", "#elseif", "#endif",
        "#error", "#file", "#fileID", "#fileLiteral", "#filePath", "#function", "#if",
        "#imageLiteral", "#keyPath", "#line", "#selector", "#sourceLocation", "#warning", "Any",
        "Protocol", "Self", "Type", "actor", "as", "assignment", "associatedtype", "associativity",
        "async", "await", "break", "case", "catch", "class", "continue", "convenience", "default",
        "defer", "deinit", "didSet", "do", "dynamic", "dynamicType", "else", "enum", "extension",
        "fallthrough", "false", "fileprivate", "final", "for", "func", "get", "guard", "higherThan",
        "if", "import", "in", "indirect", "infix", "init", "inout", "internal", "is", "isolated",
        "lazy", "left", "let", "lowerThan", "mutating", "nil", "none", "nonisolated", "nonmutating",
        "open", "operator", "optional", "override", "postfix", "precedence", "precedencegroup",
        "prefix", "private", "protocol", "public", "repeat", "required", "rethrows", "return",
        "right", "safe", "self", "set", "some", "static", "struct", "subscript", "super", "switch",
        "throw", "throws", "true", "try", "typealias", "unowned", "unsafe", "var", "weak", "where",
        "while", "willSet", "__consuming", "__owned")
    symbols("[=(){}\\[\\].,:;@#\\_&\\-<>`?!+*\\\\\\/]")
    "operatorstart" and
        "[\\/=\\-+!*%<>&|^~?\\u00A1-\\u00A7\\u00A9\\u00AB\\u00AC\\u00AE\\u00B0-\\u00B1\\u00B6\\u00BB\\u00BF\\u00D7\\u00F7\\u2016-\\u2017\\u2020-\\u2027\\u2030-\\u203E\\u2041-\\u2053\\u2055-\\u205E\\u2190-\\u23FF\\u2500-\\u2775\\u2794-\\u2BFF\\u2E00-\\u2E7F\\u3001-\\u3003\\u3008-\\u3030]"
    "operatorend" and
        "[\\u0300-\\u036F\\u1DC0-\\u1DFF\\u20D0-\\u20FF\\uFE00-\\uFE0F\\uFE20-\\uFE2F\\uE0100-\\uE01EF]"
    "operators" and "(@operatorstart)((@operatorstart)|(@operatorend))*"
    escapes("\\\\(?:[abfnrtv\\\\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
    tokenizer {
      root {
        include("@whitespace")
        include("@comment")
        include("@attribute")
        include("@literal")
        include("@keyword")
        include("@invokedmethod")
        include("@symbol")
      }
      whitespace {
        "\\s+".token("white")
        "\"\"\"".action("string.quote").state("@endDblDocString")
      }
      "endDblDocString" rules {
        "[^\"]+".token("string")
        "\\\\\"".token("string")
        "\"\"\"".action("string.quote").state("@popall")
        "\"".token("string")
      }
      "symbol" rules {
        "[{}()\\[\\]]".token("@brackets")
        "[<>](?!@symbols)".token("@brackets")
        "[.]".token("delimiter")
        "@operators".token("operator")
        "@symbols".token("operator")
      }
      comment {
        "\\/\\/\\/.*${'$'}".token("comment.doc")
        "\\/\\*\\*".action("comment.doc").state("@commentdocbody")
        "\\/\\/.*${'$'}".token("comment")
        "\\/\\*".action("comment").state("@commentbody")
      }
      "commentdocbody" rules {
        "\\/\\*".action("comment").state("@commentbody")
        "\\*\\/".action("comment.doc").state("@pop")
        "\\:[a-zA-Z]+\\:".token("comment.doc.param")
        ".".token("comment.doc")
      }
      "commentbody" rules {
        "\\/\\*".action("comment").state("@commentbody")
        "\\*\\/".action("comment").state("@pop")
        ".".token("comment")
      }
      "attribute" rules {
        "@@@identifier".action {
          cases {
            "@attributes" and "keyword.control"
            "@default" and ""
          }
        }
      }
      "literal" rules {
        "\"".action {
          token = "string.quote"
          next = "@stringlit"
        }
        "0[b]([01]_?)+".token("number.binary")
        "0[o]([0-7]_?)+".token("number.octal")
        "0[x]([0-9a-fA-F]_?)+([pP][\\-+](\\d_?)+)?".token("number.hex")
        "(\\d_?)*\\.(\\d_?)+([eE][\\-+]?(\\d_?)+)?".token("number.float")
        "(\\d_?)+".token("number")
      }
      "stringlit" rules {
        "\\\\\\(".action {
          token = "operator"
          next = "@interpolatedexpression"
        }
        "@escapes".token("string")
        "\\\\.".token("string.escape.invalid")
        "\"".action {
          token = "string.quote"
          next = "@pop"
        }
        ".".token("string")
      }
      "interpolatedexpression" rules {
        "\\(".action {
          token = "operator"
          next = "@interpolatedexpression"
        }
        "\\)".action {
          token = "operator"
          next = "@pop"
        }
        include("@literal")
        include("@keyword")
        include("@symbol")
      }
      "keyword" rules {
        "`".action {
          token = "operator"
          next = "@escapedkeyword"
        }
        "@identifier".action {
          cases {
            "@keywords" and "keyword"
            "[A-Z][a-zA-Z0-9${'$'}]*" and "type.identifier"
            "@default" and "identifier"
          }
        }
      }
      "escapedkeyword" rules {
        "`".action {
          token = "operator"
          next = "@pop"
        }
        ".".token("identifier")
      }
      "invokedmethod" rules {
        "([.])(@identifier)".action {
          cases {
            "${'$'}2" actionArray {
              token("delimeter")
              token("type.identifier")
            }
            "@default" and ""
          }
        }
      }
    }
  }
}

