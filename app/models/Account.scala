package models

import anorm.SQL
import anorm.SqlQuery
import play.api.Play.current
import play.api.db.DB

/**
 * @author jesus
 */
case class Account(clientUsername: String, number: Int, balance: Float)

object Account {
    //TODO Meter estos queries en application.conf
  private val SELECT_ALL = """select * from accounts"""
  private val SELECT_ALL_BY_USERNAME = "select * from accounts where c_username={username}"
  private val INSERT = 
    """insert into
       accounts(c_username, a_number, a_balance) 
       values({username}, {number}, {balance})"""
  private val DELETE = 
    """delete from accounts where c_username={username} and a_number={number}"""
  private val UPDATE = """update accounts 
    set a_balance={balance},
    where c_username={username} and a_number={number}"""
  
  
  def getAll: List[Account] = DB.withConnection { implicit connection =>
    val sql = SQL(SELECT_ALL)
    
    sql().map { row => 
      Account(row[String]("c_username"), row[Int]("a_number"), row[String]("a_balance").toFloat)
    }.toList
  }
  
  def insert(a: Account): Boolean = DB.withConnection { implicit connection =>
    val rowsInserted = SQL(INSERT).on(
        "username" -> a.clientUsername, 
        "number" -> a.number,
        "balance" -> a.balance).executeUpdate()
    
    rowsInserted == 1
  }
  
  def delete(username: String, number: Int): Boolean = DB.withConnection { implicit connection =>
    val rowsAffected = SQL(DELETE).on("username" -> username, "number" -> number).executeUpdate()
    
    rowsAffected == 0
  }
  
  def update(a: Account): Boolean = DB.withConnection { implicit connection =>
    val rowsUpdated = SQL(UPDATE).on(
        "username" -> a.clientUsername,
        "number" -> a.number, 
        "balance" -> a.balance).executeUpdate() 
     
        rowsUpdated == 1
  }
  
  //TODO FALTA AGREGAR MÁS MÉTODOS.
  
}