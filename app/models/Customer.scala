package models

import play.api.libs.json.Json

object Customer extends ((Int, String) => Customer) {
  implicit val customerFormat = Json.format[Customer]
}

case class Customer(id: Int, name: String)
