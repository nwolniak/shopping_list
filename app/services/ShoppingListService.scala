package services

import models.Item

import scala.collection.mutable.ListBuffer

class ShoppingListService {

  private val shoppingList = new ListBuffer[Item]()

  def getById(itemId: Long): Option[Item] = shoppingList.find(_.id == itemId)

  def getAll: List[Item] = shoppingList.toList

  def addItem(item: Item): Unit = shoppingList.addOne(item)

  def deleteById(itemId: Long): Unit =
    val idxToDelete = shoppingList.indexWhere(_.id == itemId)
    if idxToDelete != -1 then shoppingList.remove(idxToDelete)

}
