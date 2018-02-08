package login2gether.dao

import login2gether.models._
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import org.specs2.specification.{AfterEach, BeforeEach}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.WithApplication

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

@RunWith(classOf[JUnitRunner])
class SecretsDaoSpec extends Specification with BeforeEach with AfterEach {

  implicit val app: Application = GuiceApplicationBuilder().build()

  def secretsDao:  SecretsDao = {
    val app2SecretsDAO = Application.instanceCache[SecretsDao]
    app2SecretsDAO(app)
  }

  def getSecretId: Long = Await.result(secretsDao.list(), Duration.Inf).head.id
  
  "Secret model" should {

    "be retrieved by id" in new WithApplication {
      secretsDao.list()
      val secret: Secret = Await.result(secretsDao.findById(getSecretId), Duration.Inf).get
      secret.body must equalTo("secret #1 created by fixture")
    }

    "be listed along other secrets" in new WithApplication {
      val secrets: Seq[Secret] = Await.result(secretsDao.list(), Duration.Inf)
      secrets.size must equalTo(2)
    }

    "be updated if needed" in new WithApplication {
      val secretIdToUpdate: Long = getSecretId
      val secretMessage: String = "new secret"

      Await.result(secretsDao.update(secretIdToUpdate, secretMessage), Duration.Inf)

      val secret: Secret = Await.result(secretsDao.findById(secretIdToUpdate), Duration.Inf).get
      secret.body must equalTo(secretMessage)
    }

    "be updated with new viewers" in new WithApplication() {
      val secretIdToUpdate: Long = getSecretId
      val viewersList: Seq[Long] = Seq(1, 2, 3)

      Await.result(secretsDao.updateViewers(secretIdToUpdate, viewersList), Duration.Inf)
      
      val secret: Secret = Await.result(secretsDao.findById(secretIdToUpdate), Duration.Inf).get
      secret.viewers should containAllOf(viewersList)
    }

    "be deleted if needed" in new WithApplication {
      val secretIdToDelete: Long = getSecretId

      Await.result(secretsDao.delete(secretIdToDelete), Duration.Inf)

      val secret: Option[Secret] = Await.result(secretsDao.findById(secretIdToDelete), Duration.Inf)
      secret should beNone
    }


  }

  override protected def before: Any = {
    Await.result(secretsDao.insert(Secret(100, "secret #1 created by fixture", 200, Seq(201, 202))), Duration.Inf)
    Await.result(secretsDao.insert(Secret(101, "secret #2 created by fixture", 200)), Duration.Inf)
  }

  override protected def after: Any = {
    secretsDao.list().foreach { secrets =>
      secrets.foreach(secret => Await.result(secretsDao.delete(secret.id), Duration.Inf))
    }
  }
}