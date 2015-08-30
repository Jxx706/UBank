package controllers

import play.api.mvc._
import models.Account
import models.Movement
/**
 * @author jesus
 */
object Accounts extends Controller with WithClient {
  
  def list(clientId: Int) = Action { implicit request =>
    val accounts = Account.getByClient(clientId)
    Ok(views.html.accountsListing(accounts))
  }
  
  def create(clientId: Int) = Action { implicit request =>
    Ok(s"This should create a new account for ")
  }
  
  def details(clientId: Int, account: Int) = Action { implicit request =>
    val a: Option[Account] = Account.getByClientAndNumber(clientId, account)
    val m: List[Movement] = Movement.getAll(account)

    a match {
        case Some(acc) => Ok(views.html.accountDetail(acc, m))
        case None => NotFound
    }
  }
  
  def delete(clientId: Int, account: Int) = Action { implicit request =>
    Account.delete(clientId, account)
    Redirect(routes.Accounts.list(clientId)).flashing("success" -> "Eliminada la cuenta y sus movimientos exitosamente.")
  }
  
}