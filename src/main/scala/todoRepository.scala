import scala.concurrent.Future

trait todoRepository {
  def all(): Future[Seq[Todo]]

  def done(): Future[Seq[Todo]]

  def pending(): Future[Seq[Todo]]

  def create(createTodo: CreateTodo):Future[Todo]
}