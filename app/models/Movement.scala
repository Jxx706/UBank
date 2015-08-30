package models

import anorm.SQL
import java.sql.Timestamp
import anorm.SqlQuery
import play.api.Play.current
import play.api.db.DB
import java.util.Calendar
/**
 * @author jesus
 */
case class Movement(accountNumber: Int, concept: String,  value: Float, date: Timestamp = new Timestamp(Calendar.getInstance().getTimeInMillis)) {
  require(concept == "debit" || concept == "credit")
  require(value > 0.0)
}

object Movement {
  private val SELECT_ALL_BY_ACCOUNT = "select * from movements where a_number={number} order by m_date asc"
  private val INSERT = "insert into movements(a_number, m_type, m_date, m_value) values({number}, {type}, {date}, {value})"
  private val UPDATE_ACCOUNT_BALANCE = """update accounts set a_balance=a_balance{op}{value} where a_number={number}"""
  
  
  /**
   * Gets all movements linked to the account with number 'accountNumber'.
   */
  def getAll(accountNumber: Int): List[Movement] = DB.withConnection { implicit connection =>
    val sql = SQL(SELECT_ALL_BY_ACCOUNT).on("number" -> accountNumber)
    
    sql().map { row =>
      Movement(
          row[Int]("a_number"), 
          row[String]("m_type"), 
          row[String]("m_value").toFloat,
          Timestamp.valueOf(row[String]("m_date")))
    }.toList
  }
  
  /**
   * Inserts a new movement, and updates the balance of the corresponding account.
   */
  def insert(m: Movement): Boolean = DB.withConnection { implicit connection =>
    val rowsInserted = SQL(INSERT).on(
        "number" -> m.accountNumber, 
        "type" -> m.concept, 
        "date" -> m.date, 
        "value" -> m.value).executeUpdate()
        
    val op = m.concept match {
      case "debit" => "+"
      case "credit" => "-"
    }
        
    val rowsUpdated = SQL(UPDATE_ACCOUNT_BALANCE).on(
        "number" -> m.accountNumber,
        "op" -> op,
        "value" -> m.value).executeUpdate()
        
     rowsInserted == 1 && rowsUpdated == 1
  }
  
  //TODO Agregar métodos que hagan JOIN!!! 
}