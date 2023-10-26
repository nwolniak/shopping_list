package services

import models.{Item, ShoppingList}
import org.mockito.ArgumentMatchers.{any, anyLong}
import org.mockito.Mockito.when
import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import repositories.{ItemRepository, ShoppingListRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class ShoppingListServiceSpec extends AnyFunSpec
  with Matchers
  with ScalaFutures
  with MockitoSugar
  with OptionValues {

  describe("ShoppingListService#getShoppingList") {
    it("should return a shopping list when successful") {
      val itemRepository = mock[ItemRepository]
      val shoppingListRepository = mock[ShoppingListRepository]
      val shoppingListService = new ShoppingListService(itemRepository, shoppingListRepository)

      val expectedShoppingList = ShoppingList(Some(1))

      when(shoppingListRepository.getShoppingList(anyLong)).thenReturn(Future.successful(Success(Some(expectedShoppingList))))

      val result = shoppingListService.getShoppingList(anyLong)

      whenReady(result) { actualShoppingList =>
        actualShoppingList.value shouldBe expectedShoppingList
      }
    }

    it("should return None when an exception is thrown") {
      val itemRepository = mock[ItemRepository]
      val shoppingListRepository = mock[ShoppingListRepository]
      val shoppingListService = new ShoppingListService(itemRepository, shoppingListRepository)

      when(shoppingListRepository.getShoppingList(anyLong)).thenReturn(Future.successful(Failure(RuntimeException())))

      val result = shoppingListService.getShoppingList(anyLong)

      whenReady(result) { actualShoppingList =>
        actualShoppingList shouldBe None
      }
    }
  }

  describe("ShoppingListService#getShoppingLists") {
    it("should return a shopping lists when successful") {
      val itemRepository = mock[ItemRepository]
      val shoppingListRepository = mock[ShoppingListRepository]
      val shoppingListService = new ShoppingListService(itemRepository, shoppingListRepository)

      val expectedShoppingLists = Seq[ShoppingList](
        ShoppingList(Some(1)),
        ShoppingList(Some(2)),
        ShoppingList(Some(3))
      )

      when(shoppingListRepository.getShoppingLists).thenReturn(Future.successful(Success(expectedShoppingLists)))

      val result = shoppingListService.getShoppingLists

      whenReady(result) { actualShoppingLists =>
        actualShoppingLists.value should contain allElementsOf expectedShoppingLists
      }
    }

    it("should return None when an exception is thrown") {
      val itemRepository = mock[ItemRepository]
      val shoppingListRepository = mock[ShoppingListRepository]
      val shoppingListService = new ShoppingListService(itemRepository, shoppingListRepository)

      when(shoppingListRepository.getShoppingLists).thenReturn(Future.successful(Failure(RuntimeException())))

      val result = shoppingListService.getShoppingLists

      whenReady(result) { actualShoppingLists =>
        actualShoppingLists shouldBe None
      }
    }
  }

  describe("ShoppingListService#getItemWithinList") {
    it("should return an item when successful") {
      val itemRepository = mock[ItemRepository]
      val shoppingListRepository = mock[ShoppingListRepository]
      val shoppingListService = new ShoppingListService(itemRepository, shoppingListRepository)

      val expectedItem = Item(Some(1), "apple", 1)

      when(itemRepository.getItemWithinList(anyLong, anyLong)).thenReturn(Future.successful(Success(Some(expectedItem))))

      val result = shoppingListService.getItemWithinList(anyLong, anyLong)

      whenReady(result) { actualItem =>
        actualItem.value shouldBe expectedItem
      }
    }

    it("should return None when an exception is thrown") {
      val itemRepository = mock[ItemRepository]
      val shoppingListRepository = mock[ShoppingListRepository]
      val shoppingListService = new ShoppingListService(itemRepository, shoppingListRepository)

      when(itemRepository.getItemWithinList(anyLong, anyLong)).thenReturn(Future.successful(Failure(RuntimeException())))

      val result = shoppingListService.getItemWithinList(anyLong, anyLong)

      whenReady(result) { actualItem =>
        actualItem shouldBe None
      }
    }
  }

  describe("ShoppingListService#getItemsWithinList") {
    it("should return items when successful") {
      val itemRepository = mock[ItemRepository]
      val shoppingListRepository = mock[ShoppingListRepository]
      val shoppingListService = new ShoppingListService(itemRepository, shoppingListRepository)

      val expectedItems = Seq[Item](Item(Some(1), "apple", 1), Item(Some(2), "apple", 1), Item(Some(3), "apple", 1))

      when(itemRepository.getItemsWithinList(anyLong)).thenReturn(Future.successful(Success(expectedItems)))

      val result = shoppingListService.getItemsWithinList(anyLong)

      whenReady(result) { actualItems =>
        actualItems.value shouldBe expectedItems
      }
    }

    it("should return None when an exception is thrown") {
      val itemRepository = mock[ItemRepository]
      val shoppingListRepository = mock[ShoppingListRepository]
      val shoppingListService = new ShoppingListService(itemRepository, shoppingListRepository)

      when(itemRepository.getItemsWithinList(anyLong)).thenReturn(Future.successful(Failure(RuntimeException())))

      val result = shoppingListService.getItemsWithinList(anyLong)

      whenReady(result) { actualItems =>
        actualItems shouldBe None
      }
    }
  }

  describe("ShoppingListService#createShoppingList") {
    it("should return a shopping list when successful") {
      val itemRepository = mock[ItemRepository]
      val shoppingListRepository = mock[ShoppingListRepository]
      val shoppingListService = new ShoppingListService(itemRepository, shoppingListRepository)

      val expectedShoppingList = ShoppingList(Some(1))

      when(shoppingListRepository.createShoppingList).thenReturn(Future.successful(Success(expectedShoppingList)))

      val result = shoppingListService.createShoppingList

      whenReady(result) { actualShoppingList =>
        actualShoppingList.value shouldBe expectedShoppingList
      }
    }

    it("should return None when an exception is thrown") {
      val itemRepository = mock[ItemRepository]
      val shoppingListRepository = mock[ShoppingListRepository]
      val shoppingListService = new ShoppingListService(itemRepository, shoppingListRepository)

      when(shoppingListRepository.createShoppingList).thenReturn(Future.successful(Failure(RuntimeException())))

      val result = shoppingListService.createShoppingList

      whenReady(result) { actualShoppingList =>
        actualShoppingList shouldBe None
      }
    }
  }

  describe("ShoppingListService#addShoppingList") {
    it("should return a shopping list when successful") {
      val itemRepository = mock[ItemRepository]
      val shoppingListRepository = mock[ShoppingListRepository]
      val shoppingListService = new ShoppingListService(itemRepository, shoppingListRepository)

      val expectedShoppingList = ShoppingList(Some(1))

      when(shoppingListRepository.saveShoppingList(any[ShoppingList])).thenReturn(Future.successful(Success(expectedShoppingList)))

      val result = shoppingListService.addShoppingList(any[ShoppingList])

      whenReady(result) { actualShoppingList =>
        actualShoppingList.value shouldBe expectedShoppingList
      }
    }

    it("should return None when an exception is thrown") {
      val itemRepository = mock[ItemRepository]
      val shoppingListRepository = mock[ShoppingListRepository]
      val shoppingListService = new ShoppingListService(itemRepository, shoppingListRepository)

      when(shoppingListRepository.saveShoppingList(any[ShoppingList])).thenReturn(Future.successful(Failure(RuntimeException())))

      val result = shoppingListService.addShoppingList(any[ShoppingList])

      whenReady(result) { actualShoppingList =>
        actualShoppingList shouldBe None
      }
    }
  }

  describe("ShoppingListService#addItemToList") {
    it("should return an item when successful") {
      val itemRepository = mock[ItemRepository]
      val shoppingListRepository = mock[ShoppingListRepository]
      val shoppingListService = new ShoppingListService(itemRepository, shoppingListRepository)

      val expectedItem = Item(Some(1), "apple", 1)

      when(itemRepository.saveItem(any[Item])).thenReturn(Future.successful(Success(expectedItem)))

      val result = shoppingListService.addItemToList(anyLong, expectedItem)

      whenReady(result) { actualItem =>
        actualItem.value shouldBe expectedItem
      }
    }

    it("should return None when an exception is thrown") {
      val itemRepository = mock[ItemRepository]
      val shoppingListRepository = mock[ShoppingListRepository]
      val shoppingListService = new ShoppingListService(itemRepository, shoppingListRepository)

      val itemMock = mock[Item]
      when(itemRepository.saveItem(any[Item])).thenReturn(Future.successful(Failure(RuntimeException())))

      val result = shoppingListService.addItemToList(1, itemMock)

      whenReady(result) { actualItem =>
        actualItem shouldBe None
      }
    }
  }

  describe("ShoppingListService#deleteShoppingList") {
    it("should return number of affected rows when successful") {
      val itemRepository = mock[ItemRepository]
      val shoppingListRepository = mock[ShoppingListRepository]
      val shoppingListService = new ShoppingListService(itemRepository, shoppingListRepository)

      val numberOfAffectedRows = 1

      when(shoppingListRepository.deleteShoppingList(anyLong)).thenReturn(Future.successful(Success(numberOfAffectedRows)))

      val result = shoppingListService.deleteShoppingList(anyLong)

      whenReady(result) { code =>
        code.value shouldBe numberOfAffectedRows
      }
    }

    it("should return None when an exception is thrown") {
      val itemRepository = mock[ItemRepository]
      val shoppingListRepository = mock[ShoppingListRepository]
      val shoppingListService = new ShoppingListService(itemRepository, shoppingListRepository)

      when(shoppingListRepository.deleteShoppingList(anyLong)).thenReturn(Future.successful(Failure(RuntimeException())))

      val result = shoppingListService.deleteShoppingList(anyLong)

      whenReady(result) { code =>
        code shouldBe None
      }
    }
  }

  describe("ShoppingListService#deleteItemInList") {
    it("should return number of affected rows when successful") {
      val itemRepository = mock[ItemRepository]
      val shoppingListRepository = mock[ShoppingListRepository]
      val shoppingListService = new ShoppingListService(itemRepository, shoppingListRepository)

      val numberOfAffectedRows = 1

      when(itemRepository.deleteItem(anyLong, anyLong)).thenReturn(Future.successful(Success(numberOfAffectedRows)))

      val result = shoppingListService.deleteItemInList(anyLong, anyLong)

      whenReady(result) { code =>
        code.value shouldBe numberOfAffectedRows
      }
    }

    it("should return None when an exception is thrown") {
      val itemRepository = mock[ItemRepository]
      val shoppingListRepository = mock[ShoppingListRepository]
      val shoppingListService = new ShoppingListService(itemRepository, shoppingListRepository)

      when(itemRepository.deleteItem(anyLong, anyLong)).thenReturn(Future.successful(Failure(RuntimeException())))

      val result = shoppingListService.deleteItemInList(anyLong, anyLong)

      whenReady(result) { code =>
        code shouldBe None
      }
    }
  }

}
