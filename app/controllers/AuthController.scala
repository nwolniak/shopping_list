package controllers

import models.User
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import services.AuthService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AuthController @Inject()(val controllerComponents: ControllerComponents,
                               val authService: AuthService,
                               val authenticatedAction: AuthenticatedAction)(implicit ec: ExecutionContext) extends BaseController {

  implicit val userFormatter: OFormat[User] = Json.format[User]

  def register: Action[User] = Action.async(parse.json[User]) { request =>
    val user = request.body
    authService.register(user).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => Unauthorized("Invalid credentials")
    }
  }

  def login: Action[User] = Action.async(parse.json[User]) { request =>
    val user = request.body
    authService.login(user).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => Unauthorized("Invalid credentials")
    }
  }

  def deleteUser(userId: Long): Action[AnyContent] = authenticatedAction.async { request =>
    authService.deleteUser(userId).map {
      case Some(0) => NotFound
      case Some(_) => Ok
      case None => NoContent
    }
  }

}
