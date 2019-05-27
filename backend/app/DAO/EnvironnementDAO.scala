package DAO

import Model.Environnement
import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


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
class EnvironnementDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends EnvironnementComponent  with HasDatabaseConfigProvider[JdbcProfile]{
  import profile.api._
  val environnment = TableQuery[EnvironnementTable]
  def createIfNotExists(){
    val schema = environnment.schema
    db.run(schema.createIfNotExists).onComplete({
      case Success(value) => println("S" + value)
      case Failure(exception) => println("F" + exception)
    })
  }

  def getEnvironnement(id : Long):Future[Option[Environnement]] = {
    val query = environnment.filter(env => env.id === id).result.headOption
    db.run(query)
  }
  def updateEnvironnement(env: Environnement) = {
    val query = for {
      e <- environnment
      if e.id == env.id
    }yield e.code
    val update = query.update(env.code)
    update.statements.head
  }

  def deleteEnvironnement(id : Long) = {
    val query = environnment.filter(_.id ===id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)
    val sql = action.statements.head
  }

  def addEnvironnement(env: Environnement):Future[Int] = {
    val query = environnment map (e => e.code )+= env.code
    db.run(query)
  }
}
