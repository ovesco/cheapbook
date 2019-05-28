package controllers

import DAO.UsersDAO
import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents, Result}
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
    val rb: RegisterBody = gson.fromJson(request.body.asJson.mkString, classOf[RegisterBody])
    usersDao.createIfNotExists()// TODO remove
    val future = usersDao.createUser(Model.Users(Option.empty, rb.username, rb.password))
    future map {
        dbr => Ok(s"$dbr You have register a new user with username: ${rb.username} and password: ${rb.password}")
      } recover {
        case _ => Status(400)("Unable to register")
      }

  }

  def login() = Action { implicit request =>
    val rb: LoginBody = gson.fromJson(request.body.asJson.mkString, classOf[LoginBody])
    Ok(s"you are trying to login with username: ${rb.username} and password: ${rb.password}")// todo
  }

  def logout() = Action { implicit request =>
    val rb: LogoutBody = gson.fromJson(request.body.asJson.mkString, classOf[LogoutBody])
    Ok(s"you are trying to logout with token: ${rb.token}")// todo
  }
}
