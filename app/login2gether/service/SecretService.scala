package login2gether.service

import java.security.SecureRandom
import javax.inject.{Inject, Singleton}

import login2gether.dao.SecretsDao
import login2gether.models.Secret

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DefaultSecretService @Inject()(secretsDao: SecretsDao)(implicit ec: ExecutionContext) extends SecretService {

  override def listAll(): Future[Seq[Secret]] = secretsDao.list()

  override def store(ownerId: Long, secretBody: String): Future[Secret] = {
    secretsDao.insert(Secret(SecureRandom.getInstanceStrong.nextLong(), secretBody, ownerId))
  }

  override def updateBody(secretId: Long, requestUserId: Long, secretBody: String): Future[Unit] = {
    isSecretOwner(secretId, requestUserId).flatMap { isOwner =>
      if (isOwner) secretsDao.update(secretId, secretBody).map { Some(_) }
      else Future.successful(None)

    }
  }

  override def updateViewers(secretId: Long, requestUserId: Long, viewersOption: Option[Seq[Long]]): Future[Unit] = {
    isSecretOwner(secretId, requestUserId).flatMap { isOwner =>
      if (isOwner) {
        val viewers = viewersOption.getOrElse(Seq.empty)
        secretsDao.updateViewers(secretId, viewers).map { Some(_) }
      }
      else Future.successful(None)
    }
  }

  override def delete(secretId: Long, requestUserId: Long): Future[Any] = {
    isSecretOwner(secretId, requestUserId).map { isOwner =>
      if (isOwner) {
        secretsDao.delete(secretId).map { _ => Future.successful(Unit)
        }
      }
    }
  }

  private def isSecretOwner(secretId: Long, requestUserId: Long): Future[Boolean] = {
    secretsDao.findById(secretId).map { secretOption =>
      secretOption.exists(_.owner == requestUserId)
    }
  }


}

trait SecretService {
  def delete(secretId: Long, requestUserId: Long): Future[Any]

  def listAll(): Future[Seq[Secret]]

  def store(ownerId: Long, secretBody: String): Future[Secret]

  def updateBody(secretId: Long, requestUserId: Long, secretBody: String): Future[Unit]
  
  def updateViewers(secretId: Long, requestUserId: Long, viewers: Option[Seq[Long]]): Future[Unit]

}
