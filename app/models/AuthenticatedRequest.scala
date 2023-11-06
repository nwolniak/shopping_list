package models

import play.api.mvc.{Request, WrappedRequest}

class AuthenticatedRequest[A](val user: User, request: Request[A]) extends WrappedRequest[A](request)