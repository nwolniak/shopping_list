package models

case class User(id: Option[Long] = None, username: String, password: String)

object User {
  def tupled: ((Option[Long], String, String)) => User = {
    case (id, username, password) => User(id, username, password)
  }

  def unapply(user: User): Option[(Option[Long], String, String)] = {
    Some((user.id, user.username, user.password))
  }
}
