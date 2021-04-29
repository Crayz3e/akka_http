import akka.actor.{ActorSystem, Props, TypedActor}
import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.{ActorSystem => OldActorSystem}
import akka.actor.typed.scaladsl.Behaviors
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext

object Main extends App {
  implicit val log: Logger = LoggerFactory.getLogger(getClass)

  val rootBehavior: Behavior[Nothing] = Behaviors.setup[Nothing] { context =>
    implicit val ec = context.executionContext
    implicit val sys = context.system

    val calc = OldActorSystem("calc")
    val calculator = calc.actorOf(Props(new MainCalculator), "calculator")
    val router = new Router(calculator)

    Server.Start(router.route)
    Behaviors.empty
  }

  val system = akka.actor.typed.ActorSystem[Nothing](rootBehavior, "calculator")
}
