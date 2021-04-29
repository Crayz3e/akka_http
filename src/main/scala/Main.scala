import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext

object Main extends App {
  implicit val log: Logger = LoggerFactory.getLogger(getClass)

  val rootBehavior: Behavior[Nothing] = Behaviors.setup[Nothing] { context =>
    implicit val ec = context.executionContext
    implicit val sys = context.system

    val router = new Router()

    Server.Start(router.route)
    Behaviors.empty
  }

  val system = ActorSystem[Nothing](rootBehavior, "calculator")
}
