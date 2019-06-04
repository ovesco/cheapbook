package controllers

import DAO.DependenciesDAO
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.Utility
import services.Utility.gson

import scala.concurrent.ExecutionContext

@Singleton
class DependenciesController @Inject()(cc: ControllerComponents,
                                       depDao: DependenciesDAO)
                                      (implicit exec: ExecutionContext) extends AbstractController(cc) {

  case class GetBody(token: String, id: Long)
  case class PostBody(token: String, dependencies: String)
  case class PutBody(token: String, id: Long, dependencies: String)
  case class DeleteBody(token: String, id: Long)
  case class AllBody(token:String)
  def get() = Action.async { implicit request =>
    val body: GetBody = gson.fromJson(request.body.asJson.mkString, classOf[GetBody])
    depDao.getDependencies(body.id) map {
      dbr => Ok(s"${dbr.get.dependencies}")
    } recover {
      case _ => Status(400)("Error")
    }
  }

  def post() = Action.async { implicit request =>
    val body: PostBody = gson.fromJson(request.body.asJson.mkString, classOf[PostBody])
    depDao.addDependencies(Model.Dependencies(Option.empty, Utility.getUserFromToken(body.token).get, body.dependencies)) map {
      dbr => Ok(s"$dbr")
    } recover {
      case _ => Status(400)("Error")
    }
  }

  def put() = Action.async { implicit request =>
    val body: PutBody = gson.fromJson(request.body.asJson.mkString, classOf[PutBody])
    depDao.updateDependencies(Model.Dependencies(Option(body.id), Utility.getUserFromToken(body.token).get, body.dependencies)) map {
      dbr => Ok(s"$dbr")
    } recover {
      case _ => Status(400)("Error")
    }
  }

  def delete() = Action.async { implicit request =>
    val body: DeleteBody = gson.fromJson(request.body.asJson.mkString, classOf[DeleteBody])
    depDao.deleteDepndencies(body.id,Utility.getUserFromToken(body.token).get) map {
      dbr => Ok(s"$dbr")
    } recover {
      case _ => Status(400)("Error")
    }
  }

  def getAlldependencies() = Action.async { implicit request =>
    val body: AllBody = gson.fromJson(request.body.asJson.mkString, classOf[AllBody])
    depDao.allDependencies(Utility.getUserFromToken(body.token).get) map {
      dbr => Ok(s"$dbr")
    } recover {
      case _ => Status(400)("Error")
    }
  }

}
