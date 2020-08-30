package com.example.myapplication

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import net.objecthunter.exp4j.ExpressionBuilder
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException

class MainActivity : AppCompatActivity(), HistoryFragment.Listener {


    private val button0: Button by bind(R.id.button_0)
    private val button1: Button by bind(R.id.button_1)
    private val button2: Button by bind(R.id.button_2)
    private val button3: Button by bind(R.id.button_3)
    private val button4: Button by bind(R.id.button_4)
    private val button5: Button by bind(R.id.button_5)
    private val button6: Button by bind(R.id.button_6)
    private val button7: Button by bind(R.id.button_7)
    private val button8: Button by bind(R.id.button_8)
    private val button9: Button by bind(R.id.button_9)

    private val buttonCE: Button by bind(R.id.button_ce)
    private val buttonC: Button by bind(R.id.button_c)
    private val buttonBackspace: Button by bind(R.id.button_backspace)
    private val buttonDivision: Button by bind(R.id.button_division)
    private val buttonMultiplication: Button by bind(R.id.button_multiplication)
    private val buttonSubtraction: Button by bind(R.id.button_subtraction)
    private val buttonAddition: Button by bind(R.id.button_addition)
    private val buttonEqual: Button by bind(R.id.button_equal)

    private val textViewHistoryText: TextView by bind(R.id.number_history)
    private val textViewCurrentNumber: TextView by bind(R.id.number_current)

    private var isFutureOperationButtonClicked: Boolean = false
    private var isInstantOperationButtonClicked: Boolean = false
    private var isEqualButtonClicked: Boolean = false

    private var currentNumber: Double = 0.0 // Value can be changed.
    private var currentResult: Double = 0.0
    private var memory: Double = 0.0

    private var historyText = "" // Recognize type of variable without declaring it.
    private var historyInstantOperationText = ""
    private var historyActionList: ArrayList<String> = ArrayList()

    private val ZERO: String = "0" // Value cannot be changed.
    private val ONE: String = "1"
    private val TWO: String = "2"
    private val THREE: String = "3"
    private val FOUR: String = "4"
    private val FIVE: String = "5"
    private val SIX: String = "6"
    private val SEVEN: String = "7"
    private val EIGHT: String = "8"
    private val NINE: String = "9"

    private val INIT = ""

    private val ADDITION = " + "
    private val SUBTRACTION = " − "
    private val MULTIPLICATION = " × "
    private val DIVISION = " ÷ "
    private val EQUAL = " = "

    private var currentOperation = INIT


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val name=intent.getStringExtra("uname")

        supportActionBar!!.title=name + "- "+"Calculator"
        // Kotlin gives ability to use functional programming concept - lambda expressions. Thanks to them it's possible to define only actions that we want to do instead of declaring an anonymous class every time for click listener implementation.
        button0.setOnClickListener {
            onNumberButtonClick(ZERO)
        }

        button1.setOnClickListener {
            onNumberButtonClick(ONE)
        }

        button2.setOnClickListener {
            onNumberButtonClick(TWO)
        }

        button3.setOnClickListener {
            onNumberButtonClick(THREE)
        }

        button4.setOnClickListener {
            onNumberButtonClick(FOUR)
        }

        button5.setOnClickListener {
            onNumberButtonClick(FIVE)
        }

        button6.setOnClickListener {
            onNumberButtonClick(SIX)
        }

        button7.setOnClickListener {
            onNumberButtonClick(SEVEN)
        }

        button8.setOnClickListener {
            onNumberButtonClick(EIGHT)
        }

        button9.setOnClickListener {
            onNumberButtonClick(NINE)
        }

        buttonAddition.setOnClickListener {
            onFutureOperationButtonClick(ADDITION)
        }

        buttonSubtraction.setOnClickListener {
            onFutureOperationButtonClick(SUBTRACTION)
        }

        buttonMultiplication.setOnClickListener {
            onFutureOperationButtonClick(MULTIPLICATION)
        }

        buttonDivision.setOnClickListener {
            onFutureOperationButtonClick(DIVISION)
        }

        buttonCE.setOnClickListener {
            clearEntry()
        }

        buttonC.setOnClickListener {
            currentNumber = 0.0
            currentResult = 0.0
            currentOperation = INIT

            historyText = ""
            historyInstantOperationText = ""

            textViewCurrentNumber.text = formatDoubleToString(currentNumber)
            textViewHistoryText.text = historyText

            isFutureOperationButtonClicked = false
            isEqualButtonClicked = false
            isInstantOperationButtonClicked = false
        }

        buttonBackspace.setOnClickListener {

            if (isFutureOperationButtonClicked || isInstantOperationButtonClicked || isEqualButtonClicked) return@setOnClickListener

            var currentValue: String = textViewCurrentNumber.text.toString()

            val charsLimit = if (currentValue.first().isDigit()) 1 else 2

            if (currentValue.length > charsLimit)
                currentValue = currentValue.substring(0, currentValue.length - 1)
            else
                currentValue = ZERO

            textViewCurrentNumber.text = currentValue
            currentNumber = formatStringToDouble(currentValue)
        }

