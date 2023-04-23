package g4s.log

sealed trait LogLine

object LogLine {

  case class Assertion(assertion: String) extends LogLine

  object Assertion {
    val AllOK: Assertion = Assertion("AAECAAIFAAAAAAAAAFlA")
  }

  case class Run(name: Class[_], id: String, start: Long, description: Option[String], logVersion: String) extends LogLine

  object Run {
    def apply(name: Class[_], start: Long) = new Run(name, name.getSimpleName.toLowerCase, start = start, None, "3.9.3")
  }

  case class ScenarioStart(name: String, start: Long) extends LogLine //user

  case class ScenarioEnd(name: String, end: Long) extends LogLine //user

  case class Response(groups: Seq[String], name: String, start: Long, end: Long, status: Status, message: Option[String] = None) extends LogLine

  object Response {
    def ok(name: String, start: Long, end: Long) = new Response(Nil, name, start, end, status = Status.OK, None)

    def ko(name: String, start: Long, end: Long, msg: String) = new Response(Nil, name, start, end, status = Status.KO, Some(msg))
  }

  sealed trait Status {
    def value: String
  }

  object Status {
    case object OK extends Status {
      override def value: String = "OK"
    }

    case object KO extends Status {
      override def value: String = "KO"
    }
  }
}