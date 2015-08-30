package controllers

import play.api._
import models.Movement
import play.api.mvc._
import java.util.Calendar

/**
 * @author jesus
 */
object Movements extends Controller {
  def list(clientId: Int, account: Int) = Action {
    val movements = Movement.getAll(account)
    Ok(views.html.movementsListing(movements))
  }
  
  def create(clientId: Int, account: Int) = Action {
    Redirect(routes.Movements.list(clientId, account))
  }
  
}