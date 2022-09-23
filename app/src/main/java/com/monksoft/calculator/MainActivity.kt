package com.monksoft.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import com.monksoft.calculator.Constants.OPERATOR_DIV
import com.monksoft.calculator.Constants.OPERATOR_MULTI
import com.monksoft.calculator.Constants.OPERATOR_NULL
import com.monksoft.calculator.Constants.OPERATOR_POINT
import com.monksoft.calculator.Constants.OPERATOR_SUB
import com.monksoft.calculator.Constants.OPERATOR_SUM
import com.monksoft.calculator.databinding.ActivityMainBinding
import java.lang.NumberFormatException
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvOperation.run {
            addTextChangedListener { charSequense ->
                if (Operations.canReplaceOperator(charSequense.toString())) {
                    val newStr = "${text.toString().substring(0, text.length - 2)}${text.toString().substring(text.length - 1)}"
                    text = newStr
                }
            }
        }
    }

    fun onClickButton(view: View){

        val valueStr = (view as Button).text.toString()
        val operation = binding.tvOperation.text.toString()

        when(view.id){
            R.id.btnDelete  -> {
                binding.tvOperation.run {
                    if (text.length > 0) text = operation.substring(0, text.length - 1)
                }
            }
            R.id.btnClear  -> {
                binding.tvOperation.text = ""
                binding.tvResult.text = ""
            }
            R.id.btnResolve  -> checkOnResult(operation, true)

            R.id.btnDiv,
            R.id.btnMulti,
            R.id.btnSum,
            R.id.btnSub -> {
                checkOnResult(operation, false)
                addOperator(valueStr, operation)
            }
            R.id.btnPoint -> addPoint(valueStr, operation)
            else -> binding.tvOperation.append(valueStr)
        }
    }

    private fun addPoint(pointStr: String, operation: String) {
        if (!operation.contains(OPERATOR_POINT)) binding.tvOperation.append(pointStr)
        else {
            val operator = Operations.getOperator(operation)
            val values = Operations.divideOperation(operator, operation)

            if (values.size > 0) {
                val numberOne = values[0]!!
                if (values.size > 1){
                    val numberTwo = values[1]!!
                    if (numberOne.contains(OPERATOR_POINT) && !numberTwo.contains(OPERATOR_POINT)) {
                        binding.tvOperation.append(pointStr)
                    } else {
                        if (numberOne.contains(OPERATOR_POINT)) binding.tvOperation.append(pointStr)
                    }
                }
            }

        }
    }

    private fun addOperator(operator: String, operation: String) {
        val lastElement = if (operation.isEmpty()) "" else operation.substring(operation.length-1)

        if (operator == OPERATOR_SUB) {
            if (operation.isEmpty() || lastElement != OPERATOR_POINT && lastElement != OPERATOR_SUB){
                binding.tvOperation.append(operator)
            }
        } else {
            if (!operation.isEmpty() && lastElement != OPERATOR_POINT) binding.tvOperation.append(operator)
        }
    }

    private fun checkOnResult(operation: String, isFromResolve: Boolean){

        Operations.tryResolve(operation, isFromResolve, object : OnResolveListener {
            override fun onShowResult(result: Double) {
                binding.tvResult.text = String.format(Locale.ROOT, "%.2f", result)

                if (binding.tvResult.text.isNotEmpty() && !isFromResolve)
                    binding.tvOperation.text = binding.tvResult.text
            }

            override fun onShowMessage(errorRes: Int) {
                showMessage(errorRes)
            }
        })
    }

    private fun showMessage(errorRes: Int) {
        Snackbar.make(binding.root, errorRes, Snackbar.LENGTH_SHORT)
            .setAnchorView(binding.llTop).show()
    }
}