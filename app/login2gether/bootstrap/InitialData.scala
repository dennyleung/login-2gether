package login2gether.bootstrap

import javax.inject.Inject

import login2gether.dao.{SecretsDao, UsersDao}
import login2gether.models.{Secret, User}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.util.Try

private class InitialData @Inject()(usersDao: UsersDao, secretsDao: SecretsDao)(implicit executionContext: ExecutionContext) {

  def insert(): Unit = {
    val insertInitialDataFuture: Future[Unit] = for {
      count <- usersDao.count() if count == 0
      _ <- usersDao.insert(InitialData.users)
      _ <- secretsDao.insert(InitialData.secrets)
    } yield ()

    Try(Await.result(insertInitialDataFuture, Duration.Inf))
  }
  insert()
}

private object InitialData {
  def users: Seq[User] = Seq(
    User(1, "Joost", "joost01", Some(2)),
    User(2, "Janneke", "janneke01", Some(1)),
    User(3, "Robin", "robin01", None)
  )

  def secrets: Seq[Secret] = Seq(
    Secret(1, "personal secret", 1),
    Secret(2, "shared secret", 1, Seq(2, 3))

  )
}


