package controllers

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import models.User
import models.Client


/**
 * @author jesus
 */
object Login extends Controller {
  private val loginForm = Form(tuple("username" -> text, "password" -> text))
  
  def login = Action {
    Ok(views.html.login(loginForm))
  }
  
  def signin = Action { implicit request =>
    loginForm.bindFromRequest.fold(
        formWithErrors => BadRequest("oH NO!"),
        ok => {
          User.getByUsername(ok._1) match {
            case Some(user) => if (user.password == ok._2) {
              user.role match {
                case "admin" => Ok("This should redirect to admin interface")
                case "client" => Client.getByUsername(user.username) match {
                  case Some(client) => Ok(views.html.clientHome(client)).withSession(
                      "clientId" -> client.id.toString, 
                      "clientAddress" -> client.address, 
                      "clientPhone" -> client.phone,
                      "username" -> user.username)
                  case None => NotFound
                }
              }
            }  else {
              BadRequest
            }
            case None => NotFound
          }
        }
    )
  }
}