import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object Server {
  val host = "localhost"
  val port = Try(System.getenv("PORT")).map(_.toInt).getOrElse(9000)
  def Start(routes: Route)(implicit system: ActorSystem[_], ex: ExecutionContext) {
    val futurebinding = Http().newServerAt(host, port).bind(routes: Route)
    futurebinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }
}
