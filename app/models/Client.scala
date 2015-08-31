package models

import anorm.SQL
import anorm.SqlQuery
import play.api.Play.current
import play.api.db.DB

/**
 * @author jesus
 */
case class Client(id: Int, name: String, address: String, phone: String)

object Client {
  //TODO Meter estos queries en application.conf
  private val SELECT_ALL = """select * from clients"""
  private val SELECT_BY_ID = "select * from clients where c_id={id}"
  private val SELECT_BY_USERNAME = "select * from clients c where u_username={username}"
  private val INSERT = 
    """insert into
       clients(u_username, c_id, c_name, c_address, c_phone)
       values({username}, {id}, {name}, {address}, {phone})"""
  private val DELETE = 
    """delete from clients where c_id={id}"""
  private val UPDATE = """update clients 
    set c_name={name},
    c_address={address},
    c_phone={phone}
    where c_id={id}"""
  

  /**
   * Retrieves all records in table 'clients'.
   */
  def getAll: List[Client] = DB.withConnection { implicit connection =>
    val sql = SQL(SELECT_ALL) 
    sql().map { row => 
      Client(row[Int]("c_id"), row[String]("c_name"),row[String]("c_address"),row[String]("c_phone")) 
    }.toList
  }
  
  /**
   * Retrieves the information of a client identified with 'id'.
   */
  def getById(id: Int): Option[Client] = DB.withConnection { implicit connection =>
    val sql = SQL(SELECT_BY_ID).on("id" -> id)
    
    sql().map { row =>
        Client(row[Int]("c_id"),row[String]("c_name"),row[String]("c_address"),row[String]("c_phone"))
      }.toList  match {
        case Nil => None
        case x :: _ => Some(x)
      }
    }
  
  def getByUsername(username: String): Option[Client] = DB.withConnection { implicit connection =>
    val sql = SQL(SELECT_BY_USERNAME).on("username" -> username)
    
    sql().map { row =>
        Client(row[Int]("c_id"),row[String]("c_name"),row[String]("c_address"),row[String]("c_phone"))
      }.toList  match {
        case Nil => None
        case x :: _ => Some(x)
      }
    }
  
  /**
   * Inserts a new record in 'clients' table.
   */
  def insert(c: Client, username: String): Boolean = DB.withConnection { implicit connection =>
    val rowsInserted = SQL(INSERT).on(
        "username" -> username,
        "id" -> c.id,
        "name" -> c.name, 
        "address" -> c.address, 
        "phone" -> c.phone).executeUpdate()
        
        rowsInserted == 1
  }
  
  /**
   * Update de data associated to a client.
   */
  def update(c: Client): Boolean = DB.withConnection { implicit connection =>
    val rowsAffected = SQL(UPDATE).on(
        "id" -> c.id, 
        "name" -> c.name, 
        "address" -> c.address, 
        "phone" -> c.phone).executeUpdate()
        
        rowsAffected == 1
    }
  
  
  /**
   * Deletes a client identified with 'id'.
   */
  def delete(id: Int): Boolean = DB.withConnection { implicit connection =>
    SQL(SELECT_BY_ID).on("id" -> id)().map { row =>
      row[String]("c_username")
    }.toList match {
      case Nil => false
      case username :: _ =>
        SQL(DELETE).on("id" -> id).executeUpdate() == 0 &&
        User.delete(username)
    }
  }
}