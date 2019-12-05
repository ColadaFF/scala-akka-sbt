package co.com.akka.basic.repositories

import co.com.akka.basic.DbConfig
import co.com.akka.basic.domain.User
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._

import scala.concurrent.{ExecutionContext, Future}

class AccountsRepository(
                          db: H2Profile.backend.Database,
                          users: TableQuery[DbConfig.UsersTable],
                          accounts: TableQuery[DbConfig.AccountTable],
                        )(implicit ec: ExecutionContext) {

  def findAccountAndUser(accountId: Int): Future[(User, (Int, Int, Int))] = {
    val blueprint = for {
      account <-  accounts.filter(_.id === accountId).result.head
      user <- users.filter(_.id === account._2).result.head
    } yield (user, account)

    db.run(blueprint)
  }

}
