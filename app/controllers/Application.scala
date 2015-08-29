package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok("This should redirect to controllers.Login.login")
    //Ok(views.html.index("Your new application is ready."))
  }

}