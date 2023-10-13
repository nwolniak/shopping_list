package controllers

import models.Item
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import services.ShoppingListService

import javax.inject.{Inject, Singleton}
import scala.language.postfixOps

@Singleton
class ShoppingListController @Inject()(val controllerComponents: ControllerComponents,
                                       val shoppingListService: ShoppingListService) extends BaseController {


  implicit val itemFormatter: OFormat[Item] = Json.format[Item]

  def getById(itemId: Long): Action[AnyContent] = Action {
    shoppingListService.getById(itemId) match
      case Some(item) => Ok(Json.toJson(item))
      case None => NoContent
  }

  def getAll: Action[AnyContent] = Action {
    val shoppingList = shoppingListService.getAll
    if shoppingList isEmpty then NoContent
    else Ok(Json.toJson(shoppingList))
  }

  def postItem(item: Item): Action[AnyContent] = Action {
    shoppingListService.addItem(item)
    Ok
  }
  
  def deleteById(itemId: Long): Action[AnyContent] = Action {
    shoppingListService.deleteById(itemId)
    Ok
  }

}