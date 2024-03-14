package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val PugLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".pug"
    ignoreCase = true
    defaultToken = ""
    brackets {
      bracket("{","}","delimiter.curly")
      bracket("[","]","delimiter.array")
      bracket("(",")","delimiter.parenthesis")
    }
    keywords("append", "block", "case", "default", "doctype", "each", "else", "extends", "for",
        "if", "in", "include", "mixin", "typeof", "unless", "var", "when")
    "tags" and listOf("a", "abbr", "acronym", "address", "area", "article", "aside", "audio", "b",
        "base", "basefont", "bdi", "bdo", "blockquote", "body", "br", "button", "canvas", "caption",
        "center", "cite", "code", "col", "colgroup", "command", "datalist", "dd", "del", "details",
        "dfn", "div", "dl", "dt", "em", "embed", "fieldset", "figcaption", "figure", "font",
        "footer", "form", "frame", "frameset", "h1", "h2", "h3", "h4", "h5", "h6", "head", "header",
        "hgroup", "hr", "html", "i", "iframe", "img", "input", "ins", "keygen", "kbd", "label",
        "li", "link", "map", "mark", "menu", "meta", "meter", "nav", "noframes", "noscript",
        "object", "ol", "optgroup", "option", "output", "p", "param", "pre", "progress", "q", "rp",
        "rt", "ruby", "s", "samp", "script", "section", "select", "small", "source", "span",
        "strike", "strong", "style", "sub", "summary", "sup", "table", "tbody", "td", "textarea",
        "tfoot", "th", "thead", "time", "title", "tr", "tracks", "tt", "u", "ul", "video", "wbr")
    symbols("[\\+\\-\\*\\%\\&\\|\\!\\=\\/\\.\\,\\:]+")
    escapes("\\\\(?:[abfnrtv\\\\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
    tokenizer {
      root {
        "^(\\s*)([a-zA-Z_-][\\w-]*)".action {
          cases {
            "${'$'}2@tags" and {
              cases {
                "@eos" actionArray {
                  token("")
                  token("tag")
                }
                "@default" actionArray {
                  token("")
                  action("tag") {
                    next = "@tag.${'$'}1"
                  }
                }
              }
            }
            "${'$'}2@keywords" actionArray {
              token("")
              action("keyword.${'$'}2") {
              }
            }
            "@default" actionArray {
              token("")
              token("")
            }
          }
        }
        "^(\\s*)(#[a-zA-Z_-][\\w-]*)".action {
          cases {
            "@eos" actionArray {
              token("")
              token("tag.id")
            }
            "@default" actionArray {
              token("")
              action("tag.id") {
                next = "@tag.${'$'}1"
              }
            }
          }
        }
        "^(\\s*)(\\.[a-zA-Z_-][\\w-]*)".action {
          cases {
            "@eos" actionArray {
              token("")
              token("tag.class")
            }
            "@default" actionArray {
              token("")
              action("tag.class") {
                next = "@tag.${'$'}1"
              }
            }
          }
        }
        "^(\\s*)(\\|.*)${'$'}".token("")
        include("@whitespace")
        "[a-zA-Z_${'$'}][\\w${'$'}]*".action {
          cases {
            "@keywords" and {
              token = "keyword.${'$'}0"
            }
            "@default" and ""
          }
        }
        "[{}()\\[\\]]".token("@brackets")
        "@symbols".token("delimiter")
        "\\d+\\.\\d+([eE][\\-+]?\\d+)?".token("number.float")
        "\\d+".token("number")
        "\"".action("string").state("@string.\"")
        "'".action("string").state("@string.'")
      }
      "tag" rules {
        "(\\.)(\\s*${'$'})".actionArray {
          action("delimiter") {
            next = "@blockText.${'$'}S2."
          }
          token("")
        }
        "\\s+".action {
          token = ""
          next = "@simpleText"
        }
        "#[a-zA-Z_-][\\w-]*".action {
          cases {
            "@eos" and {
              token = "tag.id"
              next = "@pop"
            }
            "@default" and "tag.id"
          }
        }
        "\\.[a-zA-Z_-][\\w-]*".action {
          cases {
            "@eos" and {
              token = "tag.class"
              next = "@pop"
            }
            "@default" and "tag.class"
          }
        }
        "\\(".action {
          token = "delimiter.parenthesis"
          next = "@attributeList"
        }
      }
      "simpleText" rules {
        "[^#]+${'$'}".action {
          token = ""
          next = "@popall"
        }
        "[^#]+".action {
          token = ""
        }
        "(#{)([^}]*)(})".action {
          cases {
            "@eos" actionArray {
              token("interpolation.delimiter")
              token("interpolation")
              action("interpolation.delimiter") {
                next = "@popall"
              }
            }
            "@default" actionArray {
              token("interpolation.delimiter")
              token("interpolation")
              token("interpolation.delimiter")
            }
          }
        }
        "#${'$'}".action {
          token = ""
          next = "@popall"
        }
        "#".token("")
      }
      "attributeList" rules {
        "\\s+".token("")
        "(\\w+)(\\s*=\\s*)(\"|')".actionArray {
          token("attribute.name")
          token("delimiter")
          action("attribute.value") {
            next = "@value.${'$'}3"
          }
        }
        "\\w+".token("attribute.name")
        ",".action {
          cases {
            "@eos" and {
              token = "attribute.delimiter"
              next = "@popall"
            }
            "@default" and "attribute.delimiter"
          }
        }
        "\\)${'$'}".action {
          token = "delimiter.parenthesis"
          next = "@popall"
        }
        "\\)".action {
          token = "delimiter.parenthesis"
          next = "@pop"
        }
      }
      whitespace {
        "^(\\s*)(\\/\\/.*)${'$'}".action {
          token = "comment"
          next = "@blockText.${'$'}1.comment"
        }
        "[ \\t\\r\\n]+".token("")
        "<!--".action {
          token = "comment"
          next = "@comment"
        }
      }
      "blockText" rules {
        "^\\s+.*${'$'}".action {
          cases {
            "(${'$'}S2\\s+.*${'$'})" and {
              token = "${'$'}S3"
            }
            "@default" and {
              token = "@rematch"
              next = "@popall"
            }
          }
        }
        ".".action {
          token = "@rematch"
          next = "@popall"
        }
      }
      comment {
        "[^<\\-]+".token("comment.content")
        "-->".action {
          token = "comment"
          next = "@pop"
        }
        "<!--".token("comment.content.invalid")
        "[<\\-]".token("comment.content")
      }
      string {
        "[^\\\\\"'#]+".action {
          cases {
            "@eos" and {
              token = "string"
              next = "@popall"
            }
            "@default" and "string"
          }
        }
        "@escapes".action {
          cases {
            "@eos" and {
              token = "string.escape"
              next = "@popall"
            }
            "@default" and "string.escape"
          }
        }
        "\\\\.".action {
          cases {
            "@eos" and {
              token = "string.escape.invalid"
              next = "@popall"
            }
            "@default" and "string.escape.invalid"
          }
        }
        "(#{)([^}]*)(})".actionArray {
          token("interpolation.delimiter")
          token("interpolation")
          token("interpolation.delimiter")
        }
        "#".token("string")
        "[\"']".action {
          cases {
            "${'$'}#==${'$'}S2" and {
              token = "string"
              next = "@pop"
            }
            "@default" and {
              token = "string"
            }
          }
        }
      }
      "value" rules {
        "[^\\\\\"']+".action {
          cases {
            "@eos" and {
              token = "attribute.value"
              next = "@popall"
            }
            "@default" and "attribute.value"
          }
        }
        "\\\\.".action {
          cases {
            "@eos" and {
              token = "attribute.value"
              next = "@popall"
            }
            "@default" and "attribute.value"
          }
        }
        "[\"']".action {
          cases {
            "${'$'}#==${'$'}S2" and {
              token = "attribute.value"
              next = "@pop"
            }
            "@default" and {
              token = "attribute.value"
            }
          }
        }
      }
    }
  }
}