        buttonEqual.setOnClickListener {

            if (isFutureOperationButtonClicked) {
                currentNumber = currentResult
            }

            val historyAllText = calculateResult()

            Toast.makeText(applicationContext, historyAllText, Toast.LENGTH_LONG).show()

            historyActionList.add(historyAllText)

            historyText = StringBuilder().append(formatDoubleToString(currentResult)).toString()

            textViewHistoryText.text = ""

            isFutureOperationButtonClicked = false
            isEqualButtonClicked = true
        }

    }

    @Throws(IllegalArgumentException::class)
    private fun onNumberButtonClick(number: String, isHistory: Boolean = false) {

        var currentValue: String = textViewCurrentNumber.text.toString()
        // In Kotlin there is no more conditional operator ? : like it is in Java, which is used as a shortcut for setting a single variable to one of two states based on a single condition. Here everything can be conveniently done using if..else statement.
        // In Kotlin, using the equality operator == will call the equals method behind the scenes, so it's totaly acceptable to use it for string comparision.
        currentValue = if (currentValue == ZERO || isFutureOperationButtonClicked || isInstantOperationButtonClicked || isEqualButtonClicked || isHistory) number else StringBuilder().append(currentValue).append(number).toString()

        try {
            currentNumber = formatStringToDouble(currentValue)
        } catch (e: ParseException) {
            throw IllegalArgumentException("String must be number.")
        }

        textViewCurrentNumber.text = currentValue

        if (isEqualButtonClicked) {
            currentOperation = INIT
            historyText = ""
        }

        if (isInstantOperationButtonClicked) {
            historyInstantOperationText = ""
            textViewHistoryText.text = StringBuilder().append(historyText).append(currentOperation).toString()
            isInstantOperationButtonClicked = false
        }

        isFutureOperationButtonClicked = false
        isEqualButtonClicked = false
    }

    private fun onFutureOperationButtonClick(operation: String) {

        if (!isFutureOperationButtonClicked && !isEqualButtonClicked) {
            calculateResult()
        }

        currentOperation = operation

        if (isInstantOperationButtonClicked) {
            isInstantOperationButtonClicked = false
            historyText = textViewHistoryText.text.toString()
        }
        textViewHistoryText.text = StringBuilder().append(historyText).append(operation).toString()

        isFutureOperationButtonClicked = true
        isEqualButtonClicked = false
    }

    private fun calculateResult(): String {

        Log.d("operation:",""+currentResult+currentOperation+currentNumber)

        when (currentOperation) {
            INIT -> {
                currentResult = currentNumber
                historyText = StringBuilder().append(textViewHistoryText.text.toString()).toString()
            }
            MULTIPLICATION -> currentResult = currentResult * currentNumber
            ADDITION -> currentResult = currentResult + currentNumber
            DIVISION -> currentResult = currentResult / currentNumber
            SUBTRACTION -> currentResult = currentResult - currentNumber

        }

        textViewCurrentNumber.text = formatDoubleToString(currentResult)

        if (isInstantOperationButtonClicked) {
            isInstantOperationButtonClicked = false
            historyText = textViewHistoryText.text.toString()
            if (isEqualButtonClicked) historyText = StringBuilder().append(historyText).append(currentOperation).append(formatDoubleToString(currentNumber)).toString()
        } else {
            historyText = StringBuilder().append(historyText).append(currentOperation).append(formatDoubleToString(currentNumber)).toString()
        }

        return StringBuilder().append(historyText).append(EQUAL).append(formatDoubleToString(currentResult)).toString()
    }

    private fun useNumberFormat(): DecimalFormat {

        val symbols = DecimalFormatSymbols()
        symbols.decimalSeparator = ','

        val format = DecimalFormat("#.##############")
        format.decimalFormatSymbols = symbols

        return format
    }

    private fun formatDoubleToString(number: Double): String {
        return useNumberFormat().format(number)
    }

    private fun formatStringToDouble(number: String): Double {
        return useNumberFormat().parse(number).toDouble()
    }

    // Extension property provides similar mechanism.
    // Note that you have to define a getter method on your property for this to work.
    private val Double.sqrt: Double get() = Math.sqrt(this)

    private fun clearEntry(newNumber: Double = 0.0) {
        historyInstantOperationText = ""

        if (isEqualButtonClicked) {
            currentOperation = INIT
            historyText = ""
        }

        if (isInstantOperationButtonClicked) textViewHistoryText.text = StringBuilder().append(historyText).append(currentOperation).toString()

        isInstantOperationButtonClicked = false
        isFutureOperationButtonClicked = false
        isEqualButtonClicked = false

        currentNumber = newNumber
        textViewCurrentNumber.text = formatDoubleToString(newNumber)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Functions are defined using the “fun” keyword.
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Safe call operator ? added to the variable before invoking the property instructs the compiler to invoke the property only if the value isn't null.
        when (item?.itemId) {
            R.id.menu_item_history -> {
                HistoryFragment.newInstance(historyActionList).show(getSupportFragmentManager(), "dialog")
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    override fun onHistoryItemClicked(resultText: String) {

        try {
            onNumberButtonClick(resultText, true)
        } catch (e: IllegalArgumentException) {
            return
        }

        Toast.makeText(applicationContext, getString(R.string.history_result) + resultText, Toast.LENGTH_SHORT).show()
    }

    // Extension function created to add special behaviour to our Activity.
    // Here keyword lazy means it won’t be initialised right away but the first time the value is actually needed.
    fun <T : View> Activity.bind(@IdRes idRes: Int): Lazy<T> {
        // Function will be called only by the main thread to improve performance.
        return lazy(LazyThreadSafetyMode.NONE) { findViewById<T>(idRes) }
    }
}