package controllers

import models.{Item, ShoppingList, ShoppingListDto}
import play.api.libs.json.*
import play.api.mvc.*
import services.ShoppingListService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.language.postfixOps

@Singleton
class ShoppingListController @Inject()(val controllerComponents: ControllerComponents,
                                       val shoppingListService: ShoppingListService)(implicit ec: ExecutionContext) extends BaseController {


  implicit val itemFormatter: OFormat[Item] = Json.format[Item]
  implicit val shoppingListFormatter: OFormat[ShoppingList] = Json.format[ShoppingList]
  implicit val shoppingListDtoFormatter: OFormat[ShoppingListDto] = Json.format[ShoppingListDto]


  def createShoppingList: Action[AnyContent] = Action.async {
    shoppingListService.createShoppingList.map {
      case Some(shoppingList) => Ok(Json.toJson(shoppingList))
      case None => NoContent
    }
  }

  def postShoppingList: Action[ShoppingList] = Action.async(parse.tolerantJson[ShoppingList]) { request =>
    val shoppingList = request.body
    shoppingListService.addShoppingList(shoppingList).map {
      case Some(savedShoppingList) => Ok(Json.toJson(savedShoppingList))
      case None => NoContent
    }
  }

  def postItemToList(listId: Long): Action[Item] = Action.async(parse.tolerantJson[Item]) { request =>
    val item = request.body
    shoppingListService.addItemToList(listId, item).map {
      case Some(savedItem) => Ok(Json.toJson(savedItem))
      case None => NoContent
    }
  }

  def getShoppingList(listId: Long): Action[AnyContent] = Action.async {
    shoppingListService.getShoppingList(listId).map {
      case Some(shoppingListDto) => Ok(Json.toJson(shoppingListDto))
      case None => NoContent
    }
  }

  def getShoppingLists: Action[AnyContent] = Action.async {
    shoppingListService.getShoppingLists.map {
      case Some(shoppingListsDto) => Ok(Json.toJson(shoppingListsDto))
      case None => NoContent
    }
  }

  def getItemWithinList(listId: Long, itemId: Long): Action[AnyContent] = Action.async {
    shoppingListService.getItemWithinList(listId, itemId).map {
      case Some(item) => Ok(Json.toJson(item))
      case None => NoContent
    }
  }

  def getItemsWithinList(listId: Long): Action[AnyContent] = Action.async {
    shoppingListService.getItemsWithinList(listId).map {
      case Some(items) => Ok(Json.toJson(items))
      case None => NoContent
    }
  }

  def deleteShoppingList(listId: Long): Action[AnyContent] = Action.async {
    shoppingListService.deleteShoppingList(listId).map {
      case Some(0) => NotFound
      case Some(_) => Ok
      case None => Conflict
    }
  }

  def deleteItemInList(listId: Long, itemId: Long): Action[AnyContent] = Action.async {
    shoppingListService.deleteItemInList(listId, itemId).map {
      case Some(0) => NotFound
      case Some(_) => Ok
      case None => NoContent
    }
  }

}