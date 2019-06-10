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

  /**
    * Function to get a dependency
    * @param id The id of the wanted dependency  passed as a queryString
    * @return a JSON with the dependency or a Status 400 if failed
    */

  def get(id:String) = Action.async { implicit request =>
    depDao.getDependencies(id.toLong) map {
      dbr => Ok(gson.toJson(GetResponse(dbr.get.id.get, dbr.get.envId, dbr.get.dependencies), classOf[GetResponse]))
    } recover {
      case _ => Status(400)("Error")
    }
  }

  /**
    * Function to add a dependency to the database
    * The information needed have to be from a JSON in the body of the request and are the following :
    * - token : String => the Session token of the user
    * - envid : Long   => the id of the environment that will use the dependency
    * - dependency : String => the string of the dependency
    * @return The value of the SQL request or a Status 400 if failed
    */
  def post() = Action.async { implicit request =>
    val body: PostBody = gson.fromJson(request.body.asJson.mkString, classOf[PostBody])
    depDao.addDependencies(Model.Dependencies(Option.empty, body.envId, body.dependency)) map {
      dbr => Ok(s"$dbr")
    } recover {
      case _ => Status(400)("Error")
    }
  }

  /**
    * Function to modify a dependency
    * The information needed have to be from a JSON in the body of the request and are the following :
    * - token      : String => the Session token of the user
    * - id         : Long   => the id  of the dependency to change
    * - envid      : Long   => the id of the environment that will use the dependency
    * - dependency : String => the string of the dependency
    *
    * @return The value of the SQL request or a Status 400 if failed
    */
  def put() = Action.async { implicit request =>
    val body: PutBody = gson.fromJson(request.body.asJson.mkString, classOf[PutBody])
    depDao.updateDependencies(Model.Dependencies(Option(body.id), body.envId, body.dependency)) map {
      dbr => Ok(s"$dbr")
    } recover {
      case _ => Status(400)("Error")
    }
  }

  /**
    * Function to delete a dependency
    * @param token the session token of the user passed as queryString
    * @param id    the id of the dependency passed as queryString
    * @return The value of the SQL request or a Status 400 if failed
    */
  def delete(token: String, id: String) = Action.async { implicit request =>
    depDao.deleteDependencies(id.toLong) map {
      dbr => Ok(s"$dbr")
    } recover {
      case _ => Status(400)("Error")
    }
  }
  /**
    * Function to get all the  dependencies
    * @param token the session token of the user passed as queryString
    * @param envId the id of the environment passed as queryString
    * @return The value of the SQL request or a Status 400 if failed
    */
  def getAllDependencies(token:String, envId: String) = Action.async { implicit request =>
    depDao.allDependencies(envId.toLong) map {
      dbr => Ok(gson.toJson(GetAllResponse(dbr.toArray.map(e => GetResponse(e.id.get, e.envId, e.dependencies))), classOf[GetAllResponse]))
    } recover {
      case _ => Status(400)("Error")
    }
  }

}
