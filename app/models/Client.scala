package models

import anorm.SQL
import anorm.SqlQuery
import play.api.Play.current
import play.api.db.DB

/**
 * @author jesus
 */
case class Client(username: String, password: String, name: String, address: String, phone: String)

object Client {
  //TODO Meter estos queries en application.conf
  private val SELECT_ALL = """select * from clients"""
  private val SELECT_BY_USERNAME = "select * from clients where c_username={username}"
  private val INSERT = 
    """insert into
       clients(c_username, c_password, c_name, c_address, c_phone) 
       values({username}, {password}, {name}, {address}, {phone})"""
  private val DELETE = 
    """delete from clients where c_username={username}"""
  private val UPDATE = """update clients 
    set c_password={password},
    c_name={name},
    c_address={address},
    c_phone={phone}
    where c_username={username}"""
  
  /**
   * Retrieves all records in table 'clients'.
   */
  def getAll: List[Client] = DB.withConnection { implicit connection =>
    val sql = SQL(SELECT_ALL) 
    sql().map { row => 
      Client(row[String]("c_username"),row[String]("c_password"),row[String]("c_name"),row[String]("c_address"),row[String]("c_phone")) 
    }.toList
  }
  
  /**
   * Retrieves the information of a client identified with 'username'.
   */
  def getByUsername(username: String): Option[Client] = DB.withConnection { implicit connection =>
    val sql = SQL(SELECT_BY_USERNAME).on("username" -> username)
    
    val resultList = sql().map { row =>
        Client(row[String]("c_username"),row[String]("c_password"),row[String]("c_name"),row[String]("c_address"),row[String]("c_phone"))
      }.toList
      
      resultList match {
        case Nil => None
        case x :: _ => Some(x)
      }
    }
  
  /**
   * Inserts a new record in 'clients' table.
   */
  def insert(c: Client): Boolean = DB.withConnection { implicit connection => 
    val rowsInserted = SQL(INSERT).on(
        "username" -> c.username, 
        "password" -> c.password,
        "name" -> c.name, 
        "address" -> c.address, 
        "phone" -> c.phone).executeUpdate()
        
        rowsInserted == 1
  }
  
  def update(c: Client): Boolean = DB.withConnection { implicit connection =>
    val rowsAffected = SQL(UPDATE).on(
        "username" -> c.username, 
        "password" -> c.password,
        "name" -> c.name, 
        "address" -> c.address, 
        "phone" -> c.phone).executeUpdate()
        
        rowsAffected == 1
    }
  
  
  /**
   * Deletes a client identified with 'username'.
   */
  def delete(username: String): Int = DB.withConnection { implicit connection => SQL(DELETE).on("username" -> username).executeUpdate() } 
}