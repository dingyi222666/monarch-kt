package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val SqlLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".sql"
    ignoreCase = true
    defaultToken = ""
    brackets {
      bracket("[","]","delimiter.square")
      bracket("(",")","delimiter.parenthesis")
    }
    keywords("ABORT", "ABSOLUTE", "ACTION", "ADA", "ADD", "AFTER", "ALL", "ALLOCATE", "ALTER",
        "ALWAYS", "ANALYZE", "AND", "ANY", "ARE", "AS", "ASC", "ASSERTION", "AT", "ATTACH",
        "AUTHORIZATION", "AUTOINCREMENT", "AVG", "BACKUP", "BEFORE", "BEGIN", "BETWEEN", "BIT",
        "BIT_LENGTH", "BOTH", "BREAK", "BROWSE", "BULK", "BY", "CASCADE", "CASCADED", "CASE",
        "CAST", "CATALOG", "CHAR", "CHARACTER", "CHARACTER_LENGTH", "CHAR_LENGTH", "CHECK",
        "CHECKPOINT", "CLOSE", "CLUSTERED", "COALESCE", "COLLATE", "COLLATION", "COLUMN", "COMMIT",
        "COMPUTE", "CONFLICT", "CONNECT", "CONNECTION", "CONSTRAINT", "CONSTRAINTS", "CONTAINS",
        "CONTAINSTABLE", "CONTINUE", "CONVERT", "CORRESPONDING", "COUNT", "CREATE", "CROSS",
        "CURRENT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR",
        "DATABASE", "DATE", "DAY", "DBCC", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DEFAULT",
        "DEFERRABLE", "DEFERRED", "DELETE", "DENY", "DESC", "DESCRIBE", "DESCRIPTOR", "DETACH",
        "DIAGNOSTICS", "DISCONNECT", "DISK", "DISTINCT", "DISTRIBUTED", "DO", "DOMAIN", "DOUBLE",
        "DROP", "DUMP", "EACH", "ELSE", "END", "END-EXEC", "ERRLVL", "ESCAPE", "EXCEPT",
        "EXCEPTION", "EXCLUDE", "EXCLUSIVE", "EXEC", "EXECUTE", "EXISTS", "EXIT", "EXPLAIN",
        "EXTERNAL", "EXTRACT", "FAIL", "FALSE", "FETCH", "FILE", "FILLFACTOR", "FILTER", "FIRST",
        "FLOAT", "FOLLOWING", "FOR", "FOREIGN", "FORTRAN", "FOUND", "FREETEXT", "FREETEXTTABLE",
        "FROM", "FULL", "FUNCTION", "GENERATED", "GET", "GLOB", "GLOBAL", "GO", "GOTO", "GRANT",
        "GROUP", "GROUPS", "HAVING", "HOLDLOCK", "HOUR", "IDENTITY", "IDENTITYCOL",
        "IDENTITY_INSERT", "IF", "IGNORE", "IMMEDIATE", "IN", "INCLUDE", "INDEX", "INDEXED",
        "INDICATOR", "INITIALLY", "INNER", "INPUT", "INSENSITIVE", "INSERT", "INSTEAD", "INT",
        "INTEGER", "INTERSECT", "INTERVAL", "INTO", "IS", "ISNULL", "ISOLATION", "JOIN", "KEY",
        "KILL", "LANGUAGE", "LAST", "LEADING", "LEFT", "LEVEL", "LIKE", "LIMIT", "LINENO", "LOAD",
        "LOCAL", "LOWER", "MATCH", "MATERIALIZED", "MAX", "MERGE", "MIN", "MINUTE", "MODULE",
        "MONTH", "NAMES", "NATIONAL", "NATURAL", "NCHAR", "NEXT", "NO", "NOCHECK", "NONCLUSTERED",
        "NONE", "NOT", "NOTHING", "NOTNULL", "NULL", "NULLIF", "NULLS", "NUMERIC", "OCTET_LENGTH",
        "OF", "OFF", "OFFSET", "OFFSETS", "ON", "ONLY", "OPEN", "OPENDATASOURCE", "OPENQUERY",
        "OPENROWSET", "OPENXML", "OPTION", "OR", "ORDER", "OTHERS", "OUTER", "OUTPUT", "OVER",
        "OVERLAPS", "PAD", "PARTIAL", "PARTITION", "PASCAL", "PERCENT", "PIVOT", "PLAN", "POSITION",
        "PRAGMA", "PRECEDING", "PRECISION", "PREPARE", "PRESERVE", "PRIMARY", "PRINT", "PRIOR",
        "PRIVILEGES", "PROC", "PROCEDURE", "PUBLIC", "QUERY", "RAISE", "RAISERROR", "RANGE", "READ",
        "READTEXT", "REAL", "RECONFIGURE", "RECURSIVE", "REFERENCES", "REGEXP", "REINDEX",
        "RELATIVE", "RELEASE", "RENAME", "REPLACE", "REPLICATION", "RESTORE", "RESTRICT", "RETURN",
        "RETURNING", "REVERT", "REVOKE", "RIGHT", "ROLLBACK", "ROW", "ROWCOUNT", "ROWGUIDCOL",
        "ROWS", "RULE", "SAVE", "SAVEPOINT", "SCHEMA", "SCROLL", "SECOND", "SECTION",
        "SECURITYAUDIT", "SELECT", "SEMANTICKEYPHRASETABLE", "SEMANTICSIMILARITYDETAILSTABLE",
        "SEMANTICSIMILARITYTABLE", "SESSION", "SESSION_USER", "SET", "SETUSER", "SHUTDOWN", "SIZE",
        "SMALLINT", "SOME", "SPACE", "SQL", "SQLCA", "SQLCODE", "SQLERROR", "SQLSTATE",
        "SQLWARNING", "STATISTICS", "SUBSTRING", "SUM", "SYSTEM_USER", "TABLE", "TABLESAMPLE",
        "TEMP", "TEMPORARY", "TEXTSIZE", "THEN", "TIES", "TIME", "TIMESTAMP", "TIMEZONE_HOUR",
        "TIMEZONE_MINUTE", "TO", "TOP", "TRAILING", "TRAN", "TRANSACTION", "TRANSLATE",
        "TRANSLATION", "TRIGGER", "TRIM", "TRUE", "TRUNCATE", "TRY_CONVERT", "TSEQUAL", "UNBOUNDED",
        "UNION", "UNIQUE", "UNKNOWN", "UNPIVOT", "UPDATE", "UPDATETEXT", "UPPER", "USAGE", "USE",
        "USER", "USING", "VACUUM", "VALUE", "VALUES", "VARCHAR", "VARYING", "VIEW", "VIRTUAL",
        "WAITFOR", "WHEN", "WHENEVER", "WHERE", "WHILE", "WINDOW", "WITH", "WITHIN GROUP",
        "WITHOUT", "WORK", "WRITE", "WRITETEXT", "YEAR", "ZONE")
    operators("ALL", "AND", "ANY", "BETWEEN", "EXISTS", "IN", "LIKE", "NOT", "OR", "SOME", "EXCEPT",
        "INTERSECT", "UNION", "APPLY", "CROSS", "FULL", "INNER", "JOIN", "LEFT", "OUTER", "RIGHT",
        "CONTAINS", "FREETEXT", "IS", "NULL", "PIVOT", "UNPIVOT", "MATCHED")
    "builtinFunctions" and listOf("AVG", "CHECKSUM_AGG", "COUNT", "COUNT_BIG", "GROUPING",
        "GROUPING_ID", "MAX", "MIN", "SUM", "STDEV", "STDEVP", "VAR", "VARP", "CUME_DIST",
        "FIRST_VALUE", "LAG", "LAST_VALUE", "LEAD", "PERCENTILE_CONT", "PERCENTILE_DISC",
        "PERCENT_RANK", "COLLATE", "COLLATIONPROPERTY", "TERTIARY_WEIGHTS",
        "FEDERATION_FILTERING_VALUE", "CAST", "CONVERT", "PARSE", "TRY_CAST", "TRY_CONVERT",
        "TRY_PARSE", "ASYMKEY_ID", "ASYMKEYPROPERTY", "CERTPROPERTY", "CERT_ID", "CRYPT_GEN_RANDOM",
        "DECRYPTBYASYMKEY", "DECRYPTBYCERT", "DECRYPTBYKEY", "DECRYPTBYKEYAUTOASYMKEY",
        "DECRYPTBYKEYAUTOCERT", "DECRYPTBYPASSPHRASE", "ENCRYPTBYASYMKEY", "ENCRYPTBYCERT",
        "ENCRYPTBYKEY", "ENCRYPTBYPASSPHRASE", "HASHBYTES", "IS_OBJECTSIGNED", "KEY_GUID", "KEY_ID",
        "KEY_NAME", "SIGNBYASYMKEY", "SIGNBYCERT", "SYMKEYPROPERTY", "VERIFYSIGNEDBYCERT",
        "VERIFYSIGNEDBYASYMKEY", "CURSOR_STATUS", "DATALENGTH", "IDENT_CURRENT", "IDENT_INCR",
        "IDENT_SEED", "IDENTITY", "SQL_VARIANT_PROPERTY", "CURRENT_TIMESTAMP", "DATEADD",
        "DATEDIFF", "DATEFROMPARTS", "DATENAME", "DATEPART", "DATETIME2FROMPARTS",
        "DATETIMEFROMPARTS", "DATETIMEOFFSETFROMPARTS", "DAY", "EOMONTH", "GETDATE", "GETUTCDATE",
        "ISDATE", "MONTH", "SMALLDATETIMEFROMPARTS", "SWITCHOFFSET", "SYSDATETIME",
        "SYSDATETIMEOFFSET", "SYSUTCDATETIME", "TIMEFROMPARTS", "TODATETIMEOFFSET", "YEAR",
        "CHOOSE", "COALESCE", "IIF", "NULLIF", "ABS", "ACOS", "ASIN", "ATAN", "ATN2", "CEILING",
        "COS", "COT", "DEGREES", "EXP", "FLOOR", "LOG", "LOG10", "PI", "POWER", "RADIANS", "RAND",
        "ROUND", "SIGN", "SIN", "SQRT", "SQUARE", "TAN", "APP_NAME", "APPLOCK_MODE", "APPLOCK_TEST",
        "ASSEMBLYPROPERTY", "COL_LENGTH", "COL_NAME", "COLUMNPROPERTY", "DATABASE_PRINCIPAL_ID",
        "DATABASEPROPERTYEX", "DB_ID", "DB_NAME", "FILE_ID", "FILE_IDEX", "FILE_NAME",
        "FILEGROUP_ID", "FILEGROUP_NAME", "FILEGROUPPROPERTY", "FILEPROPERTY",
        "FULLTEXTCATALOGPROPERTY", "FULLTEXTSERVICEPROPERTY", "INDEX_COL", "INDEXKEY_PROPERTY",
        "INDEXPROPERTY", "OBJECT_DEFINITION", "OBJECT_ID", "OBJECT_NAME", "OBJECT_SCHEMA_NAME",
        "OBJECTPROPERTY", "OBJECTPROPERTYEX", "ORIGINAL_DB_NAME", "PARSENAME", "SCHEMA_ID",
        "SCHEMA_NAME", "SCOPE_IDENTITY", "SERVERPROPERTY", "STATS_DATE", "TYPE_ID", "TYPE_NAME",
        "TYPEPROPERTY", "DENSE_RANK", "NTILE", "RANK", "ROW_NUMBER", "PUBLISHINGSERVERNAME",
        "OPENDATASOURCE", "OPENQUERY", "OPENROWSET", "OPENXML", "CERTENCODED", "CERTPRIVATEKEY",
        "CURRENT_USER", "HAS_DBACCESS", "HAS_PERMS_BY_NAME", "IS_MEMBER", "IS_ROLEMEMBER",
        "IS_SRVROLEMEMBER", "LOGINPROPERTY", "ORIGINAL_LOGIN", "PERMISSIONS", "PWDENCRYPT",
        "PWDCOMPARE", "SESSION_USER", "SESSIONPROPERTY", "SUSER_ID", "SUSER_NAME", "SUSER_SID",
        "SUSER_SNAME", "SYSTEM_USER", "USER", "USER_ID", "USER_NAME", "ASCII", "CHAR", "CHARINDEX",
        "CONCAT", "DIFFERENCE", "FORMAT", "LEFT", "LEN", "LOWER", "LTRIM", "NCHAR", "PATINDEX",
        "QUOTENAME", "REPLACE", "REPLICATE", "REVERSE", "RIGHT", "RTRIM", "SOUNDEX", "SPACE", "STR",
        "STUFF", "SUBSTRING", "UNICODE", "UPPER", "BINARY_CHECKSUM", "CHECKSUM",
        "CONNECTIONPROPERTY", "CONTEXT_INFO", "CURRENT_REQUEST_ID", "ERROR_LINE", "ERROR_NUMBER",
        "ERROR_MESSAGE", "ERROR_PROCEDURE", "ERROR_SEVERITY", "ERROR_STATE", "FORMATMESSAGE",
        "GETANSINULL", "GET_FILESTREAM_TRANSACTION_CONTEXT", "HOST_ID", "HOST_NAME", "ISNULL",
        "ISNUMERIC", "MIN_ACTIVE_ROWVERSION", "NEWID", "NEWSEQUENTIALID", "ROWCOUNT_BIG",
        "XACT_STATE", "TEXTPTR", "TEXTVALID", "COLUMNS_UPDATED", "EVENTDATA", "TRIGGER_NESTLEVEL",
        "UPDATE", "CHANGETABLE", "CHANGE_TRACKING_CONTEXT", "CHANGE_TRACKING_CURRENT_VERSION",
        "CHANGE_TRACKING_IS_COLUMN_IN_MASK", "CHANGE_TRACKING_MIN_VALID_VERSION", "CONTAINSTABLE",
        "FREETEXTTABLE", "SEMANTICKEYPHRASETABLE", "SEMANTICSIMILARITYDETAILSTABLE",
        "SEMANTICSIMILARITYTABLE", "FILETABLEROOTPATH", "GETFILENAMESPACEPATH", "GETPATHLOCATOR",
        "PATHNAME", "GET_TRANSMISSION_STATUS")
    "builtinVariables" and listOf("@@DATEFIRST", "@@DBTS", "@@LANGID", "@@LANGUAGE",
        "@@LOCK_TIMEOUT", "@@MAX_CONNECTIONS", "@@MAX_PRECISION", "@@NESTLEVEL", "@@OPTIONS",
        "@@REMSERVER", "@@SERVERNAME", "@@SERVICENAME", "@@SPID", "@@TEXTSIZE", "@@VERSION",
        "@@CURSOR_ROWS", "@@FETCH_STATUS", "@@DATEFIRST", "@@PROCID", "@@ERROR", "@@IDENTITY",
        "@@ROWCOUNT", "@@TRANCOUNT", "@@CONNECTIONS", "@@CPU_BUSY", "@@IDLE", "@@IO_BUSY",
        "@@PACKET_ERRORS", "@@PACK_RECEIVED", "@@PACK_SENT", "@@TIMETICKS", "@@TOTAL_ERRORS",
        "@@TOTAL_READ", "@@TOTAL_WRITE")
    "pseudoColumns" and listOf("\$ACTION", "\$IDENTITY", "\$ROWGUID", "\$PARTITION")
    tokenizer {
      root {
        include("@comments")
        include("@whitespace")
        include("@pseudoColumns")
        include("@numbers")
        include("@strings")
        include("@complexIdentifiers")
        include("@scopes")
        "[;,.]".token("delimiter")
        "[()]".token("@brackets")
        "[\\w@#${'$'}]+".action {
          cases {
            "@operators" and "operator"
            "@builtinVariables" and "predefined"
            "@builtinFunctions" and "predefined"
            "@keywords" and "keyword"
            "@default" and "identifier"
          }
        }
        "[<>=!%&+\\-*/|~^]".token("operator")
      }
      whitespace {
        "\\s+".token("white")
      }
      comments {
        "--+.*".token("comment")
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
      "pseudoColumns" rules {
        "[${'$'}][A-Za-z_][\\w@#${'$'}]*".action {
          cases {
            "@pseudoColumns" and "predefined"
            "@default" and "identifier"
          }
        }
      }
      "numbers" rules {
        "0[xX][0-9a-fA-F]*".token("number")
        "[${'$'}][+-]*\\d*(\\.\\d*)?".token("number")
        "((\\d+(\\.\\d*)?)|(\\.\\d+))([eE][\\-+]?\\d+)?".token("number")
      }
      "strings" rules {
        "N'".action {
          token = "string"
          next = "@string"
        }
        "'".action {
          token = "string"
          next = "@string"
        }
      }
      string {
        "[^']+".token("string")
        "''".token("string")
        "'".action {
          token = "string"
          next = "@pop"
        }
      }
      "complexIdentifiers" rules {
        "\\[".action {
          token = "identifier.quote"
          next = "@bracketedIdentifier"
        }
        "\"".action {
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
        "[^\"]+".token("identifier")
        "\"\"".token("identifier")
        "\"".action {
          token = "identifier.quote"
          next = "@pop"
        }
      }
      "scopes" rules {
        "BEGIN\\s+(DISTRIBUTED\\s+)?TRAN(SACTION)?\\b".token("keyword")
        "BEGIN\\s+TRY\\b".action {
          token = "keyword.try"
        }
        "END\\s+TRY\\b".action {
          token = "keyword.try"
        }
        "BEGIN\\s+CATCH\\b".action {
          token = "keyword.catch"
        }
        "END\\s+CATCH\\b".action {
          token = "keyword.catch"
        }
        "(BEGIN|CASE)\\b".action {
          token = "keyword.block"
        }
        "END\\b".action {
          token = "keyword.block"
        }
        "WHEN\\b".action {
          token = "keyword.choice"
        }
        "THEN\\b".action {
          token = "keyword.choice"
        }
      }
    }
  }
}
