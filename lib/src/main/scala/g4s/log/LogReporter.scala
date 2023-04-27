package g4s.log

import cats.effect.IO
import cats.effect.std.Queue
import fs2.io.file.{Files, Path}
import fs2.{Pipe, Stream, text}


trait LogReporter {
  def write[A <: LogLine](item: A)(implicit E: StringEncoder[A]): IO[Unit]

  def close(): IO[Unit]
}


/**
 * Simple file writer that know how to write [[LogLine]] types
 */
object LogReporter {

  def apply(path: Path): IO[LogReporter] = newWriteToFile(path)

  private def toFile(path: Path): Pipe[IO, String, Unit] =
    _.through(text.utf8.encode).through(Files[IO].writeAll(path)).debug()

  private def newWriteToFile(path: Path): IO[LogReporter] = {
    Queue.unbounded[IO, Option[String]].flatMap { queue =>
      Stream
        .fromQueueNoneTerminated(queue)
        .through(toFile(path))
        .compile
        .drain
        .start
        .map { fiber =>
          new LogReporter {
            def write[A <: LogLine](value: A)(implicit E: StringEncoder[A]): IO[Unit] = queue.offer(Some(E.encodeLine(value)))

            def close(): IO[Unit] =
              queue.offer(None).flatMap(_ => fiber.joinWithUnit)
          }
        }
    }
  }


  implicit class LogReporterOps[A <: LogLine : StringEncoder](ll: A) {
    def write(implicit w: LogReporter): IO[Unit] = w.write(ll)
  }
}
