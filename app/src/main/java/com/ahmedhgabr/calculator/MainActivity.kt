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
        addToEquation(btnValue)
        if (btnValue == "%") {
            Log.d("TAG", "onClickOperation: %")
            val equation = binding.textViewEquation.text.toString()
            val lastNumber = equation.takeLastWhile { it.isDigit() || it == '.' }
            if (lastNumber.isNotEmpty()) {
                val percentageValue = lastNumber.toDouble() / 100
                val newEquation =
                    equation.dropLast(lastNumber.length) + percentageValue.toStringTrimmed()
                textViewInput.text = newEquation
            } else if (btnValue != "±") {

            }
        }
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
                            '/' -> output / number
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
                '/' -> output / number
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