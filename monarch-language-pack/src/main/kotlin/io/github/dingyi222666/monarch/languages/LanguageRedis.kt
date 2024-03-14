package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val RedisLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".redis"
    ignoreCase = true
    defaultToken = ""
    brackets {
      bracket("[","]","delimiter.square")
      bracket("(",")","delimiter.parenthesis")
    }
    keywords("APPEND", "AUTH", "BGREWRITEAOF", "BGSAVE", "BITCOUNT", "BITFIELD", "BITOP", "BITPOS",
        "BLPOP", "BRPOP", "BRPOPLPUSH", "CLIENT", "KILL", "LIST", "GETNAME", "PAUSE", "REPLY",
        "SETNAME", "CLUSTER", "ADDSLOTS", "COUNT-FAILURE-REPORTS", "COUNTKEYSINSLOT", "DELSLOTS",
        "FAILOVER", "FORGET", "GETKEYSINSLOT", "INFO", "KEYSLOT", "MEET", "NODES", "REPLICATE",
        "RESET", "SAVECONFIG", "SET-CONFIG-EPOCH", "SETSLOT", "SLAVES", "SLOTS", "COMMAND", "COUNT",
        "GETKEYS", "CONFIG", "GET", "REWRITE", "SET", "RESETSTAT", "DBSIZE", "DEBUG", "OBJECT",
        "SEGFAULT", "DECR", "DECRBY", "DEL", "DISCARD", "DUMP", "ECHO", "EVAL", "EVALSHA", "EXEC",
        "EXISTS", "EXPIRE", "EXPIREAT", "FLUSHALL", "FLUSHDB", "GEOADD", "GEOHASH", "GEOPOS",
        "GEODIST", "GEORADIUS", "GEORADIUSBYMEMBER", "GETBIT", "GETRANGE", "GETSET", "HDEL",
        "HEXISTS", "HGET", "HGETALL", "HINCRBY", "HINCRBYFLOAT", "HKEYS", "HLEN", "HMGET", "HMSET",
        "HSET", "HSETNX", "HSTRLEN", "HVALS", "INCR", "INCRBY", "INCRBYFLOAT", "KEYS", "LASTSAVE",
        "LINDEX", "LINSERT", "LLEN", "LPOP", "LPUSH", "LPUSHX", "LRANGE", "LREM", "LSET", "LTRIM",
        "MGET", "MIGRATE", "MONITOR", "MOVE", "MSET", "MSETNX", "MULTI", "PERSIST", "PEXPIRE",
        "PEXPIREAT", "PFADD", "PFCOUNT", "PFMERGE", "PING", "PSETEX", "PSUBSCRIBE", "PUBSUB",
        "PTTL", "PUBLISH", "PUNSUBSCRIBE", "QUIT", "RANDOMKEY", "READONLY", "READWRITE", "RENAME",
        "RENAMENX", "RESTORE", "ROLE", "RPOP", "RPOPLPUSH", "RPUSH", "RPUSHX", "SADD", "SAVE",
        "SCARD", "SCRIPT", "FLUSH", "LOAD", "SDIFF", "SDIFFSTORE", "SELECT", "SETBIT", "SETEX",
        "SETNX", "SETRANGE", "SHUTDOWN", "SINTER", "SINTERSTORE", "SISMEMBER", "SLAVEOF", "SLOWLOG",
        "SMEMBERS", "SMOVE", "SORT", "SPOP", "SRANDMEMBER", "SREM", "STRLEN", "SUBSCRIBE", "SUNION",
        "SUNIONSTORE", "SWAPDB", "SYNC", "TIME", "TOUCH", "TTL", "TYPE", "UNSUBSCRIBE", "UNLINK",
        "UNWATCH", "WAIT", "WATCH", "ZADD", "ZCARD", "ZCOUNT", "ZINCRBY", "ZINTERSTORE",
        "ZLEXCOUNT", "ZRANGE", "ZRANGEBYLEX", "ZREVRANGEBYLEX", "ZRANGEBYSCORE", "ZRANK", "ZREM",
        "ZREMRANGEBYLEX", "ZREMRANGEBYRANK", "ZREMRANGEBYSCORE", "ZREVRANGE", "ZREVRANGEBYSCORE",
        "ZREVRANK", "ZSCORE", "ZUNIONSTORE", "SCAN", "SSCAN", "HSCAN", "ZSCAN")
    operators()
    "builtinFunctions" and listOf()
    "builtinVariables" and listOf()
    "pseudoColumns" and listOf()
    tokenizer {
      root {
        include("@whitespace")
        include("@pseudoColumns")
        include("@numbers")
        include("@strings")
        include("@scopes")
        "[;,.]".token("delimiter")
        "[()]".token("@brackets")
        "[\\w@#${'$'}]+".action {
          cases {
            "@keywords" and "keyword"
            "@operators" and "operator"
            "@builtinVariables" and "predefined"
            "@builtinFunctions" and "predefined"
            "@default" and "identifier"
          }
        }
        "[<>=!%&+\\-*/|~^]".token("operator")
      }
      whitespace {
        "\\s+".token("white")
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
        "'".action {
          token = "string"
          next = "@string"
        }
        "\"".action {
          token = "string.double"
          next = "@stringDouble"
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
      "stringDouble" rules {
        "[^\"]+".token("string.double")
        "\"\"".token("string.double")
        "\"".action {
          token = "string.double"
          next = "@pop"
        }
      }
      "scopes" rules {
      }
    }
  }
}

