import akka.actor.{Actor, Props}
import akka.actor.TypedActor.context
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{Directives, Route}
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.headers.`Content-Type`
import akka.pattern.ask

import scala.concurrent.ExecutionContext
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

class Router(implicit system: ActorSystem[_],  ex:ExecutionContext) extends Actor with Directives {
  def route: Route = concat(
    path("/"){
      val calculator = context.actorOf(Props(new MainCalculator), "MainCalculator")
      concat(
        path(String) { expr =>
          calculator ! SetRequest(expr.toString)
          val result = calculator ?
        }
      )
    }
  )
}