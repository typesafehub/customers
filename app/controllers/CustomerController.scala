package controllers

import javax.inject.Inject

import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, Controller}
import repository.CustomerRepository
import slick.driver.JdbcProfile
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class CustomerController @Inject() (customerRepository: CustomerRepository) extends Controller {

  def findAll = Action.async {
    customerRepository.all().map { customers =>
      Ok(Json.toJson(customers))
    }
  }

  def findByName(name: String) = Action {
//    customers.find(_.name == name) match {
//      case Some(customer) => Ok(Json.toJson(customer))
//      case None           => BadRequest(s"Customer $name has not been found.")
//    }
    Ok("")
  }

  def insert = Action.async(parse.json) { request =>
    val name = (request.body \ "name").as[String]
    customerRepository.insert(name).map { id =>
      Ok(Json.obj("id" -> id))
    }
  }
}
