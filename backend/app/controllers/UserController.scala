package controllers

import DAO.UsersDAO
import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.Utility
import services.Utility.gson

import scala.concurrent.ExecutionContext

@Singleton
class UserController @Inject()(cc: ControllerComponents,
                               actorSystem: ActorSystem,
                               usersDao :UsersDAO)
                              (implicit exec: ExecutionContext) extends AbstractController(cc) {

  case class RegisterBody(username: String, password: String)
  case class LoginBody(username: String, password: String)
  case class LoginResponse(token: String)
  case class LogoutBody(token: String)

  def register() = Action.async { implicit request =>
    val body: RegisterBody = gson.fromJson(request.body.asJson.mkString, classOf[RegisterBody])
    usersDao.createUser(Model.Users(Option.empty, body.username, body.password)) map {
        dbr => Ok(s"$dbr You have register a new user with username: ${body.username} and password: ${body.password}")
      } recover {
        case _ => Status(400)("Unable to register")
      }
  }

  def login() = Action.async { implicit request =>
    val body: LoginBody = gson.fromJson(request.body.asJson.mkString, classOf[LoginBody])
    usersDao.getUser(body.username, body.password) map {
      dbr =>
        if (dbr.isDefined) {
          val token = Utility.generateTokenForUser(dbr.get.id.get)
          val json = gson.toJson(LoginResponse(token), classOf[LoginResponse])
          Ok(json)
        } else {
          Status(400)("Unable to login")
        }
    } recover {
      case _ => Status(400)("Unable to login")
    }
  }

  def logout() = Action { implicit request =>
    val body: LogoutBody = gson.fromJson(request.body.asJson.mkString, classOf[LogoutBody])
    Utility.removeTokenOfUser(body.token)
    Ok(s"You have logged out with token: ${body.token}")
  }
}
