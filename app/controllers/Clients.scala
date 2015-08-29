package controllers

import play.api._
import play.api.mvc._
import models.Client

/**
 * @author jesus
 */
object Clients extends Controller {
  
  def list = Action {
    Ok("This should retrieve a list of all clients." + Client.getAll.toString)
  }
  
  def create = Action {
    val n = Client.insert(Client("jesus.martinez", "1234", "Jesús Armando", "Cajica", "3203655304"))
    Ok("This should create a new client. #Clients inserted: " + n)
  }
  
  def details(username: String) = Action {
    val result = Client.getByUsername(username)
    Ok(s"This should retrieve the details of a client identified by $username. This is the result $result")
  }
  
  def delete(username: String) = Action {
    val deleted = Client.delete(username)
    Ok(s"This should redirect to the list of clients after deleting $username. Deletion successful! -> $deleted")
  }
  
  def edit(username: String) = Action {
    Ok(s"This should retrieve a form with the current information of $username available to be updated.")
  }
  
  def update(username: String) = Action {
    val n = Client.update(Client("jesus.martinez", "4321", "MArtínez Vargas", "Bogotá", "8833238"))
    Ok(s"This should update the info of $username and redirect to its details page.")
  }
  
}