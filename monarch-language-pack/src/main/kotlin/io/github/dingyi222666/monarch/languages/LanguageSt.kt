package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val StLanguage: IMonarchLanguage = buildMonarchLanguage {
  tokenPostfix = ".st"
  ignoreCase = true
  defaultToken = ""
  brackets {
    bracket("{","}","delimiter.curly")
    bracket("(",")","delimiter.parenthesis")
    bracket("[","]","delimiter.square")
  }
  keywords("if", "end_if", "elsif", "else", "case", "of", "to", "__try", "__catch", "__finally",
      "do", "with", "by", "while", "repeat", "end_while", "end_repeat", "end_case", "for",
      "end_for", "task", "retain", "non_retain", "constant", "with", "at", "exit", "return",
      "interval", "priority", "address", "port", "on_channel", "then", "iec", "file", "uses",
      "version", "packagetype", "displayname", "copyright", "summary", "vendor", "common_source",
      "from", "extends", "implements")
  "constant" and listOf("false", "true", "null")
  "defineKeywords" and listOf("var", "var_input", "var_output", "var_in_out", "var_temp",
      "var_global", "var_access", "var_external", "end_var", "type", "end_type", "struct",
      "end_struct", "program", "end_program", "function", "end_function", "function_block",
      "end_function_block", "interface", "end_interface", "method", "end_method", "property",
      "end_property", "namespace", "end_namespace", "configuration", "end_configuration", "tcp",
      "end_tcp", "resource", "end_resource", "channel", "end_channel", "library", "end_library",
      "folder", "end_folder", "binaries", "end_binaries", "includes", "end_includes", "sources",
      "end_sources", "action", "end_action", "step", "initial_step", "end_step", "transaction",
      "end_transaction")
  typeKeywords("int", "sint", "dint", "lint", "usint", "uint", "udint", "ulint", "real", "lreal",
      "time", "date", "time_of_day", "date_and_time", "string", "bool", "byte", "word", "dword",
      "array", "pointer", "lword")
  operators("=", ">", "<", ":", ":=", "<=", ">=", "<>", "&", "+", "-", "*", "**", "MOD", "^", "or",
      "and", "not", "xor", "abs", "acos", "asin", "atan", "cos", "exp", "expt", "ln", "log", "sin",
      "sqrt", "tan", "sel", "max", "min", "limit", "mux", "shl", "shr", "rol", "ror", "indexof",
      "sizeof", "adr", "adrinst", "bitadr", "is_valid", "ref", "ref_to")
  "builtinVariables" and listOf()
  "builtinFunctions" and listOf("sr", "rs", "tp", "ton", "tof", "eq", "ge", "le", "lt", "ne",
      "round", "trunc", "ctd", "сtu", "ctud", "r_trig", "f_trig", "move", "concat", "delete",
      "find", "insert", "left", "len", "replace", "right", "rtc")
  symbols("[=><!~?:&|+\\-*\\/\\^%]+")
  escapes("\\\\(?:[abfnrtv\\\\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
  tokenizer {
    root {
      "(\\.\\.)".token("delimiter")
      "\\b(16#[0-9A-Fa-f\\_]*)+\\b".token("number.hex")
      "\\b(2#[01\\_]+)+\\b".token("number.binary")
      "\\b(8#[0-9\\_]*)+\\b".token("number.octal")
      "\\b\\d*\\.\\d+([eE][\\-+]?\\d+)?\\b".token("number.float")
      "\\b(L?REAL)#[0-9\\_\\.e]+\\b".token("number.float")
      "\\b(BYTE|(?:D|L)?WORD|U?(?:S|D|L)?INT)#[0-9\\_]+\\b".token("number")
      "\\d+".token("number")
      "\\b(T|DT|TOD)#[0-9:-_shmyd]+\\b".token("tag")
      "\\%(I|Q|M)(X|B|W|D|L)[0-9\\.]+".token("tag")
      "\\%(I|Q|M)[0-9\\.]*".token("tag")
      "\\b[A-Za-z]{1,6}#[0-9]+\\b".token("tag")
      "\\b(TO_|CTU_|CTD_|CTUD_|MUX_|SEL_)[A_Za-z]+\\b".token("predefined")
      "\\b[A_Za-z]+(_TO_)[A_Za-z]+\\b".token("predefined")
      "[;]".token("delimiter")
      "[.]".action {
        token = "delimiter"
        next = "@params"
      }
      "[a-zA-Z_]\\w*".action {
        cases {
          "@operators" and "operators"
          "@keywords" and "keyword"
          "@typeKeywords" and "type"
          "@defineKeywords" and "variable"
          "@constant" and "constant"
          "@builtinVariables" and "predefined"
          "@builtinFunctions" and "predefined"
          "@default" and "identifier"
        }
      }
      include("@whitespace")
      "[{}()\\[\\]]".token("@brackets")
      "\"([^\"\\\\]|\\\\.)*${'$'}".token("string.invalid")
      "\"".action {
        token = "string.quote"
        next = "@string_dq"
        bracket = "@open"
      }
      "'".action {
        token = "string.quote"
        next = "@string_sq"
        bracket = "@open"
      }
      "'[^\\\\']'".token("string")
      "(')(@escapes)(')".actionArray {
        token("string")
        token("string.escape")
        token("string")
      }
      "'".token("string.invalid")
    }
    "params" rules {
      "\\b[A-Za-z0-9_]+\\b(?=\\()".action {
        token = "identifier"
        next = "@pop"
      }
      "\\b[A-Za-z0-9_]+\\b".action("variable.name").state("@pop")
    }
    comment {
      "[^\\/*]+".token("comment")
      "\\/\\*".action("comment").state("@push")
      "\\*/".action("comment").state("@pop")
      "[\\/*]".token("comment")
    }
    "comment2" rules {
      "[^\\(*]+".token("comment")
      "\\(\\*".action("comment").state("@push")
      "\\*\\)".action("comment").state("@pop")
      "[\\(*]".token("comment")
    }
    whitespace {
      "[ \\t\\r\\n]+".token("white")
      "\\/\\/.*${'$'}".token("comment")
      "\\/\\*".action("comment").state("@comment")
      "\\(\\*".action("comment").state("@comment2")
    }
    "string_dq" rules {
      "[^\\\\\"]+".token("string")
      "@escapes".token("string.escape")
      "\\\\.".token("string.escape.invalid")
      "\"".action {
        token = "string.quote"
        next = "@pop"
        bracket = "@close"
      }
    }
    "string_sq" rules {
      "[^\\\\']+".token("string")
      "@escapes".token("string.escape")
      "\\\\.".token("string.escape.invalid")
      "'".action {
        token = "string.quote"
        next = "@pop"
        bracket = "@close"
      }
    }
  }
}

