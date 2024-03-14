package io.github.dingyi222666.monarch.languages

import io.github.dingyi222666.monarch.common.*
 import io.github.dingyi222666.monarch.loader.dsl.*
import io.github.dingyi222666.monarch.types.IMonarchLanguage

public val CspLanguage: IMonarchLanguage by lazy {
  buildMonarchLanguage {
    tokenPostfix = ".csp"
    keywords()
    typeKeywords()
    operators()
    symbols("[=><!~?:&|+\\-*\\/\\^%]+")
    escapes("\\\\(?:[abfnrtv\\\\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})")
    tokenizer {
      root {
        "child-src".token("string.quote")
        "connect-src".token("string.quote")
        "default-src".token("string.quote")
        "font-src".token("string.quote")
        "frame-src".token("string.quote")
        "img-src".token("string.quote")
        "manifest-src".token("string.quote")
        "media-src".token("string.quote")
        "object-src".token("string.quote")
        "script-src".token("string.quote")
        "style-src".token("string.quote")
        "worker-src".token("string.quote")
        "base-uri".token("string.quote")
        "plugin-types".token("string.quote")
        "sandbox".token("string.quote")
        "disown-opener".token("string.quote")
        "form-action".token("string.quote")
        "frame-ancestors".token("string.quote")
        "report-uri".token("string.quote")
        "report-to".token("string.quote")
        "upgrade-insecure-requests".token("string.quote")
        "block-all-mixed-content".token("string.quote")
        "require-sri-for".token("string.quote")
        "reflected-xss".token("string.quote")
        "referrer".token("string.quote")
        "policy-uri".token("string.quote")
        "'self'".token("string.quote")
        "'unsafe-inline'".token("string.quote")
        "'unsafe-eval'".token("string.quote")
        "'strict-dynamic'".token("string.quote")
        "'unsafe-hashed-attributes'".token("string.quote")
      }
    }
  }
}

