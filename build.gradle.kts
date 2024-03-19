import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost

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

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    kotlin("jvm") version "1.9.0" apply false
    id("com.google.devtools.ksp").version("1.9.0-1.0.13") apply false
    id("com.vanniktech.maven.publish.base").version("0.28.0") apply false
}

fun MavenPublishBaseExtension.applyRegexLibProjects() {
    pom {
        name.set("RegexLib")
        description.set("An RegexLib with multi implement in JVM")
        inceptionYear.set("2024")
        url.set("https://github.com/dingyi222666/monarch-kt/tree/main/regex-lib")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("dingyi222666")
                name.set("dingyi222666")
                url.set("https://github.com/dingyi222666")
            }
        }
        scm {
            url.set("https://github.com/dingyi222666/monarch-kt")
            connection.set("scm:git:git://github.com/dingyi222666/monarch-kt.git")
            developerConnection.set("scm:git:ssh://git@github.com/dingyi222666/monarch-kt.git")
        }
    }
}

subprojects {
    plugins.withId("com.vanniktech.maven.publish.base") {
        configure<MavenPublishBaseExtension> {

            publishToMavenCentral(SonatypeHost.S01)
            signAllPublications()

        }
    }
}
