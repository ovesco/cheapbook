package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.Utility.gson

@Singleton
class ExeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {


  def run() = Action { implicit request =>
    Ok(s"run")// todo
  }

  def stop() = Action { implicit request =>
    Ok(s"stop")// todo
  }
}
