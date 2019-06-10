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

  /**
    * Function to register a user
    * The information needed have to be from a JSON in the body of the request and are the following :
    * - username : String => the username of the user
    * - password : String => the password of the user
    * @return An Ok or a Status 400 if failed
    */
  def register() = Action.async { implicit request =>
    val body: RegisterBody = gson.fromJson(request.body.asJson.mkString, classOf[RegisterBody])
    usersDao.createUser(Model.Users(Option.empty, body.username, body.password)) map {
        dbr => Ok(s"$dbr You have register a new user with username: ${body.username} and password: ${body.password}")
      } recover {
        case _ => Status(400)("Unable to register")
      }
  }

  /**
    * Function to login a user
    * The information needed have to be from a JSON in the body of the request and are the following :
    * - username : String => the username of the user
    * - password : String => the password of the user
    * @return An Ok with the session token or  Status 400 if failed
    */

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

  /**
    * Function to logout a user
    * The information needed have to be from a JSON in the body of the request and are the following :
    * - token : String => the session token of the user
    * @return An Ok
    */
  def logout() = Action { implicit request =>
    val body: LogoutBody = gson.fromJson(request.body.asJson.mkString, classOf[LogoutBody])
    Utility.removeTokenOfUser(body.token)
    Ok(s"You have logged out with token: ${body.token}")
  }
}
