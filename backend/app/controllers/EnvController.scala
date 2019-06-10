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
  case class GetResponse(id: Long, code: String)
  case class GetAllResponse(envs: Array[GetResponse])


  /**
    * Function to get a envrionnment
    * @param token  the session token of the user passed as queryString
    * @param id     The id of the wanted environment  passed as a queryString
    * @return a JSON with the dependency or a Status 400 if failed
    */
  def get(token: String, id: String) = Action.async { implicit request =>
    envDao.getEnvironnement(id.toLong,Utility.getUserFromToken(token).get) map {// TODO check token
      dbr => Ok(gson.toJson(GetResponse(dbr.get.id.get, dbr.get.code), classOf[GetResponse]))
    } recover {
      case _ => Status(400)("Error")
    }
  }

  /**
    * Function to add an environnment  to the database
    * The information needed have to be from a JSON in the body of the request and are the following :
    * - token : String => the Session token of the user
    * - code  : String => the code you want to execute in your environment
    * @return The value of the SQL request or a Status 400 if failed
    */
  def post() = Action.async { implicit request =>
    val body: PostBody = gson.fromJson(request.body.asJson.mkString, classOf[PostBody])
    envDao.addEnvironnement(Model.Environnement(Option.empty, Utility.getUserFromToken(body.token).get, body.code)) map {
      dbr => Ok(s"$dbr")
    } recover {
      case _ => Status(400)("Error")
    }
  }

  /**
    * Function to modify an environment
    * The information needed have to be from a JSON in the body of the request and are the following :
    * - token : String => the Session token of the user
    * - id    : Long   => the id  of the dependency to change
    * - code  : String => the string corresponding to the code
    * @return The value of the SQL request or a Status 400 if failed
    */

  def put() = Action.async { implicit request =>
    val body: PutBody = gson.fromJson(request.body.asJson.mkString, classOf[PutBody])
    envDao.updateEnvironnement(Model.Environnement(Option(body.id), Utility.getUserFromToken(body.token).get, body.code)) map {
      dbr => Ok(s"$dbr")
    } recover {
      case _ => Status(400)("Error")
    }
  }

  /**
    * Function to delete a dependency
    * @param token the session token of the user passed as queryString
    * @param id    the id of the environment passed as queryString
    * @return The value of the SQL request or a Status 400 if failed
    */
  def delete(token: String, id: String) = Action.async { implicit request =>
    envDao.deleteEnvironnement(id.toLong,Utility.getUserFromToken(token).get) map {
      dbr => Ok(s"$dbr")
    } recover {
      case _ => Status(400)("Error")
    }
  }

  /**
    * Function to get all the environment of a user
    * @param token the session token of the user passed as queryString
    * @return The value of the SQL request or a Status 400 if failed
    */

  def getAllEnvironnments(token: String)   =  Action.async { implicit request =>
    envDao.allEnvironnement(Utility.getUserFromToken(token).get) map {
      dbr => Ok(gson.toJson(GetAllResponse(dbr.toArray.map(e => GetResponse(e.id.get, e.code))), classOf[GetAllResponse]))
    } recover {
      case _ => Status(400)("Error")
    }
  }
}
