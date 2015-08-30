package controllers

import play.api._
import play.api.mvc._
import models.Account
import models.Movement
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * @author jesus
 */
object Accounts extends Controller {
  
  def list(clientId: Int) = Action {
    val accounts = Account.getByClient(clientId)
    Ok(views.html.accountsListing(accounts))
  }
  
  def create(clientId: Int) = Action {
    Ok(s"This should create a new account for ")
  }
  
  def details(clientId: Int, account: Int) = Action {
    val accountFuture: Future[Option[Account]] = Future(Account.getByClientAndNumber(clientId, account))
    val movementsFuture: Future[List[Movement]] = Future(Movement.getAll(account))
    
    for {
      a <- accountFuture
      m <- movementsFuture
    } a match {
      case Some(account) => Ok(views.html.accountDetail(account, m))
      case None => NotFound
    }
    Ok(s"This should retrieve the details of the account identified with the number $account and that belongs to $clientId")
  }
  
  //def update(username: String, account: Long) = Action {
    //TODO Pensar si se deben modificar las cuentas.
    //Ok(s"This sho")
  //}
  
  def delete(clientId: Int, account: Int) = Action {
    Redirect(routes.Accounts.list(clientId))
  }
  
}