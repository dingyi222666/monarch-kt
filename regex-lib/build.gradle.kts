plugins {
    kotlin("jvm") version "1.9.0"
}

group = "io.github.dingyi222666.kotlin.regex-lib"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}