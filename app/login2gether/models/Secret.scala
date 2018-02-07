package login2gether.models

import play.api.libs.json.{Format, Json}

case class Secret(id: Long, body: String, owner: Long, viewers: Seq[Long] = Seq.empty)

object Secret {
  implicit val jsonFormat : Format[Secret] = Json.format[Secret]
}
