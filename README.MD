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
- [ ] Highlighting support for some common languages (requires additional package)

## Usage

**_work in progress_**

## Special Thank

  [monaco-editor](https://github.com/microsoft/monaco-editor): this project is completely based on its development.
