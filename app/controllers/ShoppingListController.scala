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
                                       val shoppingListService: ShoppingListService,
                                       val authenticatedAction: AuthenticatedAction)(implicit ec: ExecutionContext) extends BaseController {


  implicit val itemFormatter: OFormat[Item] = Json.format[Item]
  implicit val shoppingListFormatter: OFormat[ShoppingList] = Json.format[ShoppingList]
  implicit val shoppingListDtoFormatter: OFormat[ShoppingListDto] = Json.format[ShoppingListDto]


  def createShoppingList: Action[AnyContent] = authenticatedAction.async { request =>
    val userId = request.user.id.get
    shoppingListService.createShoppingList(userId).map {
      case Some(shoppingList) => Created(Json.toJson(shoppingList))
      case None => NoContent
    }
  }

  def postShoppingList: Action[ShoppingList] = authenticatedAction.async(parse.tolerantJson[ShoppingList]) { request =>
    val userId = request.user.id.get
    val shoppingList = request.body
    shoppingListService.addShoppingList(shoppingList, userId).map {
      case Some(savedShoppingList) => Created(Json.toJson(savedShoppingList))
      case None => NoContent
    }
  }

  def postItemToList(listId: Long): Action[Item] = authenticatedAction.async(parse.tolerantJson[Item]) { request =>
    val userId = request.user.id.get
    val item = request.body
    shoppingListService.addItemToList(listId, userId, item).map {
      case Some(savedItem) => Created(Json.toJson(savedItem))
      case None => NoContent
    }
  }

  def getShoppingList(listId: Long): Action[AnyContent] = authenticatedAction.async { request =>
    val userId = request.user.id.get
    shoppingListService.getShoppingList(listId, userId).map {
      case Some(shoppingListDto) => Ok(Json.toJson(shoppingListDto))
      case None => NotFound
    }
  }

  def getShoppingLists: Action[AnyContent] = authenticatedAction.async { request =>
    val userId = request.user.id.get
    shoppingListService.getShoppingLists(userId).map {
      case Some(shoppingListsDto) => Ok(Json.toJson(shoppingListsDto))
      case None => NotFound
    }
  }

  def getItemWithinList(listId: Long, itemId: Long): Action[AnyContent] = authenticatedAction.async { request =>
    val userId = request.user.id.get
    shoppingListService.getItemWithinList(listId, itemId, userId).map {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  def getItemsWithinList(listId: Long): Action[AnyContent] = authenticatedAction.async { request =>
    val userId = request.user.id.get
    shoppingListService.getItemsWithinList(listId, userId).map {
      case Some(items) => Ok(Json.toJson(items))
      case None => NotFound
    }
  }

  def deleteShoppingList(listId: Long): Action[AnyContent] = authenticatedAction.async { request =>
    val userId = request.user.id.get
    shoppingListService.deleteShoppingList(listId, userId).map {
      case Some(0) => NotFound
      case Some(_) => Ok
      case None => NoContent
    }
  }

  def deleteItemInList(listId: Long, itemId: Long): Action[AnyContent] = authenticatedAction.async { request =>
    val userId = request.user.id.get
    shoppingListService.deleteItemInList(listId, itemId, userId).map {
      case Some(0) => NotFound
      case Some(_) => Ok
      case None => NoContent
    }
  }

  def putShoppingList(listId: Long): Action[ShoppingList] = authenticatedAction.async(parse.json[ShoppingList]) { request =>
    val userId = request.user.id.get
    val shoppingList = request.body
    shoppingListService.putShoppingList(listId, userId, shoppingList).map {
      case Some(savedShoppingList) => Ok(Json.toJson(savedShoppingList))
      case None => NoContent
    }
  }

  def putItemToList(listId: Long, itemId: Long): Action[Item] = authenticatedAction.async(parse.json[Item]) { request =>
    val userId = request.user.id.get
    val item = request.body
    shoppingListService.putItemToList(listId, itemId, userId, item).map {
      case Some(savedItem) => Ok(Json.toJson(savedItem))
      case None => NoContent
    }
  }

}