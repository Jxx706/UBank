package controllers

import play.api._
import play.api.mvc._
import models.Client
import play.api.data._
import play.api.data.Forms._
import models.User
/**
 * @author Jesús Martínez
 */
object Clients extends Controller with WithClient with SessionChecker {

  //Mapping for the first time thant a user its created. This form
  //includes both the info required for the client entity as for the user entity.
  private val clientWithUserForm = Form(tuple(
    "id" -> number,
    "name" -> text
      .verifying("El nombre no puede ir vacío.", _.nonEmpty),
    "address" -> text
      .verifying("La dirección no puede ir vacía", _.nonEmpty),
    "phone" -> text
      .verifying("El teléfono no puede ir vació", _.nonEmpty)
      .verifying("Sólo se admiten números en el teléfono", _.forall(_.isDigit)),
    "username" -> text
      .verifying("El nombre de usuario no puede ir vacío", _.nonEmpty),
    "password" -> text
      .verifying("Debe contener entre 6 y 20 caracteres", p => 6 <= p.length && p.length <= 20),
    "password_confirmation" -> text
      .verifying("Debe contener entre 6 y 20 caracteres", p => 6 <= p.length && p.length <= 20),
    "role" -> text))

  //Mapping for the edition of a regular client of the platform.
  private val clientForm = Form(mapping(
      "id" -> number,
      "name" -> text
        .verifying("El nombre no puede ir vacío.", _.nonEmpty),
      "address" -> text
        .verifying("La dirección no puede ir vacía", _.nonEmpty),
      "phone" -> text
        .verifying("El teléfono no puede ir vació", _.nonEmpty)
        .verifying("Sólo se admiten números en el teléfono", _.forall(_.isDigit))
      )(Client.apply)(Client.unapply))


  def list = Action { implicit request =>
    val role = request.session.get("role").getOrElse("")
    val clients = Client.getAll

    Ok(views.html.clientsListing(clients, role))
  }
  
  def newClient = Action { implicit request =>
    Ok(views.html.newClientForm(clientWithUserForm))
  }

  /**
   * Creates a new client, including his or her login information.
   */
  def create = Action { implicit request =>
    clientWithUserForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.newClientForm(formWithErrors)),
      t => {
        val clientId = t._1
        val name = t._2
        val address = t._3
        val phone = t._4
        val pass = t._7
        val passConfirmation = t._6
        val username = t._5
        val role = t._8

        if (pass != passConfirmation) {
          //TODO!
          Ok("TODO!")
        } else {
          val u = User(username, pass, role)
          val c = Client(clientId, name, address, phone)

          User.insert(u)
          Client.insert(c, u.username)

          Redirect(routes.Clients.details(c.id)).flashing("success" -> "Usuario creado exitosamente.")
        }
      }
    )
  }

  /**
   * Show the details of a client.
   */
  def details(id: Int) = Action { implicit request =>
    Client.getById(id) match {
      case Some(c) =>

        if (isAdmin)
          Ok(views.html.clientDetailAdmin(c, "admin"))
        else
          Ok(views.html.clientDetail(c))
      case None => NotFound("NOT FOUND!")
    }
  }

  /**
   * Deletes a client with all his accounts and its movements.
   */
  def delete(id: Int) = Action { implicit request =>
    Client.delete(id)
    Redirect(routes.Clients.list)
  }

  /**
   * Renders a form pre-populated with the current information of the user being edited.
   */
  def edit(id: Int) = Action { implicit request =>
    Client.getById(id) match {
      case None => NotFound
      case Some(c) =>
        Ok(views.html.clientForm(clientForm.fill(c)))
    }
    
  }

  /**
   * Updates the data associated to a user.
   */
  def update(id: Int) = Action { implicit request =>
    clientForm.bindFromRequest.fold(
        formWithErrors =>
          Client.getById(id) match {
            case None => NotFound
            case Some(c) => BadRequest(views.html.clientForm(formWithErrors.fill(c)))
          },
        client => {
           Client.update(client.copy(id = id))

          session +  ("id" -> id.toString)
          session + ("name" -> client.name)
          session + ("address" -> client.address)
          session + ("phone" -> client.phone)

           Redirect(routes.Clients.details(id)).
             withSession(session).
             flashing("success" -> "Usuario editado exitosamente.")
          }
        )
  }
  
}