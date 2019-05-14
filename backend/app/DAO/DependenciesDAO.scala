package DAO

import Model.Dependencies
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import javax.inject.Singleton

trait DependenciesComponent{
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class DependenciesTable(tag:Tag) extends Table[Dependencies](tag,"DEPENDENCIES"){
    def id = column[Long]("ID",O.PrimaryKey,O.AutoInc)
    def dependencies = column[String]("DEPENDENCIES")

    def * = (id.?,dependencies)<>(Dependencies.tupled,Dependencies.unapply)
  }
}

@Singleton
class DependenciesDAO {

}
