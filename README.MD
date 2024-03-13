# monarch-kt

A kotlin port of [monarch](https://microsoft.github.io/monaco-editor/monarch.html).

> The monarch library can allow you to specify an efficient syntax highlighter, using a declarative lexical
specification. The specification is expressive enough to specify sophisticated highlighters
with complex state transitions, dynamic brace matching, auto-completion, other language embeddings, etc. as shown in
the 'advanced' topic sections of this document. On a first read, it is safe to skip any section or paragraph marked as (
Advanced) since many of the advanced features are rarely used in most language definitions.

We not only ported the core code of monarch, but also supported the following additional features:

- Load monarch grammar from JSON or convert monarch grammar to JSON.
- Define Monarch grammar using Kotlin DSL and also be able to convert to Kotlin DSL (this allows us to convert from JSON to Kotlin DSL, avoiding the overhead of parsing JSON)
- Custom regular expression library, supports the regular expression implementation in the Kotlin standard library, [re2j](https://github.com/google/re2j), [joni](https://github.com/jruby/joni)

## Features

- Easier to use syntax than TextMate, thanks to the excellent syntax design of the original library.
- Load syntax from Kotlin DSL or JSON.
- Extensibility. You can implement your own to provide additional theme support, or change the regular expression implementation library.
- Minimal implementation. By default, it does not integrate JSON loading support, making it easy and fast to integrate into your project.

## TODO
- [ ] Highlighting support for some popular languages (requires additional package)

## Compatibility

Due to the fact that the Java standard library implementation of Regex is not fully compatible with PERL, you may encounter difficulties in loading highLight syntax files that are written and run in non-JVM environments using JSON or Kotlin DSL in the standard library’s Regex.

Therefore, we have abstracted a Regex library to ensure that regular expressions are loaded as much as possible.

Below is a concise summary of the 74 highlight syntax files in the [`monarch-json-loader`](./monarch-json-loader) project’s unit test results.

| Regex library                               | Test Results             | Failed languages                                                  | 
|---------------------------------------------|--------------------------|-------------------------------------------------------------------|
| Kotlin standard library (running on JDK 17) | 64 successful, 10 failed | liquid, cypher, less, html, scss, twig, wgsl, css, php, handlebar |
| re2j                                        | 64 successful, 10 failed | liquid, cypher, less, html, scss, twig, wgsl, css, php, handlebar |
| join (oniguruma)                            | 74 successful, 0 failed  | none                                                              |

You can [view](./monarch-json-loader/src/test) the source code of the test files and their highLight syntaxes for more information.

If you are writing your own highLight syntaxes, I suggest you use regular expressions that are compatible with Java’s regular expression standards when writing your syntaxes and then directly use the standard library’s Regex. This way, the highLight loading speed can be faster.

If you are using other people’s highLight syntaxes, I suggest you try the standard library’s built-in regular expressions first. If the loading fails and you do not have the ability to make secondary modifications, you can then use other regular expression libraries.

`re2j` may perform better than the standard library’s regular expressions in some aspects, but I have not verified this. 

join has excellent compatibility and I recommend most people use this regular expression library.

## Usage

**_work in progress_**

## Special Thank

  [monaco-editor](https://github.com/microsoft/monaco-editor): this project is completely based on its development.
