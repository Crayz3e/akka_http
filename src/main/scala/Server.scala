import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object Server {
  def Start(routes: Route)(implicit system: ActorSystem[_], ex: ExecutionContext) {
    val binding = Http().newServerAt("localhost", 8080).bind(routes: Route)
    binding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", "127.0.0.1", "8080")
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }
}
