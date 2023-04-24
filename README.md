# log-writer

This scala library supports creating performance logfile that.

## Usage

```kotlin
//gradle
dependencies {
    implementation("dev.g4s:log-writer_2.13:0.1.0")
}
```

```scala
//scala
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import fs2._
import fs2.io.file._
import g4s.log.LogLine._
import g4s.log.StringFileWriter.StringFileWriterOps

val files = Files[IO]

val logFile = for {
  tmp <- files.createTempFile(Some(buildPath.resolve("build")), "perf", "log", None)
  w  <- LogReporter(tmp)
  _ <- Assertion.AllOK.write(w)
  _ <- Run(classOf[MyLog], 123L).write(w)
  _ <- ScenarioStart("BasicPerf", 233).write(w)
  _ <- ScenarioStart("BasicPerf", 234).write(w)
  _ <- Stream.emits(Seq(1,2,3)).through(report(w)).compile.drain
  _ <- ScenarioEnd("BasicPerf", 333).write(w)
  _ <- ScenarioEnd("BasicPerf", 334).write(w)
  _ <- w.close()
  
} yield ()

logFile.unsafeRunSync()
```