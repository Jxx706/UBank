package controllers

import models.Client
import play.api.mvc.RequestHeader

/**
 * Created by Maris on 30/08/2015.
 */
trait WithClient {
  implicit def client(implicit request: RequestHeader) = {
    val session = request.session

    val id = session.get("id").get.toInt
    val name = session.get("name").get
    val address = session.get("address").get
    val phone = session.get("phone").get

    Client(id, name, address, phone)
  }
}
