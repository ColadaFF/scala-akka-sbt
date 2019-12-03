package co.com.akka.basic

import co.com.akka.basic.domain.User
import slick.dbio.Effect
import slick.jdbc
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._
import slick.lifted.ForeignKeyQuery
import slick.sql.FixedSqlAction

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


trait DbConfig {
  val db: H2Profile.backend.Database = Database
    .forConfig("h2mem1")
}

object DbConfig {





  class UsersTable(tag: Tag) extends Table[User](tag, "USERS") {
    def id = column[Int]("USER_ID", O.PrimaryKey)

    def name = column[String]("NAME")

    def lastName = column[String]("LAST_NAME")

    override def * = (id, name, lastName) <> (User.tupled, User.unapply)
  }

  val usersTableQuery: TableQuery[UsersTable] = TableQuery[UsersTable]

  //val list: List[User] = Nil

  //list.filter(_.name != "Pedro").map(_.id)
  //            String  String          Int
  //private val query: Query[Rep[Int], Int, Seq] = usersTableQuery.filterNot(_.name == "Pedro").map(_.id)

  // => SELECT ID WHERE
  //                        Rep[String]


  class AccountTable(tag: Tag) extends Table[(Int, Int, Int)](tag, "ACCOUNTS") {
    def id = column[Int]("ID", O.PrimaryKey)
    def userId = column[Int]("USER_ID")
    def balance = column[Int]("BALANCE")

    override def * = (id, userId, balance)

    def user: ForeignKeyQuery[UsersTable, User] =
      foreignKey("USER_PK", userId, usersTableQuery)(_.id) // (record => record.id)
  }

  val accountsTableQuery: TableQuery[AccountTable] = TableQuery[AccountTable]

  private val result: FixedSqlAction[Option[Int], jdbc.H2Profile.api.NoStream, Effect.Read] = accountsTableQuery.map(_.balance).max.result

  result.statements.foreach(println)


  def setupDb(db: H2Profile.backend.Database)(implicit ec: ExecutionContext) = {
    val setupDbActions = DBIO //databaseInputOutput
      .seq(
        (usersTableQuery.schema ++ accountsTableQuery.schema).create, // unir esquemas
        usersTableQuery += User(1, "Andres", "Sanchez"),
        usersTableQuery += User(2, "Rick", "Gomez"),
        usersTableQuery += User(3, "Cesar", "Perez"),
        accountsTableQuery += (1, 1, 0),
        accountsTableQuery += (2, 2, 0),
        accountsTableQuery += (3, 3, 0)
      ) // crear unos planos de acciones -> SQL

    val setupFuture: Future[Unit] = db.run(setupDbActions)


    setupFuture.onComplete {
      case Failure(exception) =>
        exception.printStackTrace()
        Console.err.println("No se pudo conectar a la base de datos")
      case Success(_) =>
        println("Conectado a base de datos")
        val future: Future[Seq[User]] = db.run(usersTableQuery.result)
        future.onComplete {
          case Success(usersList) => usersList.foreach(user => println(user))
        }
    }
  }


}
