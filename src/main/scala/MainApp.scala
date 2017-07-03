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
    <div class="row">
      { renderAmountInput(amount).bind }
      { renderDurationInput(duration).bind }
      { renderDepositsTable(amount, duration, deposits).bind }
    </div>
  }

  @dom def renderAmountInput(amount: Var[Double]) = {
    <div class="col s12 m6 input-field">
      <input type="number" id="amountInput" min="0" step="0.01"
        value={ amount.bind.toString }
        onchange={ (e: Event) => amount := amountInput.value.toDouble }
        onmouseup={ (e: Event) => amount := amountInput.value.toDouble }
        />
      <label for="amountInput" class="active">Deposit Amount ($)</label>
    </div>
  }

  @dom def renderDurationInput(duration: Var[Int]) = {
    <div class="col s12 m6 input-field">
      <input type="number" id="durationInput" min="0" step="1"
        value={ duration.bind.toString }
        onchange={ (e: Event) => duration := durationInput.value.toInt }
        onmouseup={ (e: Event) => duration := durationInput.value.toInt }
      />
      <label for="durationInput" class="active">Deposit Duration (months)</label>
    </div>
  }

  @dom def renderDepositsTable(amount: Binding[Double], duration: Binding[Int], deposits: Vars[Deposit]) = {
    <table>
      <thead>
        <tr>
          <th>Period (months)</th>
          <th>Interest Rate (%/year)</th>
          <th>Gain ($)</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        {
          deposits.map { deposit =>
            renderDeposit(
              deposit, amount, duration,
              onDelete = { deposits.get -= _ }
            ).bind
          }
        }
        { renderDepositInput(deposits).bind }
      </tbody>
    </table>
  }

  @dom def renderDeposit(deposit: Deposit, amount: Binding[Double], duration: Binding[Int], onDelete: Deposit => Unit) = {
    <tr>
      <td>{ deposit.period.toString }</td>
      <td>{ (deposit.interest * 100).toString }%</td>
      <td>
        {
          val gain = deposit.gain(amount.bind, duration.bind)
          f"${gain}%.2f"
        }
      </td>
      <td>
        <button class="btn" onclick={ (e: Event) => onDelete(deposit) }>
          <i class="material-icons">delete_forever</i>
        </button>
      </td>
    </tr>
  }

  @dom def renderDepositInput(deposits: Vars[Deposit]) = {
    def onSubmit(periodInput: HTMLInputElement, interestInput: HTMLInputElement)(e: Event) = {
      val period = periodInput.value.toInt
      val interest = interestInput.value.toDouble / 100
      deposits.get += Deposit(period, interest)
      periodInput.value = ""
      interestInput.value = ""
      periodInput.focus
      false
    }

    <tr>
      <td><input type="number" id="periodInput" data:form="newDeposit" min="1" step="1"/></td>
      <td><input type="number" id="interestInput" data:form="newDeposit" min="0" step="0.01"/></td>
      <td></td>
      <td>
        <form id="newDeposit" onsubmit={ onSubmit(periodInput, interestInput) _ }>
          <button type="submit" class="btn" onclick={ onSubmit(periodInput, interestInput) _ }>
            <i class="material-icons">playlist_add</i>
          </button>
        </form>
      </td>
    </tr>
  }

  def main(): Unit = {
    dom.render(document.getElementById("mainContainer"),
               renderMainContainer(amount, duration, deposits))
  }

}
