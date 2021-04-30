import java.util.UUID

import scala.concurrent.{ExecutionContext, Future}

class SavedTodoRepository(initialTodos:Seq[Todo] = Seq.empty)(implicit ec:ExecutionContext) extends todoRepository {

  private var todos: Vector[Todo] = initialTodos.toVector

  override def all(): Future[Seq[Todo]] = Future.successful(todos)

  override def done(): Future[Seq[Todo]] = Future.successful(todos.filter(_.done))

  override def pending(): Future[Seq[Todo]] = Future.successful(todos.filterNot(_.done))

  override def create(createTodo: CreateTodo): Future[Todo] =
    Future.successful {
      val todo = Todo(
        id = UUID.randomUUID().toString,
        title = createTodo.title,
        description = createTodo.description,
        done = false
      )
      todos = todos :+ todo
      todo
    }

}