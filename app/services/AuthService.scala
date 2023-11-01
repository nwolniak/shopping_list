package services

import com.google.inject.Inject
import models.User
import play.api.Logger
import repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class AuthService @Inject()
(val userRepository: UserRepository)
(implicit ec: ExecutionContext) {

  private val log = Logger(getClass)

  def register(user: User): Future[Option[User]] = {
    userRepository.register(user).map {
      case Failure(exception) =>
        log.error(s"Failed to login user", exception)
        None
      case Success(user) => Option(user)
    }
  }

  def login(user: User): Future[Option[User]] = {
    userRepository.login(user).map {
      case Failure(exception) =>
        log.error(s"Failed to login user", exception)
        None
      case Success(user) => user
    }
  }

}
