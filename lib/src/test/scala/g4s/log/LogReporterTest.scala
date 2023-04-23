package g4s.log

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import fs2.io.file.{Files, Path}
import g4s.log.LogLine._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.nio.file.Paths

class LogReporterTest extends AnyFreeSpec with Matchers {

  "can write log file" in {
    val buildPath = Path.fromNioPath(Paths.get(sys.props.getOrElse("user.dir", ???)))

    val files = Files[IO]
    val logFile = for {
      tmp <- files.createTempFile(Some(buildPath.resolve("build")), "perf", "log", None)
      w <- new LogReporter(tmp).writer
      _ <- w.write(Assertion.AllOK)
      _ <- w.write(Run(classOf[MyLog], 123L))
      _ <- w.write(ScenarioStart("BasicSimulation2", 233))
      _ <- w.write(ScenarioStart("BasicSimulation2", 234))
      _ <- w.write(Response.ok("request_1", 111, 112))
      _ <- w.write(Response.ok("request_1", 211, 212))
      _ <- w.write(Response.ok("request_1", 311, 312))
      _ <- w.write(ScenarioEnd("BasicSimulation2", 333))
      _ <- w.write(ScenarioEnd("BasicSimulation2", 334))
      _ <- w.close()
      perflog <- files.readUtf8(tmp).compile.string
    } yield perflog

    val expectedFileContents: String =
      s"""ASSERTION	AAECAAIFAAAAAAAAAFlA
         |RUN	g4s.log.MyLog	mylog	123\t \t3.9.3
         |USER	BasicSimulation2	START	233
         |USER	BasicSimulation2	START	234
         |REQUEST		request_1	111	112	OK\t\u0020
         |REQUEST		request_1	211	212	OK\t\u0020
         |REQUEST		request_1	311	312	OK\t\u0020
         |USER	BasicSimulation2	END	333
         |USER	BasicSimulation2	END	334
         |""".stripMargin

    logFile.unsafeRunSync() shouldBe expectedFileContents
  }
}
class MyLog











