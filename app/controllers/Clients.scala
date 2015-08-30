package controllers

import play.api._
import play.api.mvc._
import models.Client
import play.api.data._
import play.api.data.Forms._
/**
 * @author jesus
 */
object Clients extends Controller with WithClient {
  
  private val clientForm = Form(mapping(
      "id" -> number,
      "name" -> text,
      "address" -> text,
      "phone" -> text
      )(Client.apply)(Client.unapply))
  
  def list = Action { implicit request =>
    val clients = Client.getAll
    Ok(views.html.clientsListing(clients))
  }
  
  def newClient = Action { implicit request =>
    Ok(views.html.clientForm(clientForm))
  }
  
  def create = Action { implicit request =>
    clientForm.bindFromRequest.fold(
        formWithErrors => BadRequest,
        client => {
          Client.insert(client)
          Redirect(routes.Clients.details(client.id)).flashing("success" -> "Usuario creado exitosamente.")
        }
   )
  }
  
  def details(id: Int) = Action { implicit request =>
    Client.getById(id) match {
      case Some(c) =>
        Ok(views.html.clientDetail(c))
      case None => NotFound("NOT FOUND!")
    }
  }
  
  def delete(id: Int) = Action { implicit request =>
    val deleted = Client.delete(id)
    Redirect(routes.Clients.list)
  }
  
  def edit(id: Int) = Action { implicit request =>
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
           Redirect(routes.Clients.details(id)).flashing("success" -> "Usuario editado exitosamente.")
          }
        )
  }
  
}