package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.Utility.gson

@Singleton
class UserController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  case class RegisterBody(username: String, password: String)
  case class LoginBody(username: String, password: String)

  def register() = Action { implicit request =>
    val rb: RegisterBody = gson.fromJson(request.body.asJson.mkString, classOf[RegisterBody])

    Ok(s"you are trying to register with username: ${rb.username} and password: ${rb.password}")// todo
  }

  def login() = Action { implicit request =>
    val rb: LoginBody = gson.fromJson(request.body.asJson.mkString, classOf[LoginBody])
    Ok(s"you are trying to login with username: ${rb.username} and password: ${rb.password}")// todo
  }
}
