import scala.scalajs.js.JSApp

import org.scalajs.dom.document
import org.scalajs.dom.raw.{ Event, HTMLInputElement }
import com.thoughtworks.binding.{ Binding, dom }
import com.thoughtworks.binding.Binding.{ Var, Vars }

object MainApp extends JSApp {

  case class Deposit(period: Int, interest: Double) {
    def gain(amount: Double, duration: Int) = {
      val interestPerPeriod = interest * (period / 12.0)
      val fullPeriods       = duration / period
      val finalBalance      = amount * math.pow(1 + interestPerPeriod, fullPeriods)

      finalBalance - amount
    }
  }

  val amount   : Var[Double]   = Var(10000.0)
  val duration : Var[Int]      = Var(12)
  val deposits : Vars[Deposit] = Vars(Deposit(1, 0.02), Deposit(3, 0.025), Deposit(12, 0.03))

  @dom def renderMainContainer(amount: Var[Double], duration: Var[Int], deposits: Vars[Deposit]) = {
    <div>
      { renderAmountInput(amount).bind }
      { renderDurationInput(duration).bind }
      { renderDepositsTable(amount, duration, deposits).bind }
    </div>
  }

  @dom def renderAmountInput(amount: Var[Double]) = {
    <input type="number" id="amountInput"
      value={ amount.bind.toString }
      onchange={ (e: Event) => amount := amountInput.value.toDouble }
      />
  }

  @dom def renderDurationInput(duration: Var[Int]) = {
    <input type="number" id="durationInput"
      value={ duration.bind.toString }
      onchange={ (e: Event) => duration := durationInput.value.toInt }
      />
  }

  @dom def renderDepositsTable(amount: Binding[Double], duration: Binding[Int], deposits: Vars[Deposit]) = {
    <table>
      { deposits.map(renderDeposit(_, amount, duration).bind) }
    </table>
  }

  @dom def renderDeposit(deposit: Deposit, amount: Binding[Double], duration: Binding[Int]) = {
    <tr>
      <td>{ deposit.period.toString }</td>
      <td>{ (deposit.interest * 100).toString }%</td>
      <td>
        {
          val gain = deposit.gain(amount.bind, duration.bind)
          f"${gain}%.2f"
        }
      </td>
    </tr>
  }

  def main(): Unit = {
    dom.render(document.getElementById("mainContainer"),
               renderMainContainer(amount, duration, deposits))
  }

}
