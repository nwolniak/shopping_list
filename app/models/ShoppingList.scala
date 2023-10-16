package models

import scala.collection.mutable.ListBuffer

case class ShoppingList(id: Long, items: ListBuffer[Item])
