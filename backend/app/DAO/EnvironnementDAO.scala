package DAO

import Model.Environnement
import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


trait EnvironnementComponent extends UsersComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class EnvironnementTable(tag: Tag) extends Table[Environnement](tag,"ENVIRONNEMENT"){
    def id = column[Long]("ID",O.PrimaryKey,O.AutoInc)
    def code  = column[String]("CODE")
    def userId = column[Long]("USER_ID")
    def user = foreignKey("USER_FK",userId,users)(_.id)

    def * = (id.?,userId,code)<>(Environnement.tupled,Environnement.unapply)
  }
  lazy val environnment = TableQuery[EnvironnementTable]
}

@Singleton
class EnvironnementDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends EnvironnementComponent  with HasDatabaseConfigProvider[JdbcProfile]{
  import profile.api._


  /**
    * Create the schema in databse if it is not already there
    */
  def createIfNotExists(){
    val schema = environnment.schema
    db.run(schema.createIfNotExists).onComplete({
      case Success(value) => println("S" + value)
      case Failure(exception) => println("F" + exception)
    })
  }

  /**
    * Function to get a specific environnement
    * @param id     the environnement id
    * @param userId the id of the environnement owner
    * @return An option of Environnement
    */
  def getEnvironnement(id : Long,userId : Long):Future[Option[Environnement]] = {
    val query = environnment.filter(env => env.id === id && env.userId === userId).result.headOption
    db.run(query)
  }

  /**
    * Update an environnment
    * @param env the new environnement
    * @return 1 if updated or 0 if not
    */

  def updateEnvironnement(env: Environnement): Future[Int] = {
    val query = for {
      e <- environnment
      if e.id === env.id
    } yield e.code
    val update = query.update(env.code)
    db.run(update)
  }

  /**
    * Delete an envionnmenet
    * @param id     the environnement id
    * @param userId the id of the environnement owner
    * @return 1 if deleted 0 if not
    */
  def deleteEnvironnement(id : Long,userId:Long): Future[Int] = {
    val query = environnment.filter(env => env.id === id && env.userId === userId).delete
    db.run(query)
  }

  /**
    * Add an enviornnement to the database
    * @param env the environment to be added
    * @return 1 if added 0 if not
    */

  def addEnvironnement(env: Environnement):Future[Int] = {
    val query = environnment map (e => e) += env
    db.run(query)
  }

  /**
    * Return all the environnement of a user
    * @param userId  the user id
    * @return a Sequence of the user environnment
    */
  def allEnvironnement(userId:Long): Future[Seq[Environnement]]  = {
    val query = environnment.filter(env => env.userId === userId).result
    db.run(query)
  }

  //create the database at the creation of the DAO
  createIfNotExists()
}
