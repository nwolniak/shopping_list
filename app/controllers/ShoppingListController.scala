package controllers

import models.Item
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{BaseController, ControllerComponents}

import javax.inject.{Inject, Singleton}
import scala.collection.mutable.ListBuffer
import scala.language.postfixOps

@Singleton
class ShoppingListController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  private val shoppingList = new ListBuffer[Item]()
  shoppingList += Item(1, "Bananas")
  shoppingList += Item(2, "Apples")
  shoppingList += Item(3, "Grapes")

  implicit val itemFormatter: OFormat[Item] = Json.format[Item]

  def getById(id: Long) = Action {
    shoppingList.find(_.id == id) match
      case Some(item) => Ok(Json.toJson(item))
      case None => NoContent
  }

  def getAll() = Action {
    if shoppingList isEmpty then NoContent
    else Ok(Json.toJson(shoppingList))
  }

}