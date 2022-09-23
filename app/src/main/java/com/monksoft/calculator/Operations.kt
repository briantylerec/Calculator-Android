package com.monksoft.calculator

import java.lang.NumberFormatException

class Operations {
    companion object{
        fun getOperator(operation: String): String {
            var operator = if (operation.contains(Constants.OPERATOR_MULTI)) Constants.OPERATOR_MULTI
            else if (operation.contains(Constants.OPERATOR_DIV)) Constants.OPERATOR_DIV
            else if (operation.contains(Constants.OPERATOR_SUM)) Constants.OPERATOR_SUM
            else Constants.OPERATOR_NULL

            if (operator == Constants.OPERATOR_NULL && operation.lastIndexOf(Constants.OPERATOR_SUB) > 0)  operator =
                Constants.OPERATOR_SUB

            return operator
        }

        fun canReplaceOperator(charSequense: String): Boolean {
            if (charSequense.length < 2) return false

            val lastElement = charSequense[charSequense.length - 1].toString()
            val penultimateElement = charSequense[charSequense.length - 2].toString()

            return (lastElement == Constants.OPERATOR_MULTI
                    || lastElement == Constants.OPERATOR_DIV
                    || lastElement == Constants.OPERATOR_SUM) &&
                    ( penultimateElement == Constants.OPERATOR_MULTI
                            || penultimateElement == Constants.OPERATOR_DIV
                            || penultimateElement == Constants.OPERATOR_SUM
                            || penultimateElement == Constants.OPERATOR_SUB)
        }

        fun tryResolve(operationRef: String, isFromResolve: Boolean, listener: OnResolveListener) {

            if (operationRef.isEmpty()) return

            var operation = operationRef
            if (lastDigit(operation)) operation = operation.substring(0, operation.length-1)

            val operator = getOperator(operation)
            val values = divideOperation(operator, operation)

            if (values.size > 1) {
                try {
                    val numberOne = values[0]!!.toDouble()
                    val numberTwo = values[1]!!.toDouble()

                    listener.onShowResult(getResult(numberOne, operator, numberTwo))

                } catch (e: NumberFormatException){
                    if (isFromResolve) listener.onShowMessage(R.string.message_num_incorrect)
                }
            } else {
                if (isFromResolve && operator != Constants.OPERATOR_NULL) listener.onShowMessage(R.string.message_num_incorrect)
            }
        }

        fun divideOperation(operator: String, operation: String): Array<String?> {
            var values = arrayOfNulls<String>(0)

            if (operator != Constants.OPERATOR_NULL){
                if (operator == Constants.OPERATOR_SUB){
                    val indez = operation.lastIndexOf(Constants.OPERATOR_SUB)
                    if (indez < operation.length-1){
                        values = arrayOfNulls(2)
                        values[0] = operation.substring(0,indez)
                        values[1] = operation.substring(indez+1)
                    } else {
                        values = arrayOfNulls(1)
                        values[0] = operation.substring(0,indez)
                    }
                } else values = operation.split(operator).dropLastWhile { it == "" }.toTypedArray()
            }
            return values
        }

        fun lastDigit(operationRef: String): Boolean {
            if (operationRef.lastIndexOf(Constants.OPERATOR_SUM) == operationRef.length-1 ||
                operationRef.lastIndexOf(Constants.OPERATOR_SUB) == operationRef.length-1 ||
                operationRef.lastIndexOf(Constants.OPERATOR_MULTI) == operationRef.length-1 ||
                operationRef.lastIndexOf(Constants.OPERATOR_DIV) == operationRef.length-1 ||
                operationRef.lastIndexOf(Constants.OPERATOR_POINT) == operationRef.length-1 ){

                return true
            }
            return false
        }

        fun getResult(numberOne: Double, operator:String, numberTwo:Double) : Double {

            return when(operator){
                Constants.OPERATOR_SUM -> numberOne + numberTwo
                Constants.OPERATOR_SUB -> numberOne - numberTwo
                Constants.OPERATOR_MULTI -> numberOne * numberTwo
                else -> numberOne / numberTwo
            }
        }
    }
}