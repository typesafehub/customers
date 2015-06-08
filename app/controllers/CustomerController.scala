package controllers

import javax.inject.Inject

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import services.CustomerService

import scala.concurrent.duration._

class CustomerController @Inject() (customerService: CustomerService) extends Controller {

  def findAll = Action.async {
    customerService
      .getCustomers()(2.seconds)
      .map { customers =>
        Ok(Json.toJson(customers))
      }
  }

  def insert = Action.async(parse.json) { request =>
    val name = (request.body \ "name").as[String]
    customerService
      .insertCustomer(name)(2.seconds)
      .map { id =>
        Ok(Json.obj("id" -> id))
      }
  }
}
