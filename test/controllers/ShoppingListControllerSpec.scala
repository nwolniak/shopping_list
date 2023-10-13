package controllers

import models.Item
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.*
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{Json, OFormat}
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Injecting}
import services.ShoppingListService

class ShoppingListControllerSpec extends PlaySpec with GuiceOneAppPerSuite with MockitoSugar with Injecting {

  implicit val itemFormatter: OFormat[Item] = Json.format[Item]

  "ShoppingListController#getById" should {
    "return item with given id" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)
      val item = Item(1, "Apple")

      when(shoppingListServiceMock.getById(item.id)).thenReturn(Option(item))

      val result = shoppingListController.getById(item.id).apply(FakeRequest())

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe Json.toJson(item)
    }

    "return NO_CONTENT status code when there is no item with given id" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)

      when(shoppingListServiceMock.getById(anyLong())).thenReturn(Option.empty)

      val result = shoppingListController.getById(anyLong()).apply(FakeRequest())

      status(result) mustBe NO_CONTENT
    }
  }

  "ShoppingListController#getAll" should {
    "return all items" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)
      val itemList = List[Item](Item(1, "Apple"), Item(2, "Orange"), Item(3, "Grape"))

      when(shoppingListServiceMock.getAll).thenReturn(itemList)

      val result = shoppingListController.getAll.apply(FakeRequest())

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe Json.toJson(itemList)
    }

    "return NO_CONTENT status code when there is no items" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)

      when(shoppingListServiceMock.getAll).thenReturn(List[Item]())

      val result = shoppingListController.getAll.apply(FakeRequest())

      status(result) mustBe NO_CONTENT
    }
  }

  "ShoppingListController#postItem" should {
    "successfully save new item" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)
      val item = Item(1, "Apple")

      doNothing().when(shoppingListServiceMock).addItem(item)

      val result = shoppingListController.postItem(item).apply(FakeRequest())

      status(result) mustBe OK
    }
  }

  "ShoppingListController#deleteById" should {
    "successfully delete item" in {
      val shoppingListServiceMock = mock[ShoppingListService]
      val shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock)

      doNothing().when(shoppingListServiceMock).deleteById(anyLong())

      val result = shoppingListController.deleteById(anyLong()).apply(FakeRequest())

      status(result) mustBe OK
    }
  }

}
