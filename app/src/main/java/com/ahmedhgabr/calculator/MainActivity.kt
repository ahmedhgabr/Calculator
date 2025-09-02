package com.ahmedhgabr.calculator

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ahmedhgabr.calculator.databinding.ActivityMainBinding
import java.math.MathContext

class MainActivity : AppCompatActivity() {

    lateinit var textViewInput: TextView
    lateinit var textViewHistory: TextView

    lateinit var binding: ActivityMainBinding

    var isDecimal: Boolean = false
    val operations = listOf('+', '-', 'x', '/', '%', '±')
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initializeViews()
    }

    fun initializeViews() {
        textViewInput = binding.textViewResult
        textViewHistory = binding.textViewEquation
    }

    fun onClickNumber(view: View) {
        val button = view as androidx.appcompat.widget.AppCompatButton
        val btnValue = button.text.toString()
        when (btnValue) {
            "." -> {
                if (!isDecimal) {
                    isDecimal = true
                } else {
                    return
                }
            }

            else -> {
                // number clicked
            }
        }
        addToEquation(btnValue)
    }

    fun onClickOperation(view: View) {
        val button = view as androidx.appcompat.widget.AppCompatButton
        val btnValue = button.text.toString()
        if (textViewInput.text.toString().last() in operations) {
            return
        }
        isDecimal = false
        if (btnValue == "%") {
            var lastNumber = ""
            var i = 0
            textViewInput.text.toString().forEach { it ->
                if (it in operations) {
                    return@forEach
                }
                i += 1
            }
            lastNumber = textViewInput.text.toString().takeLast(i)
            if (lastNumber.isEmpty()) {
                return
            }
            val changedSign = lastNumber.toDouble() / 100
            var newEquation = textViewInput.text.toString().dropLast(i)+ changedSign.toStringTrimmed()
            textViewInput.text = newEquation
            return
        }
        else if (btnValue == "±") {
            // change the sign of the last number
            var lastNumber = ""
            var i = 0
            textViewInput.text.toString().forEach { it ->
                if (it in operations) {
                    return@forEach
                }
                i += 1
            }
            lastNumber = textViewInput.text.toString().takeLast(i)
            if (lastNumber.isEmpty()) {
                return
            }
            val changedSign = -1 * lastNumber.toDouble()
            var newEquation = ""
            if (changedSign > 0)
                newEquation = textViewInput.text.toString().dropLast(i)+ "+"+ changedSign.toStringTrimmed()
            else
                newEquation = textViewInput.text.toString().dropLast(i)+ changedSign.toStringTrimmed()
            textViewInput.text = newEquation
            return
        }
        addToEquation(btnValue)
    }


    fun onClickEquals(view: View) {
        val equation = textViewInput.text.toString()
        try {
            val result = evaluateExpression(equation)
            textViewHistory.text = equation
            val resultDouble = result.toStringTrimmed()
            textViewInput.text = resultDouble
            isDecimal = resultDouble.contains(".")
        } catch (e: Exception) {
            textViewInput.text = "Error"
        }
    }

    /*
    * it removes unnecessary .0 from the end of a double number
    * */
    fun Double.toStringTrimmed(): String {
        return if (this % 1.0 == 0.0) {
            this.toInt().toString()
        } else {
            this.toString()
        }
    }

//    private fun evaluateExpression(equation: String): Double {
//        // 2+5*3
//        if (equation.isEmpty()) {
//            return 0.0
//        }
//        var currentNumber = ""
//        var currentOperation: Char? = null
//        for (char in equation) {
//            if (char.isDigit() || char == '.') {
//                currentNumber += char
//            } else {
//                currentOperation = char
//                break
//            }
//        }
//        val leftEquation = equation.drop(currentNumber.length)
//        val rightEquation = equation.drop(currentNumber.length + 1)
//        return when (currentOperation) {
//            '*' -> evaluateExpression(leftEquation) * evaluateExpression(rightEquation)
//            '/' -> evaluateExpression(leftEquation) / evaluateExpression(rightEquation)
//            '+' -> evaluateExpression(leftEquation) + evaluateExpression(rightEquation)
//            '-' -> evaluateExpression(leftEquation) - evaluateExpression(rightEquation)
//            else -> 0.0
//        }
//    }


    private fun evaluateExpression(equation: String): Double {
        // 2+1+3
        var output = 0.0
        var currentNumber = ""
        var currentOperation: Char? = null

        for (char in equation) {
            when {
                char.isDigit() || char == '.' -> {
                    currentNumber += char
                }

                char == '+' || char == '-' || char == 'x' || char == '/' || char == '%' -> {
                    if (currentNumber.isNotEmpty()) {
                        val number = currentNumber.toDouble()
                        output = when (currentOperation) {
                            '+' -> output + number
                            '-' -> output - number
                            'x' -> output * number
                            '/' -> {
                                if (number == 0.0) {
                                    throw ArithmeticException("Division by zero")
                                }
                                output / number
                            }
                            '%' -> output % number
                            null -> number
                            else -> output
                        }
                        currentNumber = ""
                    }
                    currentOperation = char
                }
            }
        }

        if (currentNumber.isNotEmpty()) {
            val number = currentNumber.toDouble()
            output = when (currentOperation) {
                '+' -> output + number
                '-' -> output - number
                'x' -> output * number
                '/' -> {
                    if (number == 0.0) {
                        throw ArithmeticException("Division by zero")
                    }
                    output / number
                }
                '%' -> output % number
                null -> number
                else -> output
            }
        }

        return output
    }

    fun addToEquation(value: String) {
        val textview = textViewInput
        val old = textview.text.toString()
        if (old == "0") {
            textview.text = value.toString()
        } else if (old == "Error") {
            textview.text = value.toString()
        } else {
            val newText = old + value
            textview.text = newText
        }
    }

    fun onClickClear(view: View) {
        textViewInput.text = "0"
        textViewHistory.text = ""
    }

    fun onClickBackspace(view: View) {
        val textview = textViewInput
        val old = textview.text.toString()
        if (old.isNotEmpty()) {
            val newText = old.dropLast(1)
            if (newText.isEmpty()) {
                textview.text = "0"
            } else {
                textview.text = newText
            }
        }
    }


}