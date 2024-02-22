/*
 * monarch-kt - Kotlin port of Monarch library.
 * https://github.com/dingyi222666/monarch-kt
 * Copyright (C) 2024  dingyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
    implementation(project(":regex-lib"))
    implementation("com.google.re2j:re2j:1.6")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}