package models

import anorm.SQL
import anorm.SqlQuery
import play.api.Play.current
import play.api.db.DB

/**
 * @author jesus
 */
case class Account(clientId: Int, number: Int, balance: Double)

object Account {
    //TODO Meter estos queries en application.conf
  private val SELECT_ALL = """select * from accounts"""
  private val SELECT_BY_CLIENT_AND_NUMBER = """select * from accounts where c_id={id} and a_number={number}"""
  private val SELECT_ALL_BY_CLIENT = "select * from accounts where c_id={id}"
  private val INSERT = 
    """insert into
       accounts(c_id, a_number, a_balance) 
       values({id}, {number}, {balance})"""
  private val DELETE = 
    """delete from accounts where c_id={id} and a_number={number}"""
  //TODO Esto no debe usarse aquí.
  private val UPDATE = """update accounts 
    set a_balance={balance}
    where c_id={id} and a_number={number}"""
  
  /**
   * Gets all accounts
   */
  def getAll: List[Account] = DB.withConnection { implicit connection =>
    val sql = SQL(SELECT_ALL)
    
    sql().map { row => 
      Account(row[Int]("c_id"), row[Int]("a_number"), row[Double]("a_balance"))
    }.toList
  }
  
  def getByClientAndNumber(clientId: Int, number: Int): Option[Account] = 
    DB.withConnection { implicit connection =>
      val sql = SQL(SELECT_BY_CLIENT_AND_NUMBER).on("id" -> clientId, "number" -> number)
      
      sql().map { row =>
        Account(row[Int]("c_id"), row[Int]("a_number"), row[Double]("a_balance"))
      }.toList match {
        case Nil => None
        case a :: _ => Some(a)
      }
    }
  
  def getByClient(clientId: Int): List[Account] = DB.withConnection { implicit connection =>
    val sql = SQL(SELECT_ALL_BY_CLIENT).on("id" -> clientId)
    
    sql().map { row =>
      Account(row[Int]("c_id"), row[Int]("a_number"), row[Double]("a_balance"))
    }.toList
  }
  
  //def getByClientWithMovements(username: String): (Account, List[models.Movement]) = 
    //DB.withConnection { implicit connection => 
      //val sql = SQL(SELECT_ACCOUNT_WITH_MOVEMENTS).on("username" -> username)
      
      //sql().map { row =>
         
      //}
    //}
  
  def insert(a: Account): Boolean = DB.withConnection { implicit connection =>
    val rowsInserted = SQL(INSERT).on(
        "id" -> a.clientId, 
        "number" -> a.number,
        "balance" -> a.balance).executeUpdate()
    
    rowsInserted == 1
  }
  
  /**
   * Erases an account identified with 'number' and belonging to 'username'.
   */
  def delete(clientId: Int, number: Int): Boolean = DB.withConnection { implicit connection =>
    val rowsAffected = SQL(DELETE).on("id" -> clientId, "number" -> number).executeUpdate()
    
    rowsAffected == 0
  }

  def update(a: Account): Boolean = DB.withConnection { implicit connection =>
    val rowsUpdated = SQL(UPDATE).on(
        "id" -> a.clientId,
        "number" -> a.number, 
        "balance" -> a.balance).executeUpdate() 
     
        rowsUpdated == 1
  }
  
  //TODO FALTA AGREGAR MÁS MÉTODOS.
  
}