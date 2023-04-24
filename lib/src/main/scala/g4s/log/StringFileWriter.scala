package g4s.log

import cats.effect.IO

trait StringFileWriter {
  def write[A <: LogLine](item: A)(implicit E: StringEncoder[A]): IO[Unit]

  def close(): IO[Unit]
}

object StringFileWriter {
  implicit class StringFileWriterOps[A <: LogLine : StringEncoder](ll : A) {
    def write(implicit w: StringFileWriter): IO[Unit] = w.write(ll)
  }

}
