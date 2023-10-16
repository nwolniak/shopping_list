package controllers

import models.{Item, ShoppingList}
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.{any, anyLong, same}
import org.mockito.Mockito.*
import org.scalatest.GivenWhenThen
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Play.materializer
import play.api.libs.json.{Json, OFormat}
import play.api.libs.ws.WSClient
import play.api.mvc.Headers
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.ShoppingListService

import scala.collection.mutable.ListBuffer

class ShoppingListControllerSpec extends PlaySpec
  with GuiceOneAppPerSuite
  with MockitoSugar
  with GivenWhenThen {

  private implicit val wsClient: WSClient = app.injector.instanceOf[WSClient]

  private implicit val itemFormatter: OFormat[Item] = Json.format[Item]
  implicit val shoppingListFormatter: OFormat[ShoppingList] = Json.format[ShoppingList]

  "ShoppingListController#createShoppingList" should {
    "create shopping list" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)
      val shoppingList = ShoppingList(1, ListBuffer[Item]())

      when(shoppingListServiceMock.createEmptyShoppingList).thenReturn(shoppingList)

      val result = shoppingListController.createEmptyShoppingList.apply(FakeRequest())

      status(result) mustEqual OK
      contentType(result).value mustEqual "application/json"
      contentAsJson(result) mustEqual Json.toJson(shoppingList)
    }
  }

  "ShoppingListController#postShoppingList" should {
    "add shopping list" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)
      val shoppingList = ShoppingList(1, ListBuffer[Item]())

      doNothing().when(shoppingListServiceMock).addShoppingList(any[ShoppingList])

      val request = FakeRequest()
        .withHeaders(CONTENT_TYPE -> "application/json")
        .withJsonBody(Json.toJson[ShoppingList](shoppingList))

      val result = shoppingListController.postShoppingList.apply(request)

      status(result) mustBe OK
    }
  }

  "ShoppingListController#getShoppingList" should {
    "return shopping list with given id" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)
      val shoppingList = ShoppingList(1, ListBuffer[Item]())

      when(shoppingListServiceMock.getShoppingList(anyLong())).thenReturn(Option(shoppingList))

      val result = shoppingListController.getShoppingList(anyLong()).apply(FakeRequest())

      status(result) mustBe OK
      contentType(result).value mustBe "application/json"
      contentAsJson(result) mustBe Json.toJson(shoppingList)
    }
    "return NO_CONTENT status code when there is no shopping list with given id" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)

      when(shoppingListServiceMock.getShoppingList(anyLong())).thenReturn(Option.empty)

      val result = shoppingListController.getShoppingList(anyLong()).apply(FakeRequest())

      status(result) mustBe NO_CONTENT
    }
  }

  "ShoppingListController#getShoppingLists" should {
    "return shopping lists" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)
      val shoppingList1 = ShoppingList(1, ListBuffer[Item]())
      val shoppingList2 = ShoppingList(2, ListBuffer[Item]())
      val shoppingList3 = ShoppingList(3, ListBuffer[Item]())

      val shoppingLists = List[ShoppingList](shoppingList1, shoppingList2, shoppingList3)

      when(shoppingListServiceMock.getShoppingLists).thenReturn(Option(shoppingLists))

      val result = shoppingListController.getShoppingLists.apply(FakeRequest())

      status(result) mustBe OK
      contentType(result).value mustBe "application/json"
      contentAsJson(result) mustBe Json.toJson(shoppingLists)
    }
    "return NO_CONTENT status code when there is no shopping list" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)

      when(shoppingListServiceMock.getShoppingLists).thenReturn(Option.empty)

      val result = shoppingListController.getShoppingLists.apply(FakeRequest())

      status(result) mustBe NO_CONTENT
    }
  }

  "ShoppingListController#deleteShoppingList" should {
    "delete shopping list with given id" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)

      doNothing().when(shoppingListServiceMock).deleteShoppingList(anyLong())

      val result = shoppingListController.deleteShoppingList(anyLong()).apply(FakeRequest())

      status(result) mustBe OK
    }
  }

  "ShoppingListController#getItemWithinList" should {
    "return item with given id" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)
      val item = Item(1, "Apple")

      when(shoppingListServiceMock.getItemWithinList(anyLong(), anyLong())).thenReturn(Option(item))

      val result = shoppingListController.getItemWithinList(anyLong(), anyLong()).apply(FakeRequest())

      status(result) mustBe OK
      contentType(result).value mustBe "application/json"
      contentAsJson(result) mustBe Json.toJson(item)
    }

    "return NO_CONTENT status code when there is no item with given id" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)

      when(shoppingListServiceMock.getItemWithinList(anyLong(), anyLong())).thenReturn(Option.empty)

      val result = shoppingListController.getItemWithinList(anyLong(), anyLong()).apply(FakeRequest())

      status(result) mustBe NO_CONTENT
    }
  }

  "ShoppingListController#getItemsWithinList" should {
    "return all items within list" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)
      val itemList = List[Item](Item(1, "Apple"), Item(2, "Orange"), Item(3, "Grape"))

      when(shoppingListServiceMock.getItemsWithinList(anyLong())).thenReturn(Option(itemList))

      val result = shoppingListController.getItemsWithinList(anyLong()).apply(FakeRequest())

      status(result) mustBe OK
      contentType(result).value mustBe "application/json"
      contentAsJson(result) mustBe Json.toJson(itemList)
    }

    "return NO_CONTENT status code when there is no items" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)

      when(shoppingListServiceMock.getItemsWithinList(anyLong())).thenReturn(Option.empty)

      val result = shoppingListController.getItemsWithinList(anyLong()).apply(FakeRequest())

      status(result) mustBe NO_CONTENT
    }
  }

  "ShoppingListController#postItemToList" should {
    "add new item" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)
      val item = Item(1, "Apple")

      doNothing().when(shoppingListServiceMock).addItemToList(anyLong(), same(item))

      val request = FakeRequest()
        .withHeaders(Headers(CONTENT_TYPE -> "application/json"))
        .withJsonBody(Json.toJson(item))

      val result = shoppingListController.postItemToList(1).apply(request)

      status(result) mustBe OK
    }
  }

  "ShoppingListController#deleteItemInList" should {
    "successfully delete item" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)

      doNothing().when(shoppingListServiceMock).deleteItemInList(anyLong(), anyLong())

      val result = shoppingListController.deleteItemInList(anyLong(), anyLong()).apply(FakeRequest())

      status(result) mustBe OK
    }
  }

}
