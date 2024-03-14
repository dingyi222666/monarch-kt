package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val RstLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".rst"
    defaultToken = ""
    control("[\\\\`*_\\[\\]{}()#+\\-\\.!]")
    escapes("\\\\(?:@control)")
    "empty" and listOf("area", "base", "basefont", "br", "col", "frame", "hr", "img", "input",
        "isindex", "link", "meta", "param")
    "alphanumerics" and "[A-Za-z0-9]"
    "simpleRefNameWithoutBq" and "(?:@alphanumerics[-_+:.]*@alphanumerics)+|(?:@alphanumerics+)"
    "simpleRefName" and "(?:`@phrase`|@simpleRefNameWithoutBq)"
    "phrase" and "@simpleRefNameWithoutBq(?:\\s@simpleRefNameWithoutBq)*"
    "citationName" and "[A-Za-z][A-Za-z0-9-_.]*"
    "blockLiteralStart" and "(?:[!\"#${'$'}%&'()*+,-./:;<=>?@\\[\\]^_`{|}~]|[\\s])"
    "precedingChars" and "(?:[ -:/'\"<([{])"
    "followingChars" and "(?:[ -.,:;!?/'\")\\]}>]|${'$'})"
    "punctuation" and "(=|-|~|`|#|\"|\\^|\\+|\\*|:|\\.|'|_|\\+)"
    tokenizer {
      root {
        "^(@punctuation{3,}${'$'}){1,1}?".token("keyword")
        "^\\s*([\\*\\-+‣•]|[a-zA-Z0-9]+\\.|\\([a-zA-Z0-9]+\\)|[a-zA-Z0-9]+\\))\\s".token("keyword")
        "([ ]::)\\s*${'$'}".action("keyword").state("@blankLineOfLiteralBlocks")
        "(::)\\s*${'$'}".action("keyword").state("@blankLineOfLiteralBlocks")
        include("@tables")
        include("@explicitMarkupBlocks")
        include("@inlineMarkup")
      }
      "explicitMarkupBlocks" rules {
        include("@citations")
        include("@footnotes")
        "^(\\.\\.\\s)(@simpleRefName)(::\\s)(.*)${'$'}".actionArray {
          action("") {
            next = "subsequentLines"
          }
          token("keyword")
          token("")
          token("")
        }
        "^(\\.\\.)(\\s+)(_)(@simpleRefName)(:)(\\s+)(.*)".actionArray {
          action("") {
            next = "hyperlinks"
          }
          token("")
          token("")
          token("string.link")
          token("")
          token("")
          token("string.link")
        }
        "^((?:(?:\\.\\.)(?:\\s+))?)(__)(:)(\\s+)(.*)".actionArray {
          action("") {
            next = "subsequentLines"
          }
          token("")
          token("")
          token("")
          token("string.link")
        }
        "^(__\\s+)(.+)".actionArray {
          token("")
          token("string.link")
        }
        "^(\\.\\.)( \\|)([^| ]+[^|]*[^| ]*)(\\| )(@simpleRefName)(:: .*)" actionArray {
          action("") {
            next = "subsequentLines"
          }
          token("")
          token("string.link")
          token("")
          token("keyword")
          token("")
        }
        "(\\|)([^| ]+[^|]*[^| ]*)(\\|_{0,2})".actionArray {
          token("")
          token("string.link")
          token("")
        }
        "^(\\.\\.)([ ].*)${'$'}".actionArray {
          action("") {
            next = "@comments"
          }
          token("comment")
        }
      }
      "inlineMarkup" rules {
        include("@citationsReference")
        include("@footnotesReference")
        "(@simpleRefName)(_{1,2})".actionArray {
          token("string.link")
          token("")
        }
        "(`)([^<`]+\\s+)(<)(.*)(>)(`)(_)".actionArray {
          token("")
          token("string.link")
          token("")
          token("string.link")
          token("")
          token("")
          token("")
        }
        "\\*\\*([^\\\\*]|\\*(?!\\*))+\\*\\*".token("strong")
        "\\*[^*]+\\*".token("emphasis")
        "(``)((?:[^`]|\\`(?!`))+)(``)".actionArray {
          token("")
          token("keyword")
          token("")
        }
        "(__\\s+)(.+)".actionArray {
          token("")
          token("keyword")
        }
        "(:)((?:@simpleRefNameWithoutBq)?)(:`)([^`]+)(`)".actionArray {
          token("")
          token("keyword")
          token("")
          token("")
          token("")
        }
        "(`)([^`]+)(`:)((?:@simpleRefNameWithoutBq)?)(:)".actionArray {
          token("")
          token("")
          token("")
          token("keyword")
          token("")
        }
        "(`)([^`]+)(`)".token("")
        "(_`)(@phrase)(`)".actionArray {
          token("")
          token("string.link")
          token("")
        }
      }
      "citations" rules {
        "^(\\.\\.\\s+\\[)((?:@citationName))(\\]\\s+)(.*)".actionArray {
          action("") {
            next = "@subsequentLines"
          }
          token("string.link")
          token("")
          token("")
        }
      }
      "citationsReference" rules {
        "(\\[)(@citationName)(\\]_)".actionArray {
          token("")
          token("string.link")
          token("")
        }
      }
      "footnotes" rules {
        "^(\\.\\.\\s+\\[)((?:[0-9]+))(\\]\\s+.*)".actionArray {
          action("") {
            next = "@subsequentLines"
          }
          token("string.link")
          token("")
        }
        "^(\\.\\.\\s+\\[)((?:#@simpleRefName?))(\\]\\s+)(.*)".actionArray {
          action("") {
            next = "@subsequentLines"
          }
          token("string.link")
          token("")
          token("")
        }
        "^(\\.\\.\\s+\\[)((?:\\*))(\\]\\s+)(.*)".actionArray {
          action("") {
            next = "@subsequentLines"
          }
          token("string.link")
          token("")
          token("")
        }
      }
      "footnotesReference" rules {
        "(\\[)([0-9]+)(\\])(_)".actionArray {
          token("")
          token("string.link")
          token("")
          token("")
        }
        "(\\[)(#@simpleRefName?)(\\])(_)".actionArray {
          token("")
          token("string.link")
          token("")
          token("")
        }
        "(\\[)(\\*)(\\])(_)".actionArray {
          token("")
          token("string.link")
          token("")
          token("")
        }
      }
      "blankLineOfLiteralBlocks" rules {
        "^${'$'}".action("").state("@subsequentLinesOfLiteralBlocks")
        "^.*${'$'}".action("").state("@pop")
      }
      "subsequentLinesOfLiteralBlocks" rules {
        "(@blockLiteralStart+)(.*)".actionArray {
          token("keyword")
          token("")
        }
        "^(?!blockLiteralStart)".action("").state("@popall")
      }
      "subsequentLines" rules {
        "^[\\s]+.*".token("")
        "^(?!\\s)".action("").state("@pop")
      }
      "hyperlinks" rules {
        "^[\\s]+.*".token("string.link")
        "^(?!\\s)".action("").state("@pop")
      }
      comments {
        "^[\\s]+.*".token("comment")
        "^(?!\\s)".action("").state("@pop")
      }
      "tables" rules {
        "\\+-[+-]+".token("keyword")
        "\\+=[+=]+".token("keyword")
      }
    }
  }
}

