package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val TclLanguage: IMonarchLanguage = buildMonarchLanguage {
  tokenPostfix = ".tcl"
  brackets {
    bracket("(",")","delimiter.parenthesis")
    bracket("{","}","delimiter.curly")
    bracket("[","]","delimiter.square")
  }
  "specialFunctions" and listOf("set", "unset", "rename", "variable", "proc", "coroutine",
      "foreach", "incr", "append", "lappend", "linsert", "lreplace")
  "mainFunctions" and listOf("if", "then", "elseif", "else", "case", "switch", "while", "for",
      "break", "continue", "return", "package", "namespace", "catch", "exit", "eval", "expr",
      "uplevel", "upvar")
  "builtinFunctions" and listOf("file", "info", "concat", "join", "lindex", "list", "llength",
      "lrange", "lsearch", "lsort", "split", "array", "parray", "binary", "format", "regexp",
      "regsub", "scan", "string", "subst", "dict", "cd", "clock", "exec", "glob", "pid", "pwd",
      "close", "eof", "fblocked", "fconfigure", "fcopy", "fileevent", "flush", "gets", "open",
      "puts", "read", "seek", "socket", "tell", "interp", "after", "auto_execok", "auto_load",
      "auto_mkindex", "auto_reset", "bgerror", "error", "global", "history", "load", "source",
      "time", "trace", "unknown", "unset", "update", "vwait", "winfo", "wm", "bind", "event",
      "pack", "place", "grid", "font", "bell", "clipboard", "destroy", "focus", "grab", "lower",
      "option", "raise", "selection", "send", "tk", "tkwait", "tk_bisque", "tk_focusNext",
      "tk_focusPrev", "tk_focusFollowsMouse", "tk_popup", "tk_setPalette")
  symbols("[=><!~?:&|+\\-*\\/\\^%]+")
  escapes("\\\\(?:[abfnrtv\\\\\"'\\[\\]\\{\\};\\${'$'}]|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
  "variables" and "(?:\\${'$'}+(?:(?:\\:\\:?)?[a-zA-Z_]\\w*)+)"
  tokenizer {
    root {
      "[a-zA-Z_]\\w*".action {
        cases {
          "@specialFunctions" and {
            token = "keyword.flow"
            next = "@specialFunc"
          }
          "@mainFunctions" and "keyword"
          "@builtinFunctions" and "variable"
          "@default" and "operator.scss"
        }
      }
      "\\s+\\-+(?!\\d|\\.)\\w*|{\\*}".token("metatag")
      include("@whitespace")
      "[{}()\\[\\]]".token("@brackets")
      "@symbols".token("operator")
      "\\${'$'}+(?:\\:\\:)?\\{".action {
        token = "identifier"
        next = "@nestedVariable"
      }
      "@variables".token("type.identifier")
      "\\.(?!\\d|\\.)[\\w\\-]*".token("operator.sql")
      "\\d+(\\.\\d+)?".token("number")
      "\\d+".token("number")
      ";".token("delimiter")
      "\"".action {
        token = "string.quote"
        next = "@dstring"
        bracket = "@open"
      }
      "'".action {
        token = "string.quote"
        next = "@sstring"
        bracket = "@open"
      }
    }
    "dstring" rules {
      "\\[".action {
        token = "@brackets"
        next = "@nestedCall"
      }
      "\\${'$'}+(?:\\:\\:)?\\{".action {
        token = "identifier"
        next = "@nestedVariable"
      }
      "@variables".token("type.identifier")
      "[^\\\\${'$'}\\[\\]\"]+".token("string")
      "@escapes".token("string.escape")
      "\"".action {
        token = "string.quote"
        next = "@pop"
        bracket = "@close"
      }
    }
    "sstring" rules {
      "\\[".action {
        token = "@brackets"
        next = "@nestedCall"
      }
      "\\${'$'}+(?:\\:\\:)?\\{".action {
        token = "identifier"
        next = "@nestedVariable"
      }
      "@variables".token("type.identifier")
      "[^\\\\${'$'}\\[\\]']+".token("string")
      "@escapes".token("string.escape")
      "'".action {
        token = "string.quote"
        next = "@pop"
        bracket = "@close"
      }
    }
    whitespace {
      "[ \\t\\r\\n]+".token("white")
      "#.*\\\\${'$'}".action {
        token = "comment"
        next = "@newlineComment"
      }
      "#.*(?!\\\\)${'$'}".token("comment")
    }
    "newlineComment" rules {
      ".*\\\\${'$'}".token("comment")
      ".*(?!\\\\)${'$'}".action {
        token = "comment"
        next = "@pop"
      }
    }
    "nestedVariable" rules {
      "[^\\{\\}\\${'$'}]+".token("type.identifier")
      "\\}".action {
        token = "identifier"
        next = "@pop"
      }
    }
    "nestedCall" rules {
      "\\[".action {
        token = "@brackets"
        next = "@nestedCall"
      }
      "\\]".action {
        token = "@brackets"
        next = "@pop"
      }
      include("root")
    }
    "specialFunc" rules {
      "\"".action {
        token = "string"
        next = "@dstring"
      }
      "'".action {
        token = "string"
        next = "@sstring"
      }
      "\\S+".action {
        token = "type"
        next = "@pop"
      }
    }
  }
}

