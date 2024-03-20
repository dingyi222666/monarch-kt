# Regex-Lib

This regular expression library encapsulates a set of regex APIs, enabling developers to work with different regex libraries beyond just the standard one provided by the JVM.
At present, we support:

- java standard library
- re2j
- join (which refers to Oniguruma)

## Usage

```kotlin
val regex = GlobalRegex.compile("[a|b|c]", options)
println(regex.search("a"))
```