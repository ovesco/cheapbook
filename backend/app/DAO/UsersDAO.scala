package DAO

import java.security.KeyStore.TrustedCertificateEntry

import Model.Users
import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import sun.security.util.Password

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


trait UsersComponent{
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class UsersTable(tag: Tag) extends Table[Users](tag,"USERS"){
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def username = column[String]("USERNAME")
    def password = column[String]("PASSWORD")

    def * = (id.?, username,password ) <> (Users.tupled, Users.unapply)
  }

}

@Singleton
class UsersDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends UsersComponent  with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._
  val users = TableQuery[UsersTable]
  def createIfNotExists(){
    val schema = users.schema
    db.run(schema.createIfNotExists).onComplete({
      case Success(value) => println("S" + value)
      case Failure(exception) => println("F" + exception)
    })
  }

  def createUser(user: Users):Future[Int] ={
    val query = users.map(p => (p.username,p.password)) += (user.username,user.password)
    db.run(query)
  }

  def getUser(username:String,password: String):Future[Option[Users]] = {
    val query = users.filter(user => user.username === username && user.password === password).result.headOption
    db.run(query)
  }

}