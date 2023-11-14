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
        log.error(s"Failed to register user", exception)
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
  
  def deleteUser(userId: Long): Future[Option[Int]] = {
    userRepository.deleteUser(userId).map {
      case Failure(exception) =>
        log.error(s"Failed to delete user", exception)
        None
      case Success(value) => Some(value)
    }
  }

  def authenticate(authHeader: String): Future[Option[User]] = {
    val base64Credentials = authHeader.stripPrefix("Basic ")
    val credentials = new String(java.util.Base64.getDecoder.decode(base64Credentials))
    val Array(username, password) = credentials.split(":", 2)
    userRepository.getUser(username).map {
      case Failure(exception) =>
        log.error(s"Failed to authenticate user", exception)
        None
      case Success(user) => user
    }
  }

}
