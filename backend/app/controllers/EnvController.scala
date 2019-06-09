package controllers

import DAO.EnvironnementDAO
import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.Utility
import services.Utility.gson

import scala.concurrent.ExecutionContext

@Singleton
class EnvController @Inject()(cc: ControllerComponents,
                              actorSystem: ActorSystem,
                              envDao:EnvironnementDAO)
                             (implicit exec: ExecutionContext) extends AbstractController(cc) {

  case class PostBody(token: String, code: String)
  case class PutBody(token: String, id: Long, code: String)
  case class DeleteBody(token: String, id: Long)
  case class GetResponse(id: Long, code: String)
  case class GetAllResponse(envs: Array[GetResponse])

  def get(token: String, id: String) = Action.async { implicit request =>
    envDao.getEnvironnement(id.toLong,Utility.getUserFromToken(token).get) map {// TODO check token
      dbr => Ok(gson.toJson(GetResponse(dbr.get.id.get, dbr.get.code), classOf[GetResponse]))
    } recover {
      case _ => Status(400)("Error")
    }
  }

  def post() = Action.async { implicit request =>
    val body: PostBody = gson.fromJson(request.body.asJson.mkString, classOf[PostBody])
    envDao.addEnvironnement(Model.Environnement(Option.empty, Utility.getUserFromToken(body.token).get, body.code)) map {
      dbr => Ok(s"$dbr")
    } recover {
      case _ => Status(400)("Error")
    }
  }

  def put() = Action.async { implicit request =>
    val body: PutBody = gson.fromJson(request.body.asJson.mkString, classOf[PutBody])
    envDao.updateEnvironnement(Model.Environnement(Option(body.id), Utility.getUserFromToken(body.token).get, body.code)) map {
      dbr => Ok(s"$dbr")
    } recover {
      case _ => Status(400)("Error")
    }
  }

  def delete() = Action.async { implicit request =>
    val body: DeleteBody = gson.fromJson(request.body.asJson.mkString, classOf[DeleteBody])
    envDao.deleteEnvironnement(body.id,Utility.getUserFromToken(body.token).get) map {
      dbr => Ok(s"$dbr")
    } recover {
      case _ => Status(400)("Error")
    }
  }

  def getAllEnvironnments(token: String)   =  Action.async { implicit request =>
    envDao.allEnvironnement(Utility.getUserFromToken(token).get) map {
      dbr => Ok(gson.toJson(GetAllResponse(dbr.toArray.map(e => GetResponse(e.id.get, e.code))), classOf[GetAllResponse]))
    } recover {
      case _ => Status(400)("Error")
    }
  }
}
