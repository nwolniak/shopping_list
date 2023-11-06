package controllers

import models.{AuthenticatedRequest, User}
import play.api.mvc.*
import services.AuthService

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedAction @Inject()(authService: AuthService, val parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilder[AuthenticatedRequest, AnyContent] {

  override protected val executionContext: ExecutionContext = ec

  override def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] = {
    request.headers.get("Authorization") match
      case Some(authHeader) if authHeader.startsWith("Basic ") =>
        authService.authenticate(authHeader).flatMap {
          case Some(user) => block(AuthenticatedRequest(user, request))
          case None => Future.successful(Results.Unauthorized("Authorization header missing or invalid"))
        }
      case _ =>
        Future.successful(Results.Unauthorized("Authorization header missing or invalid"))
  }
}


