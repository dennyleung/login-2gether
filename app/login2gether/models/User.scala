package login2gether.models

import play.api.libs.json.{Format, Json}

case class User(id: Long, name: String, password: String, mateIdOption: Option[Long] = None)

object User {
  implicit val jsonFormat : Format[User] = Json.format[User]
}
