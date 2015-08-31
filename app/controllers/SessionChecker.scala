package controllers

import play.api.mvc.RequestHeader

/**
 * Created by Maris on 30/08/2015.
 */
trait SessionChecker {
  /**
   *  Check if the current session belong to an admin user.
   * @param request Request header.
   * @return True if the sessions belongs to an admin user; false if not
   */
  protected def isAdmin(implicit request: RequestHeader) = {
    val roleHeader = request.session.get("role")
    roleHeader.isDefined && roleHeader.get == "admin"
  }
}
