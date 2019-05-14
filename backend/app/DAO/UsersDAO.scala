package DAO

import Model.Users
import javax.inject.Singleton
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile


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
class UsersDAO {

}
