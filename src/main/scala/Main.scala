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

    val mockTodos:Seq[Todo] = Seq()

    val todos = new SavedTodoRepository(mockTodos)
    val calc = OldActorSystem("calc")
    val calculator = calc.actorOf(Props(new Calculator), "calculator")
    val router = new Router(calculator, todos)

    Server.Start(router.route)
    Behaviors.empty
  }

  val system = akka.actor.typed.ActorSystem[Nothing](rootBehavior, "calculator")
}
