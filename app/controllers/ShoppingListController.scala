package controllers

import models.{Item, ShoppingList}
import play.api.libs.json.*
import play.api.mvc.*
import services.ShoppingListService

import javax.inject.{Inject, Singleton}
import scala.language.postfixOps

@Singleton
class ShoppingListController @Inject()(val controllerComponents: ControllerComponents,
                                       val shoppingListService: ShoppingListService) extends BaseController {


  implicit val itemFormatter: OFormat[Item] = Json.format[Item]
  implicit val shoppingListFormatter: OFormat[ShoppingList] = Json.format[ShoppingList]


  def createEmptyShoppingList: Action[AnyContent] = Action {
    val shoppingList = shoppingListService.createEmptyShoppingList
    Ok(Json.toJson(shoppingList))
  }

  def postShoppingList: Action[AnyContent] = Action { implicit request =>
    request.body.asJson.foreach(body =>
      val shoppingList: ShoppingList = Json.fromJson(body).get
      shoppingListService.addShoppingList(shoppingList)
    )
    Ok
  }

  def postItemToList(listId: Long): Action[AnyContent] = Action { implicit request =>
    request.body.asJson.foreach(body =>
      val item: Item = Json.fromJson(body).get
      shoppingListService.addItemToList(listId, item)
    )
    Ok
  }

  def getShoppingList(listId: Long): Action[AnyContent] = Action {
    shoppingListService.getShoppingList(listId) match
      case Some(shoppingList) => Ok(Json.toJson(shoppingList))
      case None => NoContent
  }

  def getShoppingLists: Action[AnyContent] = Action {
    shoppingListService.getShoppingLists match
      case Some(shoppingLists) => Ok(Json.toJson(shoppingLists))
      case None => NoContent
  }

  def getItemWithinList(listId: Long, itemId: Long): Action[AnyContent] = Action {
    shoppingListService.getItemWithinList(listId, itemId) match
      case Some(item) => Ok(Json.toJson(item))
      case None => NoContent
  }

  def getItemsWithinList(listId: Long): Action[AnyContent] = Action {
    val shoppingList = shoppingListService.getItemsWithinList(listId)
    if shoppingList isEmpty then NoContent
    else Ok(Json.toJson(shoppingList))
  }

  def deleteShoppingList(listId: Long): Action[AnyContent] = Action {
    shoppingListService.deleteShoppingList(listId)
    Ok
  }

  def deleteItemInList(listId: Long, itemId: Long): Action[AnyContent] = Action {
    shoppingListService.deleteItemInList(listId, itemId)
    Ok
  }

}