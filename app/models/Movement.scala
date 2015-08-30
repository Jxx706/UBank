package models

import anorm.SQL
import java.sql.Timestamp
import anorm.SqlQuery
import play.api.Play.current
import play.api.db.DB
import java.util.{Date, Calendar}
/**
 * @author jesus
 */
case class Movement(accountNumber: Int, concept: String,  value: Double, date: Date = Calendar.getInstance().getTime) {
  require(concept == "debit" || concept == "credit")
  require(value > 0.0)
}

object Movement {
  private val SELECT_ALL_BY_ACCOUNT = "select * from movements where a_number={number} order by m_date desc"
  private val INSERT = "insert into movements(a_number, m_type, m_date, m_value) values({number}, {type}, {date}, {value})"
  
  
  /**
   * Gets all movements linked to the account with number 'accountNumber'.
   */
  def getAll(accountNumber: Int): List[Movement] = DB.withConnection { implicit connection =>
    val sql = SQL(SELECT_ALL_BY_ACCOUNT).on("number" -> accountNumber)
    
    sql().map { row =>
      Movement(
          row[Int]("a_number"), 
          row[String]("m_type"), 
          row[Double]("m_value"),
          new Date(row[Long]("m_date")))
    }.toList
  }
  
  /**
   * Inserts a new movement, and updates the balance of the corresponding account.
   */
  def insert(m: Movement): Boolean = DB.withConnection { implicit connection =>
    val rowsInserted = SQL(INSERT).on(
        "number" -> m.accountNumber, 
        "type" -> m.concept, 
        "date" -> m.date.getTime,
        "value" -> m.value).executeUpdate()
        
     rowsInserted == 1
  }
  
  //TODO Agregar métodos que hagan JOIN!!! 
}