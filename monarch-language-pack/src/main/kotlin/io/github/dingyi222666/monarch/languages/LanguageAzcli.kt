package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val AzcliLanguage: IMonarchLanguage = buildMonarchLanguage {
  tokenPostfix = ".azcli"
  ignoreCase = true
  defaultToken = "keyword"
  "str" and "[^#\\s]"
  tokenizer {
    root {
      include("@comment")
      "\\s-+@str*\\s*".action {
        cases {
          "@eos" and {
            token = "key.identifier"
            next = "@popall"
          }
          "@default" and {
            token = "key.identifier"
            next = "@type"
          }
        }
      }
      "^-+@str*\\s*".action {
        cases {
          "@eos" and {
            token = "key.identifier"
            next = "@popall"
          }
          "@default" and {
            token = "key.identifier"
            next = "@type"
          }
        }
      }
    }
    "type" rules {
      include("@comment")
      "-+@str*\\s*".action {
        cases {
          "@eos" and {
            token = "key.identifier"
            next = "@popall"
          }
          "@default" and "key.identifier"
        }
      }
      "@str+\\s*".action {
        cases {
          "@eos" and {
            token = "string"
            next = "@popall"
          }
          "@default" and "string"
        }
      }
    }
    comment {
      "#.*${'$'}".action {
        cases {
          "@eos" and {
            token = "comment"
            next = "@popall"
          }
        }
      }
    }
  }
}

