import scala.util.{Failure, Success}
import akka.actor.{Actor, ActorRef, Props, TypedActor, TypedProps}
import akka.actor.TypedActor.context
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{Directives, Route}
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.headers.`Content-Type`
import akka.pattern.ask
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout

import scala.language.postfixOps
import java.net.URLDecoder
import scala.concurrent.ExecutionContext
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

import scala.concurrent.duration.DurationInt


class Router(calculator: ActorRef)(implicit system: ActorSystem[_],  ex:ExecutionContext) extends Directives {
  implicit val timeout = Timeout(10 seconds)

  def route: Route = concat(
    path("calculator"){
      parameters('expression.as[String]) {
        (expression) =>
          calculator ! SetRequest(expression)
          return onComplete(calculator ? GetRequest("")) {
            case Success(value) =>
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, value.toString))

            case Failure(error) =>
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "failure"))
          }
      }
    }
  )
}