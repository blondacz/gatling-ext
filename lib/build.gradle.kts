
plugins {
    scala
    `java-library`
    id("io.gatling.gradle") version "3.9.3.1"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.scala-lang:scala-library:2.13.10")
    implementation("co.fs2:fs2-core_2.13:3.6.1")
    implementation("co.fs2:fs2-io_2.13:3.6.1")


    testImplementation("org.scalatest:scalatest_2.13:3.2.14")
    testImplementation("org.scalatestplus:junit-4-13_2.13:3.2.2.0")
}
