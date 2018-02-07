package login2gether.dao

import javax.inject.{Inject, Singleton}

import login2gether.models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class UserDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  class Users(tag: Tag) extends Table[User](tag, "USER") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def password = column[String]("PASSWORD")
    def mateId = column[Option[Long]]("MATE_ID")

    def * = (id, name, password, mateId) <> ((User.apply _).tupled, User.unapply)
  }

  private val users = TableQuery[Users]

  def count(): Future[Int] = {
    db.run(users.map(_.id).length.result)
  }

  def findById(id: Long): Future[Option[User]] =
    db.run(users.filter(_.id === id).result.headOption)

  def findByName(name: String): Future[Option[User]] =
    db.run(users.filter(_.name === name).result.headOption)


  def list(): Future[Seq[User]] = {
    db.run(users.result)
  }

  def insert(user: User): Future[Unit] =
    db.run(users += user).map(_ => ())

  def insert(users: Seq[User]): Future[Unit] =
    db.run(this.users ++= users).map(_ => ())


  def update(id: Long, user: User): Future[Unit] = {
    val userToUpdate: User = user.copy(id)
    db.run(users.filter(_.id === id).update(userToUpdate)).map(_ => ())
  }

  def delete(id: Long): Future[Unit] =
    db.run(users.filter(_.id === id).delete).map(_ => ())

}
