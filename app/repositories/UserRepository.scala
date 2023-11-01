package repositories

import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import tables.UserTable

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class UserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api.*

  private lazy val userQuery = TableQuery[UserTable]
  private final val insertReturning = userQuery returning userQuery

  def register(user: User): Future[Try[User]] = {
    val query = for {
      existingUser <- userQuery.filter(_.username === user.username).result.headOption
      user <- existingUser match
        case Some(_) => DBIO.failed(new Exception("User with the same username already exists"))
        case None => insertReturning += user
    } yield user

    db.run(query.asTry)
  }

  def login(user: User): Future[Try[Option[User]]] = {
    val query = userQuery
      .filter(userEntity => userEntity.username === user.username && userEntity.password === user.password)
      .result
      .headOption

    db.run(query.asTry)
  }

}
