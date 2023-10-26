package tables

import models.Item
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.{ProvenShape, Tag}

import scala.language.postfixOps

class ItemTable(tag: Tag) extends Table[Item](tag, "items") {

  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def shoppingListId = column[Long]("shopping_list_id")

  def * = (id, name, shoppingListId) <> (Item.tupled, Item.unapply)

  def shoppingList = foreignKey("shopping_list_fk", shoppingListId, TableQuery[ShoppingListTable])(_.id.get)

}
