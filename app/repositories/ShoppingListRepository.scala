package repositories

import models.ShoppingList
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import tables.ShoppingListTable

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.Try

class ShoppingListRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api.*

  private lazy val shoppingListQuery = TableQuery[ShoppingListTable]
  private final val insertReturning = shoppingListQuery returning shoppingListQuery

  def getShoppingList(listId: Long): Future[Try[Option[ShoppingList]]] = {
    val query = shoppingListQuery.filter(_.id === listId).result.headOption
    db.run(query.asTry)
  }

  def getShoppingLists: Future[Try[Seq[ShoppingList]]] = {
    val query = shoppingListQuery.result
    db.run(query.asTry)
  }

  def createShoppingList: Future[Try[ShoppingList]] = {
    val query = insertReturning += ShoppingList(Option.empty)
    db.run(query.asTry)
  }

  def saveShoppingList(shoppingList: ShoppingList): Future[Try[ShoppingList]] = {
    val query = insertReturning += shoppingList
    db.run(query.asTry)
  }

  def saveShoppingLists(shoppingLists: Seq[ShoppingList]): Future[Try[Seq[ShoppingList]]] = {
    val query = insertReturning ++= shoppingLists
    db.run(query.asTry)
  }

  def deleteShoppingList(listId: Long): Future[Try[Int]] = {
    val query = shoppingListQuery.filter(_.id === listId).delete
    db.run(query.asTry)
  }

}
