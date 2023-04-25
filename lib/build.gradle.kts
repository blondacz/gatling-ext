
plugins {
    scala
    `java-library`
    id("com.github.maiflai.scalatest") version "0.32"
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.scala-lang:scala-library:2.13.10")
    implementation("co.fs2:fs2-core_2.13:3.6.1")
    implementation("co.fs2:fs2-io_2.13:3.6.1")


    testImplementation("org.scalatest:scalatest_2.13:3.2.0")
    testRuntimeOnly("com.vladsch.flexmark:flexmark-all:0.35.10")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
    withJavadocJar()
    withSourcesJar()
}


publishing {
    publications {
        create<MavenPublication>("log-writer") {
            artifactId = "log-writer_2.13"
            from(components["java"])
            pom {
                name.set("dev.g4s:log-writer_2.13")
                packaging = "jar"
                description.set("Generation of the performance report log")
                url.set("https://github.com/blondacz/log-writter")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/blondacz/log-writter/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("blondacz")
                        name.set("Tomas Klubal")
                        email.set("tomas.klubal@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/blondacz/log-writter.git")
                    developerConnection.set("scm:git:https://github.com/blondacz/log-writter.git")
                    url.set("https://github.com/blondacz/log-writter")
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = layout.buildDirectory.dir("repos/releases")
            val snapshotsRepoUrl = layout.buildDirectory.dir("repos/snapshots")
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            name = "test"
        }
        maven {
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            credentials(PasswordCredentials::class)
            name = "sonatype"
        }
    }
}


signing {
    useGpgCmd()
    sign(publishing.publications["log-writer"])
}
