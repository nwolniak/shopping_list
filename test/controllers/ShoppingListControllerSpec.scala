package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import models.{Item, ShoppingList, ShoppingListDto, User}
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.{any, anyLong}
import org.mockito.Mockito.*
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json, OFormat}
import play.api.mvc.{AnyContent, Request, Result}
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import services.{AuthService, ShoppingListService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ShoppingListControllerSpec extends PlaySpec
  with MockitoSugar
  with BeforeAndAfterEach
  with GivenWhenThen {

  private implicit val itemFormatter: OFormat[Item] = Json.format[Item]
  implicit val shoppingListFormatter: OFormat[ShoppingList] = Json.format[ShoppingList]
  implicit val shoppingListDtoFormatter: OFormat[ShoppingListDto] = Json.format[ShoppingListDto]

  private var shoppingListServiceMock: ShoppingListService = _
  private var authServiceMock: AuthService = _
  private var shoppingListController: ShoppingListController = _
  private var request: Request[AnyContent] = FakeRequest().withHeaders("Authorization" -> "Basic dXNlcm5hbWU6cGFzc3dvcmQ=")

  val as = ActorSystem()
  implicit val materializer: Materializer = Materializer(as)

  override protected def beforeEach(): Unit = {
    shoppingListServiceMock = mock[ShoppingListService]
    authServiceMock = mock[AuthService]
    when(authServiceMock.authenticate("Basic dXNlcm5hbWU6cGFzc3dvcmQ="))
      .thenReturn(Future.successful(Some(User(id = Some(1), username = "username", password = "password"))))
    val authenticatedAction = AuthenticatedAction(authServiceMock, stubBodyParser())
    shoppingListController = ShoppingListController(stubControllerComponents(), shoppingListServiceMock, authenticatedAction)
  }

  "ShoppingListController#createShoppingList" should {
    "response with Created code when success" in {
      val user = User(id = Some(1), username = "username", password = "password")
      val shoppingList = ShoppingList(id = Some(1), userId = user.id.get)

      when(shoppingListServiceMock.createShoppingList(anyLong))
        .thenReturn(Future.successful(Some(shoppingList)))

      val result = shoppingListController.createShoppingList.apply(request)

      status(result) mustEqual CREATED
      contentType(result).value mustEqual "application/json"
      contentAsJson(result) mustEqual Json.toJson(shoppingList)
    }
    "response with NoContent code when failure" in {
      when(shoppingListServiceMock.createShoppingList(anyLong))
        .thenReturn(Future.successful(None))

      val result = shoppingListController.createShoppingList.apply(request)

      status(result) mustEqual NO_CONTENT
    }
  }

  "ShoppingListController#postShoppingList" should {
    "response with Created code when success" in {
      val shoppingList = ShoppingList(id = Some(1), userId = 1, purchaseDate = None)

      when(shoppingListServiceMock.addShoppingList(any[ShoppingList], anyLong))
        .thenReturn(Future.successful(Some(shoppingList)))

      val result = shoppingListController.postShoppingList.apply(request.withBody(shoppingList))

      status(result) mustBe CREATED
      contentType(result).value mustEqual "application/json"
      contentAsJson(result) mustEqual Json.toJson(shoppingList)
    }
    "response with NoContent code when failure" in {
      val shoppingList = ShoppingList(id = Some(1), userId = 1, purchaseDate = None)

      when(shoppingListServiceMock.addShoppingList(any[ShoppingList], anyLong))
        .thenReturn(Future.successful(None))

      val result = shoppingListController.postShoppingList.apply(request.withBody(shoppingList))

      status(result) mustBe NO_CONTENT
    }
  }

  "ShoppingListController#getShoppingList" should {
    "response with Ok code when success" in {
      val shoppingList = ShoppingList(id = Some(1), userId = 1)
      val items = Seq(
        Item(Some(1), "apple1", 1, "Szt", false, shoppingList.id.get),
        Item(Some(2), "apple2", 1, "Szt", false, shoppingList.id.get),
        Item(Some(3), "apple3", 1, "Szt", false, shoppingList.id.get)
      )
      val shoppingListDto = ShoppingListDto(shoppingList, Some(items))

      when(shoppingListServiceMock.getShoppingList(anyLong, anyLong))
        .thenReturn(Future.successful(Some(shoppingListDto)))

      val result = shoppingListController.getShoppingList(anyLong).apply(request)

      status(result) mustBe OK
      contentType(result).value mustBe "application/json"
      contentAsJson(result) mustBe Json.toJson(shoppingListDto)
    }
    "response with NotFound code when failure" in {
      when(shoppingListServiceMock.getShoppingList(anyLong, anyLong))
        .thenReturn(Future.successful(None))

      val result = shoppingListController.getShoppingList(anyLong).apply(request)

      status(result) mustBe NOT_FOUND
    }
  }

  "ShoppingListController#getShoppingLists" should {
    "response with Ok code when success" in {
      val shoppingList1 = ShoppingList(id = Some(1), userId = 1)
      val shoppingList2 = ShoppingList(id = Some(2), userId = 1)
      val shoppingList3 = ShoppingList(id = Some(3), userId = 1)
      val shoppingListDtos = Seq(
        ShoppingListDto(shoppingList1, None),
        ShoppingListDto(shoppingList2, None),
        ShoppingListDto(shoppingList3, None)
      )

      when(shoppingListServiceMock.getShoppingLists(anyLong))
        .thenReturn(Future.successful(Some(shoppingListDtos)))

      val result = shoppingListController.getShoppingLists.apply(request)

      status(result) mustBe OK
      contentType(result).value mustBe "application/json"
      contentAsJson(result) mustBe Json.toJson(shoppingListDtos)
    }
    "response with NotFound code when failure" in {
      when(shoppingListServiceMock.getShoppingLists(anyLong))
        .thenReturn(Future.successful(None))

      val result = shoppingListController.getShoppingLists.apply(request)

      status(result) mustBe NOT_FOUND
    }
  }

  "ShoppingListController#deleteShoppingList" should {
    "response with Ok code when success" in {
      when(shoppingListServiceMock.deleteShoppingList(anyLong, anyLong))
        .thenReturn(Future.successful(Some(1)))

      val result = shoppingListController.deleteShoppingList(anyLong).apply(request)

      status(result) mustBe OK
    }

    "response with NoContent code when failure" in {
      when(shoppingListServiceMock.deleteShoppingList(anyLong, anyLong))
        .thenReturn(Future.successful(None))

      val result = shoppingListController.deleteShoppingList(anyLong).apply(request)

      status(result) mustBe NO_CONTENT
    }
  }

  "ShoppingListController#getItemWithinList" should {
    "response with Ok code when success" in {
      val item = Item(Some(1), "apple1", 1, "Szt", false, 1)

      when(shoppingListServiceMock.getItemWithinList(anyLong, anyLong, anyLong))
        .thenReturn(Future.successful(Some(item)))

      val result = shoppingListController.getItemWithinList(1, 1).apply(request)

      status(result) mustBe OK
      contentType(result).value mustBe "application/json"
      contentAsJson(result) mustBe Json.toJson(item)
    }
    "response with NotFound code when failure" in {
      when(shoppingListServiceMock.getItemWithinList(anyLong, anyLong, anyLong))
        .thenReturn(Future.successful(None))

      val result = shoppingListController.getItemWithinList(1, 1).apply(request)

      status(result) mustBe NOT_FOUND
    }
  }

  "ShoppingListController#getItemsWithinList" should {
    "response with Ok code when success" in {
      val itemList = Seq(
        Item(Some(1), "apple1", 1, "Szt", false, 1),
        Item(Some(2), "apple1", 1, "Szt", false, 1),
        Item(Some(3), "apple1", 1, "Szt", false, 1)
      )

      when(shoppingListServiceMock.getItemsWithinList(anyLong, anyLong))
        .thenReturn(Future.successful(Some(itemList)))

      val result = shoppingListController.getItemsWithinList(anyLong).apply(request)

      status(result) mustBe OK
      contentType(result).value mustBe "application/json"
      contentAsJson(result) mustBe Json.toJson(itemList)
    }
    "response with NotFound code when failure" in {
      when(shoppingListServiceMock.getItemsWithinList(anyLong, anyLong))
        .thenReturn(Future.successful(None))

      val result = shoppingListController.getItemsWithinList(anyLong).apply(request)

      status(result) mustBe NOT_FOUND
    }
  }

  "ShoppingListController#postItemToList" should {
    "response with Created code when success" in {
      val item = Item(Some(1), "apple1", 1, "Szt", false, 1)

      when(shoppingListServiceMock.addItemToList(anyLong, anyLong, any[Item]))
        .thenReturn(Future.successful(Some(item)))

      val result: Future[Result] = shoppingListController.postItemToList(1).apply(request.withBody(item))

      status(result) mustBe CREATED
    }
    "response with NoContent code when failure" in {
      val item = Item(Some(1), "apple1", 1, "Szt", false, 1)

      when(shoppingListServiceMock.addItemToList(anyLong, anyLong, any[Item]))
        .thenReturn(Future.successful(None))

      val result: Future[Result] = shoppingListController.postItemToList(1).apply(request.withBody(item))

      status(result) mustBe NO_CONTENT
    }
  }

  "ShoppingListController#deleteItemInList" should {
    "response with Ok code when success" in {
      when(shoppingListServiceMock.deleteItemInList(anyLong, anyLong, anyLong))
        .thenReturn(Future.successful(Some(1)))

      val result = shoppingListController.deleteItemInList(1, 1).apply(request)

      status(result) mustBe OK
    }
    "response with NoContent code when failure" in {
      when(shoppingListServiceMock.deleteItemInList(anyLong, anyLong, anyLong))
        .thenReturn(Future.successful(None))

      val result = shoppingListController.deleteItemInList(1, 1).apply(request)

      status(result) mustBe NO_CONTENT
    }
  }

}
