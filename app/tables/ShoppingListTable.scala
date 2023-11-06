package tables

import models.ShoppingList
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.Tag

import java.sql.Date

class ShoppingListTable(tag: Tag) extends Table[ShoppingList](tag, "shopping_lists") {

  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)

  def userId = column[Long]("user_id")
  
  def purchaseDate = column[Option[Date]]("purchase_date")

  override def * = (id, userId, purchaseDate) <> (ShoppingList.tupled, ShoppingList.unapply)

  def user = foreignKey("user_fk", userId, TableQuery[UserTable])(_.id.get)

}
