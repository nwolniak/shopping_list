package services

import models.Item
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec

class ShoppingListServiceSpec extends AnyFlatSpec with GivenWhenThen {

  it should "allow item to be added" in {
    Given("an empty shopping list")
    val shoppingListService = ShoppingListService()
    val item = Item(1, "Apple")

    When("an item is added")
    shoppingListService.addItem(item)

    Then("the shopping list should have size 1")
    assert(shoppingListService.getAll.size === 1)
  }

  it should "allow multiple items to be added" in {
    Given("a shopping list populated with items")
    val shoppingListService = ShoppingListService()
    val item1 = Item(1, "Apple")
    val item2 = Item(2, "Orange")
    val item3 = Item(3, "Grape")

    When("items are added")
    shoppingListService.addItem(item1)
    shoppingListService.addItem(item2)
    shoppingListService.addItem(item3)

    Then("the shopping list should have size 3")
    assert(shoppingListService.getAll.size === 3)
  }

  it should "return empty list if there is no item added" in {
    val shoppingList = ShoppingListService().getAll
    assert(shoppingList.isEmpty)
  }

  it should "find item by id" in {
    Given("a shopping list populated with item")
    val shoppingListService = ShoppingListService()
    val item = Item(1, "Apple")

    When("getting item by its id")
    shoppingListService.addItem(item)
    val itemOption = shoppingListService.getById(item.id)

    Then("the retrieved item should be the same instance")
    assert(itemOption.isDefined)
    assert(item.eq(itemOption.get))
  }

  it should "allow item to be deleted" in {
    Given("a shopping list with populated items")
    val shoppingListService = ShoppingListService()
    val item1 = Item(1, "Apple")
    val item2 = Item(2, "Orange")
    val item3 = Item(3, "Grape")
    shoppingListService.addItem(item1)
    shoppingListService.addItem(item2)
    shoppingListService.addItem(item3)
    val itemIdToDelete = 2

    When("item with given id is deleted")
    shoppingListService.deleteById(itemIdToDelete)

    Then("the shopping list should not contain that item")
    val shoppingList = shoppingListService.getAll
    assert(shoppingList.size === 2)
    assert(!shoppingList.exists(_.id == itemIdToDelete))
  }


}
