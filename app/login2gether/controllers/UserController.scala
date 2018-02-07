package login2gether.controllers

import javax.inject.Inject

import login2gether.dao.UserDao
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

class UserController @Inject()(usersDao: UserDao,
                               controllerComponents: ControllerComponents)(implicit executionContext: ExecutionContext)
  extends AbstractController(controllerComponents)
  with CustomControllerHelper
  with I18nSupport {


  /**
    * Display the list of users
    */
  def list(): Action[AnyContent] = Action.async { implicit request =>
    usersDao.list().map { users =>
      Ok(Json.toJson(users))
    }
  }


}
