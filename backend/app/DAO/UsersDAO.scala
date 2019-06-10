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
    def username = column[String]("USERNAME", O.Unique, O.Length(32))
    def password = column[String]("PASSWORD")

    def * = (id.?, username,password ) <> (Users.tupled, Users.unapply)
  }
  lazy val users = TableQuery[UsersTable]
}

@Singleton
class UsersDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends UsersComponent  with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._


  /**
    * Create the schema in databse if it is not already there
    */
  def createIfNotExists(){
    val schema = users.schema
    db.run(schema.createIfNotExists).onComplete({
      case Success(value) => println("S" + value)
      case Failure(exception) => println("F" + exception)
    })
  }

  /**
    * Add a user to the databse
    * @param user the user to be addes
    * @return 1 if added 0 if not
    */
  def createUser(user: Users):Future[Int] ={
    val query = users.map(p => (p.username,p.password)) += (user.username,user.password)
    db.run(query)
  }

  /**
    * Return a user from the database
    * @param username the username of the user
    * @param password the password of the user
    * @return the user id the password and the username match
    */

  def getUser(username:String,password: String):Future[Option[Users]] = {
    val query = users.filter(user => user.username === username && user.password === password).result.headOption
    db.run(query)
  }

  //create the database at the creation of the DAO
  createIfNotExists()

}