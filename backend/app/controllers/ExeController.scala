package controllers

import DAO.{DependenciesDAO, EnvironnementDAO, UsersDAO}
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.Utility.gson
import services._

@Singleton
class ExeController @Inject()(cc: ControllerComponents,
                              envDao : EnvironnementDAO,
                              depDao : DependenciesDAO,
                              userDao: UsersDAO ) extends AbstractController(cc) {

  case class runBody(token : String, envId : Long)
  case class stopBody(token : String, envId : Long)
  def run() = Action { implicit request =>
    val body : runBody = gson.fromJson(request.body.asJson.mkString,classOf[runBody])
    val result = Execute.run(Utility.getUserFromToken(body.token).get,body.envId)
    Ok(s"$result")
  }

  def stop() = Action { implicit request =>
    val body : stopBody = gson.fromJson(request.body.asJson.mkString,classOf[stopBody])
    val result = Execute.stop(Utility.getUserFromToken(body.token).get,body.envId)
    Ok(s"$result")
  }
}
