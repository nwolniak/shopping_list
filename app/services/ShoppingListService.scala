package services

import com.google.inject.Inject
import models.{Item, ShoppingList, ShoppingListDto}
import play.api.Logger
import repositories.{ItemRepository, ShoppingListRepository}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class ShoppingListService @Inject()
(val itemRepository: ItemRepository,
 val shoppingListRepository: ShoppingListRepository)
(implicit ec: ExecutionContext) {

  private val log = Logger(getClass)

  def getShoppingList(listId: Long, userId: Long): Future[Option[ShoppingListDto]] = {
    shoppingListRepository.getShoppingList(listId, userId).flatMap {
      case Failure(exception) =>
        log.error(s"Failed to get shopping list", exception)
        Future.successful(None)
      case Success(shoppingList) =>
        getItemsWithinList(shoppingList.get.id.get, userId)
          .map(items => Some(ShoppingListDto(shoppingList.get, items)))
    }
  }

  //  def getShoppingLists(userId: Long): Future[Option[Seq[Option[ShoppingListDto]]]] = {
  //    shoppingListRepository.getShoppingLists(userId).flatMap {
  //      case Failure(exception) =>
  //        log.error(s"Failed to get shopping lists", exception)
  //        Future.successful(None)
  //      case Success(shoppingLists) => Future.sequence(
  //        shoppingLists.map(shoppingList => getShoppingList(shoppingList.id.get, userId))
  //      ).map(shoppingLists => Some(shoppingLists))
  //    }
  //  }
  def getShoppingLists(userId: Long): Future[Option[Seq[ShoppingListDto]]] = {
    shoppingListRepository.getShoppingLists(userId).flatMap {
      case Failure(exception) =>
        log.error(s"Failed to get shopping lists", exception)
        Future.successful(None)
      case Success(shoppingLists) => Future.sequence(
        shoppingLists.map { shoppingList =>
          getItemsWithinList(shoppingList.id.get, userId).map { items =>
            ShoppingListDto(shoppingList, items)
          }
        }).map(shoppingLists => Some(shoppingLists))
    }
  }

  def getItemWithinList(listId: Long, itemId: Long, userId: Long): Future[Option[Item]] = {
    itemRepository.getItemWithinList(listId, itemId, userId).map {
      case Failure(exception) =>
        log.error(s"Failed to get item", exception)
        None
      case Success(item) => item
    }
  }

  def getItemsWithinList(listId: Long, userId: Long): Future[Option[Seq[Item]]] = {
    itemRepository.getItemsWithinList(listId, userId).map {
      case Failure(exception) =>
        log.error(s"Failed to get items", exception)
        None
      case Success(items) => Some(items)
    }
  }

  def createShoppingList(userId: Long): Future[Option[ShoppingList]] = {
    shoppingListRepository.createShoppingList(userId).map {
      case Failure(exception) =>
        log.error(s"Failed to create shopping list", exception)
        None
      case Success(shoppingList) => Some(shoppingList)
    }
  }

  def addShoppingList(shoppingList: ShoppingList, userId: Long): Future[Option[ShoppingList]] = {
    val shoppingListWithUserId = shoppingList.copy(userId = userId)
    shoppingListRepository.saveShoppingList(shoppingListWithUserId).map {
      case Failure(exception) =>
        log.error(s"Failed to save shopping list", exception)
        None
      case Success(shoppingList) => Some(shoppingList)
    }
  }

  def addItemToList(listId: Long, userId: Long, item: Item): Future[Option[Item]] = {
    val itemWithListId = item.copy(shoppingListId = listId)
    itemRepository.saveItem(listId, userId, itemWithListId).map {
      case Failure(exception) =>
        log.error(s"Failed to add item $item to list", exception)
        None
      case Success(item) => Some(item)
    }
  }

  def addItemsToList(listId: Long, items: Seq[Item], userId: Long): Future[Option[Seq[Item]]] = {
    val itemsWithListId = items.map(_.copy(shoppingListId = listId))
    itemRepository.saveItems(itemsWithListId).map {
      case Failure(exception) =>
        log.error(s"Failed to add items to list", exception)
        None
      case Success(items) => Some(items)
    }
  }

  def deleteShoppingList(listId: Long, userId: Long): Future[Option[Int]] = {
    shoppingListRepository.deleteShoppingList(listId, userId).map {
      case Failure(exception) =>
        log.error(s"Failed to delete shopping list", exception)
        None
      case Success(value) => Some(value)
    }
  }

  def deleteItemInList(listId: Long, itemId: Long, userId: Long): Future[Option[Int]] = {
    itemRepository.deleteItem(listId, itemId, userId).map {
      case Failure(exception) =>
        log.error(s"Failed to delete item", exception)
        None
      case Success(value) => Some(value)
    }
  }

  def putShoppingList(listId: Long, userId: Long, shoppingList: ShoppingList): Future[Option[ShoppingList]] = {
    shoppingListRepository.putShoppingList(listId, userId, shoppingList).map {
      case Failure(exception) =>
        log.error(s"Failed to put shopping list", exception)
        None
      case Success(shoppingList) => shoppingList
    }
  }

  def putItemToList(listId: Long, itemId: Long, userId: Long, item: Item): Future[Option[Item]] = {
    itemRepository.putItem(listId, itemId, userId, item).map {
      case Failure(exception) =>
        log.error(s"Failed to put item", exception)
        None
      case Success(item) => item
    }
  }

}
