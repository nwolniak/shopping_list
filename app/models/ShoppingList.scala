package models

import java.sql.Date

case class ShoppingList
(
  id: Option[Long] = None,
  userId: Long,
  purchaseDate: Option[Date] = None
)

object ShoppingList {
  def tupled: ((Option[Long], Long, Option[Date])) => ShoppingList =
    (id, userId, purchaseDate) => ShoppingList(id, userId, purchaseDate)

  def unapply(shoppingList: ShoppingList): Option[(Option[Long], Long, Option[Date])] = {
    Some(shoppingList.id, shoppingList.userId, shoppingList.purchaseDate)
  }
}