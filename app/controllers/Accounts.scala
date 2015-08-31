package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import models.Account
import models.Movement
/**
 * @author Jesús Martínez
 */
object Accounts extends Controller with WithClient with SessionChecker{

  private val accountForm = Form(
    tuple(
      "id" -> number,
      "balance" -> number.verifying("Debe ser un número mayor a cero.", _ > 0d)))


  /**
   * List all the accounts linked to a given client.
   * @param clientId Client id number.
   */
  def list(clientId: Int) = Action { implicit request =>
    val accounts = Account.getByClient(clientId)

    if (isAdmin)
      Ok(views.html.accountsListingAdmin(accounts, clientId))
    else
      Ok(views.html.accountsListing(accounts))

  }

  /**
   * Creates a new account for this user.
   */
  def create(clientId: Int) = Action { implicit request =>
    accountForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.accountForm(clientId, formWithErrors)),
      tuple => {
        //We save the account's initial status.
        Account.insert(Account(clientId, tuple._1, tuple._2))
        //The first deposit must be recorded
        Movement.insert(Movement(tuple._1, "debit", tuple._2))

        Redirect(routes.Accounts.list(clientId)).flashing("success" -> s"Cuenta creada con éxito. Balance inicial: COP ${tuple._2}")
      }
    )
  }

  /**
   * Renders a form to create a new account.
   */
  def newAccount(clientId: Int) = Action { implicit request =>
    Ok(views.html.accountForm(clientId, accountForm))
  }

  /**
   * Shows the details of a bank account, including its movements.
   * @param clientId
   * @param account
   * @return
   */
  def details(clientId: Int, account: Int) = Action { implicit request =>
    val a: Option[Account] = Account.getByClientAndNumber(clientId, account)
    val m: List[Movement] = Movement.getAll(account)

    a match {
        case Some(acc) =>
          if (isAdmin) Ok(views.html.accountDetail(acc, m, "admin")) else Ok(views.html.accountDetail(acc, m))
        case None => NotFound
    }
  }


  /**
   * Deletes a bank account and all its records.
   */
  def delete(clientId: Int, account: Int) = Action { implicit request =>
    Account.delete(clientId, account)
    Redirect(routes.Accounts.list(clientId)).flashing("success" -> "Eliminada la cuenta y sus movimientos exitosamente.")
  }
  
}