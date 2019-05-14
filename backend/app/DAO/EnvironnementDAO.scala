package DAO

import Model.Environnement
import javax.inject.Singleton
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile


trait EnvironnementComponent{
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class EnvironnementTable(tag: Tag) extends Table[Environnement](tag,"ENVIRONNEMENT"){
    def id = column[Long]("ID",O.PrimaryKey,O.AutoInc)
    def code  = column[String]("CODE")

    def * = (id.?,code)<>(Environnement.tupled,Environnement.unapply)
  }

}

@Singleton
class EnvironnementDAO {

}
