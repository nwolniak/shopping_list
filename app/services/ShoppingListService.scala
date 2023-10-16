package services

import models.{Item, ShoppingList}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class ShoppingListService {

  private val shoppingListMap = mutable.HashMap[Long, ShoppingList]()

  def getShoppingList(listId: Long): Option[ShoppingList] = {
    shoppingListMap.get(listId)
  }

  def getShoppingLists: Option[List[ShoppingList]] = {
    val shoppingLists = shoppingListMap.values.toList
    Option.when(shoppingLists.nonEmpty)(shoppingLists)
  }

  def getItemWithinList(listId: Long, itemId: Long): Option[Item] = {
    shoppingListMap.get(listId)
      .map(_.items)
      .flatMap(items => items.find(_.id == itemId))
  }

  def getItemsWithinList(listId: Long): Option[List[Item]] = {
    shoppingListMap.get(listId)
      .map(_.items)
      .map(_.toList)
  }

  def createEmptyShoppingList: ShoppingList = {
    val shoppingList = ShoppingList(shoppingListMap.size, ListBuffer[Item]())
    shoppingListMap.addOne(shoppingList.id, shoppingList)
    shoppingList
  }

  def addShoppingList(shoppingList: ShoppingList): Unit = {
    shoppingListMap.addOne(shoppingList.id, shoppingList)
  }

  def addItemToList(listId: Long, item: Item): Unit = {
    shoppingListMap.get(listId)
      .map(_.items)
      .map(_.addOne(item))
  }

  def deleteShoppingList(listId: Long): Unit = {
    shoppingListMap.remove(listId)
  }

  def deleteItemInList(listId: Long, itemId: Long): Unit = {
    shoppingListMap.get(listId)
      .map(_.items)
      .map(items => {
        val idxToDelete = items.indexWhere(_.id == itemId)
        if idxToDelete != -1 then items.remove(idxToDelete)
      })
  }

}
