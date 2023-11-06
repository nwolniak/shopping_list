package repositories

import models.Item
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import tables.{ItemTable, ShoppingListTable}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.Try

class ItemRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api.*

  private lazy val itemQuery = TableQuery[ItemTable]
  private lazy val shoppingListQuery = TableQuery[ShoppingListTable]
  private final val insertReturning = itemQuery returning itemQuery

  def saveItem(listId: Long, userId: Long, item: Item): Future[Try[Item]] = {
    val ensureListExistsAction = shoppingListQuery
      .filter(list => list.userId === userId && list.id === listId)
      .result
      .headOption


    val insertItemAction = ensureListExistsAction.flatMap {
      case Some(list) =>
        insertReturning += item
      case _ =>
        DBIO.failed(new Exception("List not found"))
    }

    db.run(insertItemAction.asTry)
  }

  def saveItems(itemList: Seq[Item]): Future[Try[Seq[Item]]] = {
    val query = insertReturning ++= itemList
    db.run(query.asTry)
  }

  def getItemWithinList(listId: Long, itemId: Long, userId: Long): Future[Try[Option[Item]]] = {
    val query = for {
      (sl, i) <- shoppingListQuery
        .filter(list => list.userId === userId && list.id === listId)
        .join(itemQuery)
        .on(_.id === _.shoppingListId)
        .filter(_._2.id === itemId)
    } yield i

    db.run(query.result.headOption.asTry)
  }

  def getItemsWithinList(listId: Long, userId: Long): Future[Try[Seq[Item]]] = {
    val query = for {
      (sl, i) <- shoppingListQuery
        .filter(list => list.userId === userId && list.id === listId)
        .join(itemQuery)
        .on(_.id === _.shoppingListId)
    } yield i

    db.run(query.result.asTry)
  }

  def deleteItem(listId: Long, itemId: Long, userId: Long): Future[Try[Int]] = {
    val ensureListExistsAction = shoppingListQuery
      .filter(list => list.userId === userId && list.id === listId)
      .result
      .headOption

    val deleteItemAction = ensureListExistsAction.flatMap {
      case Some(list) =>
        itemQuery.filter(item => item.id === itemId && item.shoppingListId === listId)
          .delete
      case _ =>
        DBIO.failed(new Exception("List not found"))
    }

    db.run(deleteItemAction.asTry)
  }

  def putItem(listId: Long, itemId: Long, userId: Long, item: Item): Future[Try[Option[Item]]] = {
    val ensureListExistsAction = shoppingListQuery
      .filter(list => list.userId === userId && list.id === listId)
      .exists
      .result

    val itemAction = itemQuery.filter(_.id === itemId)

    val updateAction = ensureListExistsAction andThen
      itemAction.update(item) andThen
      itemAction.result.headOption

    db.run(updateAction.asTry)
  }

}
