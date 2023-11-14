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

  def getShoppingList(listId: Long, userId: Long): Future[Try[Option[ShoppingList]]] = {
    val query = shoppingListQuery
      .filter(shoppingList => shoppingList.id === listId && shoppingList.userId === userId)
      .result
      .headOption
    db.run(query.asTry)
  }

  def getShoppingLists(userId: Long): Future[Try[Seq[ShoppingList]]] = {
    val query = shoppingListQuery
      .filter(_.userId === userId)
      .result
    db.run(query.asTry)
  }

  def createShoppingList(userId: Long): Future[Try[ShoppingList]] = {
    val query = insertReturning += ShoppingList(userId = userId)
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

  def deleteShoppingList(listId: Long, userId: Long): Future[Try[Int]] = {
    val query = shoppingListQuery
      .filter(shoppingList => shoppingList.id === listId && shoppingList.userId === userId)
      .delete
    db.run(query.asTry)
  }

  def putShoppingList(listId: Long, userId: Long, shoppingList: ShoppingList): Future[Try[Option[ShoppingList]]] = {
    val ensureListExistsAction = shoppingListQuery
      .filter(list => list.userId === userId && list.id === listId)
      .exists
      .result

    val shoppingListAction = shoppingListQuery.filter(_.id === listId)

    val updateAction = ensureListExistsAction andThen
      shoppingListAction.update(shoppingList) andThen
      shoppingListAction.result.headOption

    db.run(updateAction.asTry)
  }

}
