package controllers

import java.util.Date

import models.{Movement, Account}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsArray, Json}
import play.api.mvc.{Action, Controller}

/**
 * Created by Jesús Martínez on 30/08/2015.
 */
object Reports extends Controller {
  //Form mapping
  val  reportForm = Form(tuple("client_id" -> number, "min_date" -> date, "max_date" -> date))

  //It results a new empty form for querying a report.
  def newReport(clientId: Int) = Action {
    Ok(views.html.reportForm(clientId, reportForm))
  }

  //Given some input, return a list of pairs, each containg a tuple of debits and credits on one field, and the actual
  //account in the other.
  def reportData = Action { implicit request =>
    reportForm.bindFromRequest.fold(
      hasErrors => BadRequest,
      tuple => {
        val id: Int = tuple._1
        val minDate: Date = tuple._2
        val maxDate: Date = tuple._3

        val accounts = Account.getByClient(id)

        val results: List[((List[Movement], List[Movement]), Account)] = accounts.map {
          a => Movement.getAll(a.number).filter(m => minDate.compareTo(m.date) <= 0 && maxDate.compareTo(m.date) >= 0).span(m => m.concept == "debit")
        }.zip(accounts)

        Ok(views.html.reportResults(results, id))
      }
    )
  }

  def monthlyBalanceData(clientId: Int, account: Int) = Action {
    val acc = Account.getByClientAndNumber(clientId, account)

    acc match {
      case None => NotFound
      case Some(a) =>
        val grouped: List[(Int, List[Movement])] = Movement.getAll(a.number).groupBy(m => m.date.getMonth).toList

        def getTotalBalance(ms: List[Movement]): Double = ms.foldLeft(0d) {
          (a, e) => e.concept match {
            case "debit" => a + e.value
            case "credit" => a - e.value
          }
        }

        val monthlyBalance: List[(Int, Double)] = grouped.map( tuple => (tuple._1 + 1, getTotalBalance(tuple._2))).sortWith(lt = (t1, t2) => t1._1 < t2._1)

        Ok(JsArray(monthlyBalance.map(t => Json.obj("month" -> t._1, "balance" -> t._2))))
    }
  }

}
