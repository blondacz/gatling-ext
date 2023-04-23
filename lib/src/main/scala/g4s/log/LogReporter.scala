package g4s.log

import cats.effect.IO
import cats.effect.std.Queue
import fs2.io.file.{Files, Path}
import fs2.{Pipe, Stream, text}

object LogReporter {

  def apply(path: Path): IO[StringFileWriter] = newWriteToFile(path)

  private def toFile(path: Path): Pipe[IO, String, Unit] =
    _.through(text.utf8.encode).through(Files[IO].writeAll(path)).debug()

  private def newWriteToFile(path: Path): IO[StringFileWriter] = {
    Queue.unbounded[IO, Option[String]].flatMap { queue =>
      Stream
        .fromQueueNoneTerminated(queue)
        .through(toFile(path))
        .compile
        .drain
        .start
        .map { fiber =>
          new StringFileWriter() {
            def write[A <: LogLine](value: A)(implicit E: StringEncoder[A]): IO[Unit] = queue.offer(Some(E.encodeLine(value)))

            def close(): IO[Unit] =
              queue.offer(None).flatMap(_ => fiber.joinWithUnit)
          }
        }
    }
  }
}
