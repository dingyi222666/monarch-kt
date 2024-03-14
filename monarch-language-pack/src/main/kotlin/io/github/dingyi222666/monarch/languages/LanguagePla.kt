package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val PlaLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".pla"
    defaultToken = ""
    brackets {
      bracket("[","]","delimiter.square")
      bracket("<",">","delimiter.angle")
      bracket("(",")","delimiter.parenthesis")
    }
    keywords(".i", ".o", ".mv", ".ilb", ".ob", ".label", ".type", ".phase", ".pair", ".symbolic",
        ".symbolic-output", ".kiss", ".p", ".e", ".end")
    "comment" and "#.*${'$'}"
    "identifier" and "[a-zA-Z]+[a-zA-Z0-9_\\-]*"
    "plaContent" and "[01\\-~\\|]+"
    tokenizer {
      root {
        include("@whitespace")
        "@comment".token("comment")
        "\\.([a-zA-Z_\\-]+)".action {
          cases {
            "@eos" and {
              token = "keyword.${'$'}1"
            }
            "@keywords" and {
              cases {
                ".type" and {
                  token = "keyword.${'$'}1"
                  next = "@type"
                }
                "@default" and {
                  token = "keyword.${'$'}1"
                  next = "@keywordArg"
                }
              }
            }
            "@default" and {
              token = "keyword.${'$'}1"
            }
          }
        }
        "@identifier".token("identifier")
        "@plaContent".token("string")
      }
      whitespace {
        "[ \\t\\r\\n]+".token("")
      }
      "type" rules {
        include("@whitespace")
        "\\w+".action {
          token = "type"
          next = "@pop"
        }
      }
      "keywordArg" rules {
        "[ \\t\\r\\n]+".action {
          cases {
            "@eos" and {
              token = ""
              next = "@pop"
            }
            "@default" and ""
          }
        }
        "@comment".action("comment").state("@pop")
        "[<>()\\[\\]]".action {
          cases {
            "@eos" and {
              token = "@brackets"
              next = "@pop"
            }
            "@default" and "@brackets"
          }
        }
        "\\-?\\d+".action {
          cases {
            "@eos" and {
              token = "number"
              next = "@pop"
            }
            "@default" and "number"
          }
        }
        "@identifier".action {
          cases {
            "@eos" and {
              token = "identifier"
              next = "@pop"
            }
            "@default" and "identifier"
          }
        }
        "[;=]".action {
          cases {
            "@eos" and {
              token = "delimiter"
              next = "@pop"
            }
            "@default" and "delimiter"
          }
        }
      }
    }
  }
}

