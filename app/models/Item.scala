package models

case class Item(id: Option[Long] = None, name: String, shoppingListId: Long)

object Item {
  def tupled: ((Option[Long], String, Long)) => Item = {
    case (id, name, shoppingListId) => Item(id, name, shoppingListId)
  }

  def unapply(item: Item): Option[(Option[Long], String, Long)] = {
    Some((item.id, item.name, item.shoppingListId))
  }
}