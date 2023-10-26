package services

import com.google.inject.Inject
import models.{Item, ShoppingList}
import play.api.Logger
import repositories.{ItemRepository, ShoppingListRepository}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class ShoppingListService @Inject()
(val itemRepository: ItemRepository,
 val shoppingListRepository: ShoppingListRepository)
(implicit ec: ExecutionContext) {

  private val shoppingListMap = mutable.HashMap[Long, ShoppingList]()

  private val log = Logger(getClass)

  def getShoppingList(listId: Long): Future[Option[ShoppingList]] = {
    shoppingListRepository.getShoppingList(listId).map {
      case Failure(exception) =>
        log.error(s"Failed to get shopping list", exception)
        None
      case Success(shoppingList) => shoppingList
    }
  }

  def getShoppingLists: Future[Option[Seq[ShoppingList]]] = {
    shoppingListRepository.getShoppingLists.map {
      case Failure(exception) =>
        log.error(s"Failed to get shopping list", exception)
        None
      case Success(shoppingLists) => Some(shoppingLists)
    }
  }

  def getItemWithinList(listId: Long, itemId: Long): Future[Option[Item]] = {
    itemRepository.getItemWithinList(listId, itemId).map {
      case Failure(exception) =>
        log.error(s"Failed to get item", exception)
        None
      case Success(item) => item
    }
  }

  def getItemsWithinList(listId: Long): Future[Option[Seq[Item]]] = {
    itemRepository.getItemsWithinList(listId).map {
      case Failure(exception) =>
        log.error(s"Failed to get items", exception)
        None
      case Success(items) => Some(items)
    }
  }

  def createShoppingList: Future[Option[ShoppingList]] = {
    shoppingListRepository.createShoppingList.map {
      case Failure(exception) =>
        log.error(s"Failed to create shopping list", exception)
        None
      case Success(shoppingList) => Some(shoppingList)
    }
  }

  def addShoppingList(shoppingList: ShoppingList): Future[Option[ShoppingList]] = {
    shoppingListRepository.saveShoppingList(shoppingList).map {
      case Failure(exception) =>
        log.error(s"Failed to save shopping list", exception)
        None
      case Success(shoppingList) => Some(shoppingList)
    }
  }

  def addItemToList(listId: Long, item: Item): Future[Option[Item]] = {
    val itemWithListId = item.copy(shoppingListId = listId)
    itemRepository.saveItem(itemWithListId).map {
      case Failure(exception) =>
        log.error(s"Failed to add item to list", exception)
        None
      case Success(item) => Some(item)
    }
  }

  def addItemsToList(listId: Long, items: Seq[Item]): Future[Option[Seq[Item]]] = {
    val itemsWithListId = items.map(_.copy(shoppingListId = listId))
    itemRepository.saveItems(itemsWithListId).map {
      case Failure(exception) =>
        log.error(s"Failed to add items to list", exception)
        None
      case Success(items) => Some(items)
    }
  }

  def deleteShoppingList(listId: Long): Future[Option[Int]] = {
    shoppingListRepository.deleteShoppingList(listId).map {
      case Failure(exception) =>
        log.error(s"Failed to delete shopping list", exception)
        None
      case Success(value) => Some(value)
    }
  }

  def deleteItemInList(listId: Long, itemId: Long): Future[Option[Int]] = {
    itemRepository.deleteItem(listId, itemId).map {
      case Failure(exception) =>
        log.error(s"Failed to delete item", exception)
        None
      case Success(value) => Some(value)
    }
  }

}
