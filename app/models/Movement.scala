package models

import anorm.SQL
import anorm.SqlQuery
import play.api.Play.current
import play.api.db.DB

/**
 * @author jesus
 */
case class Movement(accountNumber: Int, concept: String, date: java.sql.Date, value: Float) {
  require(concept == "debit" || concept == "credit")
  require(value > 0.0)
}

object Movemente {
  private val SELECT_ALL_BY_ACCOUNT = "select * from movements where a_number={number} order by m_date asc"
}