
plugins {
    scala
    `java-library`
    id("com.github.maiflai.scalatest") version "0.32"
    `maven-publish`
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
