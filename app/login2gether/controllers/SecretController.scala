package login2gether.controllers

import javax.inject.Inject

import login2gether.controllers.SecretController._
import login2gether.service.SecretService
import play.api.i18n.I18nSupport
import play.api.libs.json.{Format, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class SecretController @Inject()(secretService: SecretService,
                                 controllerComponents: ControllerComponents)(implicit executionContext: ExecutionContext)
  extends AbstractController(controllerComponents)
    with CustomControllerHelper
    with I18nSupport {

  def list(): Action[AnyContent] = Action.async { implicit request =>
    secretService.listAll().map { secrets =>
      Ok(Json.toJson(secrets))
    }
  }

  def store(): Action[StoreRequestBody] = Action.async(bodyParser = jsonBody[StoreRequestBody]) { request =>
    secretService.store(request.body.userId, request.body.body).map { storedSecret =>
      Ok(Json.toJson(storedSecret))
    }
  }

  def update(secretId: Long): Action[UpdateRequestBody] = Action.async(bodyParser = jsonBody[UpdateRequestBody]) { request =>
    secretService.updateBody(secretId, request.body.userId, request.body.body).map { _ =>
      NoContent
    }
  }

  def updateViewers(secretId: Long): Action[UpdateViewersRequestBody] = Action.async(bodyParser = jsonBody[UpdateViewersRequestBody]) { request =>
    secretService.updateViewers(secretId, request.body.userId, request.body.viewers).map { _ =>
      NoContent
    }
  }

  def delete(secretId: Long): Action[AnyContent] = Action.async { implicit request =>
    getRequestedUserIdFromHeader match {
      case(Some(requestUserId)) =>
        secretService.delete(secretId, requestUserId).map { _ =>
          NoContent
        }
      case None =>
        Future.successful(Forbidden)

    }

  }

}

object SecretController {
  case class StoreRequestBody(userId: Long, body: String)

  object StoreRequestBody {
    implicit val jsonFormat : Format[StoreRequestBody] = Json.format[StoreRequestBody]
  }

  case class UpdateRequestBody(userId: Long, body: String, viewers: Option[Seq[Long]])

  object UpdateRequestBody {
    implicit val jsonFormat : Format[UpdateRequestBody] = Json.format[UpdateRequestBody]
  }

  case class UpdateViewersRequestBody(userId: Long, viewers: Option[Seq[Long]])

  object UpdateViewersRequestBody {
    implicit val jsonFormat : Format[UpdateViewersRequestBody] = Json.format[UpdateViewersRequestBody]
  }

}
