package co.com.akka.basic.repositories

import co.com.akka.basic.DbConfig
import co.com.akka.basic.domain.{CreateUserRequest, User}
import slick.dbio.Effect
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._
import slick.sql.SqlAction

import scala.concurrent.{ExecutionContext, Future}

class UsersRepository(db: H2Profile.backend.Database, users: TableQuery[DbConfig.UsersTable])(implicit ec: ExecutionContext) {

  def findAll(): Future[Seq[User]] = {
    db.run(users.result)
  }

  def upsertUser(user: User): DBIO[Int] = {
    for {
      existing <- users.filter(_.id === user.id).result.headOption
      row = existing.getOrElse(user)
      result <- users.insertOrUpdate(row)
    } yield result
  }


}
