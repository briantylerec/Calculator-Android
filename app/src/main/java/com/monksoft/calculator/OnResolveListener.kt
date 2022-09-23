package com.monksoft.calculator

interface OnResolveListener {
    fun onShowResult (result: Double)
    fun onShowMessage (errorRes: Int)
}