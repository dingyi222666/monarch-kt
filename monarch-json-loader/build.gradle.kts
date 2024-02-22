plugins {
    kotlin("jvm") version "1.9.0"
    id("com.google.devtools.ksp").version("1.9.0-1.0.13")
}

group = "io.github.dingyi222666.kotlin"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":monarch"))
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.1")
    implementation("com.squareup.moshi:moshi:1.15.1")


    testImplementation(kotlin("test"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}