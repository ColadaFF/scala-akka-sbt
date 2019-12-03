package co.com.akka.basic.repositories

import co.com.akka.basic.DbConfig
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

class UsersRepository(db: H2Profile.backend.Database, users: TableQuery[DbConfig.UsersTable]) {

  def findAll(): Future[Seq[DbConfig.User]] = {
    db.run(users.result)
  }



}
