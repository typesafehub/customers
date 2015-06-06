package repository

import scala.concurrent.Future

import javax.inject.Inject
import models.Customer
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

class CustomerRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  private val customers = TableQuery[Customers]

  def all(): Future[Seq[Customer]] =
    db.run(customers.result)

  def insert(name: String): Future[Int] =
    db.run(customers.map(_.name).returning(customers.map(_.id)) += name)

  private class Customers(tag: Tag) extends Table[Customer](tag, "customer") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")

    def * = (id, name) <> (Customer.tupled, Customer.unapply _)
  }
}
