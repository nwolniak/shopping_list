package tables

import models.ShoppingList
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.Tag

class ShoppingListTable(tag: Tag) extends Table[ShoppingList](tag, "shopping_lists") {

  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)

  override def * = id <> (ShoppingList.tupled, ShoppingList.unapply)

}
