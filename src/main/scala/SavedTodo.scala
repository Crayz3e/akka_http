import java.util.UUID

import scala.concurrent.{ExecutionContext, Future}

class InMemoryTodoRepository(initialTodos:Seq[Todo] = Seq.empty)(implicit ec:ExecutionContext) {
  private var todos: Vector[Todo] = initialTodos.toVector

  def all(): Future[Seq[Todo]] = Future.successful(todos)
  def done(): Future[Seq[Todo]] = Future.successful(todos.filter(_.done))
  def pending(): Future[Seq[Todo]] = Future.successful(todos.filterNot(_.done))

  def create(createTodo: CreateTodo): Future[Todo] =
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