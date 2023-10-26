package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import models.{Item, ShoppingList}
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.{any, anyLong}
import org.mockito.Mockito.*
import org.scalatest.GivenWhenThen
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json, OFormat}
import play.api.mvc.{Request, Result}
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import services.ShoppingListService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ShoppingListControllerSpec extends PlaySpec
  with MockitoSugar
  with GivenWhenThen {

  private implicit val itemFormatter: OFormat[Item] = Json.format[Item]
  implicit val shoppingListFormatter: OFormat[ShoppingList] = Json.format[ShoppingList]

  val as = ActorSystem()
  implicit val materializer: Materializer = Materializer(as)

  "ShoppingListController#createShoppingList" should {
    "create shopping list" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)
      val shoppingList = ShoppingList(Some(1))

      when(shoppingListServiceMock.createShoppingList).thenReturn(Future.successful(Some(shoppingList)))

      val result = shoppingListController.createShoppingList.apply(FakeRequest())

      status(result) mustEqual OK
      contentType(result).value mustEqual "application/json"
      contentAsJson(result) mustEqual Json.toJson(shoppingList)
    }
  }

  "ShoppingListController#postShoppingList" should {
    "add shopping list" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)
      val shoppingList = ShoppingList(Some(1))

      when(shoppingListServiceMock.addShoppingList(any[ShoppingList])).thenReturn(Future.successful(Some(shoppingList)))

      val requestMock = mock[Request[JsValue]]
      when(requestMock.body).thenReturn(Json.toJson(shoppingList))

      val result = shoppingListController.postShoppingList.apply(requestMock)

      status(result) mustBe OK
    }
  }

  "ShoppingListController#getShoppingList" should {
    "return shopping list with given id" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)
      val shoppingList = ShoppingList(Some(1))

      when(shoppingListServiceMock.getShoppingList(anyLong())).thenReturn(Future.successful(Some(shoppingList)))

      val result = shoppingListController.getShoppingList(anyLong()).apply(FakeRequest())

      status(result) mustBe OK
      contentType(result).value mustBe "application/json"
      contentAsJson(result) mustBe Json.toJson(shoppingList)
    }
    "return NO_CONTENT status code when there is no shopping list with given id" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)

      when(shoppingListServiceMock.getShoppingList(anyLong())).thenReturn(Future.successful(None))

      val result = shoppingListController.getShoppingList(anyLong()).apply(FakeRequest())

      status(result) mustBe NO_CONTENT
    }
  }

  "ShoppingListController#getShoppingLists" should {
    "return shopping lists" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)
      val shoppingList1 = ShoppingList(Some(1))
      val shoppingList2 = ShoppingList(Some(2))
      val shoppingList3 = ShoppingList(Some(3))

      val shoppingLists = List[ShoppingList](shoppingList1, shoppingList2, shoppingList3)

      when(shoppingListServiceMock.getShoppingLists).thenReturn(Future.successful(Some(shoppingLists)))

      val result = shoppingListController.getShoppingLists.apply(FakeRequest())

      status(result) mustBe OK
      contentType(result).value mustBe "application/json"
      contentAsJson(result) mustBe Json.toJson(shoppingLists)
    }
    "return NO_CONTENT status code when there is no shopping list" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)

      when(shoppingListServiceMock.getShoppingLists).thenReturn(Future.successful(None))

      val result = shoppingListController.getShoppingLists.apply(FakeRequest())

      status(result) mustBe NO_CONTENT
    }
  }

  "ShoppingListController#deleteShoppingList" should {
    "delete shopping list with given id" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)

      when(shoppingListServiceMock.deleteShoppingList(anyLong())).thenReturn(Future.successful(Some(1)))

      val result = shoppingListController.deleteShoppingList(anyLong()).apply(FakeRequest())

      status(result) mustBe OK
    }
  }

  "ShoppingListController#getItemWithinList" should {
    "return item with given id" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)
      val item = Item(Some(1), "Apple", 1)

      when(shoppingListServiceMock.getItemWithinList(anyLong(), anyLong())).thenReturn(Future.successful(Some(item)))

      val result = shoppingListController.getItemWithinList(anyLong(), anyLong()).apply(FakeRequest())

      status(result) mustBe OK
      contentType(result).value mustBe "application/json"
      contentAsJson(result) mustBe Json.toJson(item)
    }

    "return NO_CONTENT status code when there is no item with given id" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)

      when(shoppingListServiceMock.getItemWithinList(anyLong(), anyLong())).thenReturn(Future.successful(None))

      val result = shoppingListController.getItemWithinList(anyLong(), anyLong()).apply(FakeRequest())

      status(result) mustBe NO_CONTENT
    }
  }

  "ShoppingListController#getItemsWithinList" should {
    "return all items within list" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)
      val itemList = List[Item](Item(Some(1), "Apple", 1), Item(Some(2), "Orange", 1), Item(Some(3), "Grape", 1))

      when(shoppingListServiceMock.getItemsWithinList(anyLong())).thenReturn(Future.successful(Some(itemList)))

      val result = shoppingListController.getItemsWithinList(anyLong()).apply(FakeRequest())

      status(result) mustBe OK
      contentType(result).value mustBe "application/json"
      contentAsJson(result) mustBe Json.toJson(itemList)
    }

    "return NO_CONTENT status code when there is no items" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)

      when(shoppingListServiceMock.getItemsWithinList(anyLong())).thenReturn(Future.successful(None))

      val result = shoppingListController.getItemsWithinList(anyLong()).apply(FakeRequest())

      status(result) mustBe NO_CONTENT
    }
  }

  "ShoppingListController#postItemToList" should {
    "add new item" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)
      val item = Item(Some(1), "Apple", 1)

      when(shoppingListServiceMock.addItemToList(anyLong(), any[Item])).thenReturn(Future.successful(Some(item)))

      val requestMock = mock[Request[JsValue]]
      when(requestMock.body).thenReturn(Json.toJson(item))

      val result: Future[Result] = shoppingListController.postItemToList(1L).apply(requestMock)

      status(result) mustBe OK
    }
  }

  "ShoppingListController#deleteItemInList" should {
    "successfully delete item" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)

      when(shoppingListServiceMock.deleteItemInList(anyLong(), anyLong())).thenReturn(Future.successful(Some(1)))

      val result = shoppingListController.deleteItemInList(anyLong(), anyLong()).apply(FakeRequest())

      status(result) mustBe OK
    }
  }

}
