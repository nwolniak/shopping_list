package services

import models.{Item, ShoppingList, ShoppingListDto}
import org.mockito.ArgumentMatchers.{any, anyLong}
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatestplus.mockito.MockitoSugar
import repositories.{ItemRepository, ShoppingListRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class ShoppingListServiceSpec extends AnyFunSpec
  with Matchers
  with ScalaFutures
  with MockitoSugar
  with BeforeAndAfterEach
  with OptionValues {

  private var itemRepository: ItemRepository = _
  private var shoppingListRepository: ShoppingListRepository = _
  private var shoppingListService: ShoppingListService = _

  override protected def beforeEach(): Unit = {
    itemRepository = mock[ItemRepository]
    shoppingListRepository = mock[ShoppingListRepository]
    shoppingListService = ShoppingListService(itemRepository, shoppingListRepository)
  }

  describe("ShoppingListService#getShoppingList") {
    it("should return a shopping list when successful") {
      val shoppingList = ShoppingList(id = Some(1), userId = 1)
      val items = Seq(
        Item(Some(1), "apple1", 1, "Szt", false, 1),
        Item(Some(2), "apple2", 1, "Szt", false, 1),
        Item(Some(3), "apple3", 1, "Szt", false, 1)
      )
      val expectedShoppingListDto = ShoppingListDto(shoppingList, Some(items))

      when(shoppingListRepository.getShoppingList(anyLong, anyLong)).thenReturn(Future.successful(Success(Some(shoppingList))))
      when(itemRepository.getItemsWithinList(anyLong, anyLong)).thenReturn(Future.successful(Success(items)))

      val result = shoppingListService.getShoppingList(anyLong, anyLong)

      whenReady(result) { actualShoppingListDto =>
        actualShoppingListDto.value shouldBe expectedShoppingListDto
      }
    }
    it("should return None when an exception is thrown") {
      when(shoppingListRepository.getShoppingList(anyLong, anyLong)).thenReturn(Future.successful(Failure(RuntimeException())))

      val result = shoppingListService.getShoppingList(anyLong, anyLong)

      whenReady(result) { actualShoppingList =>
        actualShoppingList shouldBe None
      }
    }
  }

  describe("ShoppingListService#getShoppingLists") {
    it("should return a shopping lists when successful") {
      val userId = 1
      val expectedShoppingLists = Seq(
        ShoppingList(id = Some(1), userId = userId),
        ShoppingList(id = Some(2), userId = userId),
        ShoppingList(id = Some(3), userId = userId)
      )

      val expectedShoppingListsDtos = Seq(
        ShoppingListDto(ShoppingList(id = Some(1), userId = userId), None),
        ShoppingListDto(ShoppingList(id = Some(2), userId = userId), None),
        ShoppingListDto(ShoppingList(id = Some(3), userId = userId), None)
      )

      when(shoppingListRepository.getShoppingLists(userId)).thenReturn(Future.successful(Success(expectedShoppingLists)))
      when(itemRepository.getItemsWithinList(1, userId)).thenReturn(Future.successful(Failure(RuntimeException())))
      when(itemRepository.getItemsWithinList(2, userId)).thenReturn(Future.successful(Failure(RuntimeException())))
      when(itemRepository.getItemsWithinList(3, userId)).thenReturn(Future.successful(Failure(RuntimeException())))

      val result = shoppingListService.getShoppingLists(userId)

      whenReady(result) { actualShoppingListsDtos =>
        actualShoppingListsDtos.value should contain allElementsOf expectedShoppingListsDtos
      }
    }
    it("should return None when an exception is thrown") {
      when(shoppingListRepository.getShoppingLists(anyLong)).thenReturn(Future.successful(Failure(RuntimeException())))

      val result = shoppingListService.getShoppingLists(anyLong)

      whenReady(result) { actualShoppingLists =>
        actualShoppingLists shouldBe None
      }
    }
  }

  describe("ShoppingListService#getItemWithinList") {
    it("should return an item when successful") {
      val expectedItem = Item(Some(1), "apple", 1, "Szt", false, 1)

      when(itemRepository.getItemWithinList(anyLong, anyLong, anyLong)).thenReturn(Future.successful(Success(Some(expectedItem))))

      val result = shoppingListService.getItemWithinList(anyLong, anyLong, anyLong)

      whenReady(result) { actualItem =>
        actualItem.value shouldBe expectedItem
      }
    }
    it("should return None when an exception is thrown") {
      when(itemRepository.getItemWithinList(anyLong, anyLong, anyLong)).thenReturn(Future.successful(Failure(RuntimeException())))

      val result = shoppingListService.getItemWithinList(anyLong, anyLong, anyLong)

      whenReady(result) { actualItem =>
        actualItem shouldBe None
      }
    }
  }

  describe("ShoppingListService#getItemsWithinList") {
    it("should return items when successful") {
      val expectedItems = Seq[Item](
        Item(Some(1), "apple1", 1, "Szt", false, 1),
        Item(Some(2), "apple2", 1, "Szt", false, 1),
        Item(Some(3), "apple3", 1, "Szt", false, 1)
      )

      when(itemRepository.getItemsWithinList(anyLong, anyLong)).thenReturn(Future.successful(Success(expectedItems)))

      val result = shoppingListService.getItemsWithinList(anyLong, anyLong)

      whenReady(result) { actualItems =>
        actualItems.value shouldBe expectedItems
      }
    }
    it("should return None when an exception is thrown") {
      when(itemRepository.getItemsWithinList(anyLong, anyLong)).thenReturn(Future.successful(Failure(RuntimeException())))

      val result = shoppingListService.getItemsWithinList(anyLong, anyLong)

      whenReady(result) { actualItems =>
        actualItems shouldBe None
      }
    }
  }

  describe("ShoppingListService#createShoppingList") {
    it("should return a shopping list when successful") {
      val userId = 1
      val expectedShoppingList = ShoppingList(id = Some(1), userId = userId)

      when(shoppingListRepository.createShoppingList(userId)).thenReturn(Future.successful(Success(expectedShoppingList)))

      val result = shoppingListService.createShoppingList(userId)

      whenReady(result) { actualShoppingList =>
        actualShoppingList.value shouldBe expectedShoppingList
      }
    }
    it("should return None when an exception is thrown") {
      when(shoppingListRepository.createShoppingList(anyLong)).thenReturn(Future.successful(Failure(RuntimeException())))

      val result = shoppingListService.createShoppingList(anyLong)

      whenReady(result) { actualShoppingList =>
        actualShoppingList shouldBe None
      }
    }
  }

  describe("ShoppingListService#addShoppingList") {
    it("should return a shopping list when successful") {
      val userId = 1
      val expectedShoppingList = ShoppingList(id = Some(1), userId = userId)

      when(shoppingListRepository.saveShoppingList(expectedShoppingList)).thenReturn(Future.successful(Success(expectedShoppingList)))

      val result = shoppingListService.addShoppingList(expectedShoppingList, userId)

      whenReady(result) { actualShoppingList =>
        actualShoppingList.value shouldBe expectedShoppingList
      }
    }
    it("should return None when an exception is thrown") {
      val userId = 1
      when(shoppingListRepository.saveShoppingList(any[ShoppingList])).thenReturn(Future.successful(Failure(RuntimeException())))

      val result = shoppingListService.addShoppingList(ShoppingList(userId = userId), userId)

      whenReady(result) { actualShoppingList =>
        actualShoppingList shouldBe None
      }
    }
  }

  describe("ShoppingListService#addItemToList") {
    it("should return an item when successful") {
      val listId = 1
      val userId = 1
      val expectedItem = Item(Some(1), "apple1", 1, "Szt", false, listId)

      when(itemRepository.saveItem(listId, userId, expectedItem)).thenReturn(Future.successful(Success(expectedItem)))

      val result = shoppingListService.addItemToList(listId, userId, expectedItem)

      whenReady(result) { actualItem =>
        actualItem.value shouldBe expectedItem
      }
    }
    it("should return None when an exception is thrown") {
      val userId = 1
      val listId = 1
      val item = Item(Some(1), "apple", 1, "Szt", false, listId)
      when(itemRepository.saveItem(listId, userId, item)).thenReturn(Future.successful(Failure(RuntimeException())))

      val result = shoppingListService.addItemToList(listId, userId, item)

      whenReady(result) { actualItem =>
        actualItem shouldBe None
      }
    }
  }

  describe("ShoppingListService#deleteShoppingList") {
    it("should return number of affected rows when successful") {
      val numberOfAffectedRows = 1

      when(shoppingListRepository.deleteShoppingList(anyLong, anyLong)).thenReturn(Future.successful(Success(numberOfAffectedRows)))

      val result = shoppingListService.deleteShoppingList(anyLong, anyLong)

      whenReady(result) { code =>
        code.value shouldBe numberOfAffectedRows
      }
    }
    it("should return None when an exception is thrown") {
      when(shoppingListRepository.deleteShoppingList(anyLong, anyLong)).thenReturn(Future.successful(Failure(RuntimeException())))

      val result = shoppingListService.deleteShoppingList(anyLong, anyLong)

      whenReady(result) { code =>
        code shouldBe None
      }
    }
  }

  describe("ShoppingListService#deleteItemInList") {
    it("should return number of affected rows when successful") {
      val numberOfAffectedRows = 1

      when(itemRepository.deleteItem(anyLong, anyLong, anyLong)).thenReturn(Future.successful(Success(numberOfAffectedRows)))

      val result = shoppingListService.deleteItemInList(anyLong, anyLong, anyLong)

      whenReady(result) { code =>
        code.value shouldBe numberOfAffectedRows
      }
    }
    it("should return None when an exception is thrown") {
      when(itemRepository.deleteItem(anyLong, anyLong, anyLong)).thenReturn(Future.successful(Failure(RuntimeException())))

      val result = shoppingListService.deleteItemInList(anyLong, anyLong, anyLong)

      whenReady(result) { code =>
        code shouldBe None
      }
    }
  }

}
