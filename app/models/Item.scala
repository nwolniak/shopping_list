package models

case class Item
(
  id: Option[Long] = None,
  name: String,
  units: Long,
  unitType: String,
  isRealized: Boolean,
  shoppingListId: Long
)

object Item {
  def tupled: ((Option[Long], String, Long, String, Boolean, Long)) => Item = {
    case (id, name, units, unitType, realized, shoppingListId) => Item(id, name, units, unitType, realized, shoppingListId)
  }

  def unapply(item: Item): Option[(Option[Long], String, Long, String, Boolean, Long)] = {
    Some((item.id, item.name, item.units, item.unitType, item.isRealized, item.shoppingListId))
  }
}