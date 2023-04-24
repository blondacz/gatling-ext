package g4s.log

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import fs2.io.file.{Files, Path}
import fs2.{Pipe, Stream}
import g4s.log.LogLine._
import g4s.log.StringFileWriter.StringFileWriterOps
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.nio.file.Paths

class LogReporterTest extends AnyFreeSpec with Matchers {
   def !!! : Nothing = throw new AssertionError("Never expected")

  val expectedFileContents: String =
    s"""ASSERTION	AAECAAIFAAAAAAAAAFlA
       |RUN	g4s.log.MyLog	mylog	123\t \t3.9.3
       |USER	BasicPerf2	START	233
       |USER	BasicPerf2	START	234
       |REQUEST		request_1	111	112	OK\t\u0020
       |REQUEST		request_1	211	212	OK\t\u0020
       |REQUEST		request_1	311	312	OK\t\u0020
       |USER	BasicPerf2	END	333
       |USER	BasicPerf2	END	334
       |""".stripMargin


  private val buildPath = Path.fromNioPath(Paths.get(sys.props.getOrElse("user.dir", !!!)))

  "can write log file" in {

    val files = Files[IO]
    val logFile = for {
      tmp <- files.createTempFile(Some(buildPath.resolve("build")), "perf", "log", None)
      w <- LogReporter(tmp)
      _ <- w.write(Assertion.AllOK)
      _ <- w.write(Run(classOf[MyLog], 123L))
      _ <- w.write(ScenarioStart("BasicPerf2", 233))
      _ <- w.write(ScenarioStart("BasicPerf2", 234))
      _ <- w.write(Response.ok("request_1", 111, 112))
      _ <- w.write(Response.ok("request_1", 211, 212))
      _ <- w.write(Response.ok("request_1", 311, 312))
      _ <- w.write(ScenarioEnd("BasicPerf2", 333))
      _ <- w.write(ScenarioEnd("BasicPerf2", 334))
      _ <- w.close()
      perflog <- files.readUtf8(tmp).compile.string
    } yield perflog



    logFile.unsafeRunSync() shouldBe expectedFileContents
  }

  def report(w: StringFileWriter) : Pipe[IO,Int,Unit] = _.evalMap(i =>
    w.write(Response.ok("request_1", i*100 + 11, i*100 +12))
  )

  "can write log file from stream" in {
    val files = Files[IO]

    val logFile = for {
      tmp <- files.createTempFile(Some(buildPath.resolve("build")), "perf", "log", None)
      w  <- LogReporter(tmp)
      _ <- Assertion.AllOK.write(w)
      _ <- Run(classOf[MyLog], 123L).write(w)
      _ <- ScenarioStart("BasicPerf2", 233).write(w)
      _ <- ScenarioStart("BasicPerf2", 234).write(w)
      _ <- Stream.emits(Seq(1,2,3)).through(report(w)).compile.drain
      _ <- ScenarioEnd("BasicPerf2", 333).write(w)
      _ <- ScenarioEnd("BasicPerf2", 334).write(w)
      _ <- w.close()
      perflog <- files.readUtf8(tmp).compile.string
    } yield perflog

    logFile.unsafeRunSync() shouldBe expectedFileContents

  }
}
class MyLog











