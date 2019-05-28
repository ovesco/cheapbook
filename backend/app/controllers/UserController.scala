package controllers

import DAO.UsersDAO
import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents, Result}
import services.Utility
import services.Utility.gson

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

@Singleton
class UserController @Inject()(cc: ControllerComponents,
                               actorSystem: ActorSystem,
                               usersDao :UsersDAO)
                              (implicit exec: ExecutionContext) extends AbstractController(cc) {

  case class RegisterBody(username: String, password: String)
  case class LoginBody(username: String, password: String)
  case class LogoutBody(token: String)

  def register() = Action.async { implicit request =>
    val body: RegisterBody = gson.fromJson(request.body.asJson.mkString, classOf[RegisterBody])
    usersDao.createIfNotExists()// TODO remove
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
          val token = Utility.generateTokenForUser(body.username)
          Ok(s"$dbr You are logged in as: ${body.username} with token: $token")
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
    Ok(s"You have logged out with token: ${body.token}")// todo
  }
}
