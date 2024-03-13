package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val DockerfileLanguage: IMonarchLanguage = buildMonarchLanguage {
  tokenPostfix = ".dockerfile"
  defaultToken = ""
  "variable" and "\\${'$'}{?[\\w]+}?"
  tokenizer {
    root {
      include("@whitespace")
      include("@comment")
      "(ONBUILD)(\\s+)".actionArray {
        token("keyword")
        token("")
      }
      "(ENV)(\\s+)([\\w]+)".actionArray {
        token("keyword")
        token("")
        action("variable") {
          next = "@arguments"
        }
      }
      "(FROM|MAINTAINER|RUN|EXPOSE|ENV|ADD|ARG|VOLUME|LABEL|USER|WORKDIR|COPY|CMD|STOPSIGNAL|SHELL|HEALTHCHECK|ENTRYPOINT)".action {
        token = "keyword"
        next = "@arguments"
      }
    }
    "arguments" rules {
      include("@whitespace")
      include("@strings")
      "(@variable)".action {
        cases {
          "@eos" and {
            token = "variable"
            next = "@popall"
          }
          "@default" and "variable"
        }
      }
      "\\\\".action {
        cases {
          "@eos" and ""
          "@default" and ""
        }
      }
      ".".action {
        cases {
          "@eos" and {
            token = ""
            next = "@popall"
          }
          "@default" and ""
        }
      }
    }
    whitespace {
      "\\s+".action {
        cases {
          "@eos" and {
            token = ""
            next = "@popall"
          }
          "@default" and ""
        }
      }
    }
    comment {
      "(^#.*${'$'})".action("comment").state("@popall")
    }
    "strings" rules {
      "\\\\'${'$'}".action("").state("@popall")
      "\\\\'".token("")
      "'${'$'}".action("string").state("@popall")
      "'".action("string").state("@stringBody")
      "\"${'$'}".action("string").state("@popall")
      "\"".action("string").state("@dblStringBody")
    }
    "stringBody" rules {
      "[^\\\\\\${'$'}']".action {
        cases {
          "@eos" and {
            token = "string"
            next = "@popall"
          }
          "@default" and "string"
        }
      }
      "\\\\.".token("string.escape")
      "'${'$'}".action("string").state("@popall")
      "'".action("string").state("@pop")
      "(@variable)".token("variable")
      "\\\\${'$'}".token("string")
      "${'$'}".action("string").state("@popall")
    }
    "dblStringBody" rules {
      "[^\\\\\\${'$'}\"]".action {
        cases {
          "@eos" and {
            token = "string"
            next = "@popall"
          }
          "@default" and "string"
        }
      }
      "\\\\.".token("string.escape")
      "\"${'$'}".action("string").state("@popall")
      "\"".action("string").state("@pop")
      "(@variable)".token("variable")
      "\\\\${'$'}".token("string")
      "${'$'}".action("string").state("@popall")
    }
  }
}

