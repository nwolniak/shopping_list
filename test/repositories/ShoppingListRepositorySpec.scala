package repositories

import models.ShoppingList
import org.scalatest.concurrent.Futures.whenReady
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen, OptionValues}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.db.evolutions.Evolutions
import play.api.db.{DBApi, Database}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Injecting
import play.api.{Application, Configuration}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import scala.util.{Failure, Success}

class ShoppingListRepositorySpec extends AnyFreeSpec
  with GuiceOneAppPerSuite
  with BeforeAndAfterEach
  with GivenWhenThen
  with Matchers
  with OptionValues
  with ScalaFutures
  with Injecting {

  lazy val db: Database = inject[DBApi].database("default")
  implicit val defaultPatience: PatienceConfig = PatienceConfig(timeout = Span(2, Seconds), interval = Span(5, Millis))

  override def fakeApplication(): Application = {
    GuiceApplicationBuilder()
      .loadConfig(env => Configuration.load(env, Map("config.resource" -> "application.test.conf")))
      .build()
  }

  override protected def beforeEach(): Unit = {
    Evolutions.applyEvolutions(db)
  }

  override protected def afterEach(): Unit = {
    Evolutions.cleanupEvolutions(db)
  }


  "ShoppingListRepository#createShoppingList" - {
    "should create a shopping list" in {
      val shoppingListRepository = inject[ShoppingListRepository]

      val result = shoppingListRepository.createShoppingList

      whenReady(result) {
        case Failure(exception) => fail(exception)
        case Success(value) => value.id.value shouldBe 1
      }
    }
  }

  "ShoppingListRepository#saveShoppingList" - {
    "should save a shopping list" in {
      val shoppingListRepository = inject[ShoppingListRepository]

      val shoppingList = ShoppingList(Some(1))

      val result = shoppingListRepository.saveShoppingList(shoppingList)

      whenReady(result) {
        case Failure(exception) => fail(exception)
        case Success(value) => value.id.value shouldBe 1
      }
    }
  }

  "ShoppingListRepository#saveShoppingLists" - {
    "should save shopping lists" in {
      val shoppingListRepository = inject[ShoppingListRepository]

      val shoppingLists = Seq(
        ShoppingList(Some(1)),
        ShoppingList(Some(2)),
        ShoppingList(Some(3))
      )

      val result = shoppingListRepository.saveShoppingLists(shoppingLists)

      whenReady(result) {
        case Failure(exception) => fail(exception)
        case Success(value) =>
          value should have size 3
          value should contain allElementsOf shoppingLists
      }
    }
  }

  "ShoppingListRepository#getShoppingList" - {
    "should return shopping list" in {
      val shoppingListRepository = inject[ShoppingListRepository]

      val shoppingLists = Seq(
        ShoppingList(Some(1)),
        ShoppingList(Some(2)),
        ShoppingList(Some(3))
      )

      val result = for {
        x <- shoppingListRepository.saveShoppingLists(shoppingLists)
        shoppingList <- shoppingListRepository.getShoppingList(2)
      } yield shoppingList

      whenReady(result) {
        case Failure(exception) => fail(exception)
        case Success(value) => value.value shouldBe ShoppingList(Some(2))
      }
    }

    "should return None when not found" in {
      val shoppingListRepository = inject[ShoppingListRepository]

      val shoppingLists = Seq(
        ShoppingList(Some(1)),
        ShoppingList(Some(2)),
        ShoppingList(Some(3))
      )

      val result = for {
        x <- shoppingListRepository.saveShoppingLists(shoppingLists)
        shoppingList <- shoppingListRepository.getShoppingList(5)
      } yield shoppingList

      whenReady(result) {
        case Failure(exception) => fail(exception)
        case Success(value) => value shouldBe None
      }
    }
  }

  "ShoppingListRepository#getShoppingLists" - {
    "should return shopping lists" in {
      val shoppingListRepository = inject[ShoppingListRepository]

      val shoppingLists = Seq(
        ShoppingList(Some(1)),
        ShoppingList(Some(2)),
        ShoppingList(Some(3))
      )

      val result = shoppingListRepository.saveShoppingLists(shoppingLists)
        .flatMap(_ => shoppingListRepository.getShoppingLists)

      whenReady(result) {
        case Failure(exception) => fail(exception)
        case Success(value) =>
          value should have size 3
          value should contain allElementsOf shoppingLists
      }
    }

    "should return empty list when there is not any shopping lists" in {
      val shoppingListRepository = inject[ShoppingListRepository]

      val result = shoppingListRepository.getShoppingLists

      whenReady(result) {
        case Failure(exception) => fail(exception)
        case Success(value) => value shouldBe empty
      }
    }
  }

}
