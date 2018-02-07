package login2gether.controllers

import play.api.libs.json.{JsError, JsValue, Json, Reads}
import play.api.mvc.{AnyContent, BaseControllerHelpers, BodyParser, Request}

import scala.concurrent.{ExecutionContext, Future}

trait CustomControllerHelper extends BaseControllerHelpers {

  object Header {
    val USER_ID : String = "userId"
  }

  def getRequestedUserIdFromHeader()(implicit request: Request[AnyContent]) : Option[Long] = {
    request.headers.get(Header.USER_ID).map(_.toLong)
  }

  def jsonBody[A](maxTextLength: Int)(implicit reader: Reads[A], ec: ExecutionContext): BodyParser[A] =
    BodyParser("json-body-parser") { implicit request =>
      parse.json(maxTextLength)(request) mapFuture {
        case Left(simpleResult) =>
          Future.successful(Left(simpleResult))
        case Right(jsValue: JsValue) =>
          jsValue.validate(reader) map {jsResult =>
            Future.successful(Right(jsResult))
          } recoverTotal { jsError: JsError =>
            val validationErrorMessagesMap: Map[String, Seq[String]] = jsError.errors.map { case(path, validationErrors) =>
              path.toString() -> validationErrors.map(_.message)
            }.toMap
            Future.successful(BadRequest(Json.toJson(validationErrorMessagesMap))) map Left.apply
          }
      }
    }

  def jsonBody[A](implicit reader: Reads[A], ec: ExecutionContext): BodyParser[A] = {
    jsonBody(parse.DefaultMaxTextLength)
  }
}
