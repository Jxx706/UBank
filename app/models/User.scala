package models

import anorm.SQL
import anorm.SqlQuery
import play.api.Play.current
import play.api.db.DB

/**
 * @author jesus
 */
case class User(username: String, password: String, role: String) {
  require(role == "admin" || role == "client")
}

object User {
  private val SELECT_BY_USERNAME = "select * from users where u_username={username}"
  private val INSERT = "insert into users(u_username, u_password, u_role) values({username}, {password}, {role})"
  private val DELETE = "delete from users where u_username={username}"
 
  def getByUsername(username: String): Option[User] = DB.withConnection { implicit connection =>
    val sql = SQL(SELECT_BY_USERNAME).on("username" -> username)
    
    sql().map { row =>
        User(row[String]("u_username"), row[String]("u_password"), row[String]("u_role"))
      }.toList match {
        case Nil => None
        case u :: _ => Some(u)
      }
  }
  
  def insert(u: User): Boolean = DB.withConnection { implicit connection =>
    val rowsInserted = SQL(INSERT).on(
        "username" -> u.username, 
        "password" -> u.password,
        "role" -> u.role).executeUpdate()
    
    rowsInserted == 1
  }

  def delete(username: String): Boolean = DB.withConnection { implicit  connection =>
    val rowsAffected = SQL(DELETE).on("username" -> username).executeUpdate()

    rowsAffected == 0
  }
}