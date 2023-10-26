package repositories

import models.Item
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import tables.ItemTable

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.Try

class ItemRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api.*

  private lazy val itemQuery = TableQuery[ItemTable]
  private final val insertReturning = itemQuery returning itemQuery

  def saveItem(item: Item): Future[Try[Item]] = {
    val query = insertReturning += item
    db.run(query.asTry)
  }

  def saveItems(itemList: Seq[Item]): Future[Try[Seq[Item]]] = {
    val query = insertReturning ++= itemList
    db.run(query.asTry)
  }

  def getItemWithinList(listId: Long, itemId: Long): Future[Try[Option[Item]]] = {
    val query = itemQuery.filter(item => item.shoppingListId === listId && item.id === itemId).result.headOption
    db.run(query.asTry)
  }

  def getItemsWithinList(listId: Long): Future[Try[Seq[Item]]] = {
    val query = itemQuery.filter(_.shoppingListId === listId).result
    db.run(query.asTry)
  }

  def deleteItem(listId: Long, itemId: Long): Future[Try[Int]] = {
    val query = itemQuery.filter(item => item.shoppingListId === listId && item.id === itemId).delete
    db.run(query.asTry)
  }

}
