import akka.actor._
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

case object Start
case class SetRequest(expr: String)
case class GetRequest(res: String)
case class GetResponse(res: Double)

class MainCalculator extends Actor with ActorLogging {
  implicit val timeout = Timeout(5 seconds)
  var message = ""
  var result = 0.0
  val calculatorActor = context.actorOf(Props(new Calculator), "calculator")

  def receive = {
    case r: SetRequest =>
      message = r.expr
      log.info(s"Request: $message")
      calculatorActor ! SetRequest(message)
      val respF = calculatorActor ? GetRequest("Result")
      respF pipeTo self

    case r: GetResponse =>
      // log.warning(s"Response: ${r.res}")
      sender ! r.res
      log.info(s"Response: ${r.res}")
  }
}
