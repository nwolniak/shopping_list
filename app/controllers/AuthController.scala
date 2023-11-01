package controllers

import models.User
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import services.AuthService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AuthController @Inject()(val controllerComponents: ControllerComponents,
                               val authService: AuthService)(implicit ec: ExecutionContext) extends BaseController {

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

}
