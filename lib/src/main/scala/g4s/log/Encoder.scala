package g4s.log

import g4s.log.LogLine._

trait Encoder[-A, B] {
  val NewLine: B
  val Separator: B
  val Empty: B

  def encode(a: A): B
  def encodeLine(a: A): B
}

object Encoder {
  def stringEncoder[A](fn: A => String): StringEncoder[A] = (a: A) => fn(a)

  def apply[A, B](implicit l: Encoder[A, B]): Encoder[A, B] = l

}



trait StringEncoder[A] extends Encoder[A,String] {
  val NewLine = "\n"
  val Separator = "\t"
  val Empty: String = StringEncoder.Empty
  def encode(a: A): String
  def encodeLine(a: A): String  = encode(a) + NewLine
}

object StringEncoder {
  val Empty: String = " "

  implicit val seAssertion: StringEncoder[Assertion] = Encoder.stringEncoder(a => s"ASSERTION\t${a.assertion}")
  implicit val seRun: StringEncoder[Run] = Encoder.stringEncoder(r => s"RUN\t${r.name.getName}\t${r.id}\t${r.start}\t${r.description.fold(StringEncoder.Empty)(identity)}\t${r.logVersion}")
  implicit val seScenarioStart: StringEncoder[ScenarioStart] = Encoder.stringEncoder(r => s"USER\t${r.name}\tSTART\t${r.start}")
  implicit val seScenarioEnd: StringEncoder[ScenarioEnd] = Encoder.stringEncoder(r => s"USER\t${r.name}\tEND\t${r.end}")
  implicit val seResponse: StringEncoder[Response] =
    Encoder.stringEncoder(r => s"REQUEST\t${r.groups.mkString(",")}\t${r.name}\t${r.start}\t${r.end}\t${r.status.value}\t${r.message.fold(StringEncoder.Empty)(identity)}")


}