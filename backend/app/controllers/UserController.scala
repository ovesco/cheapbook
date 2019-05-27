package controllers

import DAO.UsersDAO
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents, Result}
import services.Utility.gson

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success}

@Singleton
class UserController @Inject()(cc: ControllerComponents,
                               usersDao :UsersDAO)
                              (implicit exec: ExecutionContext) extends AbstractController(cc) {

  case class RegisterBody(username: String, password: String)
  case class LoginBody(username: String, password: String)
  case class LogoutBody(token: String)

  def register() = Action { implicit request =>
    val rb: RegisterBody = gson.fromJson(request.body.asJson.mkString, classOf[RegisterBody])
    val future = usersDao.createUser(Model.Users(Option.empty, rb.username, rb.password))
    var temp: Result = Ok("bbbbbbbbb")
    future onComplete {
      case Success(_) => temp = Ok(s"You have register a new user with username: ${rb.username} and password: ${rb.password}")// todo
      case Failure(_) => temp = Status(400)(s"You cannot register a user with username: ${rb.username} and password: ${rb.password}")
    }

    Await.result(future, Duration(1, "s"))
    temp
    //Ok("aaaaaaaaaa")// todo
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
