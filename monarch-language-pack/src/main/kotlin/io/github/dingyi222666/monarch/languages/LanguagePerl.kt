package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val PerlLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".perl"
    defaultToken = ""
    brackets {
      bracket("{","}","delimiter.bracket")
      bracket("(",")","delimiter.parenthesis")
      bracket("[","]","delimiter.square")
    }
    keywords("__DATA__", "else", "lock", "__END__", "elsif", "lt", "__FILE__", "eq", "__LINE__",
        "exp", "ne", "sub", "__PACKAGE__", "for", "no", "and", "foreach", "or", "unless", "cmp",
        "ge", "package", "until", "continue", "gt", "while", "CORE", "if", "xor", "do", "le",
        "__DIE__", "__WARN__")
    "builtinFunctions" and listOf("-A", "END", "length", "setpgrp", "-B", "endgrent", "link",
        "setpriority", "-b", "endhostent", "listen", "setprotoent", "-C", "endnetent", "local",
        "setpwent", "-c", "endprotoent", "localtime", "setservent", "-d", "endpwent", "log",
        "setsockopt", "-e", "endservent", "lstat", "shift", "-f", "eof", "map", "shmctl", "-g",
        "eval", "mkdir", "shmget", "-k", "exec", "msgctl", "shmread", "-l", "exists", "msgget",
        "shmwrite", "-M", "exit", "msgrcv", "shutdown", "-O", "fcntl", "msgsnd", "sin", "-o",
        "fileno", "my", "sleep", "-p", "flock", "next", "socket", "-r", "fork", "not", "socketpair",
        "-R", "format", "oct", "sort", "-S", "formline", "open", "splice", "-s", "getc", "opendir",
        "split", "-T", "getgrent", "ord", "sprintf", "-t", "getgrgid", "our", "sqrt", "-u",
        "getgrnam", "pack", "srand", "-w", "gethostbyaddr", "pipe", "stat", "-W", "gethostbyname",
        "pop", "state", "-X", "gethostent", "pos", "study", "-x", "getlogin", "print", "substr",
        "-z", "getnetbyaddr", "printf", "symlink", "abs", "getnetbyname", "prototype", "syscall",
        "accept", "getnetent", "push", "sysopen", "alarm", "getpeername", "quotemeta", "sysread",
        "atan2", "getpgrp", "rand", "sysseek", "AUTOLOAD", "getppid", "read", "system", "BEGIN",
        "getpriority", "readdir", "syswrite", "bind", "getprotobyname", "readline", "tell",
        "binmode", "getprotobynumber", "readlink", "telldir", "bless", "getprotoent", "readpipe",
        "tie", "break", "getpwent", "recv", "tied", "caller", "getpwnam", "redo", "time", "chdir",
        "getpwuid", "ref", "times", "CHECK", "getservbyname", "rename", "truncate", "chmod",
        "getservbyport", "require", "uc", "chomp", "getservent", "reset", "ucfirst", "chop",
        "getsockname", "return", "umask", "chown", "getsockopt", "reverse", "undef", "chr", "glob",
        "rewinddir", "UNITCHECK", "chroot", "gmtime", "rindex", "unlink", "close", "goto", "rmdir",
        "unpack", "closedir", "grep", "say", "unshift", "connect", "hex", "scalar", "untie", "cos",
        "index", "seek", "use", "crypt", "INIT", "seekdir", "utime", "dbmclose", "int", "select",
        "values", "dbmopen", "ioctl", "semctl", "vec", "defined", "join", "semget", "wait",
        "delete", "keys", "semop", "waitpid", "DESTROY", "kill", "send", "wantarray", "die", "last",
        "setgrent", "warn", "dump", "lc", "sethostent", "write", "each", "lcfirst", "setnetent")
    "builtinFileHandlers" and listOf("ARGV", "STDERR", "STDOUT", "ARGVOUT", "STDIN", "ENV")
    "builtinVariables" and listOf("\$!", "\$^RE_TRIE_MAXBUF", "\$LAST_REGEXP_CODE_RESULT", "\$"",
        "\$^S", "\$LIST_SEPARATOR", "\$#", "\$^T", "\$MATCH", "\$\$", "\$^TAINT",
        "\$MULTILINE_MATCHING", "\$%", "\$^UNICODE", "\$NR", "\$&", "\$^UTF8LOCALE", "\$OFMT",
        "\$'", "\$^V", "\$OFS", "\$(", "\$^W", "\$ORS", "\$)", "\$^WARNING_BITS", "\$OS_ERROR",
        "\$*", "\$^WIDE_SYSTEM_CALLS", "\$OSNAME", "\$+", "\$^X", "\$OUTPUT_AUTO_FLUSH", "\$,",
        "\$_", "\$OUTPUT_FIELD_SEPARATOR", "\$-", "\$`", "\$OUTPUT_RECORD_SEPARATOR", "\$.", "\$a",
        "\$PERL_VERSION", "\$/", "\$ACCUMULATOR", "\$PERLDB", "\$0", "\$ARG", "\$PID", "\$:",
        "\$ARGV", "\$POSTMATCH", "\$;", "\$b", "\$PREMATCH", "\$<", "\$BASETIME", "\$PROCESS_ID",
        "\$=", "\$CHILD_ERROR", "\$PROGRAM_NAME", "\$>", "\$COMPILING", "\$REAL_GROUP_ID", "\$?",
        "\$DEBUGGING", "\$REAL_USER_ID", "\$@", "\$EFFECTIVE_GROUP_ID", "\$RS", "\$[",
        "\$EFFECTIVE_USER_ID", "\$SUBSCRIPT_SEPARATOR", "\$\\", "\$EGID", "\$SUBSEP", "\$]",
        "\$ERRNO", "\$SYSTEM_FD_MAX", "\$^", "\$EUID", "\$UID", "\$^A", "\$EVAL_ERROR", "\$WARNING",
        "\$^C", "\$EXCEPTIONS_BEING_CAUGHT", "\$|", "\$^CHILD_ERROR_NATIVE", "\$EXECUTABLE_NAME",
        "\$~", "\$^D", "\$EXTENDED_OS_ERROR", "%!", "\$^E", "\$FORMAT_FORMFEED", "%^H",
        "\$^ENCODING", "\$FORMAT_LINE_BREAK_CHARACTERS", "%ENV", "\$^F", "\$FORMAT_LINES_LEFT",
        "%INC", "\$^H", "\$FORMAT_LINES_PER_PAGE", "%OVERLOAD", "\$^I", "\$FORMAT_NAME", "%SIG",
        "\$^L", "\$FORMAT_PAGE_NUMBER", "@+", "\$^M", "\$FORMAT_TOP_NAME", "@-", "\$^N", "\$GID",
        "@_", "\$^O", "\$INPLACE_EDIT", "@ARGV", "\$^OPEN", "\$INPUT_LINE_NUMBER", "@INC", "\$^P",
        "\$INPUT_RECORD_SEPARATOR", "@LAST_MATCH_START", "\$^R", "\$LAST_MATCH_END",
        "\$^RE_DEBUG_FLAGS", "\$LAST_PAREN_MATCH")
    symbols("[:+\\-\\^*${'$'}&%@=<>!?|\\/~\\.]")
    "quoteLikeOps" and listOf("qr", "m", "s", "q", "qq", "qx", "qw", "tr", "y")
    escapes("\\\\(?:[abfnrtv\\\\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
    tokenizer {
      root {
        include("@whitespace")
        "[a-zA-Z\\-_][\\w\\-_]*".action {
          cases {
            "@keywords" and "keyword"
            "@builtinFunctions" and "type.identifier"
            "@builtinFileHandlers" and "variable.predefined"
            "@quoteLikeOps" and {
              token = "@rematch"
              next = "quotedConstructs"
            }
            "@default" and ""
          }
        }
        "[\\${'$'}@%][*@#?\\+\\-\\${'$'}!\\w\\\\\\^><~:;\\.]+".action {
          cases {
            "@builtinVariables" and "variable.predefined"
            "@default" and "variable"
          }
        }
        include("@strings")
        include("@dblStrings")
        include("@perldoc")
        include("@heredoc")
        "[{}\\[\\]()]".token("@brackets")
        "[\\/](?:(?:\\[(?:\\\\]|[^\\]])+\\])|(?:\\\\\\/|[^\\]\\/]))*[\\/]\\w*\\s*(?=[).,;]|${'$'})".token("regexp")
        "@symbols".token("operators")
        include("@numbers")
        "[,;]".token("delimiter")
      }
      whitespace {
        "\\s+".token("white")
        "(^#!.*${'$'})".token("metatag")
        "(^#.*${'$'})".token("comment")
      }
      "numbers" rules {
        "\\d*\\.\\d+([eE][\\-+]?\\d+)?".token("number.float")
        "0[xX][0-9a-fA-F_]*[0-9a-fA-F]".token("number.hex")
        "\\d+".token("number")
      }
      "strings" rules {
        "'".action("string").state("@stringBody")
      }
      "stringBody" rules {
        "'".action("string").state("@popall")
        "\\\\'".token("string.escape")
        ".".token("string")
      }
      "dblStrings" rules {
        "\"".action("string").state("@dblStringBody")
      }
      "dblStringBody" rules {
        "\"".action("string").state("@popall")
        "@escapes".token("string.escape")
        "\\\\.".token("string.escape.invalid")
        include("@variables")
        ".".token("string")
      }
      "quotedConstructs" rules {
        "(q|qw|tr|y)\\s*\\(".action {
          token = "string.delim"
          switchTo = "@qstring.(.)"
        }
        "(q|qw|tr|y)\\s*\\[".action {
          token = "string.delim"
          switchTo = "@qstring.[.]"
        }
        "(q|qw|tr|y)\\s*\\{".action {
          token = "string.delim"
          switchTo = "@qstring.{.}"
        }
        "(q|qw|tr|y)\\s*<".action {
          token = "string.delim"
          switchTo = "@qstring.<.>"
        }
        "(q|qw|tr|y)#".action {
          token = "string.delim"
          switchTo = "@qstring.#.#"
        }
        "(q|qw|tr|y)\\s*([^A-Za-z0-9#\\s])".action {
          token = "string.delim"
          switchTo = "@qstring.${'$'}2.${'$'}2"
        }
        "(q|qw|tr|y)\\s+(\\w)".action {
          token = "string.delim"
          switchTo = "@qstring.${'$'}2.${'$'}2"
        }
        "(qr|m|s)\\s*\\(".action {
          token = "regexp.delim"
          switchTo = "@qregexp.(.)"
        }
        "(qr|m|s)\\s*\\[".action {
          token = "regexp.delim"
          switchTo = "@qregexp.[.]"
        }
        "(qr|m|s)\\s*\\{".action {
          token = "regexp.delim"
          switchTo = "@qregexp.{.}"
        }
        "(qr|m|s)\\s*<".action {
          token = "regexp.delim"
          switchTo = "@qregexp.<.>"
        }
        "(qr|m|s)#".action {
          token = "regexp.delim"
          switchTo = "@qregexp.#.#"
        }
        "(qr|m|s)\\s*([^A-Za-z0-9_#\\s])".action {
          token = "regexp.delim"
          switchTo = "@qregexp.${'$'}2.${'$'}2"
        }
        "(qr|m|s)\\s+(\\w)".action {
          token = "regexp.delim"
          switchTo = "@qregexp.${'$'}2.${'$'}2"
        }
        "(qq|qx)\\s*\\(".action {
          token = "string.delim"
          switchTo = "@qqstring.(.)"
        }
        "(qq|qx)\\s*\\[".action {
          token = "string.delim"
          switchTo = "@qqstring.[.]"
        }
        "(qq|qx)\\s*\\{".action {
          token = "string.delim"
          switchTo = "@qqstring.{.}"
        }
        "(qq|qx)\\s*<".action {
          token = "string.delim"
          switchTo = "@qqstring.<.>"
        }
        "(qq|qx)#".action {
          token = "string.delim"
          switchTo = "@qqstring.#.#"
        }
        "(qq|qx)\\s*([^A-Za-z0-9#\\s])".action {
          token = "string.delim"
          switchTo = "@qqstring.${'$'}2.${'$'}2"
        }
        "(qq|qx)\\s+(\\w)".action {
          token = "string.delim"
          switchTo = "@qqstring.${'$'}2.${'$'}2"
        }
      }
      "qstring" rules {
        "\\\\.".token("string.escape")
        ".".action {
          cases {
            "${'$'}#==${'$'}S3" and {
              token = "string.delim"
              next = "@pop"
            }
            "${'$'}#==${'$'}S2" and {
              token = "string.delim"
              next = "@push"
            }
            "@default" and "string"
          }
        }
      }
      "qregexp" rules {
        include("@variables")
        "\\\\.".token("regexp.escape")
        ".".action {
          cases {
            "${'$'}#==${'$'}S3" and {
              token = "regexp.delim"
              next = "@regexpModifiers"
            }
            "${'$'}#==${'$'}S2" and {
              token = "regexp.delim"
              next = "@push"
            }
            "@default" and "regexp"
          }
        }
      }
      "regexpModifiers" rules {
        "[msixpodualngcer]+".action {
          token = "regexp.modifier"
          next = "@popall"
        }
      }
      "qqstring" rules {
        include("@variables")
        include("@qstring")
      }
      "heredoc" rules {
        "<<\\s*['\"`]?([\\w\\-]+)['\"`]?".action {
          token = "string.heredoc.delimiter"
          next = "@heredocBody.${'$'}1"
        }
      }
      "heredocBody" rules {
        "^([\\w\\-]+)${'$'}".action {
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
      "perldoc" rules {
        "^=\\w".action("comment.doc").state("@perldocBody")
      }
      "perldocBody" rules {
        "^=cut\\b".action("type.identifier").state("@popall")
        ".".token("comment.doc")
      }
      "variables" rules {
        "\\${'$'}\\w+".token("variable")
        "@\\w+".token("variable")
        "%\\w+".token("variable")
      }
    }
  }
}

