package controllers

import play.api._
import models.{Movement, Account}
import play.api.mvc._
import java.util.Calendar
import play.api.data._
import play.api.data.Forms._
/**
 * @author Jesús Martínez
 */
object Movements extends Controller {
  private val movementForm = Form(tuple(
    "value" -> number.verifying("El monto debe ser mayor a 0", _ > 0d ),
    "concept" -> text
  ))

  def list(clientId: Int, account: Int) = Action {
    val movements = Movement.getAll(account)
    Ok(views.html.movementsListing(movements))
  }
  
  def create(clientId: Int, accountNumber: Int) = Action { implicit request =>
    movementForm.bindFromRequest.fold(
      formWithErrors => Redirect(routes.Accounts.details(clientId, accountNumber)).flashing("error" -> formWithErrors.error("value").get.message),
      tuple => {
        val concept = tuple._2
        val value = tuple._1
        val account = Account.getByClientAndNumber(clientId, accountNumber).get

        if (concept == "credit" && (account.balance - value) < 0d) {
          Redirect(routes.Accounts.details(clientId, accountNumber)).flashing("error" -> "Monto insuficiente")
        } else {
          val movement = Movement(accountNumber, tuple._2, tuple._1.toDouble)
          Movement.insert(movement)
          concept match {
            case "credit" => Account.update(account.copy(balance = account.balance - value))
            case "debit" => Account.update(account.copy(balance = account.balance + value))
          }

          Redirect(routes.Accounts.details(clientId, accountNumber)).flashing("success" -> "Operación ejecutada con éxito.")
        }

      }
    )
  }
  
}