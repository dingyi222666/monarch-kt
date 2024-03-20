import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
    id("com.vanniktech.maven.publish.base")
}

group = "io.github.dingyi222666.monarch"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(project(":regex-lib-oniguruma"))
    testImplementation(project(":monarch"))
    testImplementation("com.squareup:kotlinpoet:1.16.0")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.1")
    testImplementation("com.squareup.moshi:moshi:1.15.1")
    compileOnly(project(":monarch"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.freeCompilerArgs += "-Xcontext-receivers"
}