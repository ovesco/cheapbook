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

  def createIfNotExists(){
    val schema = environnment.schema
    db.run(schema.createIfNotExists).onComplete({
      case Success(value) => println("S" + value)
      case Failure(exception) => println("F" + exception)
    })
  }

  def getEnvironnement(id : Long,userId : Long):Future[Option[Environnement]] = {
    val query = environnment.filter(env => env.id === id && env.userId === userId).result.headOption
    db.run(query)
  }

  def updateEnvironnement(env: Environnement): Future[Int] = {
    val query = for {
      e <- environnment
      if e.id === env.id
    } yield e.code
    val update = query.update(env.code)
    db.run(update)
  }

  def deleteEnvironnement(id : Long,userId:Long): Future[Int] = {
    val query = environnment.filter(env => env.id === id && env.userId === userId).delete
    db.run(query)
  }

  def addEnvironnement(env: Environnement):Future[Int] = {
    val query = environnment map (e => e) += env
    db.run(query)
  }

  def allEnvironnement(userId:Long): Future[Seq[Environnement]]  = {
    val query = environnment.filter(env => env.userId === userId).result
    db.run(query)
  }
  createIfNotExists()
}
