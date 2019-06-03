package DAO

import Model.Dependencies
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

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
class DependenciesDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
extends DependenciesComponent  with HasDatabaseConfigProvider[JdbcProfile]{
  import profile.api._
  val dependencies = TableQuery[DependenciesTable]



  def createIfNotExists(){
    val schema = dependencies.schema
    db.run(schema.createIfNotExists).onComplete({
        case Success(value) => println("S" + value)
        case Failure(exception) => println("F" + exception)
    })
  }

  def getDependencies(id : Long) : Future[Option[Dependencies]] = {
    val query = dependencies.filter(dep => dep.id === id).result.headOption
    db.run(query)
  }

  def updateDependencies(dep: Dependencies) = {
    val query = for {
      d <- dependencies
      if d.id === dep.id
    }yield d.dependencies
    val update = query.update(dep.dependencies)
    db.run(update)
  }

  def deleteDepndencies(id : Long) = {
    val query = dependencies.filter(_.id ===id)
    val action = query.delete
    db.run(action)
  }

  def addDependencies(dep : Dependencies): Future[Int] = {
    val query = dependencies map (d => d.dependencies)+= dep.dependencies
    db.run(query)
  }

  createIfNotExists()

}
