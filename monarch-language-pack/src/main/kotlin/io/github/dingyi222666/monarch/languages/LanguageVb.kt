package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val VbLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".vb"
    ignoreCase = true
    defaultToken = ""
    brackets {
      bracket("{","}","delimiter.bracket")
      bracket("[","]","delimiter.array")
      bracket("(",")","delimiter.parenthesis")
      bracket("<",">","delimiter.angle")
      bracket("addhandler","end addhandler","keyword.tag-addhandler")
      bracket("class","end class","keyword.tag-class")
      bracket("enum","end enum","keyword.tag-enum")
      bracket("event","end event","keyword.tag-event")
      bracket("function","end function","keyword.tag-function")
      bracket("get","end get","keyword.tag-get")
      bracket("if","end if","keyword.tag-if")
      bracket("interface","end interface","keyword.tag-interface")
      bracket("module","end module","keyword.tag-module")
      bracket("namespace","end namespace","keyword.tag-namespace")
      bracket("operator","end operator","keyword.tag-operator")
      bracket("property","end property","keyword.tag-property")
      bracket("raiseevent","end raiseevent","keyword.tag-raiseevent")
      bracket("removehandler","end removehandler","keyword.tag-removehandler")
      bracket("select","end select","keyword.tag-select")
      bracket("set","end set","keyword.tag-set")
      bracket("structure","end structure","keyword.tag-structure")
      bracket("sub","end sub","keyword.tag-sub")
      bracket("synclock","end synclock","keyword.tag-synclock")
      bracket("try","end try","keyword.tag-try")
      bracket("while","end while","keyword.tag-while")
      bracket("with","end with","keyword.tag-with")
      bracket("using","end using","keyword.tag-using")
      bracket("do","loop","keyword.tag-do")
      bracket("for","next","keyword.tag-for")
    }
    keywords("AddHandler", "AddressOf", "Alias", "And", "AndAlso", "As", "Async", "Boolean",
        "ByRef", "Byte", "ByVal", "Call", "Case", "Catch", "CBool", "CByte", "CChar", "CDate",
        "CDbl", "CDec", "Char", "CInt", "Class", "CLng", "CObj", "Const", "Continue", "CSByte",
        "CShort", "CSng", "CStr", "CType", "CUInt", "CULng", "CUShort", "Date", "Decimal",
        "Declare", "Default", "Delegate", "Dim", "DirectCast", "Do", "Double", "Each", "Else",
        "ElseIf", "End", "EndIf", "Enum", "Erase", "Error", "Event", "Exit", "False", "Finally",
        "For", "Friend", "Function", "Get", "GetType", "GetXMLNamespace", "Global", "GoSub", "GoTo",
        "Handles", "If", "Implements", "Imports", "In", "Inherits", "Integer", "Interface", "Is",
        "IsNot", "Let", "Lib", "Like", "Long", "Loop", "Me", "Mod", "Module", "MustInherit",
        "MustOverride", "MyBase", "MyClass", "NameOf", "Namespace", "Narrowing", "New", "Next",
        "Not", "Nothing", "NotInheritable", "NotOverridable", "Object", "Of", "On", "Operator",
        "Option", "Optional", "Or", "OrElse", "Out", "Overloads", "Overridable", "Overrides",
        "ParamArray", "Partial", "Private", "Property", "Protected", "Public", "RaiseEvent",
        "ReadOnly", "ReDim", "RemoveHandler", "Resume", "Return", "SByte", "Select", "Set",
        "Shadows", "Shared", "Short", "Single", "Static", "Step", "Stop", "String", "Structure",
        "Sub", "SyncLock", "Then", "Throw", "To", "True", "Try", "TryCast", "TypeOf", "UInteger",
        "ULong", "UShort", "Using", "Variant", "Wend", "When", "While", "Widening", "With",
        "WithEvents", "WriteOnly", "Xor")
    "tagwords" and listOf("If", "Sub", "Select", "Try", "Class", "Enum", "Function", "Get",
        "Interface", "Module", "Namespace", "Operator", "Set", "Structure", "Using", "While",
        "With", "Do", "Loop", "For", "Next", "Property", "Continue", "AddHandler", "RemoveHandler",
        "Event", "RaiseEvent", "SyncLock")
    symbols("[=><!~?;\\.,:&|+\\-*\\/\\^%]+")
    "integersuffix" and "U?[DI%L&S@]?"
    "floatsuffix" and "[R#F!]?"
    tokenizer {
      root {
        include("@whitespace")
        "next(?!\\w)".action {
          token = "keyword.tag-for"
        }
        "loop(?!\\w)".action {
          token = "keyword.tag-do"
        }
        "end\\s+(?!for|do)(addhandler|class|enum|event|function|get|if|interface|module|namespace|operator|property|raiseevent|removehandler|select|set|structure|sub|synclock|try|while|with|using)".action {
          token = "keyword.tag-${'$'}1"
        }
        "[a-zA-Z_]\\w*".action {
          cases {
            "@tagwords" and {
              token = "keyword.tag-${'$'}0"
            }
            "@keywords" and {
              token = "keyword.${'$'}0"
            }
            "@default" and "identifier"
          }
        }
        "^\\s*#\\w+".token("keyword")
        "\\d*\\d+e([\\-+]?\\d+)?(@floatsuffix)".token("number.float")
        "\\d*\\.\\d+(e[\\-+]?\\d+)?(@floatsuffix)".token("number.float")
        "&H[0-9a-f]+(@integersuffix)".token("number.hex")
        "&0[0-7]+(@integersuffix)".token("number.octal")
        "\\d+(@integersuffix)".token("number")
        "#.*#".token("number")
        "[{}()\\[\\]]".token("@brackets")
        "@symbols".token("delimiter")
        "[\"\\u201c\\u201d]".action {
          token = "string.quote"
          next = "@string"
        }
      }
      whitespace {
        "[ \\t\\r\\n]+".token("")
        "(\\'|REM(?!\\w)).*${'$'}".token("comment")
      }
      string {
        "[^\"\\u201c\\u201d]+".token("string")
        "[\"\\u201c\\u201d]{2}".token("string.escape")
        "[\"\\u201c\\u201d]C?".action {
          token = "string.quote"
          next = "@pop"
        }
      }
    }
  }
}

