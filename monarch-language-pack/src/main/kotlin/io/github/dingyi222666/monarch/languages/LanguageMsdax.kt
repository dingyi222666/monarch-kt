package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val MsdaxLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".msdax"
    ignoreCase = true
    defaultToken = ""
    brackets {
      bracket("[","]","delimiter.square")
      bracket("{","}","delimiter.brackets")
      bracket("(",")","delimiter.parenthesis")
    }
    keywords("VAR", "RETURN", "NOT", "EVALUATE", "DATATABLE", "ORDER", "BY", "START", "AT",
        "DEFINE", "MEASURE", "ASC", "DESC", "IN", "BOOLEAN", "DOUBLE", "INTEGER", "DATETIME",
        "CURRENCY", "STRING")
    "functions" and listOf("CLOSINGBALANCEMONTH", "CLOSINGBALANCEQUARTER", "CLOSINGBALANCEYEAR",
        "DATEADD", "DATESBETWEEN", "DATESINPERIOD", "DATESMTD", "DATESQTD", "DATESYTD",
        "ENDOFMONTH", "ENDOFQUARTER", "ENDOFYEAR", "FIRSTDATE", "FIRSTNONBLANK", "LASTDATE",
        "LASTNONBLANK", "NEXTDAY", "NEXTMONTH", "NEXTQUARTER", "NEXTYEAR", "OPENINGBALANCEMONTH",
        "OPENINGBALANCEQUARTER", "OPENINGBALANCEYEAR", "PARALLELPERIOD", "PREVIOUSDAY",
        "PREVIOUSMONTH", "PREVIOUSQUARTER", "PREVIOUSYEAR", "SAMEPERIODLASTYEAR", "STARTOFMONTH",
        "STARTOFQUARTER", "STARTOFYEAR", "TOTALMTD", "TOTALQTD", "TOTALYTD", "ADDCOLUMNS",
        "ADDMISSINGITEMS", "ALL", "ALLEXCEPT", "ALLNOBLANKROW", "ALLSELECTED", "CALCULATE",
        "CALCULATETABLE", "CALENDAR", "CALENDARAUTO", "CROSSFILTER", "CROSSJOIN", "CURRENTGROUP",
        "DATATABLE", "DETAILROWS", "DISTINCT", "EARLIER", "EARLIEST", "EXCEPT", "FILTER", "FILTERS",
        "GENERATE", "GENERATEALL", "GROUPBY", "IGNORE", "INTERSECT", "ISONORAFTER", "KEEPFILTERS",
        "LOOKUPVALUE", "NATURALINNERJOIN", "NATURALLEFTOUTERJOIN", "RELATED", "RELATEDTABLE",
        "ROLLUP", "ROLLUPADDISSUBTOTAL", "ROLLUPGROUP", "ROLLUPISSUBTOTAL", "ROW", "SAMPLE",
        "SELECTCOLUMNS", "SUBSTITUTEWITHINDEX", "SUMMARIZE", "SUMMARIZECOLUMNS", "TOPN", "TREATAS",
        "UNION", "USERELATIONSHIP", "VALUES", "SUM", "SUMX", "PATH", "PATHCONTAINS", "PATHITEM",
        "PATHITEMREVERSE", "PATHLENGTH", "AVERAGE", "AVERAGEA", "AVERAGEX", "COUNT", "COUNTA",
        "COUNTAX", "COUNTBLANK", "COUNTROWS", "COUNTX", "DISTINCTCOUNT", "DIVIDE", "GEOMEAN",
        "GEOMEANX", "MAX", "MAXA", "MAXX", "MEDIAN", "MEDIANX", "MIN", "MINA", "MINX",
        "PERCENTILE.EXC", "PERCENTILE.INC", "PERCENTILEX.EXC", "PERCENTILEX.INC", "PRODUCT",
        "PRODUCTX", "RANK.EQ", "RANKX", "STDEV.P", "STDEV.S", "STDEVX.P", "STDEVX.S", "VAR.P",
        "VAR.S", "VARX.P", "VARX.S", "XIRR", "XNPV", "DATE", "DATEDIFF", "DATEVALUE", "DAY",
        "EDATE", "EOMONTH", "HOUR", "MINUTE", "MONTH", "NOW", "SECOND", "TIME", "TIMEVALUE",
        "TODAY", "WEEKDAY", "WEEKNUM", "YEAR", "YEARFRAC", "CONTAINS", "CONTAINSROW", "CUSTOMDATA",
        "ERROR", "HASONEFILTER", "HASONEVALUE", "ISBLANK", "ISCROSSFILTERED", "ISEMPTY", "ISERROR",
        "ISEVEN", "ISFILTERED", "ISLOGICAL", "ISNONTEXT", "ISNUMBER", "ISODD", "ISSUBTOTAL",
        "ISTEXT", "USERNAME", "USERPRINCIPALNAME", "AND", "FALSE", "IF", "IFERROR", "NOT", "OR",
        "SWITCH", "TRUE", "ABS", "ACOS", "ACOSH", "ACOT", "ACOTH", "ASIN", "ASINH", "ATAN", "ATANH",
        "BETA.DIST", "BETA.INV", "CEILING", "CHISQ.DIST", "CHISQ.DIST.RT", "CHISQ.INV",
        "CHISQ.INV.RT", "COMBIN", "COMBINA", "CONFIDENCE.NORM", "CONFIDENCE.T", "COS", "COSH",
        "COT", "COTH", "CURRENCY", "DEGREES", "EVEN", "EXP", "EXPON.DIST", "FACT", "FLOOR", "GCD",
        "INT", "ISO.CEILING", "LCM", "LN", "LOG", "LOG10", "MOD", "MROUND", "ODD", "PERMUT", "PI",
        "POISSON.DIST", "POWER", "QUOTIENT", "RADIANS", "RAND", "RANDBETWEEN", "ROUND", "ROUNDDOWN",
        "ROUNDUP", "SIGN", "SIN", "SINH", "SQRT", "SQRTPI", "TAN", "TANH", "TRUNC", "BLANK",
        "CONCATENATE", "CONCATENATEX", "EXACT", "FIND", "FIXED", "FORMAT", "LEFT", "LEN", "LOWER",
        "MID", "REPLACE", "REPT", "RIGHT", "SEARCH", "SUBSTITUTE", "TRIM", "UNICHAR", "UNICODE",
        "UPPER", "VALUE")
    tokenizer {
      root {
        include("@comments")
        include("@whitespace")
        include("@numbers")
        include("@strings")
        include("@complexIdentifiers")
        "[;,.]".token("delimiter")
        "[({})]".token("@brackets")
        "[a-z_][a-zA-Z0-9_]*".action {
          cases {
            "@keywords" and "keyword"
            "@functions" and "keyword"
            "@default" and "identifier"
          }
        }
        "[<>=!%&+\\-*/|~^]".token("operator")
      }
      whitespace {
        "\\s+".token("white")
      }
      comments {
        "\\/\\/+.*".token("comment")
        "\\/\\*".action {
          token = "comment.quote"
          next = "@comment"
        }
      }
      comment {
        "[^*/]+".token("comment")
        "\\*\\/".action {
          token = "comment.quote"
          next = "@pop"
        }
        ".".token("comment")
      }
      "numbers" rules {
        "0[xX][0-9a-fA-F]*".token("number")
        "[${'$'}][+-]*\\d*(\\.\\d*)?".token("number")
        "((\\d+(\\.\\d*)?)|(\\.\\d+))([eE][\\-+]?\\d+)?".token("number")
      }
      "strings" rules {
        "N\"".action {
          token = "string"
          next = "@string"
        }
        "\"".action {
          token = "string"
          next = "@string"
        }
      }
      string {
        "[^\"]+".token("string")
        "\"\"".token("string")
        "\"".action {
          token = "string"
          next = "@pop"
        }
      }
      "complexIdentifiers" rules {
        "\\[".action {
          token = "identifier.quote"
          next = "@bracketedIdentifier"
        }
        "'".action {
          token = "identifier.quote"
          next = "@quotedIdentifier"
        }
      }
      "bracketedIdentifier" rules {
        "[^\\]]+".token("identifier")
        "]]".token("identifier")
        "]".action {
          token = "identifier.quote"
          next = "@pop"
        }
      }
      "quotedIdentifier" rules {
        "[^']+".token("identifier")
        "''".token("identifier")
        "'".action {
          token = "identifier.quote"
          next = "@pop"
        }
      }
    }
  }
}

