package controllers

import play.api._
import play.api.mvc._


/**
 * @author jesus
 */
object Login extends Controller {
  
  def login = Action {
    Ok("This should prompt a login page.")
  }
  
  def signin = Action {
    Ok("This should redirect to the details page of the user that has just entered the platform.")
  }
}