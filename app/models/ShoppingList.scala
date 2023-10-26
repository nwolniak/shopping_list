package models

case class ShoppingList(id: Option[Long] = None)

object ShoppingList {
  def tupled: Option[Long] => ShoppingList = id => ShoppingList(id)

  def unapply(shoppingList: ShoppingList): Option[Option[Long]] = {
    Some(shoppingList.id)
  }
}