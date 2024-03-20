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
    implementation(project(":monarch"))

    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.1")
    implementation("com.squareup.moshi:moshi:1.15.1")

    testImplementation(kotlin("test"))
    testImplementation(project(":regex-lib-re2j"))
    testImplementation(project(":regex-lib-oniguruma"))
    testImplementation("com.squareup:kotlinpoet:1.16.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}