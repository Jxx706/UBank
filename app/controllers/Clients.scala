package controllers

import play.api._
import play.api.mvc._
import models.Client
import play.api.data._
import play.api.data.Forms._
/**
 * @author jesus
 */
object Clients extends Controller {
  
  private val clientForm = Form(mapping(
      "id" -> number,
      "name" -> text,
      "address" -> text,
      "phone" -> text
      )(Client.apply)(Client.unapply))
  
  def list = Action {
    val clients = Client.getAll
    Ok(views.html.clientsListing(clients))
  }
  
  def newClient = Action {
    Ok(views.html.clientForm(clientForm))
  }
  
  def create = Action { implicit request =>
    clientForm.bindFromRequest.fold(
        formWithErrors => BadRequest,
        client => {
          Client.insert(client)
          Redirect(routes.Clients.details(client.id))
        }
   )
  }
  
  def details(id: Int) = Action {
    Client.getById(id) match {
      case Some(c) =>
        Ok(views.html.clientDetail(c))
      case None => NotFound("NOT FOUND!")
    }
  }
  
  def delete(id: Int) = Action {
    val deleted = Client.delete(id)
    Redirect(routes.Clients.list)
  }
  
  def edit(id: Int) = Action {
    Client.getById(id) match {
      case None => NotFound
      case Some(c) =>
        Ok(views.html.clientForm(clientForm.fill(c), true))
    }
    
  }
  
  def update(id: Int) = Action { implicit request =>
    clientForm.bindFromRequest.fold(
        formWithErrors => BadRequest,
        client => {
           Client.update(client.copy(id = id))
           Redirect(routes.Clients.details(id))
          }
        )
  }
  
}