package models

case class ShoppingListDto(shoppingList: ShoppingList, items: Option[Seq[Item]])
