package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class EnvController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {


  def get() = Action { implicit request =>
    Ok(s"get env")// todo
  }

  def post() = Action { implicit request =>
    Ok(s"post env")// todo
  }

  def put() = Action { implicit request =>
    Ok(s"put env")// todo
  }

  def delete() = Action { implicit request =>
    Ok(s"delete env")// todo
  }
}
