package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class DependenciesController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {


  def get() = Action { implicit request =>
    Ok(s"get dependencies")// todo
  }

  def post() = Action { implicit request =>
    Ok(s"post dependencies")// todo
  }

  def put() = Action { implicit request =>
    Ok(s"put dependencies")// todo
  }

  def delete() = Action { implicit request =>
    Ok(s"delete dependencies")// todo
  }
}
