package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val ApexLanguage: IMonarchLanguage = buildMonarchLanguage {
  tokenPostfix = ".apex"
  defaultToken = ""
  keywords("abstract", "ABSTRACT", "Abstract", "activate", "ACTIVATE", "Activate", "and", "AND",
      "And", "any", "ANY", "Any", "array", "ARRAY", "Array", "as", "AS", "As", "asc", "ASC", "Asc",
      "assert", "ASSERT", "Assert", "autonomous", "AUTONOMOUS", "Autonomous", "begin", "BEGIN",
      "Begin", "bigdecimal", "BIGDECIMAL", "Bigdecimal", "blob", "BLOB", "Blob", "boolean",
      "BOOLEAN", "Boolean", "break", "BREAK", "Break", "bulk", "BULK", "Bulk", "by", "BY", "By",
      "case", "CASE", "Case", "cast", "CAST", "Cast", "catch", "CATCH", "Catch", "char", "CHAR",
      "Char", "class", "CLASS", "Class", "collect", "COLLECT", "Collect", "commit", "COMMIT",
      "Commit", "const", "CONST", "Const", "continue", "CONTINUE", "Continue", "convertcurrency",
      "CONVERTCURRENCY", "Convertcurrency", "decimal", "DECIMAL", "Decimal", "default", "DEFAULT",
      "Default", "delete", "DELETE", "Delete", "desc", "DESC", "Desc", "do", "DO", "Do", "double",
      "DOUBLE", "Double", "else", "ELSE", "Else", "end", "END", "End", "enum", "ENUM", "Enum",
      "exception", "EXCEPTION", "Exception", "exit", "EXIT", "Exit", "export", "EXPORT", "Export",
      "extends", "EXTENDS", "Extends", "false", "FALSE", "False", "final", "FINAL", "Final",
      "finally", "FINALLY", "Finally", "float", "FLOAT", "Float", "for", "FOR", "For", "from",
      "FROM", "From", "future", "FUTURE", "Future", "get", "GET", "Get", "global", "GLOBAL",
      "Global", "goto", "GOTO", "Goto", "group", "GROUP", "Group", "having", "HAVING", "Having",
      "hint", "HINT", "Hint", "if", "IF", "If", "implements", "IMPLEMENTS", "Implements", "import",
      "IMPORT", "Import", "in", "IN", "In", "inner", "INNER", "Inner", "insert", "INSERT", "Insert",
      "instanceof", "INSTANCEOF", "Instanceof", "int", "INT", "Int", "interface", "INTERFACE",
      "Interface", "into", "INTO", "Into", "join", "JOIN", "Join", "last_90_days", "LAST_90_DAYS",
      "Last_90_days", "last_month", "LAST_MONTH", "Last_month", "last_n_days", "LAST_N_DAYS",
      "Last_n_days", "last_week", "LAST_WEEK", "Last_week", "like", "LIKE", "Like", "limit",
      "LIMIT", "Limit", "list", "LIST", "List", "long", "LONG", "Long", "loop", "LOOP", "Loop",
      "map", "MAP", "Map", "merge", "MERGE", "Merge", "native", "NATIVE", "Native", "new", "NEW",
      "New", "next_90_days", "NEXT_90_DAYS", "Next_90_days", "next_month", "NEXT_MONTH",
      "Next_month", "next_n_days", "NEXT_N_DAYS", "Next_n_days", "next_week", "NEXT_WEEK",
      "Next_week", "not", "NOT", "Not", "null", "NULL", "Null", "nulls", "NULLS", "Nulls", "number",
      "NUMBER", "Number", "object", "OBJECT", "Object", "of", "OF", "Of", "on", "ON", "On", "or",
      "OR", "Or", "outer", "OUTER", "Outer", "override", "OVERRIDE", "Override", "package",
      "PACKAGE", "Package", "parallel", "PARALLEL", "Parallel", "pragma", "PRAGMA", "Pragma",
      "private", "PRIVATE", "Private", "protected", "PROTECTED", "Protected", "public", "PUBLIC",
      "Public", "retrieve", "RETRIEVE", "Retrieve", "return", "RETURN", "Return", "returning",
      "RETURNING", "Returning", "rollback", "ROLLBACK", "Rollback", "savepoint", "SAVEPOINT",
      "Savepoint", "search", "SEARCH", "Search", "select", "SELECT", "Select", "set", "SET", "Set",
      "short", "SHORT", "Short", "sort", "SORT", "Sort", "stat", "STAT", "Stat", "static", "STATIC",
      "Static", "strictfp", "STRICTFP", "Strictfp", "super", "SUPER", "Super", "switch", "SWITCH",
      "Switch", "synchronized", "SYNCHRONIZED", "Synchronized", "system", "SYSTEM", "System",
      "testmethod", "TESTMETHOD", "Testmethod", "then", "THEN", "Then", "this", "THIS", "This",
      "this_month", "THIS_MONTH", "This_month", "this_week", "THIS_WEEK", "This_week", "throw",
      "THROW", "Throw", "throws", "THROWS", "Throws", "today", "TODAY", "Today", "tolabel",
      "TOLABEL", "Tolabel", "tomorrow", "TOMORROW", "Tomorrow", "transaction", "TRANSACTION",
      "Transaction", "transient", "TRANSIENT", "Transient", "trigger", "TRIGGER", "Trigger", "true",
      "TRUE", "True", "try", "TRY", "Try", "type", "TYPE", "Type", "undelete", "UNDELETE",
      "Undelete", "update", "UPDATE", "Update", "upsert", "UPSERT", "Upsert", "using", "USING",
      "Using", "virtual", "VIRTUAL", "Virtual", "void", "VOID", "Void", "volatile", "VOLATILE",
      "Volatile", "webservice", "WEBSERVICE", "Webservice", "when", "WHEN", "When", "where",
      "WHERE", "Where", "while", "WHILE", "While", "yesterday", "YESTERDAY", "Yesterday")
  operators("=", ">", "<", "!", "~", "?", ":", "==", "<=", ">=", "!=", "&&", "||", "++", "--", "+",
      "-", "*", "/", "&", "|", "^", "%", "<<", ">>", ">>>", "+=", "-=", "*=", "/=", "&=", "|=",
      "^=", "%=", "<<=", ">>=", ">>>=")
  symbols("[=><!~?:&|+\\-*\\/\\^%]+")
  escapes("\\\\(?:[abfnrtv\\\\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
  digits("\\d+(_+\\d+)*")
  octaldigits("[0-7]+(_+[0-7]+)*")
  binarydigits("[0-1]+(_+[0-1]+)*")
  hexdigits("[[0-9a-fA-F]+(_+[0-9a-fA-F]+)*")
  tokenizer {
    root {
      "[a-z_${'$'}][\\w${'$'}]*".action {
        cases {
          "@keywords" and {
            token = "keyword.${'$'}0"
          }
          "@default" and "identifier"
        }
      }
      "[A-Z][\\w\\${'$'}]*".action {
        cases {
          "@keywords" and {
            token = "keyword.${'$'}0"
          }
          "@default" and "type.identifier"
        }
      }
      include("@whitespace")
      "[{}()\\[\\]]".token("@brackets")
      "[<>](?!@symbols)".token("@brackets")
      "@symbols".action {
        cases {
          "@operators" and "delimiter"
          "@default" and ""
        }
      }
      "@\\s*[a-zA-Z_\\${'$'}][\\w\\${'$'}]*".token("annotation")
      "(@digits)[eE]([\\-+]?(@digits))?[fFdD]?".token("number.float")
      "(@digits)\\.(@digits)([eE][\\-+]?(@digits))?[fFdD]?".token("number.float")
      "(@digits)[fFdD]".token("number.float")
      "(@digits)[lL]?".token("number")
      "[;,.]".token("delimiter")
      "\"([^\"\\\\]|\\\\.)*${'$'}".token("string.invalid")
      "'([^'\\\\]|\\\\.)*${'$'}".token("string.invalid")
      "\"".action("string").state("@string.\"")
      "'".action("string").state("@string.'")
      "'[^\\\\']'".token("string")
      "(')(@escapes)(')".actionArray {
        token("string")
        token("string.escape")
        token("string")
      }
      "'".token("string.invalid")
    }
    whitespace {
      "[ \\t\\r\\n]+".token("")
      "\\/\\*\\*(?!\\/)".action("comment.doc").state("@apexdoc")
      "\\/\\*".action("comment").state("@comment")
      "\\/\\/.*${'$'}".token("comment")
    }
    comment {
      "[^\\/*]+".token("comment")
      "\\*\\/".action("comment").state("@pop")
      "[\\/*]".token("comment")
    }
    "apexdoc" rules {
      "[^\\/*]+".token("comment.doc")
      "\\*\\/".action("comment.doc").state("@pop")
      "[\\/*]".token("comment.doc")
    }
    string {
      "[^\\\\\"']+".token("string")
      "@escapes".token("string.escape")
      "\\\\.".token("string.escape.invalid")
      "[\"']".action {
        cases {
          "${'$'}#==${'$'}S2" and {
            token = "string"
            next = "@pop"
          }
          "@default" and "string"
        }
      }
    }
  }
}

