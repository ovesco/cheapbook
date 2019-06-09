package controllers

import DAO.{DependenciesDAO, EnvironnementDAO, UsersDAO}
import Model.Dependencies
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.Utility.gson
import services._

import scala.concurrent.ExecutionContext

@Singleton
class ExeController @Inject()(cc: ControllerComponents,
                              envDao : EnvironnementDAO,
                              depDao : DependenciesDAO,
                              userDao: UsersDAO)
                             (implicit exec: ExecutionContext) extends AbstractController(cc) {

  case class runBody(token: String, envId : Long)
  case class stopBody(token: String, envId : Long)
  case class runResponse(kind: Int, output: String)

  def run() = Action.async { implicit request =>
    val body : runBody = gson.fromJson(request.body.asJson.mkString,classOf[runBody])
    var deps = Seq[Dependencies]()
    envDao.getEnvironnement(body.envId,Utility.getUserFromToken(body.token).get) map {
      env =>
        if (env.isDefined){
          depDao.allDependencies(body.envId) map {
            dep => deps = deps ++ dep
          } recover {
            case _ => Status(400)("No dependencies found")
          }
          val result = Execute.run(Utility.getUserFromToken(body.token).get, body.envId, env.get, deps)
          Ok(gson.toJson(runResponse(result._1, result._2), classOf[runResponse]))
        } else{
          Status(400)("Env not found")
        }
    }recover{
      case _ => Status(400)("Error")
    }
  }

  def stop() = Action { implicit request =>
    val body : stopBody = gson.fromJson(request.body.asJson.mkString,classOf[stopBody])
    val result = Execute.stop(Utility.getUserFromToken(body.token).get,body.envId)
    Ok(s"$result")
  }
}
