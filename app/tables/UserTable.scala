package tables

import models.User
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.Tag

class UserTable(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)

  def username = column[String]("username")

  def password = column[String]("password")

  def * = (id, username, password) <> (User.tupled, User.unapply)
}
