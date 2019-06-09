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

  case class PostBody(token: String, envId: Long, dependency: String)
  case class PutBody(token: String, id: Long, envId: Long, dependency: String)
  case class GetResponse(id: Long, envId: Long, dependency: String)
  case class GetAllResponse(deps: Array[GetResponse])

  def get(id:String) = Action.async { implicit request =>
    depDao.getDependencies(id.toLong) map {
      dbr => Ok(gson.toJson(GetResponse(dbr.get.id.get, dbr.get.envId, dbr.get.dependencies), classOf[GetResponse]))
    } recover {
      case _ => Status(400)("Error")
    }
  }

  def post() = Action.async { implicit request =>
    val body: PostBody = gson.fromJson(request.body.asJson.mkString, classOf[PostBody])
    depDao.addDependencies(Model.Dependencies(Option.empty, body.envId, body.dependency)) map {
      dbr => Ok(s"$dbr")
    } recover {
      case _ => Status(400)("Error")
    }
  }

  def put() = Action.async { implicit request =>
    val body: PutBody = gson.fromJson(request.body.asJson.mkString, classOf[PutBody])
    depDao.updateDependencies(Model.Dependencies(Option(body.id), body.envId, body.dependency)) map {
      dbr => Ok(s"$dbr")
    } recover {
      case _ => Status(400)("Error")
    }
  }

  def delete(token: String, id: String) = Action.async { implicit request =>
    depDao.deleteDepndencies(id.toLong,Utility.getUserFromToken(token).get) map {
      dbr => Ok(s"$dbr")
    } recover {
      case _ => Status(400)("Error")
    }
  }

  def getAllDependencies(token:String, envId: String) = Action.async { implicit request =>
    depDao.allDependencies(envId.toLong) map {
      dbr => Ok(gson.toJson(GetAllResponse(dbr.toArray.map(e => GetResponse(e.id.get, e.envId, e.dependencies))), classOf[GetAllResponse]))
    } recover {
      case _ => Status(400)("Error")
    }
  }

}
