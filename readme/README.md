# log-writer

This scala library supports creating performance logfile that.

## Usage

```groovy
//sbt
libraryDependencies += "dev.g4s" %% "log-writer" % "@VERSION@"
```
or
```groovy
//gradle
dependencies {
    implementation("dev.g4s:log-writer_2.13:@VERSION@")
}
```

```scala mdoc:compile-only
//scala
import cats.effect._
import fs2._
import fs2.io.file._
import g4s.log._
import g4s.log.LogLine._
import g4s.log.LogReporter._
import java.nio.file.Paths            

object MyLog extends IOApp.Simple {

  val buildPath = sys.props.get("user.dir").map(d => Path.fromNioPath(Paths.get(d)).resolve("build"))

  val files = Files[IO]

  override def run: IO[Unit] = {
    for {
      tmp <- files.createTempFile(buildPath, "perf", "log", None)
      w <- LogReporter(tmp)
      _ <- w.write(Assertion.AllOK)
      _ <- w.write(Run(classOf[MyLog.type], 123L))
      _ <- w.write(ScenarioStart("BasicPerf2", 233)) //first user
      _ <- w.write(ScenarioStart("BasicPerf2", 234)) //second user
      _ <- w.write(Response.ok("request_1", 111, 112))
      _ <- w.write(Response.ok("request_1", 211, 212))
      _ <- w.write(Response.ok("request_1", 311, 312))
      _ <- w.write(ScenarioEnd("BasicPerf2", 333))
      _ <- w.write(ScenarioEnd("BasicPerf2", 334))
      _ <- w.close()
    } yield ()
  }
}
```

_Also there is a bit of syntactic sugar that can be used_:
```scala
  implicit val log : LogReporter = LogReporter(tmp)
  Response.ok("request_1", 111, 112).write
```
