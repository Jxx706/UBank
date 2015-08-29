package controllers

import play.api._
import play.api.mvc._

/**
 * @author jesus
 */
object Movements extends Controller {
  def list(username: String, account: Long) = Action {
    Ok(s"This should list all the movements of the account $account that belongs to $username")
  }
  
  def create(username: String, account: Long) = Action {
    Ok(s"This should create a new movement in the account $account that belongs to $username and then should redirect to lists.")
  }
  
}