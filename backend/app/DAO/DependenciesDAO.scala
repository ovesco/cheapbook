package DAO

import Model.Dependencies
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


trait DependenciesComponent extends EnvironnementComponent  {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class DependenciesTable(tag:Tag) extends Table[Dependencies](tag,"DEPENDENCIES"){
    def id = column[Long]("ID",O.PrimaryKey,O.AutoInc)
    def envId = column[Long]("ENVIRONNEMENT_ID")
    def dependencies = column[String]("DEPENDENCIES")

    def env = foreignKey("ENVIRONNEMENT_FK",envId,environnment)(_.id)
    def * = (id.?,envId,dependencies)<>(Dependencies.tupled,Dependencies.unapply)

  }

  lazy val dependencies = TableQuery[DependenciesTable]
}

@Singleton
class DependenciesDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
extends DependenciesComponent  with HasDatabaseConfigProvider[JdbcProfile]{
  import profile.api._

  /**
    * Create the schema in databse if it is not already there
    */
  def createIfNotExists(){
    val schema = dependencies.schema
    db.run(schema.createIfNotExists).onComplete({
        case Success(value) => println("S" + value)
        case Failure(exception) => println("F" + exception)
    })
  }

  /**
    * Function to get a dependency from the database
    * @param id the Id of the dependency
    * @return An option of Dependency
    */
  def getDependencies(id : Long) : Future[Option[Dependencies]] = {
    val query = dependencies.filter(dep => dep.id === id).result.headOption
    db.run(query)
  }

  /**
    * Update a dependency
    * @param dep the dependency to update
    * @return the number of line that where updated
    */
  def updateDependencies(dep: Dependencies) = {
    val query = for {
      d <- dependencies
      if d.id === dep.id
    }yield d.dependencies
    val update = query.update(dep.dependencies)
    db.run(update)
  }

  /**
    * Delete a dependency
    * @param id the Id of the dependency to delete
    * @return 1 if delete 0 if not deleted
    */
  def deleteDependencies(id : Long) = {
    val query = dependencies.filter(dep => dep.id === id).delete
    db.run(query)
  }

  /**
    * Add a dependency to the database
    * @param dep the dependency to add
    * @return 1 if created 0 if not
    **/
  def addDependencies(dep : Dependencies): Future[Int] = {
    val query = dependencies map (d => d) += dep
    db.run(query)
  }

  /**
    * Return all the dependancies of an environnement
    * @param envId the environnement id
    * @return A sequences of all the dependencies
    */
  def allDependencies(envId : Long) : Future[Seq[Dependencies]]  = {
    val query = dependencies.filter(dep => dep.envId === envId).result
    db.run(query)
  }
  createIfNotExists()

}
