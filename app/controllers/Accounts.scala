package controllers

import play.api._
import play.api.mvc._

/**
 * @author jesus
 */
object Accounts extends Controller {
  
  def list(username: String) = Action {
    Ok(s"This should retrieve $username's accounts.")
  }
  
  def create(username: String) = Action {
    Ok(s"This should create a new account for $username")
  }
  
  def details(username: String, account: Long) = Action {
    Ok(s"This should retrieve the details of the account identified with the number $account and that belongs to $username")
  }
  
  //def update(username: String, account: Long) = Action {
    //TODO Pensar si se deben modificar las cuentas.
    //Ok(s"This sho")
  //}
  
  def delete(username: String, account: Long) = Action {
    Ok(s"This should delete the account identified with $account that belongs to $username")
  }
  
}