package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val HclLanguage: IMonarchLanguage = buildMonarchLanguage {
  tokenPostfix = ".hcl"
  defaultToken = ""
  keywords("var", "local", "path", "for_each", "any", "string", "number", "bool", "true", "false",
      "null", "if ", "else ", "endif ", "for ", "in", "endfor")
  operators("=", ">=", "<=", "==", "!=", "+", "-", "*", "/", "%", "&&", "||", "!", "<", ">", "?",
      "...", ":")
  symbols("[=><!~?:&|+\\-*\\/\\^%]+")
  escapes("\\\\(?:[abfnrtv\\\\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
  "terraformFunctions" and
      "(abs|ceil|floor|log|max|min|pow|signum|chomp|format|formatlist|indent|join|lower|regex|regexall|replace|split|strrev|substr|title|trimspace|upper|chunklist|coalesce|coalescelist|compact|concat|contains|distinct|element|flatten|index|keys|length|list|lookup|map|matchkeys|merge|range|reverse|setintersection|setproduct|setunion|slice|sort|transpose|values|zipmap|base64decode|base64encode|base64gzip|csvdecode|jsondecode|jsonencode|urlencode|yamldecode|yamlencode|abspath|dirname|pathexpand|basename|file|fileexists|fileset|filebase64|templatefile|formatdate|timeadd|timestamp|base64sha256|base64sha512|bcrypt|filebase64sha256|filebase64sha512|filemd5|filemd1|filesha256|filesha512|md5|rsadecrypt|sha1|sha256|sha512|uuid|uuidv5|cidrhost|cidrnetmask|cidrsubnet|tobool|tolist|tomap|tonumber|toset|tostring)"
  "terraformMainBlocks" and "(module|data|terraform|resource|provider|variable|output|locals)"
  tokenizer {
    root {
      "^@terraformMainBlocks([ \\t]*)([\\w-]+|\"[\\w-]+\"|)([ \\t]*)([\\w-]+|\"[\\w-]+\"|)([ \\t]*)(\\{)".actionArray {
        token("type")
        token("")
        token("string")
        token("")
        token("string")
        token("")
        token("@brackets")
      }
      "(\\w+[ \\t]+)([ \\t]*)([\\w-]+|\"[\\w-]+\"|)([ \\t]*)([\\w-]+|\"[\\w-]+\"|)([ \\t]*)(\\{)".actionArray {
        token("identifier")
        token("")
        token("string")
        token("")
        token("string")
        token("")
        token("@brackets")
      }
      "(\\w+[ \\t]+)([ \\t]*)([\\w-]+|\"[\\w-]+\"|)([ \\t]*)([\\w-]+|\"[\\w-]+\"|)(=)(\\{)".actionArray {
        token("identifier")
        token("")
        token("string")
        token("")
        token("operator")
        token("")
        token("@brackets")
      }
      include("@terraform")
    }
    "terraform" rules {
      "@terraformFunctions(\\()".actionArray {
        token("type")
        token("@brackets")
      }
      "[a-zA-Z_]\\w*-*".action {
        cases {
          "@keywords" and {
            token = "keyword.${'$'}0"
          }
          "@default" and "variable"
        }
      }
      include("@whitespace")
      include("@heredoc")
      "[{}()\\[\\]]".token("@brackets")
      "[<>](?!@symbols)".token("@brackets")
      "@symbols".action {
        cases {
          "@operators" and "operator"
          "@default" and ""
        }
      }
      "\\d*\\d+[eE]([\\-+]?\\d+)?".token("number.float")
      "\\d*\\.\\d+([eE][\\-+]?\\d+)?".token("number.float")
      "\\d[\\d']*".token("number")
      "\\d".token("number")
      "[;,.]".token("delimiter")
      "\"".action("string").state("@string")
      "'".token("invalid")
    }
    "heredoc" rules {
      "<<[-]*\\s*[\"]?([\\w\\-]+)[\"]?".action {
        token = "string.heredoc.delimiter"
        next = "@heredocBody.${'$'}1"
      }
    }
    "heredocBody" rules {
      "([\\w\\-]+)${'$'}".action {
        cases {
          "${'$'}1==${'$'}S2" actionArray {
            action("string.heredoc.delimiter") {
              next = "@popall"
            }
          }
          "@default" and "string.heredoc"
        }
      }
      ".".token("string.heredoc")
    }
    whitespace {
      "[ \\t\\r\\n]+".token("")
      "\\/\\*".action("comment").state("@comment")
      "\\/\\/.*${'$'}".token("comment")
      "#.*${'$'}".token("comment")
    }
    comment {
      "[^\\/*]+".token("comment")
      "\\*\\/".action("comment").state("@pop")
      "[\\/*]".token("comment")
    }
    string {
      "\\${'$'}\\{".action {
        token = "delimiter"
        next = "@stringExpression"
      }
      "[^\\\\\"\\${'$'}]+".token("string")
      "@escapes".token("string.escape")
      "\\\\.".token("string.escape.invalid")
      "\"".action("string").state("@popall")
    }
    "stringInsideExpression" rules {
      "[^\\\\\"]+".token("string")
      "@escapes".token("string.escape")
      "\\\\.".token("string.escape.invalid")
      "\"".action("string").state("@pop")
    }
    "stringExpression" rules {
      "\\}".action {
        token = "delimiter"
        next = "@pop"
      }
      "\"".action("string").state("@stringInsideExpression")
      include("@terraform")
    }
  }
}

