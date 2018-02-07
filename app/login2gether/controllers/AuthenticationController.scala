package login2gether.controllers

import java.util.UUID
import javax.inject.Inject

import login2gether.controllers.AuthenticationController.LoginRequestBody
import login2gether.service.AuthenticationService
import play.api.i18n.I18nSupport
import play.api.libs.json.{Format, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.mvc._

import scala.concurrent.ExecutionContext

class AuthenticationController @Inject()(authenticationService: AuthenticationService,
                                   controllerComponents: ControllerComponents)(implicit executionContext: ExecutionContext)
  extends AbstractController(controllerComponents)
  with CustomControllerHelper
  with I18nSupport {


  def login(): Action[LoginRequestBody] = Action.async(jsonBody[LoginRequestBody]) { implicit request =>
    authenticationService.authenticate(request.body.name, request.body.password).map { _ =>
      //TODO: use default JWT
      NoContent.withHeaders(
        Header.authenticationToken -> UUID.randomUUID().toString
      )
    }
  }
}

object AuthenticationController {
  case class LoginRequestBody(name: String, password: String)

  object LoginRequestBody {
    implicit val jsonFormat : Format[LoginRequestBody] = Json.format[LoginRequestBody]
  }
}
