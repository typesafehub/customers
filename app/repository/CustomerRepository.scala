package repository

import slick.jdbc.GetResult

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

  implicit object GetUnit extends GetResult[Unit] {
    def apply(rs : slick.jdbc.PositionedResult) : Unit = ()
  }

  def insert(name: String): Future[Customer] =
    db.run {
      for {
        customer <- customers.map(_.name).returning(customers) += name
      } yield customer
    }

  private class Customers(tag: Tag) extends Table[Customer](tag, "customer") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")

    def * = (id, name) <> (Customer.tupled, Customer.unapply _)
  }
}
