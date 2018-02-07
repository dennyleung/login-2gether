package login2gether.service

import javax.inject.{Inject, Singleton}

import login2gether.dao.UsersDao
import login2gether.models.Permission

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DefaultAuthenticationService @Inject()(userDao: UsersDao)(implicit ec: ExecutionContext)  extends AuthenticationService {

  override def authenticate(name: String, password: String): Future[Boolean] = {
    // TODO: check if user's mate has granted permission yet
    userDao.findByName(name).map { userOption =>
      userOption.exists { user => user.name == name && user.password == password }
    }
  }

  override def permitLogin(permission: Permission): Boolean = permission.isGranted
}

trait AuthenticationService {

  def authenticate(username: String, password: String): Future[Boolean]

  def permitLogin(permission: Permission) : Boolean
  
}
