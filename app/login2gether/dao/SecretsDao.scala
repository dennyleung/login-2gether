package login2gether.dao

import javax.inject.{Inject, Singleton}

import login2gether.models.Secret
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.QueryBase

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class SecretsDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  class Secrets(tag: Tag) extends Table[Secret](tag, "SECRET") {

    implicit val listIdsToStringConverter: BaseColumnType[Seq[Long]] = MappedColumnType.base[Seq[Long], String](
      list => list mkString ",",
      str => {
        (str split "," flatMap { strItem =>
          if (strItem.isEmpty) None
          else Some(strItem.toLong)
        }).toSeq
      }
    )

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def body = column[String]("BODY")
    def owner = column[Long]("OWNER")
    def viewers = column[Seq[Long]]("VIEWERS")

    def * = (id, body, owner, viewers) <> ((Secret.apply _).tupled, Secret.unapply)
  }

  private val secrets = TableQuery[Secrets]

  private def buildIdQuery(id: Long) = secrets.filter(_.id === id)

  def count(): Future[Int] = {
    db.run(secrets.map(_.id).length.result)
  }

  def insert(secret: Secret): Future[Secret] =
    db.run(secrets += secret).map(_ => secret)

  def insert(secrets: Seq[Secret]): Future[Unit] =
    db.run(this.secrets ++= secrets).map(_ => ())

  def findById(id: Long): Future[Option[Secret]] =
    db.run(secrets.filter(_.id === id).result.headOption)

  def list(): Future[Seq[Secret]] = {
    db.run(secrets.result)
  }

  def update(id: Long, body: String): Future[Unit] = {
    val query: Query[Rep[String], String, Seq] = {
      for (secret <- secrets if secret.id === id) yield secret.body
    }
    db.run(query.update(body)).map(_ => ())
  }

  def updateViewers(id: Long, updatedViewers: Seq[Long]): Future[Unit] = {
    findById(id).map { secretOption =>
      secretOption.map { secret =>
        val secretToUpdate = secret.copy(viewers = updatedViewers)
        db.run(secrets.filter(_.id === id).update(secretToUpdate)).map(_ => ())
      }
    }
  }

  def delete(id: Long): Future[Unit] =
    db.run(secrets.filter(_.id === id).delete).map(_ => ())

}
