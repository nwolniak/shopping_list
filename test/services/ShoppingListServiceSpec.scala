package services

import models.{Item, ShoppingList}
import org.mockito.ArgumentMatchers.anyLong
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{GivenWhenThen, OptionValues}

import scala.collection.mutable.ListBuffer

class ShoppingListServiceSpec extends AnyFlatSpec
    with GivenWhenThen
    with Matchers
    with OptionValues {

  it should "allow empty shopping list to be created" in {
    Given("not any shopping list created")
    val shoppingListService = ShoppingListService()

    When("a shopping list is created")
    val shoppingList = shoppingListService.createEmptyShoppingList

    Then("the shopping list should be empty")
    shoppingList.items shouldBe empty

    And("the shopping lists should contain that list")
    val shoppingListOpt = shoppingListService.getShoppingLists
    shoppingListOpt.value should contain(shoppingList)

    And("there should be 1 shopping list")
    shoppingListOpt.value should have size 1
  }

  it should "allow multiple shopping lists to be created" in {
    Given("not any shopping list created")
    val shoppingListService = ShoppingListService()

    When("multiple empty shopping lists are created")
    val shoppingList1 = shoppingListService.createEmptyShoppingList
    val shoppingList2 = shoppingListService.createEmptyShoppingList
    val shoppingList3 = shoppingListService.createEmptyShoppingList

    Then("shopping lists should contain created lists")
    val shoppingListOpt = shoppingListService.getShoppingLists
    shoppingListOpt.value should contain allOf(shoppingList1, shoppingList2, shoppingList3)

    And("there should be 3 shopping lists")
    shoppingListOpt.value should have size 3
  }

  it should "allow shopping list to be added" in {
    Given("an empty shopping list")
    val shoppingListService = ShoppingListService()
    val shoppingList = ShoppingList(anyLong(), ListBuffer[Item]())

    When("a shopping list is added")
    shoppingListService.addShoppingList(shoppingList)

    Then("shopping lists should contain that list")
    val shoppingListOpt = shoppingListService.getShoppingLists
    shoppingListOpt.value should contain(shoppingList)

    And("there should be 1 shopping list")
    shoppingListOpt.value should have size 1
  }

  it should "allow multiple shopping lists to be added" in {
    Given("multiple empty shopping lists")
    val shoppingListService = ShoppingListService()
    val shoppingList1 = ShoppingList(1, ListBuffer[Item]())
    val shoppingList2 = ShoppingList(2, ListBuffer[Item]())
    val shoppingList3 = ShoppingList(3, ListBuffer[Item]())

    When("shopping lists are added")
    shoppingListService.addShoppingList(shoppingList1)
    shoppingListService.addShoppingList(shoppingList2)
    shoppingListService.addShoppingList(shoppingList3)

    Then("shopping lists should contain all of the lists")
    val shoppingListOpt = shoppingListService.getShoppingLists
    shoppingListOpt.value should contain allOf(shoppingList1, shoppingList2, shoppingList3)

    And("there should be 3 shopping list")
    shoppingListOpt.value should have size 3
  }

  it should "return empty option when there is no shopping list" in {
    Given("empty shopping lists")
    val shoppingListService = ShoppingListService()

    When("getting shopping lists")
    val shoppingListOpt = shoppingListService.getShoppingLists

    Then("there should empty option as result")
    shoppingListOpt shouldBe empty
  }

  it should "return empty option when there is no searched shopping list" in {
    Given("multiple shopping lists")
    val shoppingListService = ShoppingListService()
    val shoppingList1 = ShoppingList(1, ListBuffer[Item]())
    val shoppingList2 = ShoppingList(2, ListBuffer[Item]())
    val shoppingList3 = ShoppingList(3, ListBuffer[Item]())
    shoppingListService.addShoppingList(shoppingList1)
    shoppingListService.addShoppingList(shoppingList2)

    val shoppingListNotExisting = shoppingList3

    When("getting not existing shopping list")
    val shoppingListOpt = shoppingListService.getShoppingList(shoppingListNotExisting.id)

    Then("there should empty option as result")
    shoppingListOpt shouldBe empty
  }

  it should "find shopping list by its id" in {
    Given("multiple shopping lists")
    val shoppingListService = ShoppingListService()
    val shoppingList1 = shoppingListService.createEmptyShoppingList
    val shoppingList2 = shoppingListService.createEmptyShoppingList
    val shoppingList3 = shoppingListService.createEmptyShoppingList

    val shoppingListToFind = shoppingList2

    When("getting shopping list by its id")
    val shoppingListOpt = shoppingListService.getShoppingList(shoppingListToFind.id)

    Then("the retrieved shopping list should be equal")
    shoppingListOpt.value should equal(shoppingListToFind)
  }

  it should "return shopping lists" in {
    Given("multiple shopping lists")
    val shoppingListService = ShoppingListService()
    val shoppingList1 = shoppingListService.createEmptyShoppingList
    val shoppingList2 = shoppingListService.createEmptyShoppingList
    val shoppingList3 = shoppingListService.createEmptyShoppingList

    When("getting shopping lists")
    val shoppingListsOpt = shoppingListService.getShoppingLists

    Then("the retrieved shopping lists should contain all lists")
    shoppingListsOpt.value should contain allOf(shoppingList1, shoppingList2, shoppingList3)
  }

  it should "allow shopping list to be deleted" in {
    Given("shopping list to be deleted")
    val shoppingListService = ShoppingListService()
    val shoppingList1 = shoppingListService.createEmptyShoppingList
    val shoppingList2 = shoppingListService.createEmptyShoppingList
    val shoppingList3 = shoppingListService.createEmptyShoppingList

    val shoppingListToDelete = shoppingList2

    When("shopping list is deleted by its id")
    shoppingListService.deleteShoppingList(shoppingListToDelete.id)

    Then("shopping list should be deleted")
    val shoppingListsOpt = shoppingListService.getShoppingLists
    shoppingListsOpt.value should not contain shoppingListToDelete
    And("shopping lists should have size one smaller")
    shoppingListsOpt.value should have size 2
  }

  it should "allow item to be added to empty shopping list" in {
    Given("an empty shopping list")
    val shoppingListService = ShoppingListService()
    val shoppingList = shoppingListService.createEmptyShoppingList
    val item = Item(1, "Apple")

    When("an item is added")
    shoppingListService.addItemToList(shoppingList.id, item)

    Then("shopping list should contain added item")
    val itemListOpt = shoppingListService.getItemsWithinList(shoppingList.id)
    itemListOpt.value should contain(item)

    And("the shopping list should have size 1")
    itemListOpt.value should have size 1
  }

  it should "allow multiple items to be added to shopping list" in {
    Given("an empty shopping list")
    val shoppingListService = ShoppingListService()
    val shoppingList = shoppingListService.createEmptyShoppingList
    val item1 = Item(1, "Apple")
    val item2 = Item(2, "Orange")
    val item3 = Item(3, "Grape")

    When("items are added")
    shoppingListService.addItemToList(shoppingList.id, item1)
    shoppingListService.addItemToList(shoppingList.id, item2)
    shoppingListService.addItemToList(shoppingList.id, item3)

    Then("the shopping list should contain all items")
    val shoppingListOpt = shoppingListService.getItemsWithinList(shoppingList.id)
    shoppingListOpt.value should contain allOf(item1, item2, item3)
    And("the shopping list should have size 3")
    shoppingListOpt.value should have size 3
  }

  it should "return empty option when there is no items" in {
    Given("an empty shopping list")
    val shoppingListService = ShoppingListService()
    val shoppingList = shoppingListService.createEmptyShoppingList

    When("getting item list with given shopping list id")
    val itemListOpt = shoppingListService.getItemsWithinList(shoppingList.id)

    Then("retrieved item list should be empty")
    itemListOpt.value shouldBe empty
  }

  it should "return item list" in {
    Given("a shopping list")
    val shoppingListService = ShoppingListService()
    val shoppingList = shoppingListService.createEmptyShoppingList
    val item1 = Item(1, "Apple")
    val item2 = Item(2, "Orange")
    val item3 = Item(3, "Grape")
    shoppingListService.addItemToList(shoppingList.id, item1)
    shoppingListService.addItemToList(shoppingList.id, item2)
    shoppingListService.addItemToList(shoppingList.id, item3)

    When("getting item list with given shopping list id")
    val itemListOpt = shoppingListService.getItemsWithinList(shoppingList.id)

    Then("retrieved item list should contain all items")
    itemListOpt.value should contain allOf(item1, item2, item3)

    And("retrieved item list should have size 3")
    itemListOpt.value should have size 3
  }

  it should "find item by its id" in {
    Given("a shopping list populated with items")
    val shoppingListService = ShoppingListService()
    val shoppingList = shoppingListService.createEmptyShoppingList
    val item1 = Item(1, "Apple")
    val item2 = Item(2, "Orange")
    val item3 = Item(3, "Grape")
    shoppingListService.addItemToList(shoppingList.id, item1)
    shoppingListService.addItemToList(shoppingList.id, item2)
    shoppingListService.addItemToList(shoppingList.id, item3)

    val itemToFind = item2

    When("getting item by its id")
    val itemOpt = shoppingListService.getItemWithinList(shoppingList.id, itemToFind.id)

    Then("the retrieved item should be equal")
    itemOpt.value should equal(itemToFind)
  }

  it should "return empty option when there is no searched item" in {
    Given("a shopping list populated with items")
    val shoppingListService = ShoppingListService()
    val shoppingList = shoppingListService.createEmptyShoppingList
    val item1 = Item(1, "Apple")
    val item2 = Item(2, "Orange")
    val item3 = Item(3, "Grape")
    shoppingListService.addItemToList(shoppingList.id, item1)
    shoppingListService.addItemToList(shoppingList.id, item2)

    val itemNotExisting = item3

    When("getting not existing item")
    val itemOpt = shoppingListService.getItemWithinList(shoppingList.id, itemNotExisting.id)

    Then("the result should be empty")
    itemOpt shouldBe empty
  }

  it should "allow item to be deleted" in {
    Given("a shopping list populated with items")
    val shoppingListService = ShoppingListService()
    val shoppingList = shoppingListService.createEmptyShoppingList
    val item1 = Item(1, "Apple")
    val item2 = Item(2, "Orange")
    val item3 = Item(3, "Grape")
    shoppingListService.addItemToList(shoppingList.id, item1)
    shoppingListService.addItemToList(shoppingList.id, item2)
    shoppingListService.addItemToList(shoppingList.id, item3)
    val itemToDelete = item2

    When("item with given id is deleted")
    shoppingListService.deleteItemInList(shoppingList.id, itemToDelete.id)

    Then("the shopping list should not contain that item")
    val itemListOpt = shoppingListService.getItemsWithinList(shoppingList.id)
    itemListOpt.value should not contain itemToDelete

    And("the shopping list should have size 2")
    itemListOpt.value should have size 2
  }

}
