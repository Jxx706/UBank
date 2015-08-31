package controllers

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import models.User
import models.Client


/**
 * @author Jesús Martínez
 */
object Login extends Controller {
  private val loginForm = Form(tuple("username" -> text, "password" -> text))
  
  def login = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  def logout = Action {
    Redirect(routes.Login.login).withNewSession.discardingCookies()
  }
  
  def signin = Action { implicit request =>
    val errorMsg: String = "Combinación de nombre de usuario y/o contraseña incorrecta."
    loginForm.bindFromRequest.fold(
        formWithErrors => BadRequest("oH NO!"),
        ok => {
          User.getByUsername(ok._1) match {
            case Some(user) => if (user.password == ok._2) {
              user.role match {
                case "admin" => Redirect(routes.Clients.list).withSession("username" -> user.username, "role" -> user.role )
                case "client" => Client.getByUsername(user.username) match {
                  case Some(client) => Redirect(routes.Accounts.list(client.id)).withSession(
                      "id" -> client.id.toString,
                      "name" -> client.name,
                      "address" -> client.address,
                      "phone" -> client.phone)
                  case None => InternalServerError
                }
              }
            }  else {
              Redirect(routes.Login.login).flashing("error" -> errorMsg )
            }
            case None => Redirect(routes.Login.login).flashing("error" -> errorMsg)
          }
        }
    )
  }
}