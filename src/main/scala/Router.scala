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
  implicit val timeout = Timeout(1 seconds)

  def route: Route = concat(
    path("calculator"){
      parameters('expression.as[String]) {
        (expression) =>
          var message = ""
          calculator ! SetRequest(expression)
          val result = calculator ? GetRequest("")
          result onComplete {
            case Success(value: GetResponse) =>
              message = value.res.toString
              system.log.info(message)
            case Failure(error) =>
              message = "failure"
              system.log.info("failure")
          }

          Thread.sleep(1000)
          complete(message)
      }
    }
  )
}