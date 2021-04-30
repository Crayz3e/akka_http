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


class Router(calculator: ActorRef, todoRepository: todoRepository)(implicit system: ActorSystem[_],  ex:ExecutionContext)
  extends  Directives
  with todoDirectives
  with ValidatorDirectives {
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
    },

    path("todos") {
      pathEndOrSingleSlash {
        concat(
          get {
            handleWithGeneric(todoRepository.all()) {
              todos => complete(todos)
            }
          },
          post {
            entity(as[CreateTodo]) { createTodo =>
              validateWith(CreateTodoValidator)(createTodo){
                handleWithGeneric(todoRepository.create(createTodo)){
                  todo =>
                    val seq = todoRepository.all()
                    for (i <- seq) {
                      for (j <- i) {
                        if (j.title.equals(createTodo.title)) {
                          Some(ApiError.DuplicateTitle)
                          complete("failure")
                        }
                      }
                    }

                    complete(todo)
                }
              }
            }
          }
        )
      }
    }
  )
}